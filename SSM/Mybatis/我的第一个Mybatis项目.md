# 1.在Navicat中准备数据库数据

## 1.1.创建表结构

![数据库准备](SSM/Mybatis/图片/我的第一个Mybatis/001.png)

## 1.2.添加数据

![添加表数据](SSM/Mybatis/图片/我的第一个Mybatis/002.png)

# 2.在IDEA中创建项目

![idea项目](SSM/Mybatis/图片/我的第一个Mybatis/003.png)

# 3. 开发步骤

## 3.1 添加打包方式

![添加打包方式](SSM/Mybatis/图片/我的第一个Mybatis/004.png)

## 3.2.引入依赖

```
- mybatis依赖
- mysql驱动依赖
  
```

![配置依赖](SSM/Mybatis/图片/我的第一个Mybatis/005.png)

## 3.3 编写mybatis核心配置文件:mybatis-config.xml

### 注意：

	第一：这个文件名不是必须叫做mybatis-config.xml,可以用其他的名字。只是大家都采用这个名字。
	第二：这个文件存放的位置也不是固定的，可以随意，但一般情况下，会放到类的根路径（resources目录）下。


![mybatis-config.xml](SSM/Mybatis/图片/我的第一个Mybatis/006.png)

	在mybatis-config.xml文件中添加这么一段代码（内容需要修改）
```html
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
  </mappers>
</configuration>
```

## 3.4修改mybatis-config.xml文件中的部分代码

==修改前==
![mybatis-config.xml文件修改前](SSM/Mybatis/图片/我的第一个Mybatis/008.png)
==修改后==
![mybatis-config.xml修改后](SSM/Mybatis/图片/我的第一个Mybatis/007.png)

## 3.5 编写xxxxMapper.xml文件

**在这个配置文件中编写SQL语句**

==注意点：==
1.这个文件名不是固定的
2.放的位置也不是固定的，
我们的表叫做t_car表，那么我们给这个文件起名CarMapper.xml,把它暂时放在类的根路径下（resources目录下）
![CarMapper.xml](SSM/Mybatis/图片/我的第一个Mybatis/009.png)

下面我们从==mybatis中文官网==中复制相关代码，添加到CarMapper.xml中
![CarMapper.xml添加内容](SSM/Mybatis/图片/我的第一个Mybatis/010.png)

修改CarMapper.xml文件中的部分代码
![修改CarMapper.xml](SSM/Mybatis/图片/我的第一个Mybatis/011.png)
==通过mybatis-config.xml文件中的mapper标签的resource属性关联CarMapper.xml文件的路径==
<span style="color:red">resource属性自动会从类的根路径下开始查找资源</span>
![关联CarMapper.xml](012.png)

## 3.6.编写Mybatis程序

**使用mybatis的类库，编写mybatis程序，连接数据库，做增删改查就行了。**

```text
在Mybatis中，负责执行sql语句的对象叫什么呢？
  叫做SqlSession

SqlSession是专门用来执行SQL语句的，是一个java程序和数据库之间的一次会话。

要想获取SqlSession对象，需要先获取SqlSessionFactory对象，通过SqlSessionFactory工厂来生产SqlSession对象。

怎么获取SqlSessionFactory对象呢？
	首先需要SqlSessionFactoryBuilder对象
	通过SqlSessionFactoryBuilder对象的build方法，来获取一个SqlSessionFactory对象。

那么Mybatis的核心对象包括：
 1.SqlSessionFactoryBuilder
 2.SqlSessionFactory
 3.SqlSession	

SqlSessionFactoryBuilder -> SqlSessionFactory -> SqlSession
```


运行步骤：

1. 创建MyBatisIntroductionTest.java
![创建MyBatisIntroductionTest](015.png)

2. 编写代码
<span style="color:orange">创建的输入流的形参，填入的是mybatis-config.xml的地址</span>
![代码编写](014.png)


```java
package com.powernode.mybatis.test;  
  
import org.apache.ibatis.io.Resources;  
import org.apache.ibatis.session.SqlSession;  
import org.apache.ibatis.session.SqlSessionFactory;  
import org.apache.ibatis.session.SqlSessionFactoryBuilder;  
  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
  
public class MyBatisIntroductionTest {  
    public static void main(String[] args) throws IOException {  
        //获取SqlSessionFactoryBuilder对象  
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();  
        // 获取SqlSessionFactory对象(通过SqlSessionFactoryBuilder中的build方法)  
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");  
        SqlSessionFactory sqlsessionfactory = sqlSessionFactoryBuilder.build(is);  
  
        /*  
        *   sqlSessionFactoryBuilder.build(InputStream InputStream),        *   由于build方法中的参数是一个InputStream对象，所以我们要先创建一个InputStream对象  
        *   来读取MyBatis的核心配置文件mybatis-config.xml  
        *   我们可以利用Resources类来获取InputStream对象，  
        *   Resources类中有一个静态方法getResourceAsStream(String str)  
        *   这个方法的参数是一个字符串，表示要读取的资源文件的路径，默认是从类的根路径下开始查找资源  
        * */        // 获取SqlSession对象  
        SqlSession sqlSession = sqlsessionfactory.openSession();  
  
        //执行sql语句  
        //insert方法中的参数是要执行语句的id，  
        int count = sqlSession.insert("insertCar");// 返回值是影响数据库表当中的记录条数  
        System.out.println("影响数据库表当中的记录条数:" + count);  
//        sqlSession.commit();   
    }  
}
```

