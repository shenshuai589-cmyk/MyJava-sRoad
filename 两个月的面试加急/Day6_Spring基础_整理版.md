# Day 6 · Spring IOC 核心笔记（精简整理版）

> 原笔记内容扎实，但"三级缓存"和"refresh() 流程/面试模板"讲了两遍（概览一次、深挖一次），且大量单个词也拆成多行箭头图。这版按内容合并去重，只保留一份完整讲解。

## 今日路线图

```
Spring核心思想 → IOC → BeanFactory → ApplicationContext → BeanDefinition
→ refresh() → Bean创建流程 → Bean生命周期 → 三级缓存 → 循环依赖 → AOP
```

今天的 5 个核心 ⭐⭐⭐：`refresh()`、Bean 创建流程、Bean 生命周期、三级缓存 + 循环依赖、AOP 创建代理。

---

## 一、IOC 是什么？

**没有 IOC**：`UserService` 内部自己 `new UserDao()`，两者强依赖——以后 `UserDao` 换实现，`UserService` 也要跟着改。

**有 IOC**：`UserService` 只声明 `private UserDao userDao;`，具体由谁创建、怎么创建，交给 Spring 容器负责创建、组装、管理。

> 控制反转：以前是程序员主动 `new` 对象再交给上层用；现在是组件告诉 Spring"我需要什么"，由 Spring 创建并注入。控制权从业务代码转移到了 Spring 容器，所以叫 **Inversion of Control**。

### IOC 和 DI 的区别（高频面试题）

| |是什么|
|---|---|
|IOC|一种**思想**：对象的控制权从程序员手中交给 Spring|
|DI|IOC 的**实现方式**：Dependency Injection，依赖注入，Spring 发现 A 依赖 B，就把 B 注入 A|

**标准答案**：IOC 是一种控制反转的思想，DI 是 IOC 的一种具体实现方式，Spring 通过依赖注入完成对象之间依赖关系的组装。

---

## 二、IOC 容器：BeanFactory 与 ApplicationContext

- **BeanFactory**：Spring IOC 容器最核心的接口，最重要的能力就是 `Object getBean(String name)`。
- **ApplicationContext**：在 BeanFactory 基础上的高级容器，多了国际化、事件发布、资源加载、自动注册各种后处理器等企业级功能。

关系：`ApplicationContext` 是更高级的容器，`BeanFactory` 是它的底层基础，二者不是平行概念。

平时几乎不直接用 BeanFactory，是因为 Spring Boot 里 `SpringApplication.run()` 拿到的 `context` 本身就是 IOC 容器（`ApplicationContext`），`@Autowired` 背后走的链路是 `ApplicationContext → BeanFactory → Bean`。

**标准答案**：BeanFactory 是 Spring IOC 容器的底层核心接口，负责 Bean 的创建和获取；ApplicationContext 是更高级的容器，在 BeanFactory 基础上扩展了事件、国际化、资源加载等功能，容器启动时会自动完成更多组件的初始化。

---

## 三、BeanDefinition → Bean

`@Service class UserService {}` 被扫描到后，Spring **不是**直接 `new UserService()`，而是先建立一份 **BeanDefinition**——描述这个 Bean 的"说明书"（不是 Bean 本身）：

|字段|示例值|
|---|---|
|Bean 类型|UserService|
|Bean 名称|userService|
|Scope|singleton|
|是否懒加载|false|
|是否自动注入|true|
|初始化 / 销毁方法|...|

这条线要记牢：`Class → 扫描 → BeanDefinition → 根据 BeanDefinition 创建 → Bean`。

---

## 四、refresh() 全流程总览

Spring 容器启动的核心方法是 `AbstractApplicationContext.refresh()`：

```java
public void refresh() {
    synchronized (this.startupShutdownMonitor) {
        prepareRefresh();                               // 1. 准备刷新
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();                // 2. 获取 BeanFactory
        prepareBeanFactory(beanFactory);      4           // 3. 配置 BeanFactory
        postProcessBeanFactory(beanFactory);              // 4. 子类扩展点
        invokeBeanFactoryPostProcessors(beanFactory);      // 5. 处理 BeanDefinition
        registerBeanPostProcessors(beanFactory);           // 6. 注册 BeanPostProcessor
        // initMessageSource() / initApplicationEventMulticaster() / onRefresh() / registerListeners() 等
        finishBeanFactoryInitialization(beanFactory);       // 11. 创建非懒加载单例 Bean
        finishRefresh();                                    // 12. 完成刷新
    }
}
```

