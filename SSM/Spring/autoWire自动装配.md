
### 1.根据名称自动装配（byName）
第一步:写实体类
![实体类|533](SSM/Spring/image/023.png)
第二步：写xml文件
手动书写xml:
![手动写|518](SSM/Spring/image/024.png)
根据名称自动装配：
```
<bean id="orderDao" class="com.powernode.spring6.dao.impl.OrderDao"/>  
  
<bean id="orderService" class="com.powernode.spring6.service.impl.OrderService" autowire="byName"/>
```
注意：根据name自动装配，那么被注入的对象的bean的id不可以随便写，必须是set方法set后第一个字母小写。

### 2.根据类型自动装配(byType)
第一步：书写实体类
![实体类|591](SSM/Spring/image/025.png)
第二步：编写xml文件
![xml文件|594](SSM/Spring/image/026.png)
注意：
```
autowire自动装配是根据set方法注入的，构造方法不可以
在一个配置文件中，一个实体类的配置只能有一个
```

