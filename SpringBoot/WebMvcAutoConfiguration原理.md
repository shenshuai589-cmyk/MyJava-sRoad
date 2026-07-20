
# 1.  WebMvcAutoConfiguration的生效条件

1. AutoConfiguration
```java
@AutoConfiguration(  
    after = {DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class},  
    afterName = {"org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration"}  
    
指定了 WebMvcAutoConfiguration 在DispatcherServletAutoConfiguration、TaskExecutionAutoConfiguration、ValidationAutoConfiguration这几个自动配置类加载完之后加载
)
```
2. ConditionalOnWebApplication
```java
@ConditionalOnWebApplication(  
    type = Type.SERVLET  
)
这个 条件表示只有当应用程序是一个基于Servlet的Web应用程序时，该配置才生效
```
3. ConditionalOnClass
```java
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
这个条件表示：要想使WebMvcAutoConfiguration生效，必须存在Servlet、DispatcherServlet、WebMvcConfigurer这及各类
```
4. ConditionalOnMissingBean
```java
@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})

在IoC容器当中不允许出现WebMvcConfigurationSupport这样的bean，也就是说不能出现EnableWebMvc 这个注解，一旦出现，那么WebMvcAutoConfiguration失效
```


# 2.WebMvc自动配置生效后的两个Filter Bean

1. 引入HiddenHttpMethodFilter Bean
> 这个过滤器是专门处理Rest请求。GET、POST、PUT、DELETE请求
```java
@Bean  
@ConditionalOnMissingBean({HiddenHttpMethodFilter.class})  
@ConditionalOnBooleanProperty({"spring.mvc.hiddenmethod.filter.enabled"})  
OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {  
    return new OrderedHiddenHttpMethodFilter();  
}
```
2. 引入FormContentFilter Bean
```
这个过滤器是用来处理Http请求的一个过滤器，特别是针对PUT和DELETE请求。这个过滤器的主要作用是在处理PUT和DELETE请求时，确保如果请求体中有表单格式的数据，这些数据会被正确解析并可用
```

# 3.WebMvcConfigurer接口

1. 使用WebMvc自动配置的三种情况：
```
1.全部使用springboot默认配置
2.全部不用springboot默认配置 ----> 通过添加@EnableWebMvc注解
	3.全部使用springboot默认配置自己也可以扩展配置 ----> 自定义一个类实现WebMvcConfigurer接口并在该类上添加@Configuration注解
```