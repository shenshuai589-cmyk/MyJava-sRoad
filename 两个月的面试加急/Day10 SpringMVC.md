# 一、Day 10 今天学什么？

今天跳过 MySQL，开始学习 **Spring MVC 核心面试知识**。

今天主要掌握：

- ⭐ Spring MVC 是什么
    
- ⭐ DispatcherServlet 是什么
    
- ⭐ HandlerMapping 做什么
    
- ⭐ HandlerAdapter 做什么
    
- ⭐ `@PathVariable`、`@RequestParam`、`@RequestBody`
    
- ⭐ HttpMessageConverter 是什么
    
- ⭐ `@RequestMapping` / `@GetMapping`
    
- ⭐ `@Controller`、`@ResponseBody`、`@RestController`
    
- ⭐ RequestMappingHandlerMapping
    
- ⭐ HandlerMethodArgumentResolver
    
- ⭐ `@RequestBody` + Jackson 的完整处理流程
    
- ⭐ `@Valid` / `@Validated` 参数校验
    
- ⭐ 全局异常处理 `@RestControllerAdvice` + `@ExceptionHandler`
    
- ⭐ HandlerInterceptor 拦截器
    
- ⭐ Filter 和 Interceptor 的区别
    
- ⭐ MultipartFile 文件上传
    

---

# 二、⭐ Spring MVC 是什么？

Spring MVC 是 Spring 提供的 **Web 请求处理框架**。

主要负责：

```
接收 HTTP 请求
      ↓
找到对应 Controller 方法
      ↓
执行 Controller
      ↓
处理返回结果
      ↓
返回给前端
```

### 🧠 一句话记忆

> Spring MVC 就是负责处理 HTTP 请求，并找到对应 Controller 方法执行的 Web 框架。

---

# 三、⭐⭐ DispatcherServlet 是什么？

DispatcherServlet 是 Spring MVC 的**核心前端控制器**。

可以理解成：

> Spring MVC 的“总调度员”。

所有 HTTP 请求首先都会进入 DispatcherServlet。

```
前端
 ↓
HTTP请求
 ↓
DispatcherServlet
 ↓
找到对应 Controller
 ↓
执行 Controller
 ↓
返回结果
```

注意：

DispatcherServlet **不是管理 controller 包的**。

`controller` 只是项目中的代码组织方式。

### 🧠 一句话记忆

> DispatcherServlet = Spring MVC 的核心前端控制器，负责统一接收和调度 HTTP 请求。

---

# 四、⭐⭐ HandlerMapping 是什么？

DispatcherServlet 收到请求后，需要知道：

> “这个请求应该由哪个方法处理？”

HandlerMapping 就负责：

> **根据请求找到对应的 Handler。**

例如：

```
@GetMapping("/user")
public User getUser() {
    return ...;
}
```

可以理解成建立了：

```
GET /user
    ↓
UserController.getUser()
```

### 🧠 一句话记忆

> HandlerMapping = 找谁。

更准确地说：

> HandlerMapping 根据 HTTP 请求找到对应的 Handler，通常就是 Controller 中对应的处理方法。

---

# 五、⭐⭐ HandlerAdapter 是什么？

HandlerMapping 找到 Handler 后，还需要把它执行起来。

HandlerAdapter 的作用：

> **负责调用 Handler。**

```
HandlerMapping
      ↓
找到谁
      ↓
HandlerAdapter
      ↓
执行谁
```

### 🧠 一句话记忆

> HandlerMapping = 找谁  
> HandlerAdapter = 调谁

---

# 六、⭐⭐ `@PathVariable`、`@RequestParam`、`@RequestBody`

## 1. `@PathVariable`

获取 URL 路径中的参数。

```
GET /user/100
```

```
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) {
    return ...;
}
```

最终：

```
/user/100
   ↓
id = 100
```

### 🧠

> `@PathVariable` = 路径参数

---

## 2. `@RequestParam`

获取 URL 查询参数。

```
GET /user?id=100
```

```
@GetMapping("/user")
public User getUser(@RequestParam Long id) {
    return ...;
}
```

最终：

```
?id=100
   ↓
id = 100
```

### 🧠

> `@RequestParam` = `?` 后面的查询参数

---

## 3. `@RequestBody`

获取 HTTP 请求体中的数据。

例如：

```
{
    "name": "Tom",
    "age": 20
}
```

```
@PostMapping("/user")
public void addUser(@RequestBody User user) {
    ...
}
```

`@RequestBody` 表示：

> 从 HTTP Request Body 中获取数据。

注意：

`**@RequestBody**` **本身不负责 JSON 转换。**

---

# 七、⭐⭐ HttpMessageConverter

HttpMessageConverter 负责：

```
HTTP消息 ↔ Java对象
```

例如：

```
JSON
 ↓
HttpMessageConverter
 ↓
Java对象
```

或者：

```
Java对象
 ↓
HttpMessageConverter
 ↓
JSON
```

Spring Boot 中常见的是：

```
MappingJackson2HttpMessageConverter
```

底层使用：

```
Jackson
```

完成 JSON 的序列化和反序列化。

### 🧠 一句话记忆

> HttpMessageConverter = HTTP 数据和 Java 对象之间的转换器。

---

# 八、⭐⭐ `@RequestMapping` 和 `@GetMapping`

## `@RequestMapping`

最基础的请求映射注解：

```
@RequestMapping("/user")
public User getUser() {
    return ...;
}
```

也可以指定 HTTP 方法：

```
@RequestMapping(
    value = "/user",
    method = RequestMethod.GET
)
public User getUser() {
    return ...;
}
```

---

## `@GetMapping`

`@GetMapping` 是针对 GET 请求的组合注解。

```
@GetMapping("/user")
public User getUser() {
    return ...;
}
```

还有：

```
@PostMapping
@PutMapping
@DeleteMapping
```

分别对应：

```
GET
POST
PUT
DELETE
```

| HTTP方法 | 语法   | CRUD   |
| ------ | ---- | ------ |
| GET    | 查询资源 | Select |
| POST   | 新建资源 | Insert |
| PUT    | 更新资源 | Update |
| Delete | 删除资源 | Delete |
### 🧠 面试回答

> `@GetMapping`、`@PostMapping` 等都是 `@RequestMapping` 的组合注解，用于更方便地指定 HTTP 请求方法和请求路径。

---

# 九、⭐⭐ 类上的 `@RequestMapping` + 方法上的映射

例如：

```
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/list")
    public List<User> list() {
        return ...;
    }
}
```

最终路径：

```
GET /user/list
```

因为：

```
类上的 /user
+
方法上的 /list
↓
/user/list
```

同时：

```
@GetMapping
↓
HTTP方法 = GET
```

最终形成：

```
GET /user/list
    ↓
UserController.list()
```

---

# 十、⭐⭐ `@Controller`、`@ResponseBody`、`@RestController`

## 1. `@Controller`

表示这是一个 Controller。

```
@Controller
public class UserController {
}
```

如果方法没有 `@ResponseBody`，返回值通常会被当成**视图名称**进行处理。

---

## 2. `@ResponseBody`

表示：

> 不进行视图解析，而是直接将方法返回值写入 HTTP Response Body。

例如：

