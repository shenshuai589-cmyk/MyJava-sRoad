## RBAC（Role-Based Access Control，基于角色的访问控制）

RBAC 是企业后台系统中最常用的**权限管理模型**，核心思想：

> **用户(User) 通过角色(Role) 获得权限(Permission)**

也就是：

```
用户(User)
   |
   | 拥有
   ↓
角色(Role)
   |
   | 包含
   ↓
权限(Permission)
```

例如：

一个商城后台：

| 用户  | 角色   | 权限             |
| --- | ---- | -------------- |
| 张三  | 管理员  | 用户管理、商品管理、订单管理 |
| 李四  | 运营人员 | 商品管理           |
| 王五  | 客服   | 订单查看           |

---

# 一、为什么需要RBAC？

假设不用权限系统：

Controller：

```
@GetMapping("/deleteUser")
public String deleteUser(){
    // 删除用户
}
```

任何登录用户都能访问：

```
http://xxx.com/deleteUser
```

危险。

使用RBAC：

```
请求

↓

判断用户身份

↓

查询角色

↓

判断权限

↓

允许/拒绝
```

---

# 二、RBAC核心表设计（重点）

企业最经典：

## 1. 用户表 user

```
user
----------------
id
username
password
```

例如：

|id|username|
|---|---|
|1|admin|
|2|zhangsan|

---

## 2. 角色表 role

```
role
----------------
id
role_name
```

例如：

|id|role_name|
|---|---|
|1|管理员|
|2|普通用户|

---

## 3. 权限表 permission

```
permission
----------------
id
permission_name
url
```

例如：

|id|权限|路径|
|---|---|---|
|1|删除用户|/user/delete|
|2|添加商品|/product/add|

---

## 4. 用户角色关系表 user_role

因为：

一个用户可以有多个角色

一个角色也可以有多个用户

所以：

多对多：

```
user_role
----------------
user_id
role_id
```

数据：

|user_id|role_id|
|---|---|
|1|1|
|2|2|

表示：

admin拥有管理员角色

---

## 5. 角色权限关系表 role_permission

角色：

↓

多个权限

```
role_permission
----------------
role_id
permission_id
```

例如：

|role_id|permission_id|
|---|---|
|1|1|
|1|2|

管理员拥有：

- 删除用户
- 添加商品

---

# 三、完整关系图

```
          user
           |
           |
       user_role
           |
           |
          role
           |
           |
   role_permission
           |
           |
      permission

```

也就是：

```
用户
 ↓
角色
 ↓
权限
```