**注意事项**：

1. sqlSession对象中的方法insert()的参数记录的是要执行语句的id
![id](016.png)
---
![代码中的id](017.png)

2. sqlSession对象是不会自动提交事务的，所以在代码的最后我们要手动提交事务
![手动提交事务](018.png)

## 3.7 运行代码
![运行代码](019.png)
![运行代码](020.png)

# 4.第一个Mybatis程序的小细节

1.mybatis中sql语句的结尾";"可以省略

![细节1](021.png)

2.Resources.getResourceAsStream
```
小技巧：以后凡是遇到resource这个单词，大部分情况下，这种加载资源的方式就是从类的根路径下开始加载

Resources.getResourceAsStream("mybatis-config.xml")
采用这种方式，从类路径当中加载资源，项目的移植性很强，项目从windows系统移植到linux系统不用修改代码，因为这个资源一直在类路径下。

我们也可以自己添加输入流，
InputStream is = new FileInpitStream("mybatis-config.xml"),
但是这样写文件的路径项目就要写死了，那么代码的可移植性就会变差，不推荐。
InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("mybatis-config.xml")

那么我们还可以通过类加载器的方式获取sqlSessionFactory:


```

## 4.1.mybatis的事务管理机制

```
mybaits的事务管理是通过mybatis-config.xml中的
<transactionManager type="JDBC"/>标签来进行事务管理

type的属性值包括两个：
	1.JDBC
	2.MANAGED
	type的值只能选这两个，但是不区分大小写

因为type的值有两个，所以mybatis的事务管理机制可以有两个
1. JDBC事务管理器：
   mybatis框架自己管理事务，自己采用原生的jdbc代码去管理事务
	   conn.setAutoCommit(false);开启事务
	   ...业务处理...
	   conn.commit()手动提交事务
2. MANAGED事务管理器：
   mybatis不在负责事务的管理了。事务管理交给其他容器来负责，例如：Spring

```

# 5.完整的第一个mybatis项目

```java

package com.powernode.mybatis.test;  
  
import org.apache.ibatis.io.Resources;  
import org.apache.ibatis.session.SqlSession;  
import org.apache.ibatis.session.SqlSessionFactory;  
import org.apache.ibatis.session.SqlSessionFactoryBuilder;  
  
import java.io.IOException;  
  
public class MybatisComplete {  
    public static void main(String[] args) {  
        SqlSession sqlSession = null;  
        try {  
            // 1.获取sqlSessionFactoryBuilder对象  
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();  
            //2. 通过sqlSessionFactoryBuilder的build方法获取sqlSessionFactory对象  
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"));  
            // 3.获取sqlSession  
            /*            * 注意：  
            * openSession()不要填true，不然会不开启事务，导致自动提交哦  
            * */            sqlSession = sqlSessionFactory.openSession();  
            int count = sqlSession.insert("insertCar");  
            // 影响事务的数量  
            System.out.println("完成sql语句数为："+count);  
            // 提交事务  
            sqlSession.commit();  
        } catch (Exception e) {  
            if(sqlSession!=null){  
                sqlSession.rollback();  
            }  
            e.getStackTrace();  
        }finally {  
            if(sqlSession!=null){  
                //关闭会话（释放资源）  
                sqlSession.close();  
            }  
        }  
    }  
}
```






#  ==开发经验==

##  resources目录的细节

1. 放在resources目录当中的，一般都是资源文件，配置文件。直接放到resources目录下的资源，等同于放到了类的根路径下。

## 从XML中构建SqlSessionFactory

1. 在Mybatis中一定有一个很重要的对象，这个对象是：SqlSessionFactory对象。

2. SqlSessionFactory对象的创建需要XML

3. XML是什么
	它是一个配置文件

4. mybatis中有两个主要的配置文件

==mybatis-config.xml和xxxxMapper.xml==

	其中一个是：mybatis-config.xml，这是核心配置文件，主要配置连接数据库的信息等。(一般只有一个表)

	另一个是：xxxxMapper.xml，这个文件是专门用来编写SQL语句的配置文件(一张表一个)


## SqlSessionFactoryBuilder、sqlSessionFactory、sqlSession的获取步骤细节


在编写代码中，先获取的是SqlSessionFactoryBuilder对象，
再通过SqlSessionFactoryBuilder对象的build()方法获取sqlSessionFactory对象，
那么在使用build方法的时候，形参需要传递的是一个输入流，所以先创建一个输入流，
那么我们使用Resources类中的getResourceAsStream()方法获取输入流，
getResourceAsStream()形参传递的则是mybatis-config.xml的地址，获取sqlSessionsFactory
通过sqlSessionFactory对象中的openSession()方法获取sqlSession

```
/*  
*   sqlSessionFactoryBuilder.build(InputStream InputStream),  
*   由于build方法中的参数是一个InputStream对象，所以我们要先创建一个InputStream对象  
*   来读取MyBatis的核心配置文件mybatis-config.xml  
*   我们可以利用Resources类来获取InputStream对象，  
*   Resources类中有一个静态方法getResourceAsStream(String str)  
*   这个方法的参数是一个字符串，表示要读取的资源文件的路径，默认是从类的根路径下开始查找资源  
* */
  
```

## 执行sql语句的细节

通过sqlSession.insert()方法来执行sql语句，insert()方法的形参是要==执行的语句的id==,在xxxxMapper.xml文件中配置

![](017.png)