```
@Controller
public class UserController {

    @ResponseBody
    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

返回的 User 会进一步通过 HttpMessageConverter 转换成 JSON。

---

## 3. `@RestController`

可以理解成：

```
@RestController
≈
@Controller + @ResponseBody
```

所以：

```
@RestController
public class UserController {

    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

相当于：

```
@Controller
public class UserController {

    @ResponseBody
    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

### 🧠 一句话记忆

> `@RestController = @Controller + @ResponseBody`

---

# 十一、⭐⭐ RequestMappingHandlerMapping

这是今天进一步深入的重点。

`HandlerMapping` 是一个接口/抽象概念。

Spring MVC 中有一个非常重要的实现：

```
RequestMappingHandlerMapping
```

它主要负责处理：

```
@RequestMapping
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
```

等注解。

所以：

```
HandlerMapping
      ↑
RequestMappingHandlerMapping
```

可以简单理解：

> `RequestMappingHandlerMapping` 专门处理基于 `@RequestMapping` 等注解建立的请求映射。

---

# 十二、⭐⭐ Spring 启动时建立请求映射

例如：

```
@RestController  ==> responseBody + Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/list")
    public List<User> list() {
        return ...;
    }
}
```

Spring 启动时，会扫描 Controller 和其中的请求映射。

大致过程：

```
Spring容器启动
      ↓
扫描Controller
      ↓
发现 UserController
      ↓
发现 list() 方法
      ↓
发现 @GetMapping("/list")
      ↓
解析请求映射信息
      ↓
注册映射关系
```

最终可以理解成：

```
GET /user/list
      ↓
UserController.list()
```

所以真正请求进来时，不需要临时扫描所有 Controller。

---

# 十三、⭐⭐ 请求阶段

当前端发送：

```
GET /user/list
```

大致过程：

```
DispatcherServlet
      ↓
RequestMappingHandlerMapping
      ↓
根据 GET + /user/list 查找
      ↓
找到 UserController.list()
      ↓
HandlerAdapter
      ↓
执行 list()
```

### 🧠 启动阶段 vs 请求阶段

**启动阶段：**

> RequestMappingHandlerMapping 扫描 Controller，建立请求和 Handler 的映射。

**请求阶段：**

> DispatcherServlet 调用 HandlerMapping，根据请求找到 Handler，再通过 HandlerAdapter 执行。

---

# 十四、⭐⭐ HandlerMethodArgumentResolver

Controller 方法可能有很多不同类型的参数：

```
@GetMapping("/user/{id}")
public User getUser(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestBody User user) {
    ...
}
```

Spring 怎么知道：

- `id` 从哪里取？
    
- `name` 从哪里取？
    
- `user` 从哪里取？
    

这就是：

> **HandlerMethodArgumentResolver**

它负责：

> **解析 Controller 方法的参数。**

可以简单理解：

```
Controller方法参数
        ↓
HandlerMethodArgumentResolver
        ↓
判断参数上的注解
        ↓
选择对应的参数解析器
```

---

# 十五、⭐⭐ `@PathVariable` 参数解析

例如：

```
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) {
    return ...;
}
```

请求：

```
GET /user/100
```

大致过程：

```
/user/100
    ↓
HandlerMapping
    ↓
找到 getUser()
    ↓
HandlerAdapter
    ↓
参数解析器发现 @PathVariable
    ↓
提取 100
    ↓
类型转换成 Long
    ↓
getUser(100)
```

所以：

> HandlerMapping 负责找到方法，HandlerAdapter 在执行方法时会协调参数解析。

更深入一点：

> 真正负责解析 `@PathVariable` 等方法参数的是 HandlerMethodArgumentResolver。

---

# 十六、⭐⭐ `@RequestParam` 参数解析

例如：

```
@GetMapping("/user")
public User getUser(@RequestParam Long id) {
    return ...;
}
```

请求：

```
GET /user?id=100
```

过程：

```
/user?id=100
      ↓
@RequestParam
      ↓
提取 id=100
      ↓
转换成 Long
      ↓
getUser(100)
```

### 🧠

```
@PathVariable → URL路径

@RequestParam → ?后的查询参数
```

---

# 十七、⭐⭐ `@RequestBody` 参数解析

例如：

```
@PostMapping("/user")
public User addUser(@RequestBody User user) {
    return user;
}
```

请求体：

```
{
    "name": "Tom",
    "age": 20
}
```

过程：

```
HTTP Request Body
      ↓
@RequestBody
      ↓
参数解析器
      ↓
HttpMessageConverter
      ↓
MappingJackson2HttpMessageConverter
      ↓
Jackson
      ↓
User对象
      ↓
addUser(user)
```

---

# 十八、⭐⭐ Spring 为什么知道 JSON 要转换成 User？

例如：

```
@PostMapping("/user")
public User addUser(@RequestBody User user) {
    return user;
}
```

Spring 可以通过**反射**获取 Controller 方法的参数信息：

```
参数类型 = User
参数注解 = @RequestBody
```

于是 Spring 就知道：

> 请求体中的 JSON 应该转换成 User 类型。

过程：

```
@RequestBody User user
        ↓
反射获取参数类型
        ↓
目标类型 = User
        ↓
HttpMessageConverter
        ↓
Jackson
        ↓
User对象
```

### ⚠️ 注意

不是：

> “调用字节码文件。”

而是：

> **Spring 通过反射获取方法参数的类型信息。**

---

# 十九、⭐⭐⭐ 综合案例

Controller：

```
@PostMapping("/user/{id}")
public User update(
        @PathVariable Long id,
        @RequestParam String type,
        @RequestBody User user) {

    return userService.update(id, type, user);
}
```

请求：

```
POST /user/100?type=vip
```

Body：

```
{
    "name": "Tom",
    "age": 20
}
```

最终：

```
update(100, "vip", new User("Tom", 20));
```

三个参数来源：

```
id
↓
@PathVariable
↓
/user/100
↓
100
```

```
type
↓
@RequestParam
↓
?type=vip
↓
"vip"
```

```
user
↓
@RequestBody
↓
JSON
↓
HttpMessageConverter
↓
Jackson
↓
User对象
```

---

# 二十、⭐⭐⭐ Spring MVC 完整请求流程

这是 Day10 最重要的知识点之一。

```
                     HTTP请求
                         ↓
                DispatcherServlet
                     总调度员
                         ↓
          RequestMappingHandlerMapping (HandlerMapping)
                         ↓
                    找到 Handler
                         ↓
                  HandlerAdapter
                         ↓
             HandlerMethodArgumentResolver  (处理器方法参数解析器)
                         ↓
              ┌──────────┼──────────┐
              ↓          ↓          ↓
        @PathVariable @RequestParam @RequestBody
              ↓          ↓          ↓
          路径参数     查询参数      请求体
                                      ↓
                           HttpMessageConverter
                                      ↓
                                   Jackson
                                      ↓
                                  Java对象
              └──────────┬──────────┘
                         ↓
                  执行 Controller
                         ↓
                    返回 Java对象
                         ↓
                HttpMessageConverter
                         ↓
                       JSON
                         ↓
                      前端
```

---

# 二十一、⭐⭐ @Valid / @Validated 参数校验

## 1. 为什么需要参数校验？

例如：

```
@PostMapping("/user")
public User register(@RequestBody UserDTO user) {
    return userService.register(user);
}
```

前端可能传：

```
{
    "username": "",
    "age": 10
}
```

如果要求：

- username 不能为空
    
- age 必须大于等于 18
    

如果不使用参数校验，就需要手动判断：

```
if (user.getUsername() == null || user.getUsername().isEmpty()) {
    // ...
}

