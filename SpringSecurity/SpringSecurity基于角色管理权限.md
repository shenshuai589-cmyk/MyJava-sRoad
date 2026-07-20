
在 **Spring Security 中实现 RBAC（基于角色的权限控制）**，本质就是：

> **登录时加载用户 → 查询用户角色 → 查询角色权限 → 放入 SecurityContext → 请求访问时根据权限判断是否放行**

Spring Security 本身不负责你的数据库设计，它提供认证和授权框架，你需要把 RBAC 数据模型接进去。

下面按照企业项目的真实流程讲。

---

# 一、Spring Security RBAC整体流程

完整流程：

```
用户登录

    ↓

输入用户名密码

    ↓

Spring Security认证

    ↓

UserDetailsService查询用户

    ↓

查询数据库

    ↓

用户
 ↓
用户角色
 ↓
角色权限

    ↓

封装成UserDetails

    ↓

保存Authentication

    ↓

后续请求携带Token

    ↓

Security过滤器解析用户身份

    ↓

判断用户是否拥有权限

    ↓

允许 / 拒绝访问
```


Spring Security如何实现RBAC？

项目采用RBAC权限模型，通过用户、角色、权限三张核心表以及关联表维护权限关系。用户登录时，Spring Security通过UserDetailsService查询用户信息，同时加载用户对应角色和权限，并封装成UserDetails对象保存到SecurityContext中。请求访问接口时，通过过滤器获取用户认证信息，再利用@PreAuthorize或者权限表达式判断用户是否具有对应权限，从而实现接口级权限控制。如果结合JWT，则在请求中解析Token重新构建Authentication对象完成无状态认证。

在 **Spring Security 中基于角色管理权限（Role-Based Access Control）** 是 RBAC 最简单的一种实现方式。

核心思想：

> **给用户分配角色，访问接口时判断用户是否拥有某个角色。**

例如：

```
用户(User)

   ↓

角色(Role)

   ↓

访问权限
```

比如：

|用户|角色|能访问|
|---|---|---|
|admin|ADMIN|后台管理|
|张三|USER|普通业务接口|
|李四|VIP|VIP接口|

---

# 一、Spring Security角色权限模型

Spring Security中：

```
User
 |
 |
Authentication
 |
 |
GrantedAuthority
 |
 |
ROLE_ADMIN
ROLE_USER
```

实际上：

**角色也是一种权限（Authority）**

区别：

|类型|示例|判断方法|
|---|---|---|
|角色|ROLE_ADMIN|hasRole()|
|权限|user:delete|hasAuthority()|

---

# 二、数据库设计（角色管理）

## 1. 用户表

sys_user

```
CREATE TABLE sys_user(
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(100)
);
```

数据：

|id|username|
|---|---|
|1|admin|
|2|user|

---

## 2. 角色表

sys_role

```
CREATE TABLE sys_role(
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50)
);
```

数据：

|id|role_name|
|---|---|
|1|ADMIN|
|2|USER|

---

## 3. 用户角色关联表

sys_user_role

```
CREATE TABLE sys_user_role(
    user_id BIGINT,
    role_id BIGINT
);
```

例如：

```
user_id     role_id

1             1
```

表示：

admin拥有ADMIN角色。

---

# 三、登录时加载角色

Spring Security认证入口：

```
UserDetailsService
```

实现：

```
@Service
public class UserDetailsServiceImpl 
implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(
            String username) {


        // 查询数据库用户
        User user =
        userMapper.selectByUsername(username);


        // 查询角色
        List<String> roles =
        roleMapper.selectRoles(user.getId());


        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(
                    roles.toArray(new String[0])
                )
                .build();

    }

}
```

---

假设数据库返回：

```
ADMIN
USER
```

Spring Security会自动转换成：

```
ROLE_ADMIN

ROLE_USER
```

---

# 四、配置角色访问控制

## Spring Security 6写法

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {


@Bean
SecurityFilterChain filterChain(
        HttpSecurity http)
        throws Exception {


    http.authorizeHttpRequests(auth -> auth

        // ADMIN才能访问
        .requestMatchers("/admin/**")
        .hasRole("ADMIN")


        // USER和ADMIN都可以
        .requestMatchers("/user/**")
        .hasAnyRole(
            "USER",
            "ADMIN"
        )


        // 其他请求认证即可
        .anyRequest()
        .authenticated()

    );


    return http.build();

}

}
```

---

# 五、Controller中使用角色控制

开启：

```
@EnableMethodSecurity
```

---

## 1. 单角色

```
@RestController
public class AdminController {


@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String admin(){

    return "管理员页面";

}

}
```

只有：

```
ROLE_ADMIN
```

用户可以访问。

---

## 2. 多角色

```
@PreAuthorize(
"hasAnyRole('ADMIN','USER')"
)
@GetMapping("/list")
public String list(){

return "列表";

}
```

---

# 六、角色认证流程

完整流程：

```
用户登录

↓

输入账号密码

↓

UserDetailsService

↓

查询用户

↓

查询角色

↓

封装UserDetails


UserDetails:

username：admin

authorities:

[
 ROLE_ADMIN
]


↓

Authentication


↓

SecurityContext保存


==================


访问:

/admin/delete


↓

Security过滤器


↓

检查Authentication


↓

判断：

hasRole('ADMIN')


↓

通过
```


# 角色和权限的区别（面试重点）

## 角色

粗粒度：

```
ADMIN
USER
VIP
```

例如：

管理员进入后台。

---

## 权限

细粒度：

```
user:add

user:delete

product:update
```

例如：

管理员里面：

```
√ 删除用户

× 修改商品价格
```

---

企业一般：

```
角色
  |
  |
权限
```

即：

```
用户

 ↓

角色

 ↓

权限
```