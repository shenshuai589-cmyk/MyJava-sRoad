### 依赖注入
依赖注入实现了控制反转的思想
<span style="color:red">spring通过依赖注入的方式完成Bean管理</span>
<span style="color:red">spring管理说的是：Bean对象的创建，以及Bean对象中属性的赋值（Bean对象之间的关系维护）</span>

#### 1.set方法注入
![set方法注入|550](SSM/Spring/image/006.png)
只写set方法是没有用的，还会报空指针异常，需要在xml文件中配置相关信息

![xml文件配置|622](SSM/Spring/image/007.png)
```txt
注意：光写一个set方法还不够，必须得在xml文件中配置相关信息才可以使用：
- 配置dao:
<bean id="userDaoBean" class="com.powernode.spring6.dao.impl.UserDaoImplForMysql"/>  

- 配置service:
<bean id="userServiceBean" class="com.powernode.spring6.service.impl.UserServiceImpl">  
 
property              
name:属性名称，要求：必须是set方法中set后，第一个字母小写  
ref:引用类型，引用其他（要注入的）bean的id  
    <property name="userDaoImpl" ref="userDaoBean"/>  
</bean>
```

### 2.构造方法注入
第一步：创建bean类
![vipDao|573](SSM/Spring/image/008.png)
第二步：创建service类
![service|546](SSM/Spring/image/009.png)
第三步：编写xml配置相关信息
![vipDao|569](SSM/Spring/image/010.png)
注意：
```java
构造方法注入和set方法注入不同：
    <bean id="userDaoBean" class="com.powernode.spring6.dao.impl.UserDaoImplForMysql"/>  
    <bean id="vipDaoBean" class="com.powernode.spring6.dao.impl.vipDao"/>  
  
    <bean id="customerServiceBean" class="com.powernode.spring6.service.impl.customerService">  
        <constructor-arg index="0" ref="userDaoBean"/>  
        <constructor-arg index="1" ref="vipDaoBean"/>  
    </bean>
其中，构造方法注入使用constructor-arg标签：
	index="" :表示全参构造方法中的参数索引，第一个为0，第二个为1，以此类推  
	ref="":用来指向该索引对应bean的id值	
```
![有参构造](SSM/Spring/image/011.png)

### 生成内部bean
在property标签中嵌套bean标签叫做内部bean
```xml
   <bean id="orderServiceBean2" class="com.powernode.spring6.service.impl.OrderService">  
        <property name="orderDao">  
<!--在property标签中嵌套bean标签，这就是内部bean-->  
            <bean class="com.powernode.spring6.dao.impl.OrderDao"/>  
        </property>  
    </bean>

```