if (user.getAge() < 18) {
    // ...
}
```

字段一多，代码就会非常麻烦。

所以 Spring 通常配合 **Bean Validation** 自动进行参数校验。

---

## 2. `@Valid`

例如：

```
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @Min(value = 18, message = "年龄不能小于18")
    private Integer age;
}
```

Controller：

```
@PostMapping("/user")
public User register(
        @Valid @RequestBody UserDTO user) {

    return userService.register(user);
}
```

`@Valid` 的作用：

> **告诉 Spring：这个对象需要进行 Bean Validation 参数校验。**

例如：

```
{
    "username": "",
    "age": 10
}
```

那么：

```
@NotBlank
    ↓
username 校验失败

@Min(18)
    ↓
age 校验失败
```

---

## 3. `@Valid` 的处理流程

```
前端 JSON
   ↓
@RequestBody
   ↓
HttpMessageConverter
   ↓
JSON → UserDTO
   ↓
@Valid
   ↓
Bean Validation
   ↓
@NotBlank / @Min 等规则
   ↓
校验成功 / 失败
```

对于：

```
@Valid @RequestBody UserDTO user
```

这种场景，参数校验失败时通常会抛出：

```
MethodArgumentNotValidException
```

---

## 4. `@Validated`

`@Validated` 是 Spring 提供的校验注解。

简单理解：

```
@Valid
    ↓
Bean Validation 标准注解
    ↓
主要用于开启参数校验

@Validated
    ↓
Spring 提供的扩展
    ↓
支持校验分组等能力
```

### 🧠 面试回答

> `@Valid` 是 Bean Validation 标准注解，主要用于开启参数校验；`@Validated` 是 Spring 提供的扩展，支持校验分组，使用更加灵活。

初级面试阶段不需要深入校验分组源码。

---

# 二十二、⭐⭐ 全局异常处理

参数校验失败后可能出现：

```
MethodArgumentNotValidException
```

实际项目中，我们不希望每个 Controller 都自己写异常处理。

所以 Spring MVC 提供了：

```
@RestControllerAdvice
+
@ExceptionHandler
```

---

## 1. `@RestControllerAdvice`

可以理解为：

> **全局异常处理器。**

例如：

```
@RestControllerAdvice
public class GlobalExceptionHandler {

}
```

它可以统一处理多个 Controller 抛出的异常。

---

## 2. `@ExceptionHandler`

例如：

```
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result handle(MethodArgumentNotValidException e) {
    return Result.error("参数校验失败");
}
```

意思是：

> 如果出现 `MethodArgumentNotValidException`，就交给这个方法处理。

也可以处理其他异常：

```
@ExceptionHandler(NullPointerException.class)
public Result handle(NullPointerException e) {
    return Result.error("系统出现空指针异常");
}
```

---

## 3. 完整流程

```
Controller
   ↓
@Valid 参数校验
   ↓
校验失败
   ↓
MethodArgumentNotValidException
   ↓
@RestControllerAdvice
   ↓
@ExceptionHandler
   ↓
统一返回 Result
```

### 🧠 面试回答

> 可以使用 `@RestControllerAdvice` 配合 `@ExceptionHandler` 实现全局异常处理。`@RestControllerAdvice` 用于定义全局异常处理器，`@ExceptionHandler` 指定具体异常的处理方法，从而统一处理 Controller 层抛出的异常并返回统一结果。

---

# 二十三、⭐⭐ HandlerInterceptor 拦截器

## 1. 为什么需要拦截器？

假设系统有：

```
/user/list
/order/create
/order/list
/product/detail
```

我们希望在 Controller 执行之前统一做一些事情：

- 登录校验
    
- 权限校验
    
- 请求日志
    
- 接口耗时统计
    

如果每个 Controller 都写一遍，会非常麻烦。

所以 Spring MVC 提供：

```
HandlerInterceptor
```

可以在 Controller 执行前后进行统一处理。

---

## 2. 三个核心方法

```
public interface HandlerInterceptor {

    boolean preHandle(...);

    void postHandle(...);

    void afterCompletion(...);
}
```

执行顺序可以简单记为：

```
请求
 ↓
preHandle()
 ↓
Controller
 ↓
postHandle()
 ↓
afterCompletion()
```

### `preHandle`

Controller 执行之前。

常用于：

- 登录校验
    
- 权限校验
    
- 请求日志
    

```
@Override
public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler) {

    // 登录校验

    return true;
}
```

返回：

```
return true;
```

表示：

> 放行，继续执行 Controller。

返回：

```
return false;
```

表示：

> 拦截请求，不再执行 Controller。

---

### `postHandle`

Controller 执行之后。

可以用于：

- Model 处理
    
- Controller 执行后的额外处理
    

---

### `afterCompletion`

整个请求处理完成之后。

常用于：

- 日志
    
- 资源清理
    
- 请求结束后的处理
    

### 🧠 一句话记忆

> Interceptor 是 Spring MVC 提供的请求拦截机制，可以在 Controller 执行前后进行处理，其中 `preHandle` 最常用于登录和权限校验。

---

# 二十四、⭐⭐ Filter 和 Interceptor 的区别

这是面试比较高频的对比题。

---

## 1. 所属规范不同

```
Filter
    ↓
Servlet 规范

Interceptor
    ↓
Spring MVC
```

---

## 2. 执行位置不同

大致可以理解为：

```
客户端
  ↓
Filter
  ↓
DispatcherServlet
  ↓
Interceptor
  ↓
Controller
```

所以：

> **Filter 比 Interceptor 更靠前。**

---

## 3. 拦截范围不同

Filter：

> 更底层，针对进入 Servlet 的请求。

Interceptor：

> Spring MVC 层面的拦截机制，更接近 Controller。

---

## 🧠 面试回答

> Filter 和 Interceptor 的主要区别是所属规范、执行位置和拦截范围不同。Filter 属于 Servlet 规范，执行在 DispatcherServlet 之前；Interceptor 属于 Spring MVC，执行在 DispatcherServlet 处理请求的过程中，更接近 Controller。

### 🧠 简单比喻

```
用户
 ↓
大楼保安（Filter）
 ↓
公司前台（DispatcherServlet）
 ↓
部门门禁（Interceptor）
 ↓
具体员工（Controller）
```

---

# 二十五、⭐⭐ MultipartFile 文件上传

Spring MVC 中可以通过 `MultipartFile` 接收前端上传的文件。

前端通常使用：

```
multipart/form-data
```

Controller：

```
@PostMapping("/upload")
public String upload(MultipartFile file) {
    // 保存文件
    return "success";
}
```

`MultipartFile` 是 Spring MVC 对上传文件提供的封装。

常见方法：

```
file.getOriginalFilename(); // 原文件名

file.getSize();             // 文件大小

file.getContentType();      // 文件类型

file.transferTo(...);       // 保存文件
```

### 🧠 一句话记忆

> **文件上传 →** `**multipart/form-data**` **→** `**MultipartFile**`

### 🧠 面试回答

> Spring MVC 可以通过 `MultipartFile` 接收前端以 `multipart/form-data` 方式上传的文件，然后使用 `transferTo()` 等方法将文件保存到指定位置。

---

# 二十六、🧠 Day10 最终记忆框架

一定要记住：

```
DispatcherServlet
    ↓
总调度

HandlerMapping
    ↓
找谁

HandlerAdapter
    ↓
执行谁

HandlerMethodArgumentResolver
    ↓
Controller参数从哪里来

HttpMessageConverter
    ↓
HTTP数据 ↔ Java对象

