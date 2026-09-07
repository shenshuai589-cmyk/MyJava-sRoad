Spring MVC 是 Java 后端面试中非常常见的一块，尤其是：

- ⭐ Spring MVC 是什么？
    
- ⭐ DispatcherServlet 是什么？
    
- ⭐ HandlerMapping 做什么？
    
- ⭐ HandlerAdapter 做什么？
    
- ⭐ `@PathVariable`、`@RequestParam`、`@RequestBody` 区别
    
- ⭐ HttpMessageConverter 是什么？
    
- ⭐ `@RequestMapping` / `@GetMapping` 是怎么找到 Controller 方法的？
    
- ⭐ `@RestController` 和 `@Controller` 的区别
    
- ⭐ `@ResponseBody` 的作用
    
- ⭐ Spring MVC 完整请求流程
    

---

# 二、⭐ Spring MVC 是什么？

Spring MVC 是 Spring 提供的一个 **Web 请求处理框架**。

它主要负责：

```text
接收 HTTP 请求
        ↓
找到对应的 Controller 方法
        ↓
执行 Controller
        ↓
处理返回结果
        ↓
返回给前端
```

例如前端发送：

```text
GET /user/1
```

Spring MVC 会找到：

```java
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

然后执行这个方法，并将结果返回给前端。

### 🧠 一句话记忆

> Spring MVC 就是负责处理 HTTP 请求，并找到对应 Controller 方法执行的 Web 框架。

---

# 三、⭐⭐ DispatcherServlet 是什么？

DispatcherServlet 是 Spring MVC 的**核心前端控制器**。

可以把它理解成：

> Spring MVC 的“总调度员”。

所有 HTTP 请求首先都会进入 DispatcherServlet。

例如：

```text
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

`controller` 只是我们项目中常见的代码组织方式。

DispatcherServlet 真正负责的是：

> 统一接收和调度 HTTP 请求。

### 🧠 一句话记忆

> DispatcherServlet = Spring MVC 的核心前端控制器，负责统一接收和调度请求。

---

# 四、⭐⭐ HandlerMapping 是什么？

DispatcherServlet 收到请求之后，需要知道：

> “这个请求应该由哪个 Controller 方法处理？”

这时候就需要 HandlerMapping。

HandlerMapping 的作用：

> **根据请求找到对应的 Handler。**

例如：

```java
@GetMapping("/user")
public User getUser() {
    return ...;
}
```

Spring MVC 会建立类似这样的映射：

```text
GET /user
    ↓
UserController.getUser()
```

当请求：

```text
GET /user
```

进入 DispatcherServlet 后：

```text
DispatcherServlet
        ↓
HandlerMapping
        ↓
找到 UserController.getUser()
```

### 🧠 一句话记忆

> HandlerMapping = 找谁处理。

---

# 五、⭐⭐ HandlerAdapter 是什么？

HandlerMapping 找到了 Controller 方法之后，还需要把这个方法执行起来。

这时候就需要 HandlerAdapter。

HandlerAdapter 的作用：

> **负责调用 Handler。**

简单理解：

```text
HandlerMapping
    ↓
找到谁
    ↓
HandlerAdapter
    ↓
执行谁
```

例如：

```java
@GetMapping("/user")
public User getUser() {
    return ...;
}
```

流程：

```text
HandlerMapping
    ↓
找到 getUser()
    ↓
HandlerAdapter
    ↓
调用 getUser()
```

### 🧠 一句话记忆

> HandlerMapping = 找谁  
> HandlerAdapter = 调谁

---

# 六、⭐⭐ `@PathVariable`、`@RequestParam`、`@RequestBody`

这三个注解非常重要。

## 1. `@PathVariable`

获取 URL 路径中的参数。

例如：

```text
GET /user/100
```

代码：

```java
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) {
    return ...;
}
```

这里：

```text
{id} → 100
```

最终：

```java
id = 100
```

### 🧠 记忆

> `@PathVariable` = 路径参数

---

## 2. `@RequestParam`

获取 URL 查询参数。

例如：

```text
GET /user?id=100
```

