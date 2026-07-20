
Spring Security的原理就是一个过滤器链，内部包含了提供各种功能的过滤器。
```
                用户发送请求
                      │
                      ▼
            DelegatingFilterProxy
                      │
                      ▼
            FilterChainProxy（核心过滤器）
                      │
          ┌───────────┴───────────┐
          │                       │
          ▼                       ▼
 UsernamePasswordAuthenticationFilter
          │
          ▼
 获取用户名、密码
          │
          ▼
 AuthenticationManager（认证管理器）
          │
          ▼
 AuthenticationProvider（认证提供者）
          │
          ▼
 UserDetailsService（查询用户）
          │
          ▼
    查询数据库（MySQL）
          │
          ▼
 返回 UserDetails
          │
          ▼
 PasswordEncoder 校验密码
          │
   ┌──────┴──────┐
   │             │
成功           失败
 │               │
 ▼               ▼
生成Authentication   抛异常
 │               │
 ▼               ▼
存入SecurityContext 返回401
 │
 ▼
继续执行过滤器
 │
 ▼
Controller
 │
 ▼
业务处理
 │
 ▼
返回响应
```

