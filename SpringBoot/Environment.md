
# Spring Environment 接口

## 1. 什么是 Environment

`Environment` 是 Spring 框架中的一个核心接口，用于：

- 获取配置文件中的属性
- 获取操作系统环境变量
- 获取 JVM 启动参数
- 管理和判断当前激活的 Profile 环境

接口定义：

```java
public interface Environment extends PropertyResolver
```

继承关系：

```text
PropertyResolver
        ↑
    Environment
        ↑
ConfigurableEnvironment
```

---

## 2. 获取 Environment 对象

### 方式一：自动注入（最常用）

```java
@RestController
public class UserController {

    @Autowired
    private Environment environment;

}
```

---

### 方式二：通过 ApplicationContext 获取

```java
ApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("spring.xml");

Environment environment =
        applicationContext.getEnvironment();
```

---

## 3. 常用方法

### 3.1 getProperty()

获取配置项的值。

配置：

```properties
username=admin
```

代码：

```java
String username =
        environment.getProperty("username");
```

结果：

```text
admin
```

---

### 3.2 指定默认值

```java
String age =
        environment.getProperty("age", "18");
```

当配置项不存在时：

```text
18
```

---

### 3.3 指定返回类型

```java
Integer port =
        environment.getProperty(
                "server.port",
                Integer.class
        );
```

配置：

```properties
server.port=8080
```

结果：

```java
8080
```

---

### 3.4 判断属性是否存在

```java
boolean flag =
        environment.containsProperty("username");
```

结果：

```java
true
```

---

## 4. 读取 application.properties 配置

配置文件：

```properties
user.name=zhangsan
user.age=20
```

代码：

```java
@RestController
public class UserController {

    @Autowired
    private Environment environment;

    @RequestMapping("/user")
    public String user() {

        String name =
                environment.getProperty("user.name");

        String age =
                environment.getProperty("user.age");

        return name + ":" + age;
    }
}
```

结果：

```text
zhangsan:20
```

---

## 5. 读取操作系统环境变量

### Windows

```text
JAVA_HOME
Path
TEMP
```

### Linux

```bash
echo $JAVA_HOME
```

Spring 中读取：

```java
String javaHome =
        environment.getProperty("JAVA_HOME");
```

结果：

```text
D:\Java\jdk17
```

---

## 6. 读取 JVM 参数

启动程序：

```bash
java -Dusername=admin Main
```

获取参数：

```java
String username =
        environment.getProperty("username");
```

结果：

```text
admin
```

---

## 7. Profile 环境管理

企业开发常见环境：

```text
开发环境（dev）
测试环境（test）
生产环境（prod）
```

---

### 配置激活环境

```properties
spring.profiles.active=dev
```

---

### 获取当前激活环境

```java
String[] profiles =
        environment.getActiveProfiles();

for (String profile : profiles) {
    System.out.println(profile);
}
```

结果：

```text
dev
```

---

### 判断环境是否激活

Spring Boot 2.4 以前：

```java
environment.acceptsProfiles("dev");
```

Spring Boot 2.4 以后推荐：

```java
environment.matchesProfiles("dev");
```

结果：

```java
true
```

---

## 8. Environment 与 @Value 的区别

配置：

```properties
username=admin
```

---

### @Value

```java
@Value("${username}")
private String username;
```

特点：

- 获取单个配置项
- 使用简单
- 最常用

---

### Environment

```java
@Autowired
private Environment environment;
```

```java
String username =
        environment.getProperty("username");
```

特点：

- 动态获取配置
- 支持动态拼接 key
- 更灵活

例如：

```java
String key = "user.name";

String value =
        environment.getProperty(key);
```

这是 `@Value` 做不到的。

---

## 9. 工作中的使用场景

### 场景一：读取固定配置（最常见）

使用：

```java
@Value("${server.port}")
private String port;
```

或者：

```java
@ConfigurationProperties
```

---

### 场景二：动态读取配置

使用：

```java
Environment
```

例如：

```java
String dbType = "mysql";

String url =
        environment.getProperty(
                "datasource." + dbType + ".url"
        );
```

这种动态拼接配置项的场景非常适合使用 Environment。

---

# 面试题

## 什么是 Spring 的 Environment？

Environment 是 Spring 提供的运行环境接口，用于统一管理应用程序的配置属性、系统环境变量、JVM 参数以及 Profile 环境。

常用方法：

```java
getProperty()
containsProperty()
getActiveProfiles()
matchesProfiles()
```

在 Spring Boot 中：

- application.properties
- application.yml
- JVM 参数
- 操作系统环境变量

最终都会被封装到 Environment 对象中统一管理。

---

## Environment 和 @Value 的区别？

| 对比项 | Environment | @Value |
|---------|---------|---------|
| 使用方式 | 编程式获取 | 注解注入 |
| 获取单个配置 | 支持 | 支持 |
| 动态读取配置 | 支持 | 不支持 |
| 批量读取配置 | 支持 | 不方便 |
| 使用频率 | 较少 | 非常高 |

一般开发：

- 固定配置 → `@Value`
- 配置对象 → `@ConfigurationProperties`
- 动态配置 → `Environment`
