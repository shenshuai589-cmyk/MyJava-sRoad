
### 1.默认值

当我们第一次使用springsecurity是，没有对yml文件进行配置时，那么系统会给我们一个默认的id和password，生产的password会显示在idea的控制台中。


### 2.修改用户和密码

那么每次启动都会生成一个新的password很麻烦，所以我们可以手动配置用户信息

```yml
spring:
  security:  
    user:  
      name: chan  
      password: lan
```