`synchronized (this.startupShutdownMonitor)`：容器启动/关闭涉及大量共享状态（BeanFactory、BeanDefinition、各种后处理器、监听器），需要同步避免多线程并发 refresh/close 导致状态混乱。

|步骤|阶段名称|核心职责|比喻|
|---|---|---|---|
|1|`refresh()`|触发容器刷新的入口方法|按下工厂"启动总开关"|
|2|BeanFactory|创建 `DefaultListableBeanFactory`，初始化基础环境|搭建工厂毛坯车间|
|3|BeanDefinition|扫描/解析配置，注册元数据（此时未实例化 Bean）|收集所有产品的"设计图纸"|
|4|BeanFactoryPostProcessor|Bean 实例化之前拦截、修改 BeanDefinition|"总工程师"开工前审查图纸|
|5|BeanPostProcessor|实例化并注册后置处理器，供后续 Bean 创建时调用|流水线安排"质检员"|
|6|创建 Bean|所有非懒加载单例 Bean 完成实例化、属性注入、初始化（含 AOP）|流水线批量生产|
|7|完成启动|清理缓存，发布 `ContextRefreshedEvent`|产品入库，工厂开业|

---

## 五、refresh() 逐步拆解

### 5.1 prepareRefresh()——准备容器自身状态

**注意：这一步还没有创建任何 Bean**，不是 new UserService，也不是执行 `@Autowired`，只是在正式初始化 BeanFactory 之前把 ApplicationContext 自身状态准备好，具体做 5 件事：

1. 记录启动时间 `this.startupDate = System.currentTimeMillis()`
2. 设置容器状态 `active = true`、`closed = false`（容器生命周期：创建 → refresh() → 运行中 → close() → 关闭，需要状态标记）
3. 初始化 Environment（`initPropertySources()`）——管理 `server.port`、`spring.datasource.url`、环境变量、JVM 系统属性、Profile 等配置，后面组件初始化要读取这些配置（比如根据 `spring.profiles.active=dev` 决定用哪份配置文件）
4. `validateRequiredProperties()`——校验必须存在的配置是否缺失，缺失则启动失败
5. 准备早期事件 `earlyApplicationEvents`——如果事件发生时监听器还没注册完，先暂存，等监听器注册完再处理

### 5.2 obtainFreshBeanFactory()——获取新的 BeanFactory

内部走 `refreshBeanFactory()`。不同 `ApplicationContext` 实现（`AnnotationConfigApplicationContext`、`ClassPathXmlApplicationContext`）创建 BeanFactory 的方式不同。之所以叫 "Fresh"，是因为这是"重新建立"而非复用旧的 BeanFactory。

此时 BeanFactory 里已经有一批 **BeanDefinition**，但还没有 Bean 实例——BeanDefinition 和 Bean 实例是两个不同阶段的东西。

### 5.3 prepareBeanFactory()——配置 BeanFactory 基础能力

给 BeanFactory 设置 ClassLoader、`BeanExpressionResolver`，添加一些系统级 `BeanPostProcessor`（如 `ApplicationContextAwareProcessor`，负责处理 `ApplicationContextAware` 接口回调）。这一步还做了两件容易被问到的事：

- **`beanFactory.ignoreDependencyInterface(XxxAware.class)`**——把 `EnvironmentAware`、`ApplicationContextAware` 等一系列 `xxxAware` 接口标记为"不走普通自动装配规则"，因为这些接口应该由专门的 `ApplicationContextAwareProcessor` 处理，而不是让 `@Autowired` 的依赖解析机制去处理，两者是不同的处理路径。
- **`beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory)`**——告诉 BeanFactory："如果有人需要 `BeanFactory` / `ApplicationContext` 这类特殊类型，直接给我这个指定实例"，而不是再创建一个新的 Bean。

**Aware 接口机制**：Spring 有一系列 `xxxAware` 接口（`BeanNameAware`、`BeanFactoryAware`、`ApplicationContextAware`、`EnvironmentAware`、`ResourceLoaderAware`、`ApplicationEventPublisherAware`），核心思想是让 Bean 能感知 Spring 容器提供的某些底层对象。Bean 创建时，`ApplicationContextAwareProcessor` 会判断这个 Bean 是否实现了某个 Aware 接口，如果是，就把对应的对象（`ApplicationContext`、`Environment`……）传给它，调用对应的 `setXxx()` 方法。为什么不直接用 `@Autowired` 拿？因为 Spring 内部大量基础设施 Bean 需要在很底层的阶段就能拿到这些对象，Aware 是比 `@Autowired` 更底层的机制。