@Valid
    ↓
参数校验

@RestControllerAdvice
    ↓
全局异常处理

HandlerInterceptor
    ↓
Controller前后进行拦截

MultipartFile
    ↓
文件上传
```

几个注解：

```
@PathVariable
    ↓
路径参数

@RequestParam
    ↓
?后的查询参数

@RequestBody
    ↓
HTTP请求体

@ResponseBody
    ↓
返回值写入Response Body

@RestController
    ↓
@Controller + @ResponseBody

@Valid
    ↓
触发参数校验

@RestControllerAdvice
    ↓
全局异常处理
```

---

# 二十七、🎯 Day10 面试题

### 问题1 ⭐

Spring MVC 是什么？

> Spring MVC 是 Spring 提供的 Web 请求处理框架，负责接收 HTTP 请求、找到对应的 Controller 方法并执行，然后将结果返回给前端。

---

### 问题2 ⭐⭐

DispatcherServlet 是什么？

> DispatcherServlet 是 Spring MVC 的核心前端控制器，负责统一接收和调度 HTTP 请求。

---

### 问题3 ⭐⭐

HandlerMapping 和 HandlerAdapter 分别做什么？

> HandlerMapping 根据请求找到对应的 Handler，HandlerAdapter 负责调用 Handler。

---

### 问题4 ⭐⭐

`@PathVariable`、`@RequestParam`、`@RequestBody` 有什么区别？

> `@PathVariable` 获取 URL 路径中的参数；`@RequestParam` 获取 URL 查询参数；`@RequestBody` 获取 HTTP 请求体中的数据。

---

### 问题5 ⭐⭐

HttpMessageConverter 是干什么的？

> HttpMessageConverter 负责 HTTP 消息和 Java 对象之间的转换，例如将 JSON 转换成 Java 对象，或者将 Java 对象转换成 JSON。

---

### 问题6 ⭐⭐

`@RestController` 和 `@Controller` 有什么区别？

> `@RestController` 可以理解为 `@Controller + @ResponseBody`，Controller 方法返回的数据会直接写入 HTTP Response Body，通常用于开发 REST API。

---

### 问题7 ⭐⭐⭐

Spring 是什么时候建立 `GET /user/list` 和 `UserController.list()` 的映射关系的？

> Spring 启动时，RequestMappingHandlerMapping 会扫描 Controller 中的请求映射注解，并建立请求和 Handler 之间的映射关系。

---

### 问题8 ⭐⭐⭐

HandlerMethodArgumentResolver 是干什么的？

> HandlerMethodArgumentResolver 负责解析 Controller 方法的参数，例如解析 `@PathVariable`、`@RequestParam`、`@RequestBody` 等参数。

---

### 问题9 ⭐⭐⭐

`@RequestBody` 为什么可以把 JSON 转换成 Java 对象？

> Spring 通过反射获取 Controller 方法参数的类型，然后通过参数解析器找到 HttpMessageConverter，由 MappingJackson2HttpMessageConverter 使用 Jackson 将 JSON 转换成对应的 Java 对象。

---

### 问题10 ⭐⭐

`@Valid` 是干什么的？

> `@Valid` 用于触发 Bean Validation 参数校验，根据 DTO 上的 `@NotBlank`、`@Min` 等校验注解对参数进行校验。

---

### 问题11 ⭐⭐

`@Valid` 和 `@Validated` 有什么区别？

> `@Valid` 是 Bean Validation 标准注解，主要用于开启参数校验；`@Validated` 是 Spring 提供的扩展，支持校验分组。

---

### 问题12 ⭐⭐

参数校验失败通常会抛出什么异常？

> 对于 `@Valid @RequestBody` 参数校验失败的情况，通常会抛出 `MethodArgumentNotValidException`。

---

### 问题13 ⭐⭐

Spring MVC 如何实现全局异常处理？

> 可以使用 `@RestControllerAdvice` 配合 `@ExceptionHandler` 实现全局异常处理。`@RestControllerAdvice` 定义全局异常处理器，`@ExceptionHandler` 指定具体异常的处理方法。

---

### 问题14 ⭐⭐

HandlerInterceptor 是干什么的？

> HandlerInterceptor 是 Spring MVC 提供的请求拦截机制，可以在 Controller 执行前后进行统一处理。

---

### 问题15 ⭐⭐

Interceptor 的三个核心方法是什么？

> `preHandle`、`postHandle`、`afterCompletion`。

其中：

```
preHandle
    ↓
Controller之前

postHandle
    ↓
Controller之后

afterCompletion
    ↓
整个请求完成之后
```

---

### 问题16 ⭐⭐

`preHandle()` 返回 `true` 和 `false` 分别代表什么？

> 返回 `true` 表示放行，继续执行 Controller；返回 `false` 表示拦截请求，不再继续执行 Controller。

---

### 问题17 ⭐⭐⭐

Filter 和 Interceptor 有什么区别？

> Filter 属于 Servlet 规范，执行位置更靠前；Interceptor 属于 Spring MVC，执行在 DispatcherServlet 处理请求的过程中，更接近 Controller。两者的拦截范围也不同。

---

### 问题18 ⭐⭐

Spring MVC 怎么实现文件上传？

> Spring MVC 可以使用 `MultipartFile` 接收前端以 `multipart/form-data` 方式上传的文件，然后通过 `transferTo()` 等方法保存文件。

---

# 二十八、🎯 Day10 一句话总结

> **Spring MVC 中，DispatcherServlet 负责总调度，HandlerMapping 负责根据请求找到 Handler，HandlerAdapter 负责执行 Handler，HandlerMethodArgumentResolver 负责解析 Controller 方法参数，HttpMessageConverter 负责 HTTP 数据和 Java 对象之间的转换；**`**@Valid**` **负责参数校验，**`**@RestControllerAdvice + @ExceptionHandler**` **负责全局异常处理，Interceptor 负责 Controller 前后的请求拦截，MultipartFile 负责文件上传。**


# Day 10 补充：面试追问层

> 这份是 `Day10_SpringMVC.md` 的补充，编号从「二十九」接着往下排，可以直接粘到原笔记末尾。 版本说明：代码基于 Spring Boot 3.x（`jakarta.*` 包）+ Jackson 2。如果你的项目是 Boot 2.x，把 `jakarta` 换成 `javax` 即可；如果已经升到 Boot 4 / Jackson 3，Jackson 相关的包名和配置方式可能有变化，以项目实际版本为准。

---

# 二十九、⭐⭐⭐ 完整请求流程（补全版）

原笔记的流程图只画了「入参」这一半，缺了 **拦截器、返回值处理、异常处理**。补全后：

```
客户端
  ↓
Filter 链（Servlet 规范，在 DispatcherServlet 之前）
  ↓
DispatcherServlet
  ↓
HandlerMapping.getHandler()
  ↓
返回 HandlerExecutionChain（= Handler + 拦截器链）
  ↓
拦截器 preHandle()        ← 按注册顺序，任何一个返回 false 就中断
  ↓
HandlerAdapter.handle()
  ↓
HandlerMethodArgumentResolver 解析参数
  （@RequestBody → HttpMessageConverter → Jackson）
  ↓
执行 Controller 方法
  ↓
HandlerMethodReturnValueHandler 处理返回值
  （@ResponseBody → HttpMessageConverter → JSON 写入 Response）
  ↓
拦截器 postHandle()       ← 逆序
  ↓
视图渲染（REST 项目没有这一步）
  ↓
