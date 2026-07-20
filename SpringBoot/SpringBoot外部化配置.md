
## 一、什么是外部化配置

> 外部化配置（Externalized Configuration）是 Spring Boot 的核心特性之一。

作用：

```
将配置数据从代码中分离出来
```

例如：

```
String username = "root";
String password = "123456";
```

硬编码存在很多问题：

- 修改需要重新编译
- 不同环境需要修改代码
- 不利于维护

因此 Spring Boot 提倡：

```
配置写到配置文件
代码负责读取配置
```

---
## 二、常见配置文件

### application.properties

```
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/test
spring.datasource.username=root
spring.datasource.password=123456
```

springboot有查找application.properties的顺序：
1. file:./config/
2. file:./
3. classpath:/config/
4. classpath:/

---

### application.yml

推荐使用。

```
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: 123456
```

---
## 三、读取配置

### 方式1：@Value

配置：

```
user:
  name: zhangsan
  age: 20
```

读取：

```
@Component
public class User {

    @Value("${user.name}")
    private String name;

    @Value("${user.age}")
    private Integer age;
}
```

---

### 方式2：Environment

```
@Autowired
private Environment environment;
```

读取：

```
String name = environment.getProperty("user.name");
```

---

### ==方式3：@ConfigurationProperties（推荐）==

==读取配置的核心作用：把我写在配置文件（application.yml或application.properties）中的一大堆配置、自动、批量的注入到一个java实体类的对象属性中==
配置：

```
user:
  name: zhangsan
  age: 20
```

实体类：
==注意：这个类本身必须是 Spring 的 Bean（所以通常要配合 `@Component` 注解使用）。==
```
@Component
@ConfigurationProperties(prefix = "user")
public class User {

    private String name;
    private Integer age;

    // getter/setter
}
```

Spring 自动完成绑定。

==注意事项==

1. 使用该注解时，pojo类必须被@Component或@Configuration注解标注
2. 被注入的类必须要有setter方法
3. 命名规则
   - java的属性名使用小驼峰命名法
   - 配置文件
`first-name: zhangsan` 短横线隔开（Kebab-case）最推荐！Spring Boot 官方推荐的 `.yml` 键名写法。
`firstName: zhangsan`驼峰命名（Camel-case）标准 Java 风格，在 .yml 中也常用。
`first_name: zhangsan`下划线隔开（Snake-case）常见于数据库字段映射或传统配置。
`FIRST_NAME: zhangsan` 大写加下划线（UPPER_CASE）通常用于系统环境变量（Environment Variables）