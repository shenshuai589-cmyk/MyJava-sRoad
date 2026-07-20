## 1.给简单类型注入

第一步：编写bean实体类

![简单类型|554](SSM/Spring/image/012.png)
第二步：编写xml文件
==注意：给简单类型注入时，property里面写的就不是ref属性了，而是value属性了==
```
<!--    给简单类型注入-->  
    <bean id="UserBean" class="com.powernode.spring6.bean.User">  
        <property name="username" value="张三"/>  
        <property name="password" value="123456"/>  
        <property name="age" value="20"/>  
    </bean>
```

### 2.给数组注入

- .**1简单类型的数组**
创建bean实体类：
![实体类|551](SSM/Spring/image/013.png)
编写xml文件
![spring-array.xml](SSM/Spring/image/014.png)
==注意：给简单类型的数组注入时，用到的标签是\<array>标签，里面嵌套\<value>标签==

- **2.给引用数据类型的数组注入**
创建bean类
![woman|538](SSM/Spring/image/015.png)
![yuqianBean|460](SSM/Spring/image/016.png)
编写xml文件
![443](SSM/Spring/image/017.png)

## 3.给list和set注入
编写bean实体类
```java
package com.powernode.spring6.bean;  
import java.util.List;  
import java.util.Set;  
  
public class Person {  
    private List<String> name;  
    private Set<String> address;  
    public void setName(List<String> name) {  
        this.name = name;  
    }  
    public void setAddress(Set<String> address) {  
        this.address = address;  
    }  
    @Override  
    public String toString() {  
        return "Person{" +  
                "name=" + name +  
                ", address=" + address +  
                '}';  
    }  
}
```
编写xml文件
```xml
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
    <!--    list和set注入-->  
    <bean id="PersonBean" class="com.powernode.spring6.bean.Person">  
        <property name="name">  
            <list>  
                <value>张三</value>  
                <value>李四</value>  
                <value>王五</value>  
            </list>  
        </property>  
        <property name="address">  
            <set>  
                <value>北京</value>  
                <value>上海</value>  
                <value>广州</value>  
            </set>  
        </property>  
    </bean>  
</beans>
```
编写测试程序
```java
//给List和Set注入  
@Test  
public void testForListAndSet(){  
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-list-set.xml");  
    Person personBean = applicationContext.getBean("PersonBean", Person.class);  
    System.out.println(personBean);  
}
```
运行结果：
Person{name=[张三, 李四, 王五], address=[北京, 上海, 广州]}

## 4.给Map注入

```xml
<!--属性值-->
private Map<Integer, String> tel;
<!--xml配置信息-->
<property name="tel">  
    <map>  
        <entry key="1" value="110"/>  
        <entry key="2" value="120"/>  
        <entry key="3" value="119"/>  
    </map>  
</property>
```

## 5.给Properties注入
```xml 
<!--属性值-->
private Properties  properties;

<!--配置xml文件-->
<property name="properties">  
    <props>  
        <prop key="driver">com.mysql.cj.jdbc.Driver</prop>  
        <prop key="url">jdbc:mysql://localhost:3306/spring</prop>  
        <prop key="username">root</prop>  
    </props>  
</property>
```

## 6.注入特殊字符

特殊字符：`> < ' " &`
可以通过\<![CDATA[ ]]>实现
```xml
<bean id="mathBean" class="com.powernode.spring6.bean.MathBean">  
    <property name="result">  
        <value ><![CDATA[2 < 3]]></value>  
    </property>  
</bean>
```
==注意：使用\<![CDATA[ ]]>时，只能在value标签中使用==

## 7.P命名空间注入
第一步：在xml文件，\<beans>标签上添加P命名空间的相关信息
```xml
<beans xmlns="http://www.springframework.org/schema/beans"  
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"  
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
添加p命名空间的相关信息：如下

xmlns:p="http://www.springframework.org/schema/p"  
</beans>

```
第二步：编写xml文件
```xml
<bean id="birthBean" class="java.util.Date"/>  
<bean id="DogBean" class="com.powernode.spring6.bean.Dog" p:name="旺财" p:age="3" p:brith-ref="birthBean"/>
```
第三步：编写测试代码
```java
@Test  
public void testPSpace(){  
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-p.xml");  
    Dog dogBean = applicationContext.getBean("DogBean", Dog.class);  
    System.out.println(dogBean);  
}
```
运行结果：
Dog{name='旺财', age=3, brith=Sun May 17 16:59:38 GMT+08:00 2026}