拦截器 afterCompletion()  ← 逆序，无论是否抛异常都会执行
  ↓
响应返回客户端

任何一步抛异常
  ↓
HandlerExceptionResolver
  ↓
@ExceptionHandler（由 ExceptionHandlerExceptionResolver 处理）
```

### 🧠 两个加分点

1. `@RequestBody` / `@ResponseBody` 背后是同一个类：`RequestResponseBodyMethodProcessor`，它**同时实现了** `HandlerMethodArgumentResolver`（入参）和 `HandlerMethodReturnValueHandler`（返回值），内部都是调用 `HttpMessageConverter`。
2. 拦截器不是 DispatcherServlet 自己去找的：`HandlerMapping` 返回的就是 `HandlerExecutionChain`，拦截器链已经跟 Handler 绑在一起了。

### 传统（带视图）版本

面试官如果问的是老版本流程，按这个答：

```
1. 请求到达 DispatcherServlet
2. HandlerMapping 找到 Handler，返回 HandlerExecutionChain
3. HandlerAdapter 调用 Handler（Controller 方法）
4. Controller 返回 ModelAndView
5. ViewResolver 根据视图名解析出具体 View
6. View 渲染（把 Model 数据填进页面）
7. 响应返回客户端
```

前后端分离项目里，Controller 加了 `@ResponseBody`，第 4~6 步被跳过：返回值直接由 `HttpMessageConverter` 写成 JSON。

---

# 三十、⭐⭐⭐ Filter 和 Interceptor 的区别（完整版）

原笔记只写了「所属规范、执行位置、拦截范围」，最后一项太虚，追问会答不上来。完整对比：

|维度|Filter|Interceptor|
|---|---|---|
|所属规范|Servlet 规范|Spring MVC|
|依赖的容器|Servlet 容器（Tomcat）|Spring MVC（通过 `WebMvcConfigurer` 注册）|
|执行位置|DispatcherServlet **之前**|DispatcherServlet **内部**，Controller 前后|
|拦截范围|进入容器的所有请求（包括非 Spring 管理的 Servlet）|只拦截进入 DispatcherServlet 的请求|
|能否拿到 Handler|不能（只有 request / response）|能（`preHandle` 的第三个参数就是 handler，可以强转 `HandlerMethod` 拿到方法和注解）|
|使用 Spring Bean|Spring Boot 里用 `@Component` 注册的 Filter 可以注入；本质上由容器管理，传统项目需要 `DelegatingFilterProxy`|本身就是 Spring Bean，可直接注入|
|回调粒度|一个 `doFilter`，包住整个后续链|三个方法：`preHandle` / `postHandle` / `afterCompletion`|
|全局异常处理|`@RestControllerAdvice` **抓不到** Filter 里抛的异常|能抓到（异常发生在 DispatcherServlet 内）|
|执行顺序控制|`@Order` 或 `FilterRegistrationBean.setOrder`|按注册顺序|
|典型用途|字符编码、跨域、XSS 过滤、全链路日志、粗粒度限流|登录校验、权限校验、接口耗时统计|

补充一个容易被问到的点：**Spring Boot 里静态资源也会经过 DispatcherServlet**（由 `ResourceHttpRequestHandler` 处理），所以拦截器也可能拦到静态资源，需要用 `excludePathPatterns` 排除。

### 🧠 面试回答（完整版）

> Filter 属于 Servlet 规范，由容器调用，执行在 DispatcherServlet 之前，能拦截所有进入容器的请求，但拿不到 Spring MVC 的 Handler 信息；Interceptor 属于 Spring MVC，执行在 DispatcherServlet 内部，只拦截进入 DispatcherServlet 的请求，可以拿到具体的 Controller 方法，也方便使用 Spring 容器里的 Bean。另外，`@RestControllerAdvice` 只能处理 DispatcherServlet 内部抛出的异常，抓不到 Filter 里的异常。一般编码、跨域这类和 Spring 无关的用 Filter，登录、权限这类和业务相关的用 Interceptor。

---

# 三十一、⭐⭐ 拦截器怎么写、怎么注册（登录校验完整示例）

## 1. 自定义拦截器

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 放行 CORS 预检请求（OPTIONS 不带 Authorization，不放行会一直 401）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            writeUnauthorized(response);
            return false;   // 拦截，不再执行 Controller
        }

        // 3. 校验 token 并取出用户 id（JwtUtil 是伪代码，换成你项目里的工具类）
        Long userId;
        try {
            userId = JwtUtil.parseUserId(token);
        } catch (Exception e) {
            writeUnauthorized(response);
            return false;
        }

        // 4. 把用户 id 放进 ThreadLocal，后面的 Controller / Service 直接取
        UserContext.set(userId);
        return true;        // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 一定要清理：Tomcat 线程池会复用线程，不清理会串用户、造成内存泄漏
        UserContext.remove();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
    }
}
```

## 2. ThreadLocal 工具类

```java
public class UserContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long get() {
        return HOLDER.get();
    }

    public static void remove() {
        HOLDER.remove();
    }
}
```

## 3. 注册拦截器

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")                       // 拦截所有
                .excludePathPatterns(                          // 排除不需要登录的
                        "/user/login",
                        "/user/register",
                        "/error");
    }
}
```

### 🧠 要点

- 注册用 `WebMvcConfigurer.addInterceptors`，路径用 `addPathPatterns` / `excludePathPatterns`。
- `preHandle` 返回 `false` 时，**响应要自己写**（状态码 + JSON），因为已经不会进入 Controller，也就走不到全局异常处理里去统一格式。
- 用 `ThreadLocal` 存当前用户，**必须在 `afterCompletion` 里 `remove()`**。

---

# 三十二、⭐⭐ 多个拦截器的执行顺序，以及 postHandle / afterCompletion 的修正

## 1. 执行顺序

假设注册了拦截器 A、B（先 A 后 B）：

```
A.preHandle
   ↓
B.preHandle
   ↓
Controller
   ↓
B.postHandle      ← 逆序
   ↓
A.postHandle
   ↓
B.afterCompletion ← 逆序
   ↓
A.afterCompletion
```

### 如果 B.preHandle 返回 false

```
A.preHandle  (true)
B.preHandle  (false)  → 后面的拦截器和 Controller 都不执行
A.afterCompletion     ← 只对「preHandle 返回过 true」的拦截器，逆序执行
```

即：`afterCompletion` 只会执行到 `preHandle` 返回了 `true` 的那些拦截器；B 自己的 `afterCompletion` 不会执行。

## 2. 对原笔记的修正

|方法|原笔记|补充|
|---|---|---|
|`postHandle`|「Model 处理」|只在**有视图**的场景有意义。`@RestController` / `@ResponseBody` 场景下，响应在 Controller 方法返回时就已经写出，`postHandle` 里**改不了响应**。REST 项目里基本用不上|
|`afterCompletion`|「日志、资源清理」|正确。补充：只要对应的 `preHandle` 返回了 true，**无论 Controller 是否抛异常都会执行**，所以最适合做资源清理（比如 `ThreadLocal.remove()`）和耗时统计|

---

# 三十三、⭐⭐⭐ @Valid 和 @Validated（完整版）

## 1. 区别

|维度|`@Valid`|`@Validated`|
|---|---|---|
|来源|Bean Validation 标准（`jakarta.validation`）|Spring（`org.springframework.validation.annotation`）|
|可标注的位置|方法、参数、字段、构造器|类、方法、参数（**不能标字段**）|
|校验分组|不支持|支持|
|嵌套对象级联校验|支持（标在嵌套对象字段上）|不能用来做级联|
|标在 Controller 类上|无意义|开启对 `@RequestParam` / `@PathVariable` 的校验|

依赖：`spring-boot-starter-validation`（Boot 2.3 之后不再自带，需要单独加）。

## 2. 校验分组

```java
public interface Create {}
public interface Update {}

