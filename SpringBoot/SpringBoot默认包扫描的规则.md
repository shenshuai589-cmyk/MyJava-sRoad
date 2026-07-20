# Spring Boot 默认包扫描规则

## 一、Spring Boot 的默认包扫描起点

假设启动类如下：

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
   public static void main(String[] args) {
       SpringApplication.run(DemoApplication.class, args);
   }
}
```

> `@SpringBootApplication` 实际上是三个注解的组合：
```
@SpringBootConfiguration  // 表示这是配置类
@EnableAutoConfiguration  // 启动自动配置
@ComponentScan            // 组件扫描
包扫描规则主要来源于 `@ComponentScan`
```
# 二、默认扫描规则

- **扫描起点**：启动类所在的包（例：`com.example.demo`）
- **扫描范围**：启动类所在包及其子包

示意：

```
com.example.demo

│

├── controller <- 会被扫描

├── service <- 会被扫描

├── repository <- 会被扫描

└── config <- 会被扫描
```
> 注意：上层包（如 `com.example`）不会被默认扫描

# 三、如何自定义扫描包

通过 `scanBasePackages` 或 `@ComponentScan` 指定扫描包：

```java
@SpringBootApplication(scanBasePackages = {"com.example.demo", "com.example.util"})  
public class DemoApplication {  
public static void main(String[] args) {  
SpringApplication.run(DemoApplication.class, args);  
	}  
}

```

或：

```java

@ComponentScan(basePackages = {"com.example.demo", "com.example.util"})
@SpringBootApplication
public class DemoApplication { ... }
```

# 4.默认会扫描的组件
默认会扫描带有以下注解的类：  
  
```java  
@Component  
@Service  
@Repository  
@Controller  
@RestController  
@Configuration  
```

