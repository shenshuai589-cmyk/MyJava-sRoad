第一步：在webapps目录下新建一个目录，起名crm（项目的名称）
- crm就是这个webapp的跟

第二步：在webapp的根目录下新建一个目录：WEB-INF
- 注意：这个目录的名字是Servlet规范中的规范，必须全部大写，必须一模一样

第三步：在WEB-INF目录下新建一个目录：classes
- 注意：这个目录的名字必须全部小写的classes。这也是Servlet规范中规定的。另外这个目录下一定存放的是java程序编译之后的class文件（这里存放的是字节码文件）
第四步：在WEB-INF目录下新建一个目录：lib
- 注意这个目录不是必须的，但如果一个webapp需要第三方的jar包的话，这个jar包要放在这个lib目录下，这个目录的名字也不能随便写，必须全部小写的lib。例如java语言连接数据库需要数据库的驱动jar包，jar包必须放在lib目录下。

第五步：在WEB-INF目录下新建一个文件：web.xml
- 注意：这个文件是必须的，这个文件名必须叫web.xml。这个文件必须放在这里。一个合法的webapp，web.xml文件是必须的，这个web.xml文件就是一个配置文件，在这个配置文件里描述了请求路径和Servlet类之间的对照关系。
- 这个文件最好从其他wenapp中拷贝，最好不要手写没必要
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                      https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
  version="6.0"
  metadata-complete="true">
</web-app>

```

第六步：编写一个java程序，这个小java程序不能随意开发，这个java小程序必须实现Servlet接口。
- 这个Servlet接口不在jdk中。（因为servlet不是javase，servlet是javaee，是另外的一套类库）
- Servlet接口（Servlet.class文件）是Oracle提供的。
- Servlet接口是JavaEE的规范中的一员
- Tomcat服务器实现了Servlet规范，所以Tomcat服务器也需要使用Servlet接口。Tomcat服务器的CATALINA\lib目录下有一个servlet-api.jar，解压这个Servlet-api.jar。解压这个servlet-api.jar之后你会看见一个Servlet.class文件
第七步：编写java文件（HelloServlet）并编译HelloServlet文件

第八步：将编译好的.class文件放到WEB-INF\classes目录下
第九步：在web.xml文件中编写配置信息，让请求路径和Servlet类关联在一起
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                      https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
  version="6.0"
  metadata-complete="true">
	<!--Servlet描述信息-->
	<servlet>
		<servlet-name>suibianxie</servlet-name>
		<servlet-class></servlet-class>
	</servlet>
    <!--servlet映射信息-->
	<servlet-mapping>
		<servley-name>suibianxie</servlet-name>
		<url-pattern>/dasds/ddddFas/dadfa/sa/afaf</url-pattern>
	</servlet-mapping>
</web-app>

```
第十步：启动Tomcat
第十一步：打开浏览器，在浏览器地址栏输入：
http://127.0.0.1:8080/crm/dasds/ddddFas/dadfa/sa/afaf
web.xml文件中的两个《servlet-name》里面的内容必须一样

## 解决tomcat在命令行中的乱码问题

![解决乱码问题](JavaWeb/图片/002.png)
将里面的UTF_8改成GBK
![解决乱码问题](JavaWeb/图片/003.png)

```
webapproot
	|--------WEB-INF
		|--------lib
		|--------web.xml
	|--------html
	|--------css
	|--------javaScript
	|--------image
	
```

**Servlet连接数据库**

```
package com.bjpowernode.servlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
public class StudentServlet implements Servlet{
	public void init(ServletConfig config)throws ServletException{
		
	}
	public void service(ServletRequest request,ServletResponse response) 
		throws ServletException,IOException{
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			try{
				Class.forName("com.mysql.cj.jdbc.Driver");
				String url = "jdbc:mysql://localhost:3306/powernode";
				String user = "root";
				String password = "250712";
				conn = DriverManager.getConnection(url,user,password);
				//获取预编译的数据库操作对象
				String sql = "select no,name from t_student";
				ps = conn.prepareStatement(sql);
				//执行sql
				rs = ps.executeQuery();
				//处理查询结果集
				while(rs.next()){
					String no = rs.getString("no");
					String name = rs.getString("name");
					System.out.println(no+", "+name);
					out.println(no + ", " + name + "<br>");
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(rs != null){
					try{
						rs.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(ps != null){
					try{
						ps.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(conn != null){
					try{
						conn.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			}
	}
	public void destroy(){
		
	}
	public String getServletInfo(){
		return "";
	}
	public ServletConfig getServletConfig(){
		return null;
	}
}
jdbc六步	
```

## 使用idea开发servlet

第一步：new一个project（可以在空工程中新建module）
第二步：新建一个module(普通javase)
第三步：让模块变成javaee模块：选中当前模块，ctrl+shift+a，搜索Add Framework Support，然后点击Web Application并勾选 `Create web.xml`。
**重点：需要注意的：在IDEA工具中根据Web Application模块生成的目录中有一个web目录，这个目录就相当于我们之前的oa目录**
第四步：可以删除index.jsp文件
第五步：编写Servlet（StudentServlet类）
StudentServlet implements Servlet发现报错了，
点击project Structure 找到module，右边有个depency点击添加即可
![添加依赖|550](JavaWeb/图片/005.png)

第六步：重写Servlet接口中的方法，并在service方法中编写业务代码
第七步：在WEB-INF下新建一个lib并把连接数据库的jar加进去
第八步：在web.xml文件中完成StudentServlet类的注册。
第九步：给一个html页面，在html中给个超链接，用户点击这个超链接，发送请求，Tomcat执行后台StudentServlet
第十步:让IDEA工具关联tomcat服务器，关联的过程中将webapp部署到Tomcat上 ctrl+shift+A搜索Edit Configurations
![tomcat|292](JavaWeb/图片/006.png)

![tomcat|574](JavaWeb/图片/007.png)
![tomcat|570](JavaWeb/图片/008.png)
![tomcat|573](JavaWeb/图片/009.png)
第十一步：启动tomcat服务器