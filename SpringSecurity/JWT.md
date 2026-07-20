
### 1. JWT（JSON Web Token）

JWT是一种开放的行业标准（RFC 7519），用于安全地双方之间传输信息，常用于各方之间传输信息，特别是在身份认证领域使用非常广泛。


### 2. JWT的数据结构

![JWT的一个例子](SpringSecurity/图片/005.png)
他是一个很长的字符串，中间用"."分隔成三个部分
注意 ：JWT内部是没有换行的，这里只是为了便于展示，将他写成几行

**JWT的三个部分依次是**：

Header（头部）
Payload（负载）这里可以携带业务数据
Signature（签名）

写成一行就是这样：Header.Payload.Signature


### Header

Header部分原文是一个JSON对象，描述JWT的元数据，通常如下：
```json
{
	"alg":"HS256",
	"type":"JWT"
}
```

其中alg属性表示签名的算法（algorithm），默认是HMAC SHA256（HS256）；
typ属性表示令牌（token）的类型（type），JWT令牌统一写成JWT；
使用Base64URL算法转成字符串，就得到了Header部分

### Payload

Payload部分原文也是一个JSON对象，用来存放实际需要传递的数据，JWT定义了7个官方字段选用：
- iss（issuer）：签发人
- exp（expiration time）：过期时间
- sub（subject）：主题
- aud（audience）：受众
- nbf（not before）：生效时间
- iat（Issued At）：签发时间
- jti（JWT ID）：编号

我们可以不使用官方的字段，我们可以使用任何字段来传递数据，比如：
```json
{
	"number":"1234567890",
	"name":"cat",
	"phone":"18888888888"
}
```
这个JSON对象也要使用Base64URL算法转成字符串，但是Base64URL算法不是加密算法，他是编码算法，是可以解码出原文的，也就是说JWT负载中的数据任何人都可以解码到原文（不安全），所以不要把私密信息（密码，验证码等）放在这个部分；

### Signature

Signature部分是对前两部分的前面，==防止数据串改==
首先指定一个密钥（secret），这个，密钥只有服务器知道，不能泄露给用户，然后使用Header里面指定的签名算法（HMAC SHA256，按照下面的公式产生签名：
```
HMACSHA256(
  base64UrlEncode(header)+"."+
  base64UrlEncode(payload),
  secret)
)
```

### JWT的使用

两个jwt的依赖
```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-jwt</artifactId>
    <version>5.8.32</version>
</dependency>
```


利用java-jwt使用JWT
```java
public class PWTUtil{
	public static final String secret = "0S/12dSd0-2Sfdjkgh3OPYs";  
	  
	// 怎么生成jwt这个字符串  
	public static String createToken(String userJson){  
	    Map<String,Object> header = new HashMap<>();  
	    header.put("alg","HS256");  
	    header.put("typ","JWT");  
	  
	    return JWT.create()  
	            .withHeader(header)  
	            .withClaim("user",userJson)  
	            .withClaim("phone","13218307382")  
	            .withClaim("email","cat@qq.com")  
	            .sign(Algorithm.HMAC256(secret));  
	}  
	  
	// 验证jwt是否被篡改过  
	public static boolean verifyToken(String token){  
	    try{  
	        // 使用密钥创建一个jwt验证对象  
	        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();  
	        // 使用验证器对象验证jwt，如果验证没有抛异常，说明验证通过，反之就是没有通过  
	        jwtVerifier.verify(token);  
	        // 如果验证没有抛异常，返回true表示验证通过  
	        return true;  
	    }catch (Exception e){  
	        e.printStackTrace();  
	    }  
	    return false;  
	}  
	//解析出负载数据  
	  
	public static String parseToken(String token){  
	    //使用密钥创建一个jwt验证器对象  
	    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();  
	    // 验证JWT，得到一个解码后的hwt对象  
	    DecodedJWT decodedJWT = jwtVerifier.verify(token);  
	    //通过解码后的jwt对象获取负载的数据  
	    Claim user = decodedJWT.getClaim("user");  
	    System.out.println(user.asString());  
	    Claim phone = decodedJWT.getClaim("phone");  
	    System.out.println(phone.asString());  
	    Claim email = decodedJWT.getClaim("email");  
	    System.out.println(email.asString());  
	    return user.asString();  
	  
	}
}
```


第二种使用hutool-jwt
```java
public static final String secret = "OS/1213dfhfuduf";  
@Test  
void contextLoads() {  
    Map<String,Object> payload = new HashMap<>();  
    payload.put("id",12345);  
    payload.put("phone","13151313008");  
    payload.put("birthDay",new Date());  
    String token = JWTUtil.createToken(payload, secret.getBytes());  
    System.out.println(token);  
  
    // 第二种  
  
    String token1 = JWTUtil.createToken(payload, JWTSignerUtil.hs256(secret.getBytes()));  
    System.out.println(token1);  
  
  
    // 验证jwt  
    boolean verify = JWTUtil.verify(token, secret.getBytes());  
    System.out.println(verify);  
  
  
    // 解析负载数据  
    JWT parseToken = JWTUtil.parseToken(token);  
    JWTPayload payLoads = parseToken.getPayload();  
    Object id = payLoads.getClaim("id");  
    Object phone = payLoads.getClaim("phone");  
    Object birthDay = payLoads.getClaim("birthDay");  
  
    System.out.println(id+ ", " + phone + ", " + birthDay);  
  
    JSONObject payloads = parseToken.getPayloads();  
    payloads.forEach((k,v)->{  
        System.out.println(k + ": " + v);  
    });
```