public class UserDTO {

    @NotNull(groups = Update.class, message = "更新时 id 不能为空")
    private Long id;

    @NotBlank(groups = {Create.class, Update.class}, message = "用户名不能为空")
    private String username;
}
```

```java
@PostMapping("/user")
public Result add(@Validated(Create.class) @RequestBody UserDTO dto) {
    ...
}

@PutMapping("/user")
public Result update(@Validated(Update.class) @RequestBody UserDTO dto) {
    ...
}
```

## 3. 嵌套对象级联校验（只有 `@Valid` 能做）

```java
public class OrderDTO {

    @NotBlank
    private String orderNo;

    @Valid            // 不加这个，AddressDTO 里的校验注解不会生效
    @NotNull
    private AddressDTO address;
}
```

## 4. 校验 `@RequestParam` / `@PathVariable`

```java
@Validated                       // 必须标在类上
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/{id}")
    public User get(@PathVariable @Min(1) Long id,
                    @RequestParam @NotBlank String name) {
        ...
    }
}
```

## 5. 各种失败场景对应的异常

|场景|抛出的异常|
|---|---|
|`@Valid @RequestBody` 校验失败|`MethodArgumentNotValidException`|
|表单 / query 绑定 POJO 校验失败|`BindException`（或它的子类 `MethodArgumentNotValidException`，不同版本略有差异，全局处理里建议两个都处理）|
|类上 `@Validated` + `@RequestParam` / `@PathVariable` 校验失败|`ConstraintViolationException`|
|Spring 6.1+，方法参数校验失败且类上没有 `@Validated`|`HandlerMethodValidationException`|
|缺少必填的 `@RequestParam`|`MissingServletRequestParameterException`|
|JSON 格式错误、字段类型不匹配|`HttpMessageNotReadableException`|
|上传文件超过大小限制|`MaxUploadSizeExceededException`|

> 注：Spring 6.1+ 中，如果 Controller 类上加了 `@Validated`，走的是 AOP 代理校验，仍然抛 `ConstraintViolationException`；没加时才由 MVC 内置的方法校验处理，抛 `HandlerMethodValidationException`。

### 🧠 面试回答

> `@Valid` 是 Bean Validation 标准注解，可以标在字段上做嵌套对象的级联校验；`@Validated` 是 Spring 提供的，支持校验分组，还可以标在类上，对 `@RequestParam`、`@PathVariable` 这类简单参数做校验。`@Valid @RequestBody` 校验失败抛 `MethodArgumentNotValidException`，类上 `@Validated` 校验简单参数失败抛 `ConstraintViolationException`，这两个一般都要在全局异常处理里统一处理。

---

# 三十四、⭐⭐ 全局异常处理（完整可用版）

原笔记只有单个 handler 的片段，这里给一个可以直接用的完整版本，包含自定义业务异常和兜底处理。

```java
// 自定义业务异常
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

```java
@Slf4j   // 没用 Lombok 的话换成 LoggerFactory.getLogger(...)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. @Valid @RequestBody 校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleBodyValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + "：" + f.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return Result.error(400, msg);
    }

    // 2. 表单 / query 绑定对象校验失败
    @ExceptionHandler(BindException.class)
    public Result handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + "：" + f.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return Result.error(400, msg);
    }

    // 3. @Validated 标在类上，校验 @RequestParam / @PathVariable 失败
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        return Result.error(400, msg);
    }

    // 4. JSON 格式错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error(400, "请求体格式错误");
    }

    // 5. 自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // 6. 兜底：所有没被上面捕获的异常
    @ExceptionHandler(Exception.class)
    public Result handleOther(Exception e) {
        log.error("系统异常", e);          // 详细堆栈只打在日志里
        return Result.error(500, "系统繁忙，请稍后再试");   // 不要把异常细节返回给前端
    }
}
```

### 🧠 要点

- Spring 会选择**最匹配的异常类型**对应的 handler，所以 `Exception.class` 兜底不会抢走更具体的 handler。
- 兜底 handler 一定要**打日志**，同时**不要把异常信息原样返回前端**（会泄露实现细节）。
- 追问：为什么抓不到 Filter 里的异常？→ Filter 在 DispatcherServlet 之外，`@RestControllerAdvice` 只处理 DispatcherServlet 内部抛出的异常。

---

# 三十五、⭐⭐ 统一返回结构 Result + ResponseBodyAdvice

## 1. Result

```java
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    // getter / setter 省略（或者用 Lombok 的 @Data），Jackson 序列化需要 getter
}
```

## 2. 自动包装（不想每个方法都写 `Result.success(...)`）

```java
@RestControllerAdvice(basePackages = "com.xxx.controller")   // 只作用于你自己的 controller 包
public class ResultWrapAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ResultWrapAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 已经是 Result 的不再重复包装（全局异常处理返回的就是 Result）
        return !Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // String 是个坑：返回 String 时用的是 StringHttpMessageConverter，
        // 直接返回 Result 对象会 ClassCastException，所以要手动转成 JSON 字符串
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return Result.success(body);
    }
}
```

### 🧠 要点

- `ResponseBodyAdvice` 在返回值**写出之前**介入，位置在 `HandlerMethodReturnValueHandler` 内部、`HttpMessageConverter` 之前。
- String 返回值是经典的坑：会走 `StringHttpMessageConverter`，需要手动序列化；这时 Content-Type 可能是 `text/plain`，需要的话在接口上指定 `produces = "application/json"`。
- `basePackages` 限定范围，避免把 Swagger、Actuator 等非业务接口也包装了。

---

# 三十六、⭐⭐ 跨域 CORS

## 1. 为什么会跨域

浏览器的**同源策略**：协议、域名、端口有一个不同就是跨域。 注意：**跨域是浏览器的限制**，Postman、curl 不受影响；请求其实已经到达后端了，是浏览器拦截了响应。

## 2. 简单请求 vs 预检请求

- 简单请求（GET / POST 且 Content-Type 是表单类，没有自定义头）：直接发送，靠响应头判断是否允许。
- 非简单请求（`application/json`、带 `Authorization` 头、PUT / DELETE 等）：浏览器先发一个 **`OPTIONS` 预检请求**，服务端返回允许的来源、方法、头之后，才发真正的请求。

这就是为什么登录拦截器要放行 `OPTIONS`，否则预检请求没带 token，一直被 401，真正的请求永远发不出去。

## 3. Spring MVC 的解决方式

