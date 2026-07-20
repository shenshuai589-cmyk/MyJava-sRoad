# 1. 整合Mybatis
# 1.准备表
![数据库表|546](SpringBoot/images/我的第一个项目/020.png)
# 2.创建工程
![springboot-12-mybatis|414](SpringBoot/images/我的第一个项目/021.png)

# 3.引入mybatis-spring-boot-starter依赖

```xml
<dependency> 
	<groupId>org.mybatis.spring.boot</groupId> 
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<<version>3.0.3</version>   
</dependency>
```
# 4.配置yml或properties信息

1.数据源信息
```property
# 数据源配置  
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  
spring.datasource.url=jdbc:mysql://localhost:3306/springboot  
spring.datasource.username=root  
spring.datasource.password=250712  
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
```
2.mybatis映射文件信息
```yml
mybatis: 
# 映射文件对应的 XML 路径 
	mapper-locations: classpath:mapper/*.xml 
	# 实体类所在的包，配置后在 XML 中可以直接写类名，不用写全类名 
	type-aliases-package: com.example.demo.entity configuration: 
	# 开启驼峰命名自动映射（如数据库 user_id 自动映射到实体类 userId）
	map-underscore-to-camel-case: true
```

# 5.编写实体类
```java
@Data
package com.example.demo.entity; 
public class User { 
	private Long id; 
	private String username; 
	private String password;
	 // Getter, Setter, ToString
}
```

# 6.编写sql映射文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.mapper.UserMapper">
    <select id="findAll" resultType="User">
        SELECT id, username, password FROM user
    </select>
</mapper>
```

# 7.业务层调用
```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    

    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
}
```

# 2.整合SSM

## 1.创建工程
![springboot3-14-ssm|555](SpringBoot/images/我的第一个项目/026.png)

## 2.使用mybatis逆向工程将mybatis创建出来

![逆向工程|252](SpringBoot/images/我的第一个项目/027.png)
## 3.配置相关信息

1.给pojo对象添加无参构造和带全部参数构造
```java
@AllArgsConstructor  
@NoArgsConstructor
```
2.给主入口添加mapper包扫描
```java
@MapperScan(basePackages = "com.powernode.springboot.repository")
```
3.配置properties文件
```properties
# 数据源  
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  
spring.datasource.url=jdbc:mysql://localhost:3306/springboot  
spring.datasource.username=root  
spring.datasource.password=250712  
spring.datasource.type=com.zaxxer.hikari.HikariDataSource  
  
# MyBatis配置  
# 起别名  
mybatis.type-aliases-package=com.powernode.ssm.bean  
# Mapper XML文件位置  
mybatis.mapper-locations=classpath:/mapper/*.xml  
# 开启自动映射驼峰命名  
mybatis.configuration.map-underscore-to-camel-case=true
```

## 4.写service层和controller层
VipService
```java
package com.powernode.springboot.service;  
  
import com.powernode.springboot.bean.Vip;  
  
public interface VipService {  
  
    Vip findByCardNumber(String cardNumber);  
}
```
VipServiceImpl
```java
package com.powernode.springboot.service.impl;  
  
import com.powernode.springboot.bean.Vip;  
import com.powernode.springboot.repository.VipMapper;  
import com.powernode.springboot.service.VipService;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  
  
@Service  
public class VipServiceImpl implements VipService {  
    /*  
    * service层要注入dao层，那么repository就是和mapper、dao是一样的，所以要引入VipMapper  
    * */    @Autowired  
    private VipMapper  vipMapper;  
  
  
    @Override  
    public Vip findByCardNumber(String cardNumber) {  
        return vipMapper.selectByCardNumber(cardNumber);  
    }  
}
```
VipController
```java
package com.powernode.springboot.controller;  
  
import com.powernode.springboot.bean.Vip;  
import com.powernode.springboot.service.VipService;  
import org.apache.ibatis.annotations.Param;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class VipController {  
  
    @Autowired  
    private VipService vipService;  
  
    @GetMapping("/detail")  
    public Vip detail(@RequestParam("cn") String cardNumber) {  
        return vipService.findByCardNumber(cardNumber);  
    }  
}
```

## 5.增加Mapper.xml
```xml
<select id="selectByCardNumber" resultType="Vip">  
  select * from t_vip where card_number = #{cardNumber}
</select>
```

## 6.运行主入口方法









