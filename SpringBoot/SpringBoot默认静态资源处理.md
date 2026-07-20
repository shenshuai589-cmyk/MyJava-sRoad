
## `spring.web.resources.add-mappings`（要不要开启静态资源落地方案）

- **干嘛用的：** 这是个总开关。Spring Boot 默认自带一套“帮你在后台找静态文件”的机制（默认是 `true`）。
    
- **通俗解释：**
    
    - 如果保持默认（或者配成 `true`），你在项目的 `src/main/resources/static` 文件夹里放一张 `logo.png`，在浏览器输入 `http://localhost:8080/logo.png` 就能直接看到图片。
        
    - 如果你把它改成 `false`（让静态资源处理失效），Spring Boot 的这个“贴心功能”就关闭了。你再访问任何图片，它都会直接报 404，除非你自己在代码里手动写一个 Controller 去读取并返回这个图片。
        
- **开发建议：** **永远不要动它**，保持默认开启即可。
    

## 2. `spring.mvc.static-path-pattern`（访问静态资源的“前缀暗号”）

- **干嘛用的：** 规定浏览器访问静态资源时，URL 里必须带有什么样的**前缀**。
    
- **默认情况：** 默认是 `/`。这意味着**不需要任何前缀**。
    
    - 比如你的图片叫 `avatar.jpg`，你直接通过 `http://localhost:8080/avatar.jpg` 就能访问。
        
- **如果你改了它：** 假设你把它改成了 `spring.mvc.static-path-pattern=/resources/`。
    
    - 那么对不起，你直接访问 `http://localhost:8080/avatar.jpg` 就会报 404 了。
        
    - 你必须在 URL 前面加上大喊一声“暗号”：`http://localhost:8080/resources/avatar.jpg`，Spring Boot 才会去后台的静态文件夹里帮你找图。
        
- **开发建议：** 通常保持默认 `/`。只有当你的业务接口（Controller）的路径和静态资源名字经常冲突时，才会加一个类似 `/static/` 的前缀来做区分。
    

## 3. `spring.mvc.webjars-path-pattern`（前端依赖的访问暗号）

- **干嘛用的：** `Webjars` 是一种把前端框架（比如 jQuery, Bootstrap）打包成 Java 的 `.jar` 包引入项目的一种技术。这个配置是给它们定义访问前缀的。
    
- **通俗解释：** 现在基本不用这个了！因为现在的企业级开发全部都是**前后端分离**（前端用 Vue/React 独立开发，后端只写接口返回 JSON 数据）。后端压根不需要用 Webjars 去管理前端的 jQuery 库。
    
- **开发建议：** **直接无视它**。