代码：

```java
@GetMapping("/user")
public User getUser(@RequestParam Long id) {
    return ...;
}
```

这里：

```text
?id=100
```

对应：

```java
id = 100
```

### 🧠 记忆

> `@RequestParam` = `?` 后面的参数

---

## 3. `@RequestBody`

获取 HTTP 请求体中的数据。

例如前端发送：

```json
{
    "username": "zhangsan",
    "age": 20
}
```

代码：

```java
@PostMapping("/user")
public void addUser(@RequestBody User user) {
    ...
}
```

`@RequestBody` 表示：

> 从 HTTP 请求 Body 中获取数据，并绑定到 Java 对象。

---

# 七、⭐⭐ HttpMessageConverter 是什么？

`@RequestBody` 本身并不是负责 JSON 转换的。

真正负责：

```text
HTTP消息
    ↕
Java对象
```

转换的是：

> HttpMessageConverter

例如前端发送：

```json
{
    "username": "zhangsan",
    "age": 20
}
```

Spring MVC 需要把 JSON 转成：

```java
User user
```

这个过程中会使用 HttpMessageConverter。

在 Spring Boot 中，通常使用 Jackson 来完成 JSON 的序列化和反序列化。

---

## JSON → Java

```text
JSON
 ↓
HttpMessageConverter
 ↓
Java对象
```

例如：

```json
{
    "name": "Tom"
}
```

转换成：

```java
User user
```

这叫：

> 反序列化

---

## Java → JSON

Controller 返回：

```java
User user
```

然后：

```text
Java对象
 ↓
HttpMessageConverter
 ↓
JSON
 ↓
前端
```

这叫：

> 序列化

### 🧠 一句话记忆

> HttpMessageConverter 负责 HTTP 数据与 Java 对象之间的转换。

---

# 八、⭐⭐ `@RequestMapping` 和 `@GetMapping`

## 1. `@RequestMapping`

最基础的请求映射注解。

例如：

```java
@RequestMapping("/user")
public User getUser() {
    return ...;
}
```

也可以指定 HTTP 方法：

```java
@RequestMapping(
    value = "/user",
    method = RequestMethod.GET
)
public User getUser() {
    return ...;
}
```

---

## 2. `@GetMapping`

`@GetMapping` 是专门用于 GET 请求的组合注解。

```java
@GetMapping("/user")
public User getUser() {
    return ...;
}
```

类似的还有：

```java
@PostMapping
@PutMapping
@DeleteMapping
```

分别对应：

```text
GET
POST
PUT
DELETE
```

### 🧠 面试回答

> `@GetMapping`、`@PostMapping` 等都是 `@RequestMapping` 的组合注解，用于更方便地指定 HTTP 请求方法和请求路径。

---

# 九、⭐⭐ Controller 上也可以使用 `@RequestMapping`

例如：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/list")
    public List<User> list() {
        return ...;
    }

    @GetMapping("/detail")
    public User detail() {
        return ...;
    }
}
```

那么最终路径就是：

```text
GET /user/list
GET /user/detail
```

因为：

```text
类上的 @RequestMapping
        +
方法上的 @GetMapping
        ↓
最终请求路径
```

例如：

```java
@RequestMapping("/user")
```

```java
@GetMapping("/list")
```

最终：

```text
GET /user/list
```

---

# 十、⭐⭐ `@Controller` 和 `@RestController`

这是 Spring MVC 非常常见的面试题。

## `@Controller`

表示这是一个 Controller。

例如：

```java
@Controller
public class UserController {

    @GetMapping("/user")
    public User getUser() {
        return ...;
    }
}
```

如果没有 `@ResponseBody`：

```java
return user;
```

通常会被当成：

> 视图名称

例如：

```java
return "user";
```

可能表示：

```text
跳转到 user 页面
```

---

# 十一、⭐⭐ `@ResponseBody`

`@ResponseBody` 表示：

> 不进行视图解析，而是直接将方法返回值写入 HTTP Response Body。

例如：

```java
@Controller
public class UserController {

