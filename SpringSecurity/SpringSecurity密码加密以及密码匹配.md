**开发中，数据库中的密码一般是以加密的形式记录到数据库表中**
**SpringSecurity推荐的密码加密方式有BCrypt**

### 1. BCrypt

BCrypt是实现的PasswordEncoder接口，常用的方法有：
- encode    ---->用来密码加密
- matches  ----->用来密码解密
```java
@Resource  
private PasswordEncoder passwordEncoder;  
@Test  
void test01() {  
    String password = "250712";  
    String encodingPassword = passwordEncoder.encode(password);  
    System.out.println(encodingPassword);  
  
    boolean matches = passwordEncoder.matches(password, encodingPassword);  
    System.out.println(matches);  
  
}

/*
$2a$10$VBVYykDwIf3dsSHWnsHx3uNqGvaqynjywYuTngXy3a1lH6.BiUB1S
true
*/
```


### 2. BCrypt加密原理

输入的明文密码比如是aaa111，通过==随机加盐==(22位的字符串)后在使用BCrypt进行加密得到密文密码【$2a$10$VBVYykDwIf3dsSHWnsHx3uNqGvaqynjywYuTngXy3a1lH6.BiUB1S】，
其密文密码的组成部分：version+salt+hash,然后存入数据库