### ① 全局配置（推荐）

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5173")   // 你的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);       // 预检结果缓存 1 小时
    }
}
```

注意：`allowCredentials(true)` 时**不能**配合 `allowedOrigins("*")`，要用 `allowedOriginPatterns`（或者写明具体来源）。

### ② 局部：`@CrossOrigin`

```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController { ... }
```

### ③ 其他方式

- `CorsFilter`（Filter 层面处理，Spring Security 项目里常用）
- Nginx / 网关统一处理（生产环境常见）

### 🧠 面试回答

> 跨域是浏览器同源策略的限制。非简单请求会先发 OPTIONS 预检请求。Spring 里可以用 `WebMvcConfigurer.addCorsMappings` 做全局配置，或者用 `@CrossOrigin` 做局部配置，也可以用 `CorsFilter`，生产环境一般在 Nginx 或网关层统一处理。要注意登录拦截器要放行 OPTIONS 请求，另外 `allowCredentials(true)` 时不能把来源配成通配符 `*`。

---

# 三十七、⭐⭐ Controller 是单例吗？线程安全吗？

**默认是单例**：Controller 也是 Spring Bean，默认 scope 是 singleton，整个应用只有一个实例。

**线程模型**：Tomcat 收到请求后，从线程池里取一个线程处理这个请求（默认最大 200 个线程），所以**多个线程会同时调用同一个 Controller 实例**。

### 结论

- Controller（以及 Service）是**无状态**的话，就是线程安全的。
- **不要在 Controller / Service 里放可变的成员变量**（比如 `private int count;`、`private User currentUser;`），多个请求会互相覆盖。
- 需要保存请求级别的数据：用**方法的局部变量**，或者 `ThreadLocal`（用完必须 `remove()`），需要共享计数用并发工具（`AtomicInteger`、`ConcurrentHashMap`）。

### 🧠 面试回答

> Controller 默认是单例的，Tomcat 用线程池处理请求，多个线程会共用同一个 Controller 实例。所以 Controller 和 Service 要保持无状态，不要定义可变的成员变量；需要保存请求级数据可以用局部变量或者 ThreadLocal，用完记得清理。

---

# 三十八、⭐⭐ 日期格式与 Long 精度丢失

## 1. `@DateTimeFormat` 和 `@JsonFormat` 的区别

|注解|属于|作用场景|
|---|---|---|
|`@DateTimeFormat`|Spring|**表单 / query 参数**（`@RequestParam`、绑定的 POJO）的字符串 → 日期类型|
|`@JsonFormat`|Jackson|**JSON**（`@RequestBody` 入参和 `@ResponseBody` 返回值）的序列化 / 反序列化|

```java
// query 参数：GET /order?date=2026-09-30
@GetMapping("/order")
public List<Order> list(@RequestParam
                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) { ... }
```

```java
// JSON 字段
public class OrderDTO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
```

全局配置：

- `java.util.Date`：可以用 `spring.jackson.date-format=yyyy-MM-dd HH:mm:ss` 和 `spring.jackson.time-zone=GMT+8`。
- `LocalDateTime`：上面这个配置对它**不生效**，建议用 `@JsonFormat`，或者自定义 `Jackson2ObjectMapperBuilderCustomizer`。

## 2. Long 精度丢失

JavaScript 的 Number 只能安全表示到 2^53 - 1（约 16 位），而雪花算法生成的 ID 有 19 位。后端返回 `Long` 型的 ID，前端解析后**末尾几位会变成 0**，导致查不到数据。

解决：把 `Long` 序列化成**字符串**。

### ① 字段级

```java
@JsonSerialize(using = ToStringSerializer.class)
private Long id;
```

### ② 全局

```java
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}
```

### 🧠 面试回答

> `@DateTimeFormat` 是 Spring 的，处理表单和 query 参数里的日期字符串；`@JsonFormat` 是 Jackson 的，处理 JSON 里的日期。Long 类型的 ID 超过 JS 的安全整数范围会丢失精度，可以在后端用 `ToStringSerializer` 把 Long 序列化成字符串。

---

# 三十九、⭐⭐ 请求参数细节补充

## 1. `@RequestParam` 细节

```java
@GetMapping("/user")
public User get(@RequestParam(required = false, defaultValue = "1") Integer page) { ... }
```

- `required` 默认 `true`：缺少参数抛 `MissingServletRequestParameterException`（400）。
- `defaultValue`：没传时用默认值（设置了 `defaultValue`，`required` 自动变成 false）。
- 可以接收 `List<String>` / 数组：`?ids=1&ids=2&ids=3`。
- 它读的是 `request.getParameter()`，所以**既能读 URL 上的查询参数，也能读 `x-www-form-urlencoded` 表单字段**。

## 2. 没有注解时的默认行为

|参数类型|不加注解时相当于|
|---|---|
|简单类型（`String`、`Integer`…）|隐式的 `@RequestParam(required = false)`|
|自定义 POJO|`@ModelAttribute`，把 query / 表单参数按字段名绑定到对象上|

所以 **GET 请求要传对象，直接用 POJO，不要加 `@RequestBody`**。

## 3. 其他常用注解

```java
@GetMapping("/info")
public String info(@RequestHeader("Authorization") String token,
                   @CookieValue("sessionId") String sessionId) { ... }
```

## 4. Content-Type 和参数注解的对应关系

|前端 Content-Type|后端怎么接|
|---|---|
|`application/json`|`@RequestBody`|
|`application/x-www-form-urlencoded`|`@RequestParam` 或 POJO（不加注解）|
|`multipart/form-data`|`MultipartFile` + `@RequestParam` / POJO（文件和普通字段一起）|
|JSON + 文件同时上传|`@RequestPart`|

## 5. 为什么一个方法只能有一个 `@RequestBody`？

请求体是一个**输入流**，只能读一次。读完就转成对象了，所以只能有一个 `@RequestBody` 参数。多个数据要么合成一个 DTO，要么拆到 query / 路径里。

---

# 四十、⭐⭐ 文件上传补充

## 1. 大小限制配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB       # 单个文件上限（默认 1MB）
      max-request-size: 50MB    # 整个请求上限（默认 10MB）
```

超限会抛 `MaxUploadSizeExceededException`，建议加到全局异常处理里，返回「文件过大」的友好提示。

## 2. 一个相对安全的上传示例

```java
@PostMapping("/upload")
public Result upload(@RequestParam("file") MultipartFile file) throws IOException {

    // 1. 判空
    if (file.isEmpty()) {
        return Result.error(400, "文件不能为空");
    }

    // 2. 校验类型（Content-Type 可被伪造，更严格要校验文件头魔数）
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
        return Result.error(400, "只允许上传图片");
    }

    // 3. 不用原文件名保存：用 UUID + 后缀，避免重名覆盖和路径穿越（../）
    String original = file.getOriginalFilename();
    String suffix = "";
    if (original != null && original.contains(".")) {
        suffix = original.substring(original.lastIndexOf('.'));
    }
    String newName = UUID.randomUUID() + suffix;

    // 4. 保存到绝对路径（transferTo 用相对路径容易保存到意想不到的位置）
    File dest = new File("/data/upload/" + newName);
    dest.getParentFile().mkdirs();
    file.transferTo(dest);

    return Result.success(newName);
}
```

### 🧠 要点

- 上传目录**不要放在 Web 可直接访问的根目录里**。
- 生产环境通常不存本地磁盘，而是上传到 **OSS / MinIO** 之类的对象存储，数据库里只存文件的访问路径。
- 不要相信客户端给的文件名、Content-Type。

---

# 四十一、⭐ REST 方法与幂等性

|方法|安全（不改变数据）|幂等（多次请求结果一致）|
|---|---|---|
|GET|是|是|
|PUT|否|是（同一个请求多次执行，最终状态一样）|
|DELETE|否|是（删一次和删多次，最终都是「没有了」）|
|POST|否|**否**（重复提交可能创建多条数据）|

POST 重复提交怎么防：

- 数据库**唯一索引**（比如订单号、业务唯一键）。
- 提交前先申请一个**防重 token**，提交时校验并删除（Redis）。
- Redis **分布式锁**。

