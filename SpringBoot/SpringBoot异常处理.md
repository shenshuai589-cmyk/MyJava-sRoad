# 1.SpringMvc的错误处理方法

## 1.1 局部异常处理@ExceptionHandler

> 局部异常处理**只能作用于当前 Controller 类**。如果其他 Controller 抛出了相同的异常，它是管不到的。

**实现方式**

》直接在具体的 Controller 类中定义一个方法，并加上 `@ExceptionHandler` 注解。
```java
package com.powernode.springboot.controller;  
  
import org.springframework.web.bind.annotation.ExceptionHandler;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.PathVariable;  
import org.springframework.web.bind.annotation.RestController;  
  
import java.util.HashMap;  
import java.util.Map; 

@RestController  
public class UserController {  
    @GetMapping("/resource/{id}")  
    public String getResults(@PathVariable("id") Long id){  
        if(id== 1){  
            throw new IllegalArgumentException("无效ID"+id);  
        }  
        return "ID="+id ;  
    }  
    
    // 局部异常处理器：只拦截当前 UserController 抛出的 IllegalArgumentException    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleLocalException(IllegalArgumentException e) {  
        Map<String, Object> map = new HashMap<>();  
        map.put("code", 400);  
        map.put("msg", "局部捕获：" + e.getMessage());  
        return map;  
    }  
}
```

**优点**： 针对性强，适合某些特定 Controller 需要特殊处理的场景。
**缺点**：代码冗余度高。如果每个 Controller 都要写一遍，根本无法维护

## 1.2 SpringMVC全局异常处理：@ControllerAdvice / @RestControllerAdvice

> 为了解决局部处理无法复用的问题，Spring 3.2 引入了全局异常处理器。它利用了 AOP（面向切面编程）的思想，在运行时动态地将异常处理逻辑织入到所有的 Controller 方法中。

**实现方式**

》通常结合 `@ExceptionHandler` 一起使用。如果是前后端分离项目，直接使用 `@RestControllerAdvice`（相当于 `@ControllerAdvice` + `@ResponseBody`）

```java

@ControllerAdvice  
public class GlobalExceptionHandler {  
    @ResponseBody  
    @ExceptionHandler(IllegalArgumentException.class)  
    public String handlerIllegalArgumentException(IllegalArgumentException e) {  
        return "错误信息：" +  e.getMessage();  
    }  
}
```
当然也可以直接使用@RestControllerAdvice注解
```java
@RestControllerAdvice  
public class GlobalExceptionHandler {    
    @ExceptionHandler(IllegalArgumentException.class)  
    public String handlerIllegalArgumentException(IllegalArgumentException e) {  
        return "错误信息：" +  e.getMessage();  
    }  
}
```
![全局异常处理](SpringBoot/images/我的第一个项目/031.png)
![全局异常处理](SpringBoot/images/我的第一个项目/032.png)

# 2. SpringBoot错误处理方法

当前端请求发生错误时，springboot的处理步骤：
==精确错误码文件 ----> 模糊错误码文件--->通用错误页面---->默认错误处理==
1.当发生404错误时，springboot会去classpath:/templates/error包下找404.xml,如果classpath:/templates/error包下没有404.html，则会去classpath:/static/error包下找404.html
2.若第1步都没找到，springboot会去classpath:/templates/error包下找4xx.html，如果classpath:/templates/error包没有4xx.html，则会去classpath:/static/error包下找4xx.html
3.若第2步也没找到，springboot会去classpath:/templates包下找error.html
4.若上述步骤都没有找到对应的文件,sprinboot则会返回错误端点/error

# 3.SpringBoot的异常处理

Spring Boot 主要提供了以下几种机制来实现异常处理，最推荐也是最常用的做法是 **`@ControllerAdvice` + `@ExceptionHandler`**

## 3.1. 核心解决方案：`@RestControllerAdvice`

**核心步骤**

第一步：定义统一的返回结果对象
> 为了让前端方便处理，无论是成功还是失败，后端都应该返回结构一致的 JSON

```java
@Data
public class ApiResponse<T> {

	private int code; // 状态码，例如 200 成功，500 失败，4001 参数错误
	private String message; // 提示信息
	private T data; // 成功时的数据


// 构造方法、Getter/Setter 省略

	public static <T> ApiResponse<T> error(int code, String message) {
	
		ApiResponse<T> response = new ApiResponse<>();
		
		response.setCode(code);
		
		response.setMessage(message);
		
		return response;
	}
}
```

第二步：编写全局异常处理器

使用 `@RestControllerAdvice` 声明一个全局处理类，内部用 `@ExceptionHandler` 捕获特定异常。

```java
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.HttpStatus;

@RestControllerAdvice

public class GlobalExceptionHandler {
    // 1. 捕获自定义的业务异常
    @ExceptionHandler(BusinessException.class)

    public ApiResponse<?> handleBusinessException(BusinessException e) {
        // 假设 BusinessException 里自带了 code 和 message
        return ApiResponse.error(e.getCode(), e.getMessage());

    }

    // 2. 捕获特定的系统异常（例如：空指针异常）

    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<?> handleNullPointerException(NullPointerException e) {
        return ApiResponse.error(500, "程序开小差了（空指针异常）");
    }

    // 3. 捕获兜底的所有其他异常（防止未知的异常直接暴露给用户）
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        // 实际开发中，这里一定要用 log.error("系统异常", e) 记录日志
        return ApiResponse.error(500, "系统未知错误，请联系管理员");
    }
}
```
