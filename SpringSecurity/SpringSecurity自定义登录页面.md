
## 1.在自定义登录账号和密码基础上添加controller方法

```java
@RequestMapping("/toLogin")  
public String toLogin() {  
    return "login";  
}
```

## 2.引入thymeleaf模板字符串并新建login.html

```java
<dependency>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-thymeleaf</artifactId>  
</dependency>
```

```html
<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta charset="UTF-8">  
    <title>登录</title>  
</head>  
<body>  
    <form action="/login" method="post">  
        账号:<input type="text" name="username" placeholder="Username"><br>  
        密码:<input type="password" name="password" placeholder="Password"><br>  
        <input name="_csrf" type="hidden" th:value="${_csrf.token}">  
        <input type="submit" value="登录">  
    </form>  
</body>  
</html>
```

## 3.由于我们手动创建了登录页，那么自动登录页的所以作用就失效了，需要手动添加

```java
@Bean  
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {  
    return httpSecurity  
            .formLogin((formLogin) ->{  
                formLogin  
                        .loginProcessingUrl("/login")  
                        .loginPage("/toLogin");  
                    })  
            .authorizeHttpRequests((authorizeHttpRequests)->{  
                authorizeHttpRequests  
                        .requestMatchers("/toLogin").permitAll()  
                        .anyRequest().authenticated();  
            })  
            .build();  
  
}
```

