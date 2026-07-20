
# 1. MVC

## 1. MVC是什么
```
mvc是一种软件设计模式：
M:Model 模型层（用来处理数据和业务）
	Model层的责任：
		- Service(业务层)
		- Dao（数据访问层）
		- 实体类
V:View 视图层（页面展示）
	view的责任：
		- 页面展示
		- 与用户交互
		- 收集用户输入
	常见技术：
		 - HTML
		 - CSS
		 - JavaScript
		 - JSP
		 - Thymeleaf
		 - Vue
C: Controller 控制层（用来接收请求，调度）
	Controller层的责任：
		- 接收请求
		- 调用业务层
		- 返回结果
```
## 1.Dao

```
什么是Dao;
	- Data Access Object（数据访问对象）
Dao层的作用：
	- Dao只负责数据库表的CRUD，没有业务在这里面
一般情况下，一张表对应一个Dao对象

```