---

# 四十二、🎯 Day10 补充面试题

### 问题19 ⭐⭐⭐

说一下 Spring MVC 的执行流程。

> 请求先经过 Filter 到达 DispatcherServlet；DispatcherServlet 调用 HandlerMapping，找到 Handler，返回的是包含 Handler 和拦截器链的 HandlerExecutionChain；依次执行拦截器的 `preHandle`；然后由 HandlerAdapter 调用 Handler，执行过程中 HandlerMethodArgumentResolver 负责解析参数，`@RequestBody` 会用 HttpMessageConverter 把 JSON 转成对象；Controller 执行完，如果是 `@ResponseBody`，返回值由 HttpMessageConverter 写成 JSON 返回；如果是传统视图方式，则返回 ModelAndView，由 ViewResolver 解析视图并渲染；最后依次执行 `postHandle` 和 `afterCompletion`。过程中抛出的异常由 HandlerExceptionResolver 处理，`@ExceptionHandler` 就是在这里生效的。

---

### 问题20 ⭐⭐⭐

Filter 和 Interceptor 的区别？（完整版）

> 见「三十、Filter 和 Interceptor 的区别（完整版）」中的面试回答。核心四点：所属规范、执行位置、能否拿到 Handler、能否被 `@RestControllerAdvice` 处理异常。

---

### 问题21 ⭐⭐

多个拦截器的执行顺序是什么？`preHandle` 返回 false 会怎样？

> `preHandle` 按注册顺序执行，`postHandle` 和 `afterCompletion` 按相反顺序执行。如果某个 `preHandle` 返回 false，后面的拦截器和 Controller 都不会执行，已经返回过 true 的拦截器的 `afterCompletion` 会逆序执行。

---

### 问题22 ⭐⭐

`afterCompletion` 一般用来做什么？Controller 抛异常时会执行吗？

> 一般用于资源清理，比如 `ThreadLocal.remove()`，也可以用于统计耗时。只要对应的 `preHandle` 返回了 true，不管 Controller 有没有抛异常，`afterCompletion` 都会执行。

---

### 问题23 ⭐⭐⭐

`@Valid` 和 `@Validated` 的区别？

> 见「三十三」中的面试回答。核心：`@Valid` 是标准注解，能标在字段上做级联校验；`@Validated` 是 Spring 的，支持分组，能标在类上校验简单参数。

---

### 问题24 ⭐⭐

`@Valid @RequestBody` 校验失败和 `@RequestParam` 校验失败，抛的异常一样吗？

> 不一样。`@Valid @RequestBody` 校验失败抛 `MethodArgumentNotValidException`；在 Controller 类上加 `@Validated` 校验 `@RequestParam` / `@PathVariable`，失败抛 `ConstraintViolationException`。两种都需要在全局异常处理里分别处理。

---

### 问题25 ⭐⭐

为什么 `@RestControllerAdvice` 抓不到 Filter 里抛的异常？

> 因为 Filter 在 DispatcherServlet 之前执行，属于 Servlet 容器层面。`@RestControllerAdvice` 是 Spring MVC 的机制，只处理 DispatcherServlet 内部（Interceptor、Controller）抛出的异常。

---

### 问题26 ⭐⭐

怎么用拦截器实现登录校验？

> 自定义一个类实现 `HandlerInterceptor`，在 `preHandle` 里从请求头取 token 并校验，校验失败就写 401 响应并返回 false，成功就把用户信息存入 ThreadLocal 并返回 true；再通过 `WebMvcConfigurer.addInterceptors` 注册，用 `excludePathPatterns` 排除登录、注册等接口。ThreadLocal 要在 `afterCompletion` 里清理，因为线程池会复用线程。

---

### 问题27 ⭐⭐

怎么解决跨域？为什么 Postman 调用没有跨域问题？

> 可以用 `WebMvcConfigurer.addCorsMappings` 做全局配置，也可以用 `@CrossOrigin` 或 `CorsFilter`，生产环境一般在 Nginx 或网关处理。跨域是浏览器的同源策略限制，Postman 不是浏览器，没有这个限制。

---

### 问题28 ⭐⭐

Controller 是单例吗？线程安全吗？

> 默认是单例的。Tomcat 用线程池处理请求，多个线程共用同一个 Controller 实例，所以要保持无状态，不要定义可变的成员变量。

---

### 问题29 ⭐⭐

怎么实现统一返回格式？有什么坑？

> 定义统一的 Result 类，可以在每个方法里手动包装，也可以用 `ResponseBodyAdvice` 在返回值写出前统一包装。要注意已经是 Result 的不重复包装，以及返回值是 String 时会走 `StringHttpMessageConverter`，直接返回对象会 `ClassCastException`，需要手动序列化成 JSON 字符串。

---

### 问题30 ⭐⭐

`@DateTimeFormat` 和 `@JsonFormat` 有什么区别？

> `@DateTimeFormat` 是 Spring 的，处理表单和 query 参数里的日期字符串；`@JsonFormat` 是 Jackson 的，处理 JSON 请求体和响应里的日期。

---

### 问题31 ⭐⭐

后端返回的 Long 类型 ID 前端拿到后末尾变成 0，是什么原因？怎么解决？

> JavaScript 的 Number 只能安全表示到 2^53 - 1，雪花算法生成的 19 位 ID 会丢失精度。可以在后端用 Jackson 的 `ToStringSerializer` 把 Long 序列化成字符串，可以字段级配置，也可以全局配置。

---

### 问题32 ⭐⭐

`@RequestParam` 和不加注解有什么区别？前端传 JSON 和传表单，后端分别怎么接？

> 简单类型不加注解相当于 `required = false` 的 `@RequestParam`，POJO 不加注解相当于 `@ModelAttribute`，按字段名绑定 query 或表单参数。前端传 JSON 用 `@RequestBody`，传表单（`x-www-form-urlencoded`）用 `@RequestParam` 或直接用 POJO 接收。

---

### 问题33 ⭐⭐

一个方法为什么只能有一个 `@RequestBody`？

> 请求体是输入流，只能读取一次，读完就转换成对象了，所以一个方法只能有一个 `@RequestBody` 参数。

---

### 问题34 ⭐⭐

文件上传要注意什么？

> 要配置 `max-file-size` 和 `max-request-size` 限制大小，并在全局异常里处理超限异常；不要用原文件名存储，用 UUID 重命名避免重名和路径穿越；校验文件类型，不能只信任 Content-Type；上传目录不要放在 Web 可直接访问的位置，生产环境通常用 OSS 存储。

---

### 问题35 ⭐

POST 和 PUT 的区别？哪些方法是幂等的？

> GET、PUT、DELETE 是幂等的，POST 不是，重复提交可能创建多条数据。POST 重复提交可以用唯一索引、防重 token 或分布式锁来防止。

---

# 四十三、🎯 补充版一句话总结

> **在原来的基础上补上四件事：一是完整流程要包含拦截器（`HandlerExecutionChain`）、返回值处理和异常处理；二是 Filter 和 Interceptor 要能答出「能否拿到 Handler」「能否被全局异常处理」这两个细节差异；三是参数校验要区分 `@Valid` / `@Validated` 以及各自对应的异常类型；四是准备好项目里会用到的实战点，包括登录拦截器 + ThreadLocal、跨域、Result 统一返回、Long 精度和日期格式、文件上传安全，并且能落到自己项目的真实用法上。**