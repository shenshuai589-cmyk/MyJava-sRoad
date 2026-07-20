# @Value 注解详解

## 一、什么是 @Value

`@Value` 是 Spring 提供的注解。

作用：

```text
给属性注入值
```

可以从：

- 配置文件
    
- 环境变量
    
- 系统属性
    
- 普通字符串
    

中获取数据并注入到 Bean 中。

---

## 二、基本使用

### 直接赋值

```java
@Component
public class User {

    @Value("张三")
    private String name;

}
```

效果：

```java
name = "张三";
```

---

## 三、读取配置文件

### application.yml

```yaml
user:
  name: zhangsan
  age: 20
```

---

### Java代码

```java
@Component
public class User {

    @Value("${user.name}")
    private String name;

    @Value("${user.age}")
    private Integer age;

}
```

启动后：

```java
name = "zhangsan"
age = 20
```

---

## 四、读取 application.properties

### 配置文件

```properties
user.name=zhangsan
user.age=20
```

---

### 读取

```java
@Value("${user.name}")
private String name;

@Value("${user.age}")
private Integer age;
```

效果相同。

---

## 五、默认值

如果配置不存在：

```java
@Value("${user.name:admin}")
private String name;
```

含义：

```text
如果user.name存在
    使用配置值

如果不存在
    使用admin
```

例如：

```java
@Value("${server.port:8080}")
private Integer port;
```

---

## 六、注入系统属性

例如：

```java
@Value("${JAVA_HOME}")
private String javaHome;
```

读取：

```text
系统环境变量
```

---

## 七、支持 SpEL 表达式

Spring Expression Language

### 数学运算

```java
@Value("#{10 + 20}")
private Integer num;
```

结果：

```java
30
```

---

### 调用Bean属性

```java
@Component
public class User {

    private String name = "张三";

    public String getName() {
        return name;
    }
}
```

```java
@Component
public class Test {

    @Value("#{user.name}")
    private String username;

}
```

结果：

```java
username = "张三"
```

---

## 八、工作中的常见写法

### 读取端口

```java
@Value("${server.port}")
private Integer port;
```

---

### 读取数据库地址

```java
@Value("${spring.datasource.url}")
private String url;
```

---

### 读取项目名称

```java
@Value("${spring.application.name}")
private String appName;
```

---

## 九、@Value 的缺点

例如：

```java
@Value("${user.name}")
private String name;

@Value("${user.age}")
private Integer age;

@Value("${user.address}")
private String address;

@Value("${user.phone}")
private String phone;
```

属性多时：

```text
代码冗长
难维护
```

---

## 十、推荐方案

使用：

```java
@ConfigurationProperties
```

例如：

```yaml
user:
  name: zhangsan
  age: 20
  address: 北京
```

```java
@Component
@ConfigurationProperties(prefix = "user")
public class User {

    private String name;
    private Integer age;
    private String address;

}
```

自动绑定所有属性。

---

## 十一、面试题

### @Value 的作用？

用于给 Bean 属性注入值，可以从配置文件、环境变量、系统属性或 SpEL 表达式中获取数据。

---

### @Value 如何读取配置文件？

```java
@Value("${user.name}")
private String name;
```

---

### @Value 如何设置默认值？

```java
@Value("${user.name:admin}")
private String name;
```

---

### @Value 和 @ConfigurationProperties 区别？

#### @Value

```text
读取单个配置
适合少量属性
```

#### @ConfigurationProperties

```text
批量绑定配置
类型安全
适合配置对象
开发中推荐
```

---

## 一句话总结

```text
@Value 用于给属性注入值。

最常见写法：

@Value("${配置项名称}")

适合读取少量配置。

大量配置推荐使用
@ConfigurationProperties。
```