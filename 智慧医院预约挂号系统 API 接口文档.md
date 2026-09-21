# 智慧医院预约挂号系统 API 接口文档

## 一、项目简介

智慧医院预约挂号系统是一个基于 Spring Boot 开发的医院管理系统。

主要实现：

- 用户注册、登录
- 患者信息管理
- 医生管理
- 科室管理
- 预约挂号
- 就诊记录
- 处方管理
- 药品管理
- 管理员管理
- 操作日志

---

# 二、技术栈

后端：

- Java 17
- Spring Boot 3
- MyBatis
- MySQL 8
- Redis
- JWT
- AOP

前端：

- Vue 3
- Axios
- Element Plus

暂不使用：

- Spring Cloud
- Nacos
- Gateway
- RabbitMQ
- Elasticsearch
- Docker
- Spring Security

---

# 三、统一返回格式

所有接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 成功

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 参数错误

```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

### 未登录

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null
}
```

### 无权限

```json
{
  "code": 403,
  "message": "无权限访问",
  "data": null
}
```

### 服务器异常

```json
{
  "code": 500,
  "message": "服务器异常",
  "data": null
}
```

---

# 四、用户模块

基础路径：

```text
/user
```

---

## 4.1 用户注册

### 请求

```http
POST /user/register
```

### 请求参数

```json
{
  "username": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138000"
}
```

### 参数说明

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| realName | String | 是 | 真实姓名 |
| phone | String | 是 | 手机号 |

### 返回

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

---

# 五、登录模块

## 5.1 用户登录

### 请求

```http
POST /user/login
```

### 请求参数

```json
{
  "username": "zhangsan",
  "password": "123456"
}
```

### 返回

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "xxxxxxxxxx"
  }
}
```

登录成功后，前端保存 JWT。

后续需要登录的接口：

```http
Authorization: Bearer xxxxxxxxxx
```

---

# 六、个人信息

## 6.1 获取个人信息

### 请求

```http
GET /user/info
```

### 请求头

```http
Authorization: Bearer token
```

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "realName": "张三",
    "phone": "13800138000",
    "role": "PATIENT"
  }
}
```

---

## 6.2 修改个人信息

### 请求

```http
PUT /user/info
```

### 请求参数

```json
{
  "realName": "张三",
  "phone": "13900139000"
}
```

---

## 6.3 修改密码

### 请求

```http
PUT /user/password
```

### 请求参数

```json
{
  "oldPassword": "123456",
  "newPassword": "654321",
  "confirmPassword": "654321"
}
```

---

# 七、科室模块

基础路径：

```text
/department
```

---

## 7.1 查询科室列表

```http
GET /department/list
```

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "内科",
      "description": "负责内科疾病诊疗"
    },
    {
      "id": 2,
      "name": "外科",
      "description": "负责外科疾病诊疗"
    }
  ]
}
```

---

## 7.2 查询科室详情

```http
GET /department/{id}
```

例如：

```http
GET /department/1
```

---

## 7.3 新增科室

管理员权限。

```http
POST /admin/department
```

### 请求参数

```json
{
  "name": "内科",
  "description": "负责内科疾病诊疗"
}
```

---

## 7.4 修改科室

管理员权限。

```http
PUT /admin/department/{id}
```

---

## 7.5 删除科室

管理员权限。

```http
DELETE /admin/department/{id}
```

---

# 八、医生模块

基础路径：

```text
/doctor
```

---

## 8.1 查询医生列表

```http
GET /doctor/list
```

### 请求参数

可选：

```text
departmentId
name
```

例如：

```http
GET /doctor/list?departmentId=1
```

---

## 8.2 查询医生详情

```http
GET /doctor/{id}
```

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "李医生",
    "departmentId": 1,
    "departmentName": "内科",
    "title": "主治医师",
    "introduction": "擅长内科常见疾病诊疗"
  }
}
```

---

## 8.3 新增医生

管理员权限。

```http
POST /admin/doctor
```

### 请求参数

```json
{
  "name": "李医生",
  "departmentId": 1,
  "title": "主治医师",
  "phone": "13800138000",
  "introduction": "擅长内科常见疾病诊疗"
}
```

---

## 8.4 修改医生

管理员权限。

```http
PUT /admin/doctor/{id}
```

---

## 8.5 删除医生

管理员权限。

```http
DELETE /admin/doctor/{id}
```

---

# 九、预约挂号模块

基础路径：

```text
/appointment
```

这是整个项目的核心模块之一。

---

## 9.1 查询医生可预约时间

```http
GET /appointment/available
```

### 请求参数

```text
doctorId
date
```

例如：

```http
GET /appointment/available?doctorId=1&date=2026-09-20
```

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "time": "08:00-09:00",
      "available": true
    },
    {
      "time": "09:00-10:00",
      "available": false
    },
    {
      "time": "10:00-11:00",
      "available": true
    }
  ]
}
```

---

## 9.2 创建预约

患者权限。

```http
POST /appointment
```

### 请求参数

```json
{
  "doctorId": 1,
  "appointmentDate": "2026-09-20",
  "timeSlot": "08:00-09:00"
}
```

### 返回

```json
{
  "code": 200,
  "message": "预约成功",
  "data": {
    "appointmentId": 1001
  }
}
```

---

## 9.3 查询我的预约

```http
GET /appointment/my
```

### 返回

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1001,
      "doctorName": "李医生",
      "departmentName": "内科",
      "appointmentDate": "2026-09-20",
      "timeSlot": "08:00-09:00",
      "status": 0
    }
  ]
}
```

