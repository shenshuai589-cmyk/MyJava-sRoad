```java
@Test  
public void testScope(){  
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-scope.xml");  
    SpringBean scopeBean = applicationContext.getBean("scopeBean", SpringBean.class);  
    System.out.println(scopeBean);  
    SpringBean scopeBean1 = applicationContext.getBean("scopeBean", SpringBean.class);  
    System.out.println(scopeBean1);  
    SpringBean scopeBean2 = applicationContext.getBean("scopeBean", SpringBean.class);  
    System.out.println(scopeBean2);  
}
```

```
运行结果：

com.powernode.spring6.bean.SpringBean@36fc695d
com.powernode.spring6.bean.SpringBean@36fc695d
com.powernode.spring6.bean.SpringBean@36fc695d
可以看出，spring的作用域是单例的，如果想修改成多例的，则需要在bean标签中添加scope="prototype"
那么此时再次运行：
com.powernode.spring6.bean.SpringBean@5bf0d49
嘻嘻嘻
com.powernode.spring6.bean.SpringBean@5b7a5baa
嘻嘻嘻
com.powernode.spring6.bean.SpringBean@776aec5c
```

除此之外，scope属性还有其他的值，例如request或session
```

```

