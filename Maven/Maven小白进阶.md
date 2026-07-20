# 1. 自己的一些心得

## 1.1 社区版的Idea创建web工程

当我们使用社区版的idea，想要创建web工程是不可以的，那我又不想花钱去购买专业版的，那么我可以这么做：

```text
1.第一步：
打开想要用作web工程的pom.xml
2.配置打包方式：
在pom.xml中设置<packaging>war</packaging>
3.添加Servlet依赖
<dependency>  
    <groupId>jakarta.servlet</groupId>  
    <artifactId>jakarta.servlet-api</artifactId>  
    <version>6.0.0</version>  
    <scope>provided</scope>  
</dependency>

4.在src/main目录下创建webapp文件夹

5. 手动将模块部署到tomcat上
   1.打包项目 -->在idea终端运行 mvn cleanpackage
   2.复制war到tomcat的webapps文件夹中 -->apache-tomcat-10.1.50/webapps/pro04-module-web.war
   3.启动tomcat -->进入到apache-tomcat-10.1.50/bin 双击打开start.bat
   4.访问项目：--> 浏览器搜索 http://localhost:8080/pro04-module-web

```


# 2.Maven生命周期

 Maven生命周期 == Maven构建项目的完整流程

### 2.1 Maven的3大生命周期

 - clean : 清理项目
 - default : 编译、测试、打包、部署
 - site : 生成项目文档