预约状态：

```text
0 = 待就诊
1 = 已就诊
2 = 已取消
```

---

## 9.4 取消预约

```http
PUT /appointment/{id}/cancel
```

### 返回

```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

---

# 十、医生接诊模块

基础路径：

```text
/doctor/appointment
```

---

## 10.1 查询我的患者预约

医生权限。

```http
GET /doctor/appointment/list
```

---

## 10.2 开始接诊

医生权限。

```http
PUT /doctor/appointment/{id}/start
```

---

# 十一、就诊记录模块

基础路径：

```text
/medical-record
```

---

## 11.1 创建就诊记录

医生权限。

```http
POST /medical-record
```

### 请求参数

```json
{
  "appointmentId": 1001,
  "patientId": 5,
  "diagnosis": "急性胃炎",
  "description": "患者腹痛、恶心",
  "advice": "注意饮食，按时服药"
}
```

---

## 11.2 查询我的就诊记录

患者权限。

```http
GET /medical-record/my
```

---

## 11.3 查询患者历史就诊记录

医生权限。

```http
GET /medical-record/patient/{patientId}
```

---

# 十二、药品模块

基础路径：

```text
/medicine
```

---

## 12.1 查询药品列表

```http
GET /medicine/list
```

---

## 12.2 查询药品详情

```http
GET /medicine/{id}
```

---

## 12.3 新增药品

管理员权限。

```http
POST /admin/medicine
```

### 请求参数

```json
{
  "name": "阿莫西林",
  "price": 20.50,
  "stock": 100,
  "description": "抗感染药物"
}
```

---

## 12.4 修改药品

```http
PUT /admin/medicine/{id}
```

---

## 12.5 删除药品

```http
DELETE /admin/medicine/{id}
```

---

# 十三、处方模块

## 13.1 创建处方

医生权限。

```http
POST /prescription
```

### 请求参数

```json
{
  "patientId": 5,
  "medicalRecordId": 2001,
  "items": [
    {
      "medicineId": 1,
      "quantity": 2
    },
    {
      "medicineId": 3,
      "quantity": 1
    }
  ]
}
```

---

## 13.2 查询我的处方

患者权限。

```http
GET /prescription/my
```

---

## 13.3 查询处方详情

```http
GET /prescription/{id}
```

---

# 十四、管理员模块

基础路径：

```text
/admin
```

管理员可以：

```text
用户管理
医生管理
科室管理
药品管理
预约管理
```

---

## 14.1 查询用户列表

```http
GET /admin/user/list
```

支持：

```text
username
role
status
```

---

## 14.2 修改用户状态

```http
PUT /admin/user/{id}/status
```

### 请求参数

```json
{
  "status": 0
}
```

---

## 14.3 查询所有预约

```http
GET /admin/appointment/list
```

---

# 十五、操作日志

这个模块后期通过 AOP 实现。

## 15.1 查询操作日志

管理员权限。

```http
GET /admin/log/list
```

日志记录：

```text
用户
操作内容
请求地址
请求参数
执行耗时
操作时间
```

例如：

```json
{
  "id": 1,
  "username": "admin",
  "operation": "新增医生",
  "requestUrl": "/admin/doctor",
  "executionTime": 35,
  "createTime": "2026-09-16 10:30:00"
}
```

---

# 十六、权限说明

系统角色：

| 角色 | 说明 |
|---|---|
| PATIENT | 患者 |
| DOCTOR | 医生 |
| ADMIN | 管理员 |

### PATIENT

可以：

```text
注册
登录
查看医生
查看科室
预约挂号
取消预约
查看自己的预约
查看自己的就诊记录
查看自己的处方
修改个人信息
```

### DOCTOR

可以：

```text
登录
查看自己的预约
接诊
创建就诊记录
查看患者历史记录
创建处方
```

### ADMIN

可以：

```text
用户管理
医生管理
科室管理
药品管理
预约管理
操作日志
```

---

# 十七、项目开发顺序

不要按照接口文档从上往下全部写。

我们按照难度逐渐增加：

### 第一阶段：基础 CRUD

```text
① 用户注册
② 用户查询
③ 用户修改
④ 科室 CRUD
⑤ 医生 CRUD
```

### 第二阶段：登录

```text
⑥ BCrypt 密码加密
⑦ JWT 登录
⑧ 登录拦截器
⑨ UserContext
```

### 第三阶段：核心业务

```text
⑩ 预约挂号
⑪ 取消预约
⑫ 医生接诊
⑬ 就诊记录
```

### 第四阶段：Redis

```text
⑭ 验证码
⑮ 医生/科室缓存
⑯ 预约防重复
```

### 第五阶段：AOP

```text
⑰ 权限注解
⑱ 权限 AOP
⑲ 操作日志
```

### 第六阶段：项目优化

```text
⑳ SQL 索引优化
㉑ 事务
㉒ 接口幂等
㉓ 全局异常处理
㉔ 参数校验
```

---

# 十八、第一版项目目标

第一版不追求“功能特别多”。

最终保证下面这条业务链能够完整跑通：

```text
患者注册
   ↓
登录
   ↓
查看科室
   ↓
查看医生
   ↓
选择医生
   ↓
预约挂号
   ↓
医生登录
   ↓
查看预约
   ↓
接诊
   ↓
填写就诊记录
   ↓
开处方
   ↓
患者查看就诊记录
   ↓
患者查看处方
```

这条链跑通之后，再加 Redis、AOP、权限、日志等技术点。