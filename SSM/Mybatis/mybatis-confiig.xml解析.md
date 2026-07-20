```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<!-- configuration 表示mybatis-config.xml的根标签是configuration -->  
<!DOCTYPE configuration  
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"  
        "http://mybatis.org/dtd/mybatis-3-config.dtd">  
<configuration>  
<!--    environments表示配置的环境-->  
    <!--    default表示默认使用环境,可以写其他environment的id值 -->  
    <environments default="mybatisDB">  
<!--   environment表示每一个环境，一个environment对应一个database-->  
<!--   这个是mybatis的一个环境,连接的数据库是powernode-->  
<!--   一个环境environment对应一个SqlSessionFactory-->  
        <environment id="powernodeDB">  
            <transactionManager type="JDBC"/>  
            <dataSource type="POOLED">  
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>  
                <property name="url" value="jdbc:mysql://localhost:3306/powernode"/>  
                <property name="username" value="root"/>  
                <property name="password" value="250712"/>  
            </dataSource>  
        </environment>  
 <!--        这个是mybatis的一个环境,连接的数据库是mybatis-->  
        <environment id="mybatisDB">  
            <transactionManager type="JDBC"/>  
            <dataSource type="POOLED">  
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>  
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>  
                <property name="username" value="root"/>  
                <property name="password" value="250712"/>  
            </dataSource>  
        </environment>  
    </environments>  
    <mappers>  
        <mapper resource="CarMapper.xml"/>  
    </mappers>  
</configuration>
```

**1. !DOCTYPE configuration 的解析**

```text
configuration表示的是mybatis-config.xml的根目录是configuration标签

<configuration></configuration>
```

**2. environments default="mybatisDB"/**

```text
environments:表示配置的环境

一个mybatis-config.xml文件中只能有一对<environments></environments>标签
<environments>标签下可以有多个<envoronment></envoronment>标签
default：表示默认使用的环境，可以填environment中的id值
```

**3. environment id="powernodeDB"**

```text
environment:表示一个环境，一个环境对应一个数据库（database）
一个环境environment对应一个SqlSessionFactory 

id：表示该环境的名称，可以用来区分每个环境
```

**4. transactionManager type="JDBC" 和 dataSource type="POOLED"**

```text
transactionManager：表示事务管理，就是用来管理事务的
type:表示使用的那种事务管理，有两个可选值：
1.jdbc :表示使用mybatis自身的事务管理，本质上就是封装了jdbc的事务
2.managed : 表示mybatis自己不管理事务，交给第三方管理例如spring

dataSource ：表示数据源
dataSource的作用：
为程序提供Connection对象，所有数据库连接池也就是一个数据库
常见的数据库连接池：
1.druid --> 德鲁伊
2.c3p0
3.dbcp
type:用来指定数据源的类型
有三个值：
1.UNPOOLED:不是用数据库连接池技术，每一次请求都创建一个新的connection对象
2.POOLED:使用mybatis自己实现的数据库连接池
3.JNDI:集成其他第三方的数据库连接池
```

**5. property标签**

```
<property name="driver" value="com.mysql.cj.jdbc.Driver"/>  
<property name="url" value="jdbc:mysql://localhost:3306/powernode"/>  
<property name="username" value="root"/>  
<property name="password" value="250712"/> 


1. <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
   这个是一个固定配置，每一个环境中的都一样
2. <property name="url" value="jdbc:mysql://localhost:3306/powernode"/>
   这个是用来配置url的，格式为: 
   jdbc:mysql:// 主机号:端口号/数据库名
3. <property name="username" value="root"/>
   这个是用来配置用户名的
4.<property name="password" value="250712"/> 
	这个是用来写用户名对应的密码的
```

### 写一个测试程序对environment进行测试

```java
public class ConfigurationTest {  

/*
build()方法中有两个参数
第一个参数：表示输入流，输入流的参数写的是对应的mybatis-config.xml的地址
第二个参数：表示你要使用的是那个环境，也就是你要测试的database，不写则是environments中default中的那个database

*/

/*
第一个测试程序：
我们默认没写build（）方法中的第二个参数，表示对默认的database进行操作，也就是id = mybatisDB，对应的数据库是：mybatis
*/
    @Test  
    public void testEnvironment() throws Exception {  
        SqlSessionFactoryBuilder  sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();  
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"));  
        SqlSession sqlSession = sqlSessionFactory.openSession();  
        int count = sqlSession.insert("insertCar");  
        System.out.println(count);  
        sqlSession.commit();  
        sqlSession.close();  
    }
