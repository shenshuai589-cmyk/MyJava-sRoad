### 1.ResponseBody（非常重要）

当处理器方法上添加@ResponseBody注解，那么这个放啊的返回值不再是逻辑视图名称了
```java
@GetMapping("/ajax")
@Responsebody
public String ajax(){
	return "你跑不过我你信不信";
}
```
![Responsebody注解](SSM/SpringMVCByMySelf/图片/01.png)
上面使用的消息转换器是SpringHttpMessageConverter

**将对象转换成json字符串**

1. 创建pojo对象
```java
package com.powernode.springmvc.bean;  
  
public class User {  
  
    private Long id;  
    private String name;  
    private String password;  
  
    public User() {  
    }  
  
    public User(Long id, String name, String password) {  
        this.id = id;  
        this.name = name;  
        this.password = password;  
    }  
  
    public Long getId() {  
        return id;  
    }  
  
    public void setId(Long id) {  
        this.id = id;  
    }  
  
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getPassword() {  
        return password;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }  
  
    @Override  
    public String toString() {  
        return "User{" +  
                "id=" + id +  
                ", name='" + name + '\'' +  
                ", password='" + password + '\'' +  
                '}';  
    }  
}
```
2. 书写控制器方法
```java
@RequestMapping(value = "/ajax",method = RequestMethod.GET)  
@ResponseBody  
public User ajax() throws IOException {  
    User user = new User(10000L,"zhangsan","12134423");  
    return user;  
}
```
3. 添加pom依赖
==这个依赖可以将具体对象转换成json格式字符串，也可以将json格式字符串转换成具体的对象==
```xml 
        <dependency>  
            <groupId>com.fasterxml.jackson.core</groupId>  
            <artifactId>jackson-databind</artifactId>  
            <version>2.19.2</version>  
        </dependency>
```
运行代码
![](SSM/SpringMVCByMySelf/图片/02.png)
上述程序使用的消息转换器是：MappingJackson2HttpMessageConverter

---

### 2.RestController注解

```
@ResController注解 =  @ResponseBody+@Controller
```
当一个类中添加了@ResController注解，表示该类自动添加了@Controller注解，并且该类所有的方法上自动添加了@ResponseBody注解

---
### 3. RequestBody注解

- @RequestBody注解只能使用在Controller方法的形参上
- 这个注解的作用是直接将请求体传递给java程序，在java程序中可以直接使用一个String类型的变量接受这个请求体的内容。
- 底层使用的Http消息转换器是:FormHttpMessageConverter

@RequestBody注解的重要用法：如果前端请求体当中提交的数据是JSON格式，那么@ResquestBody可以将提交的JSON格式的字符串转换成java对象，前提是使用jackson的依赖

---

### 4.@RequestBody底层干了什么
```txt

请求JSON  
↓  
DispatcherServlet  
↓  
HandlerAdapter  
↓  
HttpMessageConverter  
↓  
Jackson  
↓  
Java对象

```

