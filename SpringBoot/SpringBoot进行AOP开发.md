
# Spring Boot AOP 开发详解

Spring Boot 中的 **AOP（Aspect Oriented Programming，面向切面编程）** 是实现**日志记录、权限校验、事务管理、性能监控**等功能的利器。它的核心思想是：**在不改变原有业务代码的前提下，横向切入公共功能。**

---

## 1. 核心概念大白话

在编写代码之前，必须先搞懂这 4 个核心术语：

* **Aspect（切面）：** 一个专职负责公共功能的类（比如 `LogAspect` 日志切面类）。
* **JoinPoint（连接点）：** 程序执行过程中的任意位置（在 Spring 中，通常指**每一个业务方法**）。
* **Pointcut（切入点）：** 决定哪些方法需要被“切入”，通过“表达式”或“注解”来筛选连接点。
* **Advice（通知）：** 具体的执行时机和要做的事。例如：在方法执行前做，还是执行后做？

---

## 2. 快速上手：实现一个日志切面

我们用一个最常见的场景：**在用户调用 Service 方法时，自动打印方法名和耗时。**

### 第一步：引入依赖
==在 `pom.xml` 中引入 AOP 的启动器:==
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 第二步：编写业务代码（模拟）
```java
@Service
public class UserService {
    public void createUser(String name) {
        System.out.println("正在创建用户: " + name);
        // 模拟业务耗时
        try { Thread.sleep(200); } catch (InterruptedException e) {}
    }
}
```

### 第三步：编写切面类（核心）
创建一个类，加上 `@Aspect` 和 `@Component` 注解。

```java
@Aspect
@Component
public class LogAspect {

    // 1. 定义切入点：筛选 com.example.service 包下所有类的所有方法
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void servicePt() {}

    // 2. 定义环绕通知：在方法执行前后做点什么
    @Around("servicePt()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 获取目标方法名
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP 前置] 开始执行方法: " + methodName);

        // 核心：调用目标方法本身（等同于执行了 UserService.createUser）
        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        System.out.println("[AOP 后置] 方法 " + methodName + " 执行结束，耗时: " + (end - start) + "ms");

        return result; // 返回目标方法的执行结果
    }
}
```

---

## 3. 五种通知类型（Advice）

AOP 一共支持 5 种通知，它们的执行时机和区别如下：

| 注解                    | 通知类型 | 执行时机 / 特点                                   |
| :-------------------- | :--- | :------------------------------------------ |
| **`@Before`**         | 前置通知 | 目标方法**执行前**运行。                              |
| **`@AfterReturning`** | 返回通知 | 目标方法**正常返回后**运行（如果方法抛异常，则不执行）。              |
| **`@AfterThrowing`**  | 异常通知 | 目标方法**抛出异常后**运行。                            |
| **`@After`**          | 后置通知 | 目标方法**执行后**运行（无论正常还是异常，类似于 `finally`）。      |
| **`@Around`**         | 环绕通知 | **最强大**。手动控制方法是否执行（`proceed()`），可同时在前后编织代码。 |

---

## 4. 进阶玩法：基于“自定义注解”触发 AOP（企业级推荐）

在实际开发中，`execution(...)` 表达式如果写得不好，很容易切错方法。更优雅、更常见的做法是：**谁想用 AOP，谁就加个注解。**

### 1) 定义一个自定义注解
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyLog {
    String value() default ""; // 可以传参，比如 "用户模块"
}
```

### 2) 修改切面类（改用注解匹配）
```java
@Aspect
@Component
public class MyLogAspect {

    // 切入点改为：只要方法上加了 @MyLog 注解，就切入
    @Around("@annotation(myLog)")
    public Object doLog(ProceedingJoinPoint point, MyLog myLog) throws Throwable {
        // 获取注解里的值
        System.out.println("模块名称: " + myLog.value());
        
        return point.proceed();
    }
}
```

### 3) 在业务方法上使用
```java
@Service
public class OrderService {
    
    @MyLog("订单模块-创建订单") // 只要加了这个注解，就会自动触发上面的 AOP
    public void createOrder() {
        System.out.println("生成订单中...");
    }
}
```

---

## 5. AOP 最大的坑：类内部自调用失效

这是 AOP 最容易踩的死穴。因为 Spring AOP 的底层是基于 **动态代理** 实现的。只有当别的类调用 `UserService` 的方法时，Spring 才能在外部套上代理外壳。

**失效场景：**
```java
@Service
public class UserService {

    public void methodA() {
        // 内部直接调用 methodB
        methodB(); 
    }

    @MyLog("测试")
    public void methodB() {
        System.out.println("B方法执行");
    }
}
```
**结果：** 当外部调用 `methodA()` 时，`methodB()` 上的 AOP **不会生效**！因为 `methodB()` 是被 `this`（对象自身）直接调用的，没有经过 Spring 的代理对象。

**解决办法：**
1. 别在内部自调用，抽离到不同的 Service 中。
2. 注入自己（延迟加载）：
   ```java
   @Autowired
   @Lazy
   private UserService userService; // 注入自己的代理对象
   
   public void methodA() {
       userService.methodB(); // 通过代理对象调用，AOP 成功生效！
   }
   ```