### 5.4 postProcessBeanFactory()——留给子类的扩展点

不同 `ApplicationContext` 子类可以在 BeanFactoryPostProcessor 执行前对 BeanFactory 做额外处理，目前了解即可。

### 5.5 invokeBeanFactoryPostProcessors()——处理 BeanDefinition ⭐⭐⭐

回答一个关键问题：**`@Component`、`@Configuration`、`@Bean`、`@ComponentScan` 到底是怎么被识别并注册成 BeanDefinition 的？**

核心角色：**`ConfigurationClassPostProcessor`**。举例：

```java
@Configuration
@ComponentScan("com.example")
public class AppConfig {}

@Component
public class UserService {}
```

流程：`解析 AppConfig` → `发现 @ComponentScan` → `扫描 com.example 包` → `发现 UserService` → `创建 UserService 的 BeanDefinition` → `注册到 BeanDefinitionRegistry`（BeanDefinition 注册中心，负责注册/查询/删除 BeanDefinition）。**此时 UserService 对象还没有被创建**，只有它的 BeanDefinition。

**比喻**：BeanDefinition = 施工图，Bean = 建好的房子，BeanFactoryPostProcessor 就是开工前修改/完善施工图的人。

多个 `BeanFactoryPostProcessor` 之间有执行顺序：`PriorityOrdered > Ordered > 普通`。

**⭐ 高频面试点**——不要混淆两个 PostProcessor：

||处理对象|发生时机|
|---|---|---|
|`BeanFactoryPostProcessor`|BeanDefinition（"改图纸"）|Bean 实例化**之前**|
|`BeanPostProcessor`|Bean 对象本身（"加工成品"）|Bean **创建过程中**（不是注册时执行）|

Spring 启动可以理解为两大阶段：**① 准备 BeanDefinition**（`@Component`/`@Configuration`/`@Bean`/`@ComponentScan`/`@Import` → BeanDefinition）；**② 根据 BeanDefinition 创建 Bean**（实例化 → 属性注入 → 初始化 → AOP → Bean）。

### 5.6 registerBeanPostProcessors()——注册 BeanPostProcessor

为后续 Bean 创建过程准备好各类处理器，例如：

- `AutowiredAnnotationBeanPostProcessor`：处理 `@Autowired`
- `CommonAnnotationBeanPostProcessor`：处理 `@Resource`、`@PostConstruct`、`@PreDestroy`
- `AbstractAutoProxyCreator` 及其子类：负责 AOP 代理的生成

`BeanPostProcessor` 有 `postProcessBeforeInitialization` / `postProcessAfterInitialization` 两个方法，**AOP 代理通常在 `postProcessAfterInitialization()` 里生成**。

先处理 BeanDefinition（5.5）再注册 BeanPostProcessor（5.6），最后才创建 Bean，是因为 BeanPostProcessor 要参与 Bean 的创建过程，必须提前准备好。

### 5.7 finishBeanFactoryInitialization()——真正创建 Bean ⭐⭐⭐

核心入口 `preInstantiateSingletons()`：触发所有**非懒加载单例 Bean** 的创建（`scope=singleton` 且没加 `@Lazy`）。如果 Bean 加了 `@Lazy`，容器启动时不创建，第一次被用到时才创建。

Bean 创建完整调用链：

```
getBean() → doGetBean() → createBean() → doCreateBean()
   → createBeanInstance() → populateBean() → initializeBean() → Bean 完成
```

用造车类比：

1. **`getBean()` / `doGetBean()`** —— 查仓库：有现成的直接给，没有就去工厂造。
2. **`createBean()` / `doCreateBean()`** —— 下达生产指令，进入车间。
3. **`createBeanInstance()`（造车架）** —— 用反射 `new` 出对象，此时属性全是 `null`（空壳，无发动机无轮胎）。Spring 会根据 BeanDefinition 决定用构造方法 / 工厂方法 / Supplier / CGLIB 中的哪种方式实例化。
4. **`populateBean()`（装配件）** —— 依赖注入（DI），把 `@Autowired` 标记的属性、其他 Bean 填进去（装发动机、轮胎）。
5. **`initializeBean()`（精装修与上牌）** —— 依次执行：`Aware` 接口回调 → `BeanPostProcessor.before`（`postProcessBeforeInitialization()`）→ 初始化方法（`@PostConstruct`、或实现 `InitializingBean` 接口的 `afterPropertiesSet()` 方法、或 XML 里配的自定义 `init-method`）→ `BeanPostProcessor.after`（`postProcessAfterInitialization()`，**AOP 动态代理在这里生成**，相当于给车喷漆镀膜）。
6. **Bean 完成** —— 一个完整可用的对象，放入一级缓存，交付使用。

