**1.什么是Servlet生命周期**
- Servlet对象什么时候被创建
- Servlet对象什么时候被销毁
- Servlet对象创建了几个
- Servlet对象的生命周期表示 : 一个Servlet对象从出生到最后的死亡，整个过程是怎样的
**Servlet对象是有谁来维护的？**
- Servlet对象的创建，对象上方法的调用，对象最终的销毁，JavaWeb程序员是无权干预的
- Servlet对象的生命周期由Tomcat服务器（Web Server）全权负责
- Tomcat服务器通常我们又称为：WEB容器【Web Container】

**研究：服务器在启动的Servlet对象有没有被创建出来**（默认情况下）
- 在Servlet中提供一个无参构造，启动服务器的时候看看构造方法是否执行
- 经过测试得出结论：默认情况下，服务器在启动的时候Servlet对象并不会被实例化
- 这个设计师合理的。用户在没有发生请求的之前，如果提前创建出来所有的Servlet对象，必然是耗费内存的，并且创建出来的Servlet如果一直没有用户访问，显然是没必要先创建
- 怎么放服务器启动的时候创建Servlet对象呢？
	  - 在Servlet标签下添加<ｌｏａｄ－ｏｎ－ｓｔａｒｔｕｐ＞子标签，在该子标签中填写整数，整数值越小越先被创建出来 （优先级越高）

```
    <servlet>  
        <servlet-name>Aservlet</servlet-name>  
        <servlet-class>com.bjpowernode.javaWeb.servlet.Aservlet</servlet-class>  
		<!-- <load-on-startup>0</load-on-startup>-->  
    </servlet>
```

**Servlet生命周期**
- 默认情况下服务器启动是不会实例化Servlet对象的
- 用户发送一起请求（），控制台输出了以下内容：
```
Bservlet���췽��ִ����
Aservlet's init method execute
Aservlet's init method execute
```
- 根据以上输出内容得知结论：
  - 用户在发送第一次请求的时候Servlet对象被实例化（Bservlet的构造方法被执行了。并且执行的是无参构造方法）
  - Bservlet对象被创建出来之后。Tomcat服务器马上调用了BServlet对象的init方法。（init方法在被执行的时候，Bservlet对象已经存在了。已经被创建出来了）
  - 用户发送第一次请求的时候，init方法执行之后，Tomcat服务器马上调用Bservlet对象的service方法。
- 用户继续发送请求，控制台输出以下内容：
```
Aservlet's service method execute
```
- 根据上面输出的结果得知，用户在发送第二次或者第三次，或者第四次请求的时候，Servlet对象并没有新建，还是使用之前创建好的Servlet对象，直接调用该Servlet对象的service方法，这说明：
  - 第一：Servlet对象是单例的（单实例的，但是Servlet对象是单实例的，但是Servlet类并不符合单例模式。我们称之为假单例） 
  - 第二：无参构造方法、init方法只在第一次用户发送请求的时候执行。也就是说无参构造方法只执行一次，init方法也只会被Tomcat服务器调用一次。
  - 第三：只要用户发送一次请求：service方法必须会被tomcat服务器调用一次，请求100次，service方法就被调用100次
- 关闭服务器的时候，会打印以下内容：
```
Aservlet's destroy method execute
```
- 通过以上输出的内容，可以得出结论：
- Servlet的destroy方法只被Tomcat服务器调用一次
- destroy方法实在什么时候被调用的？
   - 在服务器关闭的时候。因为服务器关闭要销毁Bservlet对象的内存
   - 服务器在销毁Bservlet对象内存前，Tomcat服务器会自动调用Bservlet对象的destroy方法
- destroy方法调用的时候，对象销毁了还是没销毁？
  - 没有销毁，因为调用destroy方法需要Servlet对象，如果被销毁了，那么destroy方法就不会执行了  