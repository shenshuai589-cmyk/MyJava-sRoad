
**由于我们使用springsecurity的时候，登录密码是自动生成的，但是我们写项目都是用数据库**

那么可以修改成数据库中的账号密码

## 1.Service接口继承UserDetailsService接口

```java
public interface UserService extends UserDetailsService {  
}
```

## 2.Service接口的实现类实现Service接口并重写方法

```java
public class UserServiceImpl implements UserService {
	@Override  
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    }
}
```


### UserDetailsService接口中的内容
```java
package org.springframework.security.core.userdetails;  
  
public interface UserDetailsService {  
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;  
}
```

## 3.使用逆向工程构建dao层
![mybatis逆向工程|202](SpringSecurity/图片/003.png)

## 4.完成service
```java
@Service  
public class UserServiceImpl implements UserService {  
    @Autowired  
    private UserMapper userMapper;  
  
    @Override  
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
        User user = userMapper.selectByLoginAct(username);  
        if (user == null) {  
            throw new UsernameNotFoundException("账户不存在");  
        }  
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()  
                .username(user.getLoginAct())  
                .password(user.getLoginPwd())  
                .authorities(AuthorityUtils.NO_AUTHORITIES)  
                .build();  
        return userDetails;  
    }  
}
```

## 5.现加一个PasswordEncoder

```java
package com.powernode.config;  
  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  
import org.springframework.security.crypto.password.PasswordEncoder;  
  
@Configuration  
public class SecurityConfig {  
  
    @Bean  
    public PasswordEncoder passwordEncoder() {  
        return new BCryptPasswordEncoder();  
    }  
}
```

## 6.运行代码