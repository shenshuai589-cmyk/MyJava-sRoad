> springboot项目当中要进行分页查询+PageHelper

## 1.引入依赖
```xml
<!--        PageHelper-->  
    <dependency>  
        <groupId>com.github.pagehelper</groupId>  
        <artifactId>pagehelper-spring-boot-starter</artifactId>  
        <version>2.1.0</version>  
    </dependency>
```

##  2. 配置 application.yml (可选)

通常情况下，引入 Starter 后 PageHelper 就能直接工作，但为了确保它能完美识别你的数据库类型（如 MySQL、Oracle），建议在配置文件中加上基础配置：
```yml
pagehelper:
# 指定数据库方言，PageHelper 会自动检测，也可以手动指定
	helperDialect: mysql
	# 启用合理化。如果当前页页码 < 1，会自动查询第一页；如果页码 > 总页数，会自动查询最后一页
	reasonable: true
	# 支持通过 Mapper 接口参数来传递分页参数
	supportMethodsArguments: true
	# 用于控制是否在检测到特定的参数时进行分页
	params: count=countSql
```

## 3. 在 Service 层使用 PageHelper

>PageHelper 的核心使用原理是基于 **ThreadLocal**。你只需要在**紧邻 MyBatis 查询方法执行之前**调用 `PageHelper.startPage()` 即可。

```java

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VipServiceimpl implements VipService {
    @Autowired
    private VipMapper vipMapper;
    @Override
    public PageInfo<Vip> findByPage(int pageNum, int pageSize) {
        // 1. 开启分页：传入当前页码（从1开始）和每页显示条数
        // 注意：这行代码必须紧跟在你要执行的 MyBatis 查询方法前面！
        PageHelper.startPage(pageNum, pageSize);
        // 2. 执行你的正常查询方法（Mapper里的SQL不需要写 limit）
        List<Vip> list = vipMapper.selectAll();
        // 3. 将查询结果包装进 PageInfo 对象中
        // PageInfo 包含了非常丰富的导航信息：总条数、总页数、当前页、是否有上一页/下一页等
        PageInfo<Vip> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
```
## 4.控制器 (Controller) 接收
>在 Controller 层，你只需要接收前端传来的 `pageNum`（当前页）和 `pageSize`（每页大小），然后将 `PageInfo` 对象直接返回给前端即可。

```java

@RestController
@RequestMapping("/vip")
public class VipController {

    @Autowired
    private VipService vipService;

    @GetMapping("/list")
    public PageInfo<Vip> list(@RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "10") int pageSize) {
        return vipService.findByPage(pageNum, pageSize);
    }
}
```
---
### Controller层代码解析
### 1. 类级别注解（组件声明与路由）

### `@RestController`

- **拆解：** 它实际上是一个组合注解，等同于 `@Controller` + `@ResponseBody`。

- **作用：** 1. 告诉 Spring 容器：“这是一个控制器类，请把它实例化并接管过去（注入到 IoC 容器中）”。 2. 声明该类中所有方法的返回值，都会**自动转换成 JSON 格式**发送给浏览器，而不是去寻找一个 HTML 页面模板。这在如今前后端分离的项目中是标准配置。

### `@RequestMapping("/vip")`

- **作用：** 规定了当前类中所有接口的**基础路由（根路径）**。

- **通俗解释：** 只要是访问以 `/vip` 开头的 URL 请求（例如 `http://localhost:8080/vip/...`），都会被分发到这个类来处理。

## 2. 依赖注入

### `@Autowired private VipService vipService;`

- **作用：** 引入业务逻辑层的核心接口（Service 层）。

- **通俗解释：** Controller 自己不干具体的体力活（比如怎么查数据库、怎么算分页），它把这些活儿都委托给 `vipService` 去做。`@Autowired` 会自动把 Spring 容器中实现好的 Service 实例“插”到这里供我们调用。

## 3. 方法级别剖析（接口实现）

我们把这个 `list` 方法拆成三部分来看：**请求映射**、**参数接收**、**业务处理与返回**。

### ① 请求映射：`@GetMapping("/list")`

- **作用：** 限制该接口只接受 **HTTP GET** 请求，且具体的访问路径是 `/list`。

- **路径组合：** 结合类上的路径，这个接口的完整访问 URL 就是：`/vip/list`。


### ② 参数接收：`@RequestParam(...)`

Java

```
@RequestParam(defaultValue = "1") int pageNum,
@RequestParam(defaultValue = "10") int pageSize
```

- **作用：** 用来接收前端通过 URL 拼接传过来的查询参数（例如 `/vip/list?pageNum=2&pageSize=5`）。

- **关键机制（防错处理）：**
    
    - `defaultValue = "1"`：**极其重要**。如果前端第一页刚加载，忘记传 `pageNum` 参数了，Spring 会自动给它赋一个默认值 `1`。
    
    - `defaultValue = "10"`：同理，如果前端没指定每页显示多少条，默认就按每页 `10` 条来处理。
    
    - 这样能极大地增强接口的健壮性，防止因为前端漏传参数导致后端报空指针或类型转换异常。
    

### ③ 业务处理与返回：`public PageInfo<Vip> list(...) { ... }`

- **返回值类型 `PageInfo<Vip>`：**
    
    - 这是一个“包装类”，里面不仅装了从数据库查出来的 `List<Vip>` 成员列表数据，还包含了 PageHelper 帮你计算好的诸如 `total`（总条数）、`pages`（总页数）等分页元数据。
    
- **方法体 `return vipService.findByPage(pageNum, pageSize);`：**
    
    - Controller 拿着前端传来的 `pageNum` 和 `pageSize`，直接调用 Service 层的 `findByPage` 方法。
    
    - Service 执行完毕后，把带有分页数据的 `PageInfo` 对象交还给 Controller，Controller 再顺手 `return` 出去。
    
    - 因为类上有 `@RestController`，这个 `PageInfo` 对象最终会被框架自动转成前端最喜欢的 **JSON 字符串** 发送出去。

---
## 5. 返回给前端的数据结构

>当前端调用该接口时，PageHelper 封装的 `PageInfo` 会返回一个极其规范的 JSON 结构，包含分页所需的全部元数据：

