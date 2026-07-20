# 1.  web开发配置前缀都有哪些

自动配置类 ——> 配置某些组件（一个组件对应一个功能）------->组件需要数据，数据来源是XxxProperties属性类对象--------->XxxProperties属性类对象的数据来源于application.properties的配置

```properties
#springmvc相关配置
spring.mvc

# web开发通用配置
spring.web

# 文件上传配置
spring.servlet.multipart.

# 服务器配置
server.
```

