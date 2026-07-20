### 1.通过构造方法实例化

通过调用bean的无参数构造方法
1. 实体类：
```java
package com.powernode.spring6.bean;  
  
public class SpringBean {  
        public SpringBean() {  
            System.out.println("SpringBean对象被创建了");  
        }  
}
```
2. xml配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">  
  
    <bean id="SpringBean" class="com.powernode.spring6.bean.SpringBean"/>  
</beans>

```
3. 测试代码
```java
@Test  
public void testSpringBean(){  
     ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-bean.xml");  
     SpringBean springBean = applicationContext.getBean("SpringBean", SpringBean.class);  
     System.out.println(springBean);  
 }
```

### 2.通过简单工厂模式实例化
1. 创建实体类（Star）
```java
package com.powernode.spring6.bean;  
  
public class Star {  
    public Star(){  
        System.out.println("丫够燥的...");  
    }  
} 
```
2. 创建工厂实体类
```java
package com.powernode.spring6.bean;  
  
public class StarFactory {  
  
    public static Star getStar(){  
        return new Star();  
    }  
}
```
3. 编写xml文件
使用工厂进行实体化的时候，需要提供的两个东西：
 - 工厂实体类的全包名路径
 - 指定的方法
```xml
<!--    注意：使用简单工厂进行实体化的时候，需要在bean标签标明工厂实体类的全包名路径和指定方法-->  
    <bean id="factory" class="com.powernode.spring6.bean.StarFactory" factory-method="getStar"/>
```

### 3.通过factory-bean实例化
1. 创建实体类（Gun）
```java
package com.powernode.spring6.bean;  
  
public class Gun {  
    public Gun(){  
        System.out.println("GunGunGun");  
    }  
}
```
2. 创建具体实体类的工厂类（GunFactory）
```java
package com.powernode.spring6.bean;  
  
public class GunFacotry {  
  
    public Gun get(){  
        return new Gun();  
    }  
}
```
3. 编写xml文件
注意：需要配置两个\<bean>标签
第一个：需要配置id，以及class，class写的是具体工厂类的全包名路径
第二个：需要配置:
factory-bean:填写的是实体工厂类配合的bean的id
factory-method：工厂类的方法
```xml
<bean id="GunFactory" class="com.powernode.spring6.bean.GunFacotry"/>  
<bean id="gun" factory-bean="GunFactory" factory-method="get"/>
```

### 4.通过FacotryBean接口实例化

- ==注意:使用当前方法进行实例化的前提就是Factory类需要实现FactoryBean接口==
- 使用这种方式后，xml中就可以不用配置factory-bean和factory-method
1. 创建实体类（Person）
```java
package com.powernode.spring6.bean;  
  
public class Person {  
    public Person() {  
        System.out.println("我是个人，不是个东西!!!");  
    }  
}
```
2. 创建FactoryBean对象
```java
package com.powernode.spring6.bean;  
  
import org.springframework.beans.factory.FactoryBean;  
  
public class PersonFactoryBean implements FactoryBean<Person> {  
    @Override  
    public Person getObject() throws Exception {  
        return new Person();  
    }  
  
    @Override  
    public Class<?> getObjectType() {  
        return Person.class;  
    }  
  
//    是否是单例的：  
//      true为单例  false为多例  
    @Override  
    public boolean isSingleton() {  
        return true;  
    }  
}
```
3. 编写xml文件
```xml
<bean id="person" class="com.powernode.spring6.bean.PersonFactoryBean"/>
```