> 注意区分：`registerBeanPostProcessors()`（5.6）只是"提前把加工工具准备好、注册到 BeanFactory"，这里第 5 步的 before/after 才是"真正拿工具来加工这个 Bean"——一个是准备工具，一个是使用工具，容易被问混。

### 5.8 finishRefresh()——完成刷新

发布 `ContextRefreshedEvent` 事件，标志 Spring IOC 容器启动完成。

---

## 六、Bean 生命周期（完整版）

```
实例化 Bean → 属性赋值（DI） → Aware 接口回调 → BeanPostProcessor.before
→ 初始化（@PostConstruct / InitializingBean / 自定义 init-method）
→ BeanPostProcessor.after（AOP 代理常在此生成） → 业务使用 → 销毁
   （@PreDestroy / DisposableBean / 自定义 destroy-method）
```

（对应 XML 方式：自定义类实现 `BeanPostProcessor`，重写 `postProcessBeforeInitialization` / `postProcessAfterInitialization`，把这个类配置成一个 Bean 后，会对**同一个容器（BeanFactory）管理的所有 Bean** 生效——哪怕这个容器是由多个 XML 文件组合装配起来的，也不是"只对某一个 XML 文件里的 Bean 生效"。）

---

## 七、三级缓存与循环依赖 ⭐⭐⭐（今天最难的部分）

### 7.1 什么是循环依赖

```java
class A { @Autowired B b; }
class B { @Autowired A a; }
```

A 依赖 B，B 又依赖 A。如果没有特殊处理，手动 `new A()` → 需要 B → `new B()` → 需要 A → `new A()` → ……会无限递归，最终 `StackOverflowError`。

Spring 的解法是：**不等对象完全创建完成才放入容器**，先创建一个属性还没填完的"半成品"对象，提前暴露出去，让对方先拿着用。

Spring 靠一个集合 **`singletonsCurrentlyInCreation`** 记录"当前正在创建中的 Bean 名字"。创建 B 时发现需要 A，会去检查 A 是否在这个集合里——如果在，说明 A 正在创建中（而不是还没开始创建），这时候就不会重新 `new A()`，而是走"提前暴露"的三级缓存逻辑去拿 A 的早期引用。

### 7.2 三级缓存结构

本质是 `DefaultSingletonBeanRegistry` 里的三个 Map：

|缓存层级|变量名|类型|存储内容|
|---|---|---|---|
|一级缓存|`singletonObjects`|`Map<String, Object>`|**成品 Bean**：完整走完实例化 + 属性赋值 + 初始化 + AOP 的单例 Bean|
|二级缓存|`earlySingletonObjects`|`Map<String, Object>`|**半成品 Bean**：已实例化、属性可能还没注入完的 Bean（或提前生成的代理对象），防止重复创建代理|
|三级缓存|`singletonFactories`|`Map<String, ObjectFactory<?>>`|**Bean 工厂**：能够生成早期 Bean 引用的匿名工厂，核心目的是**延迟生成 AOP 代理**|

### 7.3 为什么必须是三级，不能只有二级？

关键原因：**AOP**。如果一个 Bean 需要代理（比如加了 `@Transactional`），最终放入容器的应该是**代理对象**而不是原始对象。如果只有二级缓存直接暴露原始对象，等后面发现这个 Bean 需要代理时就已经晚了——外部可能已经拿到了原始对象的引用。三级缓存里放的是 `ObjectFactory`（工厂，不是对象本身），**延迟到真正需要的时候才决定**：执行工厂方法时，如果需要代理就返回代理对象，不需要就返回原始对象。

### 7.4 完整流程（A 依赖 B，B 依赖 A）

