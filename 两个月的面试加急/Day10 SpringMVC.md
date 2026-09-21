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