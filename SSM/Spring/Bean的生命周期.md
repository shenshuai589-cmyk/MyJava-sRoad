
## 1.粗略的五步

1. 实体化bean
```java

package com.powernode.spring6.bean;  
  
public class User {  
      
    private String name;  
  
    public void setName(String name) {  
        this.name = name;  
    }  
      
    public User(){  
        System.out.println("丫够躁的!!!");  
    }  
      
}
```
2. 给属性赋值
```java
private String name;  
  
public void setName(String name) {  
    this.name = name;  
}
```
3. 第三步初始化bean（手动添加一个初始化方法）
```java
public void initBean(){  
    System.out.println("初始化一下子!!!");  
}
```
4. 使用bean
```java
@Test  
public void testLifeCycleFive(){  
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("lifeCycle.xml");  
    User user = applicationContext.getBean("user", User.class);  
    //调用bean  
    System.out.println("第四步"+user);
```

5. 销毁bean（手动添加一个destroy方法）
```java
public void destroyBean(){  
    System.out.println("销毁一下子!!!");  
}
```

注意事项：
- 销毁bean需要调用ClassPathXmlApplicationContext中的close方法，那么我们就要对ApplicationContext转型
- 需要手动在xml文件中的bean标签给出init-method和destrpy-method方法

## 2.生命周期的七步

新增的两步分别是在init方法前和方法后执行的，需要创建一个后置处理器对象实现BeanPostProcessor接口
```java
package com.powernode.spring6.bean;  
  
import org.springframework.beans.BeansException;  
import org.springframework.beans.factory.config.BeanPostProcessor;  
  
public class LogBeanPostProcessor implements BeanPostProcessor {  
    @Override  
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {  
        System.out.println("执行后置处理器的before方法");  
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);  
    }  
  
    @Override  
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
        System.out.println("执行后置处理器的after方法");  
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);  
    }  
}
```
- 配置到xml文件中
```xml
<bean class="com.powernode.spring6.bean.LogBeanPostProcessor"/>
```
- 运行测试代码
```txt

丫够躁的!!!
执行后置处理器的before方法
初始化一下子!!!
执行后置处理器的after方法
第四步com.powernode.spring6.bean.User@2a5c8d3f
销毁一下子!!!
```

## 3.生命周期十步


