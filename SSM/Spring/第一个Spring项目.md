### 1.创建模块
![spring6-002-first|354](SSM/Spring/image/002.png)
### 2.配置pom文件
引入spring-context依赖
```xml
<!--    依赖-->  
    <dependencies>  
<!--        spring context-->  
<!--        当引入spring-context依赖之后，表示将spring的基础依赖引入了-->  
        <dependency>  
            <groupId>org.springframework</groupId>  
            <artifactId>spring-context</artifactId>  
            <version>6.2.11</version>  
        </dependency>  
<!--        junit-->  
        <dependency>  
            <groupId>junit</groupId>  
            <artifactId>junit</artifactId>  
            <version>4.13.2</version>  
            <scope>test</scope>  
        </dependency>  
    </dependencies>
```
![引入依赖|610](SSM/Spring/image/003.png)
### 3.写bean类和配置spring核心配置文件
- bean类
![User.class|569](SSM/Spring/image/004.png)
- **spring.xml配置文件**
```xml
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
  
    <!--  
        id: bean的唯一标识  
        class: java类的全限定类名  
     -->  
    <bean id="UserBean" class="com.powernode.bean.User"/>  
</beans>
```
### 4.写test代码
```java
@Test  
public void testFirstSpring(){  
    //第一步:获取Spring容器对象  
        // ApplicationContext:应用上下文(应用环境),其实就是spring容器  
        /*  
         ClassPathXmlApplicationContext的形参填写的是spring.xml文件的路径,  
         由于当前spring.xml文件就是在resources根路径下,所以直接写文件名就行  
         */    
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");  
    //第二步:根据bean的id从Spring容器中获取这个对象  
    Object userBean = applicationContext.getBean("UserBean");  
    Object userDaoBean = applicationContext.getBean("userDaoBean");  
    System.out.println(userDaoBean);  
    System.out.println(userBean);  
  
}
```