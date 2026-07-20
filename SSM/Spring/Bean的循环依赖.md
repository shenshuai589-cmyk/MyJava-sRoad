### 1.单例和set注入

什么叫循环：
- 例：丈夫类中有妻子类，妻子类中有丈夫类
1. 创建丈夫类
```java
package com.powernode.spring6.bean;  
  
public class Husband {  
    private String name;  
    private Wife wife;  
  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getName() {  
        return name;  
    }  
  
    public void setWife(Wife wife) {  
        this.wife = wife;  
    }  
    @Override  
    public String toString() {  
        return "Husband{" +  
                "name='" + name + '\'' +  
                ", wife=" + wife.getName() +  
                '}';  
    }  
}
```
2. 创建妻子类
```java
package com.powernode.spring6.bean;  
  
public class Wife {  
  
    private String name;  
  
    private Husband husband;  
  
    public void setName(String name) {  
        this.name = name;  
    }  
    public String getName() {  
        return name;  
    }  
  
    public void setHusband(Husband husband) {  
        this.husband = husband;  
    }  
  
    @Override  
    public String toString() {  
        return "Wife{" +  
                "name='" + name + '\'' +  
                ", husband=" + husband.getName() +  
                '}';  
    }  
}
```
3. 编写xml文件
```xml
<bean id="husband" class="com.powernode.spring6.bean.Husband" scope="singleton">  
    <property name="name" value="小哥哥"/>  
    <property name="wife" ref="wife"/>  
</bean>  
<bean id="wife" class="com.powernode.spring6.bean.Wife" scope="singleton">  
    <property name="name" value="小姐姐"/>  
    <property name="husband" ref="husband"/>  
</bean>
```
4. 编写测试代码
```java
@Test  
public void testCirculator() {  
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");  
    Husband husband = applicationContext.getBean("husband", Husband.class);  
    System.out.println(husband);  
      
    Wife wife = applicationContext.getBean("wife", Wife.class);  
    System.out.println(wife);  
  
}

运行结果：
Husband{name='小哥哥', wife=小姐姐}
Wife{name='小姐姐', husband=小哥哥}
```