/*
第二个测试程序：
我们写了build（）方法中的第二个参数，为id = powernodeDB对应的环境，也就是对powernode数据库进行操作。
*/
    @Test  
    public void testEnvironment1() throws Exception {  
        SqlSessionFactoryBuilder  sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();  
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"), "powernodeDB");  
        SqlSession sqlSession = sqlSessionFactory.openSession();  
        int count = sqlSession.insert("insertCar");  
        System.out.println(count);  
        sqlSession.commit();  
        sqlSession.close();  
  
    }  
}

```

## 写一个测试程序对UNPOOLED和POOLED进行测试

1. type = "UNPOOLED"

```java
public class ConfigurationTest {  
    @Test  
    public void testDataSource() throws Exception {  
        SqlSessionFactoryBuilder  sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();  
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"));  
        SqlSession sqlSession = sqlSessionFactory.openSession();  
        sqlSession.insert("car.insertCar");  
        sqlSession.commit();  
        sqlSession.close();  
  
  
        System.out.println("-----------------------------------");  
        SqlSession sqlSession1 = sqlSessionFactory.openSession();  
        sqlSession1.insert("car.insertCar");  
        sqlSession1.commit();  
        sqlSession1.close();  
    }
```

```test

我们先设置dataSource的type="UNPOOLED",那么我们看测试代码的结果的连接池地址：
sqlSession的connection：
Connection [com.mysql.cj.jdbc.ConnectionImpl@a4b2d8f]

sqlSession1的地址为：
Connection [com.mysql.cj.jdbc.ConnectionImpl@4cc6fa2a]

显然可见是两个不同的connection

```

```text
我们现在设置dataSource的type="POOLED",那么我们看测试代码的结果的连接池地址：

sqlSession的connection：
Created connection 201274566.
Connection [com.mysql.cj.jdbc.ConnectionImpl@bff34c6]

sqlSession1的地址为：
connection 201274566 from pool.
Connection [com.mysql.cj.jdbc.ConnectionImpl@bff34c6]
```
1.  UNPOOLED
![UNPOOLED|648](059.png)


2. POOLED
![POOLED|656](057.png)

### POOLED的其他参数

1. poolMaximumActiveConnections
```
连接池当中同一时刻最多的连接数，默认值10
```

![poolMaximumActiveConnections|556](061.png)
我们现在对poolMaximumActiveConnections进行配置，连接数量为3个，进行测试：

```java
    @Test  
    public void testPoolMaximumActiveConnections() throws IOException {  
        for (int i = 0; i < 4; i++) {  
            SqlSession sqlSession = SqlSessionUtil.openSession();  
            sqlSession.insert("car.insertCar");  
//            sqlSession.commit();  
        }  
    }
```

那么此时我们进行测试：
![测试结果|525](062.png)
```
我们发现，三个connection对象正常开启，但是到第四个他在等待，那他为什么要等待呢？

我们的poolMaximumActiveConnections 的value = "3",那么也就是的同一时刻连接对象最多就是三个，只有等上面的提交事务了才可以进行下面的连接。
```

我们发现上面图片中的绿色方框中有20000，那么这代表什么意思呢，是否可以进行修改呢？

2. poolTimeToWait
```text
每隔n秒打印日志，并尝试获取连接对象，默认值20000

那么如果我们想要修改默认时间，我们则需要进行手动配置poolTimeToWait的value值
```
![poolTimeToWait](063.png)
运行结果：
![poolTimeToWait](064.png)

3. properties
![properties](065.png)

我们为了提高代码的灵活性，提出了properties标签进行配置
```text
<properties>  
    <property name="jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>  
    <property name="jdbc.url" value="jdbc:mysql://localhost:3306/powernode""/>  
    <property name="jdbc.username" value="root"/>  
    <property name="jdbc.password" value="250712"/>  
</properties>

这里的占位符为${}，不是#{}

<property name="driver" value="${jdbc.driver}"/>  
<property name="url" value="${jdbc.url}"/>  
<property name="username" value="${jdbc.username}"/>  
<property name="password" value="${jdbc.password}"/>
```
我们要properties的方式统一配置property信息，方便管理也方便统一修改，但是这样的方式还是不方便，所有我们推进了jdbc.properties文件进行配置
![jdbc.properties|542](066.png)
![jdbc.properties|542](067.png)
