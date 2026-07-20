### @SpringBootApplication

> SpringBootApplication的组成
```
	@SpringBootApplication  
	=  
	@Configuration  
	+  
	@EnableAutoConfiguration  
	+  
	@ComponentScan
```

#### 1. @Configuration

> 这个注解的作用是：告诉spring这是一个配置类，用来代替XML配置文件

**使用xml文件配置的时候**
```xml
<bean id="userService"  class="com.powernode.service.UserService"/>
```

**使用注解的时候**
```java
@Configuration  
public class SpringConfig {  
  
@Bean  
//@Bean 就相当于上面xml中配置的<bean id="userService"  class="com.powernode.service.UserService"/>
public UserService userService() {  
	return new UserService();  
	}  
}
```
那么他的执行流程就是
```txt
	发现@Configuration  
	↓  
	读取配置类  
	↓  
	执行@Bean方法  
	↓  
	创建对象  
	↓  
	放入IOC容器
```
#### 2. @ComponentScan

> ComponentScan的作用就是自动扫描指定包下的Bean

**扫描哪些注解**
```txt
	@Component  
	@Service  
	@Repository  
	@Controller  
	@RestController  
	@Configuration
```

**扫描的过程**
```txt
	com.powernode  
	│  
	├── Application  
	│  
	├── controller  
	│ └── UserController  
	│  
	├── service  
	│ └── UserService  
	│  
	└── dao  
	└── UserDao
```

>  当启动主入口时，spring会扫描com.powernode及其所有子包，这也是为什么启动类必须放在根包下

---

#### 3.@EnableAutoConfiguration
作用：

```
开启自动配置
```

根据依赖自动配置组件。

例如引入：

```
spring-boot-starter-web
```

自动配置：

```
Tomcat
SpringMVC
DispatcherServlet
Jackson
```

无需手动配置。

---
#### 5、为什么启动类放根包

推荐结构：

```
com.powernode
│
├── Application
├── controller
├── service
├── mapper
└── entity
```

原因：

```
@ComponentScan
```

默认扫描：

```
当前包及其子包
```

如果启动类放在根包：

```
com.powernode
```

则：

```
controller
service
mapper
entity
```

都会被扫描。

---
#### 6、SpringApplication.run()

源码：

```
public static ConfigurableApplicationContext run(
        Class<?> primarySource,
        String... args) {

    return new SpringApplication(primarySource)
            .run(args);
}
```

作用：

```
启动SpringBoot应用
```

主要完成：

```
创建SpringApplication对象
↓
创建IOC容器
↓
加载配置
↓
自动配置
↓
启动Tomcat
↓
返回ApplicationContext
```

---
#### 7、IOC容器理解

IOC容器本质可以理解为：

```
Map<String,Object>
```

例如：

```
IOC
│
├── userService
├── userController
└── userMapper
```

之后：

```
@Autowired
private UserService userService;
```

Spring直接从IOC容器中获取对象。

---
#### 9、面试必背

### Spring Boot 主入口类有什么作用？

负责启动整个 Spring Boot 项目，创建 IOC 容器、加载配置、自动配置组件并启动内嵌服务器。

---

### @SpringBootApplication 是什么？

组合注解：

```
@Configuration
@EnableAutoConfiguration
@ComponentScan
```

---

### 为什么启动类放根包？

因为：

```
@ComponentScan
```

默认扫描当前包及其子包。

放在根包可以扫描整个项目。

---

### SpringApplication.run() 做了什么？

```
创建IOC容器
↓
加载配置
↓
扫描Bean
↓
自动配置
↓
启动Tomcat
```