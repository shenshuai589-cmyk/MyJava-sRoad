
**1.创建一个springboot项目**
pom
```java
<?xml version="1.0" encoding="UTF-8"?>  
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">  
    <modelVersion>4.0.0</modelVersion>  
    <parent>  
        <groupId>org.springframework.boot</groupId>  
        <artifactId>spring-boot-starter-parent</artifactId>  
        <version>4.1.0</version>  
        <relativePath/> <!-- lookup parent from repository -->  
    </parent>  
    <groupId>com.trace</groupId>  
    <artifactId>SecurityQuicklyStart</artifactId>  
    <version>0.0.1-SNAPSHOT</version>  
    <name>SecurityQuicklyStart</name>  
    <description>SecurityQuicklyStart</description>  
    <url/>  
    <licenses>  
        <license/>  
    </licenses>  
    <developers>  
        <developer/>  
    </developers>  
    <scm>  
        <connection/>  
        <developerConnection/>  
        <tag/>  
        <url/>  
    </scm>  
    <properties>  
        <java.version>17</java.version>  
    </properties>  
    <dependencies>  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-webmvc</artifactId>  
        </dependency>  
  
        <dependency>  
            <groupId>org.projectlombok</groupId>  
            <artifactId>lombok</artifactId>  
            <optional>true</optional>  
        </dependency>  
        <dependency>  
            <groupId>org.springframework.boot</groupId>  
            <artifactId>spring-boot-starter-webmvc-test</artifactId>  
            <scope>test</scope>  
        </dependency>  
    </dependencies>  
  
    <build>  
        <plugins>  
            <plugin>  
                <groupId>org.springframework.boot</groupId>  
                <artifactId>spring-boot-maven-plugin</artifactId>  
                <configuration>  
                    <excludes>  
                        <exclude>  
                            <groupId>org.projectlombok</groupId>  
                            <artifactId>lombok</artifactId>  
                        </exclude>  
                    </excludes>  
                </configuration>  
            </plugin>  
            <plugin>  
                <groupId>org.apache.maven.plugins</groupId>  
                <artifactId>maven-compiler-plugin</artifactId>  
                <executions>  
                    <execution>  
                        <id>default-compile</id>  
                        <phase>compile</phase>  
                        <goals>  
                            <goal>compile</goal>  
                        </goals>  
                        <configuration>  
                            <annotationProcessorPaths>  
                                <path>  
                                    <groupId>org.projectlombok</groupId>  
                                    <artifactId>lombok</artifactId>  
                                </path>  
                            </annotationProcessorPaths>  
                        </configuration>  
                    </execution>  
                    <execution>  
                        <id>default-testCompile</id>  
                        <phase>test-compile</phase>  
                        <goals>  
                            <goal>testCompile</goal>  
                        </goals>  
                        <configuration>  
                            <annotationProcessorPaths>  
                                <path>  
                                    <groupId>org.projectlombok</groupId>  
                                    <artifactId>lombok</artifactId>  
                                </path>  
                            </annotationProcessorPaths>  
                        </configuration>  
                    </execution>  
                </executions>  
            </plugin>  
        </plugins>  
    </build>  
</project>
```

**2.创建启动类**
~~~java
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
  
@SpringBootApplication  
public class SecurityQuicklyStartApplication {  
  
    public static void main(String[] args) {  
        SpringApplication.run(SecurityQuicklyStartApplication.class, args);  
    }  
  
}
~~~

**3.编写HelloController**
```java
package com.security.controller;  
  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class HelloController {  
    @RequestMapping("/hello")  
    public String hello() {  
        return "Hello";  
    }  
  
}
```

**4.引入SpringSecurity**

```java
<dependency>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-security</artifactId>  
</dependency>
```

此时启动项目的时候会出现一个登录页：
![登陆页面|514](SpringSecurity/图片/001.png)
包括退出页面：
![退出页面|516](SpringSecurity/图片/002.png)