    @ResponseBody
    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

最终：

```text
Java对象
 ↓
HttpMessageConverter
 ↓
JSON
 ↓
HTTP Response Body
```

---

# 十二、⭐⭐ `@RestController`

`@RestController` 是：

```text
@Controller
+
@ResponseBody
```

的组合注解。

所以：

```java
@RestController
public class UserController {

    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

相当于：

```java
@Controller
public class UserController {

    @ResponseBody
    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
```

因此我们现在开发 REST API 时，经常使用：

```java
@RestController
```

### 🧠 一句话记忆

> `@RestController = @Controller + @ResponseBody`

---

# 十三、⭐⭐ Spring MVC 完整请求流程

这是 Day10 最重要的知识点之一。

假设前端发送：

```http
POST /user
Content-Type: application/json
```

请求 Body：

```json
{
    "name": "Tom",
    "age": 20
}
```

Controller：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.add(user);
    }
}
```

完整流程：

```text
前端发送 HTTP 请求
        ↓
DispatcherServlet
        ↓
HandlerMapping
        ↓
找到 UserController.addUser()
        ↓
HandlerAdapter
        ↓
调用 Controller 方法
        ↓
@RequestBody
        ↓
HttpMessageConverter
        ↓
JSON → User对象
        ↓
执行 addUser()
        ↓
返回 User对象
        ↓
HttpMessageConverter
        ↓
User对象 → JSON
        ↓
HTTP Response
        ↓
返回前端
```

---

# 十四、⭐⭐ HandlerMapping 到底是怎么找到 Controller 方法的？

例如：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/list")
    public List<User> list() {
        return ...;
    }
}
```

Spring 启动时，会扫描 Controller 中的请求映射。

最终可以理解成建立了这样的关系：

```text
GET /user/list
        ↓
UserController.list()
```

当请求：

```text
GET /user/list
```

进来：

```text
DispatcherServlet
        ↓
HandlerMapping
        ↓
找到 UserController.list()
```

然后：

```text
HandlerAdapter
        ↓
执行 list()
```

所以：

> `@RequestMapping`、`@GetMapping` 等注解，本质上是在告诉 Spring MVC：这个 Controller 方法负责处理什么请求。

---

# 十五、⭐ Day10 面试题

## 问题1 ⭐

Spring MVC 是什么？

### 推荐回答：

> Spring MVC 是 Spring 提供的 Web 请求处理框架，主要负责接收 HTTP 请求，根据请求找到对应的 Controller 方法并执行，然后将处理结果返回给前端。

---

## 问题2 ⭐⭐

DispatcherServlet 是什么？

### 推荐回答：

> DispatcherServlet 是 Spring MVC 的核心前端控制器，负责统一接收和调度 HTTP 请求。

---

## 问题3 ⭐⭐

HandlerMapping 和 HandlerAdapter 分别做什么？

### 推荐回答：

> HandlerMapping 根据请求找到对应的 Handler，HandlerAdapter 负责调用 Handler。

### 🧠 记忆：

```text
HandlerMapping → 找谁
HandlerAdapter → 调谁
```

---

## 问题4 ⭐⭐

`@PathVariable`、`@RequestParam`、`@RequestBody` 有什么区别？

### 推荐回答：

> `@PathVariable` 获取 URL 路径中的参数，例如 `/user/1`；`@RequestParam` 获取 URL 查询参数，例如 `/user?id=1`；`@RequestBody` 获取 HTTP 请求体中的数据，例如 JSON。

---

## 问题5 ⭐⭐

HttpMessageConverter 是干什么的？

### 推荐回答：

> HttpMessageConverter 负责 HTTP 消息和 Java 对象之间的转换，例如将前端发送的 JSON 转成 Java 对象，也可以将 Controller 返回的 Java 对象转换成 JSON。

---

## 问题6 ⭐⭐

`@RestController` 和 `@Controller` 有什么区别？

### 推荐回答：

> `@RestController` 相当于 `@Controller` 加上 `@ResponseBody`，Controller 方法返回的数据会直接写入 HTTP Response Body，通常用于开发 REST API。

---

