
首先我们先写一个controller用来跳转 ==Principal principal==
```java
@ResponseBody  
@RequestMapping("/welcome")  
public Object welcome(Principal principal){  
    return principal;  
}
```

我们之前是通过UserDetails接口来接收信息的，但是里面显示的信息不够全面，所以我们可以自己写一个user对象实现UserDetails接口
```java
public class User implements UserDetails,Serializable {  
    /**
```

实现里面的方法
```java
// --------------- 实现UserDetails接口中的方法----------  
  
 @Override  
 public Collection<? extends GrantedAuthority> getAuthorities() {  
     return List.of();  
 }  
  
 @Override  
 public String getPassword() {  
     return this.loginPwd;  
 }  
  
 @Override  
 public String getUsername() {  
     return this.loginAct;  
 }  
  
 @Override  
 public boolean isAccountNonExpired() {  
     return this.accountNoExpired==1;  
 }  
  
 @Override  
 public boolean isAccountNonLocked() {  
     return this.accountNoLocked==1;  
 }  
  
 @Override  
 public boolean isCredentialsNonExpired() {  
     return this.credentialsNoExpired==1;  
 }  
  
 @Override  
 public boolean isEnabled() {  
     return this.accountEnabled==1;  
 }
```

将serviceimpl中的UserDetails内容换成我们自己写的
```java
@Override  
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
    User user = userMapper.selectByLoginAct(username);  
    if (user == null) {  
        throw new UsernameNotFoundException("账户不存在");  
    }  
    return user;  
}
```

修改securityConfig中的内容

==由于springSecurity登陆成功默认的跳转方式是跳转到我们访问的地址，但是我们现在修改了controller，所以需要手动配置==

```java
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {  
    http  
        .formLogin(formLogin -> formLogin  
            .loginProcessingUrl("/user/login")  
            .loginPage("/toLogin")  
                // 默认情况下，spring security登陆成功后是跳转到登陆前访问的那个地址  
            .successForwardUrl("/welcome") // 定制登录成功后跳转的地址
```


 **在实际开发中有些数据是不能显示的，所以我们可以进行修改**
- 部分字段不显示  @JsonIgnore
```java
@JsonIgnore  
private String loginPwd;
```

- 修改样式 @JsonFormat
```java
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")  
private Date createTime;
```

由于我们可能有很多的时间格式和时区需要更改，所以我们可以统一写到yml文件中
```java
spring:
  jackson:  
    date-format: yyyy-MM-dd HH:mm:ss  
    time-zone: GMT+8
```
