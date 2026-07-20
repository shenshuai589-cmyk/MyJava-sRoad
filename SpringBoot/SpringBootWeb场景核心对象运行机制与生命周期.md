# Spring Boot Web 场景核心对象运行机制与生命周期

本篇文档详细拆解了 Spring Boot 在启动 Web 场景时，底层的核心对象定义、它们之间的逻辑调用关系，以及在容器启动过程中的严格初始化与创建先后顺序。

---

## 1. 核心对象定义

在 Spring Boot 运行 Web 场景时，底层主要由以下四个核心对象相互配合、层层递进：

* **`application.yml` / `application.properties`（全局配置文件）**：外部的纯文本配置文件，用于存放开发人员自定义的参数（如服务器端口、环境配置等）。
* **`ServerProperties`（属性配置映射类）**：Spring Boot 内置的一个 Java Bean。它的核心作用是将配置文件中的文本数据，转换成 Java 对象的属性，并提供一套默认的参数。
* **Web 自动配置类（以 `ServletWebServerFactoryAutoConfiguration` 为代表）**：负责组装 Web 组件的“核心工厂类”。它是一个带有 `@Configuration` 注解的 Java 类，内部包含了创建 Tomcat、Jetty 等服务器的逻辑。
* **`Tomcat` / `ViewResolver`（具体的容器内部组件）**：最终注入到 Spring IOC 容器中、开箱即用的技术组件（整个 Web 场景启动时，一共有约 101 个类似的组件被创建）。

---

## 2. 对象之间的逻辑关系与调用链路

这些对象在系统启动时构成了一条**“数据向下传递，对象向上注册”**的完整闭环：

1. **数据绑定关系（文本 $
ightarrow$ 类属性）**：
   `ServerProperties` 通过注解 `@ConfigurationProperties(prefix = "server")` 与 `application.yml` 建立绑定。你在 `application.yml` 中写的任意配置，都会被自动赋值到 `ServerProperties` 对象的对应字段上。
2. **依赖注入关系（配置类 $
ightarrow$ 自动配置类）**：
   Web 自动配置类通过 `@EnableConfigurationProperties(ServerProperties.class)` 注解，将已经填充好用户参数的 `ServerProperties` 对象作为构造参数或成员变量注入到自己内部。
3. **生产与包含关系（自动配置类 $
ightarrow$ 最终组件）**：
   Web 自动配置类内部拥有挂载了 `@Bean` 注解的方法。它就像一个工厂，读取 `ServerProperties` 里的参数，并在底层通过 Java 代码（如 `new Tomcat()`）实例化具体的组件，最后将其交付给 Spring 的 IOC 容器管理。

---

## 3. 创建与初始化的先后顺序

在 Spring Boot 启动并刷新 IOC 容器的过程中，这些对象的加载和创建有着严格的先后顺序：

### 步骤 ①：解析并加载 `application.yml`
* **时机**：在 Spring 容器完全初始化之前（`Environment` 环境准备阶段）。
* **动作**：Spring Boot 的监听器会首先扫描并读取磁盘上的 `application.yml` 文本内容，将其加载到内存的 `PropertySources` 中，此时它们还只是键值对形式的配置数据。

### 步骤 ②：注册并实例化 `ServerProperties`
* **时机**：Spring 容器开始解析配置类、扫描 Bean 的定义阶段。
* **动作**：Spring 发现了 Web 自动配置类上声明的 `@EnableConfigurationProperties(ServerProperties.class)`，于是开始在容器中创建 `ServerProperties` 的 Bean 实例。在实例化的过程中，Spring 底层的绑定器（Binder）会将步骤 ① 中加载的 `application.yml` 参数，正式注入到该对象的属性中（如果用户没配，则保留原有的默认值，如端口 8080）。

### 步骤 ③：解析 Web 自动配置类
* **时机**：紧随步骤 ② 之后，容器解析 `@Configuration` 类内部组件的阶段。
* **动作**：Spring 容器开始处理 Web 自动配置类（如 `ServletWebServerFactoryAutoConfiguration`）。此时，步骤 ② 中已经实例化好且带有数据的 `ServerProperties` 对象，会被作为参数直接注入到这个自动配置类中。

### 步骤 ④：创建并注册 `Tomcat` 与 `ViewResolver` 等组件
* **时机**：容器刷新的中后期，开始执行 `@Bean` 方法生产具体实例的阶段。
* **动作**：自动配置类内部的 `@Bean` 方法被触发。例如，创建 Tomcat 工厂的方法会去读取已经注入进去的 `ServerProperties` 中的 `port` 属性，然后执行类似 `tomcat.setPort(properties.getPort())` 的底层代码。最终，完全配置好的 `Tomcat` 实例和 `ViewResolver` 实例被创建出来，并作为单例 Bean 正式注册到 Spring IOC 容器中。

---

## 4. 核心顺序流总结

```
[1. 外部纯文本] application.yml 
       │  (首先被读取到内存环境)
       ▼
[2. 配置实体] ServerProperties 实例化 ──> (接收来自 yml 的参数，若无则走默认值)
       │  (作为图纸参数，注入到下一阶段)
       ▼
[3. 配置工厂] Web 自动配置类被解析
       │  (执行内部 @Bean 方法，开始生产组件)
       ▼
[4. 核心组件] Tomcat / ViewResolver 实例被创建并塞入 IOC 容器