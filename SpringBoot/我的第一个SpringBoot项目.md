1.创建工程
![创建工程|522](SpringBoot/images/我的第一个项目/001.png)
2.==使用springboot需要继承springboot父工程==
在pom文件中添加下面代码
```xml
<parent>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-parent</artifactId>  
    <version>3.3.3</version>  
</parent>
```
3.引入web启动器依赖
```xml
<!--引入Spring Boot web启动器依赖-->  
<dependency>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-web</artifactId>  
</dependency>
```

4.编写主入口程序
==所有的springboot主入口都要加上@SpringBootApplication标注==
```java
package com.powernode.springboot;  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
@SpringBootApplication  
public class MyApplication {  
    public static void main(String[] args) {  
        SpringApplication.run(MyApplication.class, args);  
    }  
}
```
5.编写控制器
- ==注意：controller必须写在与主入口同包或者主入口的子包当中==
```java
package com.powernode.springboot.controller;  
  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class HelloController {  
  
    @GetMapping("/hello")  
  
    public String hello() {  
        return "hello,springboot3!!!";  
    }  
}
```
6.运行主入口main方法
