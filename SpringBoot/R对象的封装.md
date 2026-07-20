
为了统一前端的处理逻辑，企业里都会采用一个通用的**统一结果封装对象**，通常命名为 `Result`、`ApiResponse` 或者你提到的 **`R`**

一个合格、严谨通用的 `R` 对象，应该具备以下特点：
- **泛型支持**：适应各种不同的返回数据类型（如单个对象、List、Map 等）。
- **链式编程（Fluent API）**：让后端写代码时可以用 `.ok().data(...)` 丝滑顺畅。
- **状态码枚举化**：拒绝硬编码，所有的状态码和提示信息都应该由枚举统一管理。

## 1. 核心基石：状态码枚举类（ResultCode）
```java
public enum ResultCode {
    
    // 成功状态码
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 (4xx)
    BAD_REQUEST(400, "非法请求"),
    UNAUTHORIZED(401, "暂未登录或 token 已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "请求资源不存在"),
    
    // 服务端错误 (5xx)
    ERROR(500, "服务器内部错误"),
    
    // 自定义业务错误 (可根据公司业务自行扩展)
    PARAM_ERROR(1001, "参数校验失败"),
    USER_NOT_EXIST(2001, "用户不存在"),
    PASSWORD_ERROR(2002, "密码错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() { return code; }
    public String getMessage() { return message; }
}

```

## 2. 主角登场：万能 R 对象封装

这是核心类。注意看里面的泛型 `<T>` 和静态流式方法：
```java
import java.io.Serializable;

/**
 * 全局统一返回结果对象
 * @param <T> 响应数据的类型
 */
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    // 是否成功
    private Boolean success;
    // 状态码
    private Integer code;

    // 提示信息
    private String message;
    // 响应数据
    private T data;
    // 隐藏构造方法，强迫使用静态方法创建对象
    private R() {}
    // ================== 快捷成功的静态方法 ==================
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        return r;
    }
    public static <T> R<T> ok(T data) {
        R<T> r = ok();
        r.setData(data);
        return r;
    }

    // ================== 快捷失败的静态方法 ==================

    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(ResultCode.ERROR.getMessage());
        return r;
    }

    public static <T> R<T> fail(String message) {
        R<T> r = fail();
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(Integer code, String message) {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<>();
        r.setSuccess(false);
        r.setCode(resultCode.getCode());
        r.setMessage(resultCode.getMessage());
        return r;
    }

    // ================== 链式调用支持 (Builder模式变形) ==================

    public R<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    public R<T> data(T data) {
        this.setData(data);
        return this;
    }

    // ================== Getter / Setter ==================

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

## 3. 在 Controller 层怎么用？
```java
@RestController
@RequestMapping("/vip")
public class VipController {
    @Autowired
    private VipService vipService;

    // 1. 返回分页数据 (成功)
    @GetMapping("/list")
    public R<PageInfo<Vip>> list(@RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        PageInfo<Vip> pageInfo = vipService.findByPage(pageNum, pageSize);
        // 直接包裹在 R.ok() 里面
        return R.ok(pageInfo); 
    }

    // 2. 返回单个对象 (成功)
    @GetMapping("/{id}")
    public R<Vip> getById(@PathVariable Long id) {
        Vip vip = vipService.getById(id);
        if (vip == null) {
            // 自定义错误提示
            return R.fail(ResultCode.USER_NOT_EXIST); 
        }
        return R.ok(vip);
    }

    // 3. 链式调用示例 (临时修改提示信息)
    @PostMapping("/add")
    public R<Void> add(@RequestBody Vip vip) {
        boolean success = vipService.save(vip);
        if (success) {
            return R.ok().message("会员添加成功啦！");
        }
        return R.fail().message("添加失败，请稍后再试");
    }
}
```