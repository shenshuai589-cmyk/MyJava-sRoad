# Entity、DTO、VO 企业级开发规范

在 SpringBoot、SpringMVC、MyBatis 企业开发中，经常会看到：

- Entity（实体类）
- DTO（Data Transfer Object）
- VO（View Object）

三者职责不同，合理分层能够提高系统的安全性、可维护性和扩展性。

---

# 一、Entity（实体类）

## 1. 什么是 Entity

Entity（实体类）：

> 与数据库表对应的 Java 对象。

通常一个表对应一个实体类。

例如数据库表：

```sql
tb_user
------------------
id
username
password
email
```

对应实体类：

```java
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;
}
```

---

## 2. Entity 的作用

主要负责：

```text
数据库 ↔ Java对象
```

用于：

- MyBatis 查询结果封装
- 数据库存储
- 数据库更新

例如：

```java
User user = userMapper.selectById(1L);
```

返回的就是 Entity。

---

## 3. Entity 特点

### 字段通常与数据库保持一致

```java
private Long id;
private String username;
private String password;
private String email;
```

### 不参与页面展示设计

Entity 是数据库模型。

不是给前端展示的数据模型。

---

# 二、DTO（Data Transfer Object）

## 1. 什么是 DTO

DTO：

> Data Transfer Object（数据传输对象）

用于：

```text
前端 → 后端
```

或者：

```text
服务 → 服务
```

之间的数据传输。

---

## 2. 为什么需要 DTO

例如用户注册：

前端发送：

```json
{
  "username":"admin",
  "password":"123456",
  "confirmPassword":"123456",
  "email":"admin@qq.com"
}
```

数据库表：

```sql
tb_user
------------------
id
username
password
email
```

其中：

```java
confirmPassword
```

只是用于校验。

数据库并不存在该字段。

因此不能直接使用 Entity。

---

## 3. DTO 示例

```java
public class UserRegisterDTO {

    private String username;

    private String password;

    private String confirmPassword;

    private String email;
}
```

Controller 接收：

```java
@PostMapping("/register")
public Result register(
        @RequestBody UserRegisterDTO dto){
}
```

---

## 4. 常见 DTO

### 新增

```java
UserAddDTO
```

### 修改

```java
UserUpdateDTO
```

### 登录

```java
LoginDTO
```

### 查询条件

```java
UserQueryDTO
```

例如：

```java
public class UserQueryDTO {

    private String username;

    private Integer ageStart;

    private Integer ageEnd;
}
```

---

## 5. DTO 特点

职责：

```text
接收请求参数
```

特点：

- 与数据库无关
- 面向业务
- 可以包含额外字段
- 可以添加校验注解

例如：

```java
@NotBlank
private String username;
```

---

# 三、VO（View Object）

## 1. 什么是 VO

VO：

> View Object（视图对象）

用于：

```text
后端 → 前端
```

返回展示数据。

---

## 2. 为什么需要 VO

数据库：

```sql
tb_user
------------------
id
username
password
email
```

Entity：

```java
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;
}
```

如果直接返回：

```java
return user;
```

前端得到：

```json
{
  "id":1,
  "username":"admin",
  "password":"123456",
  "email":"admin@qq.com"
}
```

密码被暴露。

存在安全风险。

---

## 3. VO 示例

```java
public class UserVO {

    private Long id;

    private String username;

    private String email;
}
```

返回：

```json
{
  "id":1,
  "username":"admin",
  "email":"admin@qq.com"
}
```

---

## 4. VO 特点

职责：

```text
封装展示数据
```

特点：

- 面向前端
- 可以隐藏敏感字段
- 可以新增展示字段
- 与数据库结构无关

例如：

```java
public class UserVO {

    private Long id;

    private String username;

    private String email;

    private String roleName;
}
```

roleName 可能来自多表查询。

数据库中未必存在该字段。

---

# 四、三者关系

## 请求流程

```text
前端
 │
 ▼
DTO
 │
 ▼
Controller
 │
 ▼
Service
 │
 ▼
Entity
 │
 ▼
Mapper
 │
 ▼
MySQL
```

---

## 返回流程

```text
MySQL
 │
 ▼
Entity
 │
 ▼
Service
 │
 ▼
VO
 │
 ▼
Controller
 │
 ▼
前端
```

---

# 五、完整案例

## 前端请求

```json
{
  "username":"admin"
}
```

---

## DTO

```java
public class UserQueryDTO {

    private String username;
}
```

---

## Entity

```java
public class User {

    private Long id;

    private String username;

    private String password;

    private String email;
}
```

---

## VO

```java
public class UserVO {

    private Long id;

    private String username;

    private String email;
}
```

---

## Controller

```java
@PostMapping("/user")
public UserVO query(
        @RequestBody UserQueryDTO dto){

    User user = userService.query(dto);

    UserVO vo = new UserVO();

    BeanUtils.copyProperties(user, vo);

    return vo;
}
```

---

# 六、项目目录结构

```text
com.xxx.project
│
├─ controller
│
├─ service
│
├─ mapper
│
├─ entity
│   └─ User.java
│
├─ dto
│   ├─ UserAddDTO.java
│   ├─ UserUpdateDTO.java
│   ├─ LoginDTO.java
│   └─ UserQueryDTO.java
│
├─ vo
│   ├─ UserVO.java
│   └─ OrderVO.java
│
└─ common
```

---

# 七、面试题

## DTO 和 VO 有什么区别？

DTO（Data Transfer Object）：

- 用于接收请求参数
- 用于服务间传输数据
- 属于输入对象

VO（View Object）：

- 用于封装返回数据
- 面向页面展示
- 属于输出对象

Entity：

- 与数据库表对应
- 用于数据库操作

---

# 八、记忆口诀

```text
DTO：接收数据
      （前端 → 后端）

Entity：操作数据
         （后端 ↔ 数据库）

VO：返回数据
     （后端 → 前端）
```

---

# 九、企业级最佳实践

不要直接使用 Entity 接收前端参数：

```java
@PostMapping
public void add(User user){
}
```

不推荐。

---

推荐：

```java
@PostMapping
public void add(
        @RequestBody UserAddDTO dto){
}
```

---

不要直接返回 Entity：

```java
return user;
```

不推荐。

---

推荐：

```java
return userVO;
```

---

企业级标准流程：

```text
DTO
 ↓
Entity
 ↓
VO
```

即：

```text
前端
 ↓
DTO
 ↓
Service
 ↓
Entity
 ↓
数据库

数据库
 ↓
Entity
 ↓
Service
 ↓
VO
 ↓
前端
```

这是 SpringBoot 企业项目中最常见、最规范的对象设计方案。