```
创建 A → 实例化 A（原始对象）→ A 放入三级缓存（存的是创建 A 的 ObjectFactory）
→ 填充 A 属性 → 发现需要 B → 创建 B → 实例化 B → 填充 B 属性 → 发现需要 A
→ 一级缓存查 A：没有；二级缓存查 A：没有；三级缓存查 A：有工厂
→ 执行 ObjectFactory.getObject()（如果 A 需要 AOP，这里返回代理对象）→ 得到 A 的早期引用
→ 这个早期引用被放入二级缓存，同时从三级缓存中移除（此后如果还有别的 Bean 也需要 A，直接从二级缓存拿，不会重复调用工厂、不会重复生成代理）
→ B 拿到 A 的早期引用，B 完成，放入一级缓存 → A 拿到完整的 B，A 自身也完成初始化
→ A 从二级缓存移除，放入一级缓存
```

### 7.5 三级缓存的局限性

- ✅ 能解决：**单例 Bean + setter（属性）注入**的循环依赖
- ❌ 不能解决：**构造器循环依赖**——因为创建 A 之前必须先有构造参数 B，创建 B 之前又必须先有构造参数 A，根本没有机会提前暴露半成品对象
- ❌ 不能解决：**prototype（原型）Bean** 的循环依赖——原型 Bean 每次创建都是新对象，没有统一容器缓存管理

### 7.6 面试标准答案

> Spring 为了解决单例 Bean 的循环依赖问题，引入了三级缓存。一级缓存存放完整初始化后的 Bean，二级缓存存放提前暴露的半成品 Bean，三级缓存存放 Bean 创建工厂。三级缓存的作用是延迟决定提前暴露的是原始对象还是代理对象，从而支持 AOP 场景下的循环依赖；它只能解决单例 + setter 注入的循环依赖，无法解决构造器循环依赖和 prototype 循环依赖。

---

## 八、Spring IOC 容器启动过程 —— 背诵模板

> Spring IOC 容器启动的核心方法是 `ApplicationContext` 的 `refresh()` 方法。首先通过 `obtainFreshBeanFactory()` 获取 BeanFactory 并加载 BeanDefinition，然后 `prepareBeanFactory()` 对 BeanFactory 做基础配置。之后执行 `BeanFactoryPostProcessor`（`invokeBeanFactoryPostProcessors()`），对 BeanDefinition 进行修改和解析，例如处理 `@Configuration`、`@Bean`、`@ComponentScan` 等配置。接着 `registerBeanPostProcessors()` 注册 `BeanPostProcessor`，为后续 Bean 创建提供扩展能力。最后通过 `finishBeanFactoryInitialization()` 创建非懒加载单例 Bean，在创建过程中完成实例化、依赖注入、初始化以及 AOP 代理，最终通过 `finishRefresh()` 完成整个 IOC 容器启动。

这个回答覆盖初级开发、1~3 年经验面试、Spring 源码方向都够用。

---

## 九、易错点 Top 5（面试陷阱）

1. **refresh() 就是创建 Bean 吗？** ❌ 不是。refresh() 是 IOC 容器启动的总入口，只有后半部分（`finishBeanFactoryInitialization`）才真正触发 Bean 创建。
2. **`BeanFactoryPostProcessor` 和 `BeanPostProcessor` 一样吗？** ❌ 不一样。前者改"图纸"（BeanDefinition），后者加工"成品"（Bean 对象）。
3. **`BeanPostProcessor` 什么时候执行？** 不是注册的时候执行，而是在 Bean **创建过程中**执行。
4. **三级缓存解决所有循环依赖吗？** ❌ 不是，只解决**单例 Bean 的 setter 属性循环依赖**。
5. **为什么不能只有二级缓存？** 因为二级缓存无法延迟决定要不要生成代理对象，三级缓存能在需要时才决定暴露原始对象还是代理对象，从而支持 AOP 场景下的循环依赖。

---

## 十、今日检查清单

- [ ] 能说清 IOC 和 DI 的区别
- [ ] 能说清 BeanFactory 和 ApplicationContext 的区别
- [ ] 能说清 BeanDefinition 和 Bean 的区别
- [ ] 能完整背出 refresh() 的关键步骤，并说出每一步的核心职责
- [ ] 能说清 BeanFactoryPostProcessor 和 BeanPostProcessor 的区别与执行时机
- [ ] 能完整画出 Bean 创建六步链路（getBean → ... → initializeBean）
- [ ] 能说出 Bean 生命周期完整八步
- [ ] 能说清三级缓存分别存什么、为什么必须是三级不是二级
- [ ] 能说清三级缓存解决不了哪两种循环依赖