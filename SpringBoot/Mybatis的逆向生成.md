> 想要实现Mybatis逆向生成，需要在idea中下载一个插件：free mybatis tools

![free mybatis tools|344](SpringBoot/images/我的第一个项目/022.png)

# 1.创建工程sprintboot3-13-generator
![springboot-13-generator|347](SpringBoot/images/我的第一个项目/023.png)
里面导入了lombok、mybatis以及mysql的依赖

# 2. 连接idea自带的数据库，右击想要逆工程的表
![右击表格并点击mybatis-generator|290](SpringBoot/images/我的第一个项目/024.png)

填写完信息：
![mybatis逆工程|467](SpringBoot/images/我的第一个项目/025.png)

# 3.添加一些缺少的信息

1.主入口程序添加
@MapperScan("com.powernode.springboot.repository")
```java
package com.powernode.springboot;  
  
import org.apache.ibatis.annotations.Mapper;  
import org.mybatis.spring.annotation.MapperScan;  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
  
@MapperScan("com.powernode.springboot.repository")  
@SpringBootApplication  
public class Springboot313GeneratorApplication {  
  
    public static void main(String[] args) {  
       SpringApplication.run(Springboot313GeneratorApplication.class, args);  
    }  
}
```
2.application.yml文件配置相关信息
```yml
spring:  
  application:  
    name: springboot3-13-generator  
#数据源  
  datasource:  
    driver-class-name: com.mysql.cj.jdbc.Driver  
    url: jdbc:mysql://localhost:3306/springboot  
    username: root  
    password: 250712  
    type: com.zaxxer.hikari.HikariDataSource  
  
#mybatis配置  
mybatis:  
    configuration:  
        # 开启驼峰命名转换  
        map-underscore-to-camel-case: true  
        # mapper映射文件位置  
        mapper-locations: classpath:mapper/*.xml  
        # 实体类所在包（起别名）  
        type-aliases-package: com.powernode.springboot.bean
```
