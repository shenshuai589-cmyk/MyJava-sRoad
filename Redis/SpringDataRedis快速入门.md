
# 1.引入依赖

```xml
redis依赖
<dependency>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-data-redis-test</artifactId>  
    <scope>test</scope>  
</dependency>  

<dependency>  
    <groupId>org.apache.commons</groupId>  
    <artifactId>commons-pool2</artifactId>  
</dependency>
```

# 2.配置yml文件

```yml
spring:
	redis:  
	  data:  
	   host: 192.168.80.128  
	   port: 6379  
	   password: 123456  
	   database: 0  
	   pool:  
	     max-active: 8
```

# 3.引入RedisTemplate

