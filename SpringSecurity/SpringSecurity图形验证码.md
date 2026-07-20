
## 改内容是基于手动设置登录页

### 1.引入hutool的图形验证码依赖

```java
<dependency>  
    <groupId>cn.hutool</groupId>  
    <artifactId>hutool-crypto</artifactId>  
    <version>5.8.46</version>  
    <scope>compile</scope>  
</dependency>
```

### 2.在login.html页面中新添一个input
```html
<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta charset="UTF-8">  
    <title>登录</title>  
</head>  
<body>  
    <from action="/user/login" method="post">  
        账号:<input type="text" name="username" placeholder="Username"><br>  
        密码:<input type="password" name="password" placeholder="Password"><br>  
        <input type="text" name="captcha"> <img src="/api/captcha"/><br>  
  
        <input name="_csrf" type="hidden" th:value="${_csrf.token}"><br>  
        <input type="submit" value="登 录">  
    </from>  
</body>  
</html>
```

### 3.新建一个CaptchaController

```java
@Controller  
public class CaptchaController {  
  
    @RequestMapping("/api/captcha")  
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {  
  
        response.setContentType("image/jpeg");  
        //1. 生成圆形验证码图片  
        ICaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 30, 4, 5, 1);  
  
        //2.把图片里面的验证码字符串保存到后端  
        request.getSession().setAttribute("captcha", circleCaptcha.getCode());  
  
        // 3.把生成的验证码图片以io流的方式写出去  
        circleCaptcha.write(response.getOutputStream());  
    }  
}
```

不单单可以生成圆形，还可以生成其他形状，以及自定义内容

```java
// 自定义随机生成四位数字
public class MyCodeGenerator implements CodeGenerator {  
    @Override  
    public String generate() {  
        int code = 1000+ new Random().nextInt(9000);  
        return String.valueOf(code);  
    }  
  
    @Override  
    public boolean verify(String s, String s1) {  
        return false;  
    }  
}
```



