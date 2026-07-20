# 1.创建项目结构

```txt

underworld-ssm/
├── src/
│ └── main/
│ ├── java/   <---存放java代码的根目录

│ │ └── com/

│ │ └── powernode/

│ │ └── underworld/   <---com+powernode+underworld == 项目的地址

│ │ ├── common/ (放 Result.java)  <--- 用来放前后端消息

│ │ ├── pojo/ (放 实体类)

│ │ ├── vo/ (放 UnderworldStaffVo.java)  <---- View Object 视图对象

│ │ ├── mapper/ (放 Mapper 接口) <--- dao层

│ │ ├── service/ (放 Service 接口及实现类)  <----业务逻辑层

│ │ └── controller/ (放 Controller 类)   <---- 前端控制层

│ ├── resources/   <---根目录

│ │ ├── db.properties (数据库连接配置文件)

│ │ ├── spring-context.xml (Spring 核心与事务配置)

│ │ ├── spring-mvc.xml (Spring MVC 路由与扫描配置)

│ │ └── mapper/

│ │ └── UnderworldStaffMapper.xml

│ └── webapp/ (⚠️ 传统 Web 容器的根目录)

│ ├── index.html (把我们刚写好的玉帝 CRUD 网页放这！)

│ └── WEB-INF/

│ └── web.xml (整个项目的总调度核心)

└── pom.xml
```

## 2.编写数据库的外部配置

**db.properties**
```properties
jdbc.driver=com.mysql.cj.jdbc.Driver  
jdbc.url=jdbc:mysql://localhost:3306/underworld  
jdbc.username=root  
jdbc.password=250712
```

# 3. 编写spring-context.xml（spring核心配置）文件

```xml
1. 组件扫描
<context:component-scan base-package="com.powernode.underworld">  
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>  
</context:component-scan>
2.加载外部资源

3.添加数据源

4.配置sqlSessionFactory

5.配置扫描xml映射文件位置

6.扫描mapper接口

7.配置事务管理
```

# 4.配置Spring-MVC文件

spring-mvc.xml

```xml
<!-- 1. 只扫描 Controller 控制层注解 -->

<context:component-scan base-package="com.powernode.underworld.controller" use-default-filters="false">

<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>

</context:component-scan>


<!-- 2. 开启 Spring MVC 注解驱动 (自动注入 Jackson 转换器，让 @ResponseBody 生效) -->

<mvc:annotation-driven />

<!-- 3. 极其重要：放行静态资源！否则 index.html 会被前端拦截报404 -->

<mvc:default-servlet-handler />
```

