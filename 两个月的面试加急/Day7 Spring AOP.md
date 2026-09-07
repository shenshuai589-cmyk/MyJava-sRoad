# 🚀 Day 7：Spring AOP + `@Transactional`

## 一、今日目标

今天重点掌握 **7 个问题**：

| 优先级 | 面试问题                                    |
| --- | --------------------------------------- |
| ⭐⭐⭐ | 1. 什么是 AOP？解决什么问题？                      |
| ⭐⭐⭐ | 2. Spring AOP 底层是怎么实现的？                 |
| ⭐⭐⭐ | 3. JDK 动态代理和 CGLIB 有什么区别？               |
| ⭐⭐⭐ | 4. Spring AOP 是什么时候创建代理对象的？             |
| ⭐⭐⭐ | 5. `@Transactional` 为什么能够实现事务？          |
| ⭐⭐  | 6. `@Transactional` 常见失效场景有哪些？          |
| ⭐⭐  | 7. `@Aspect`、`@Before`、`@Around` 分别是什么？ |

---

# 第一部分：什么是 AOP？

## 1. AOP 是什么？

AOP：

> **Aspect Oriented Programming，面向切面编程。**

它是一种编程思想。

主要解决：

> **将日志、事务、权限、监控等与核心业务无关的横切逻辑抽离出来，避免大量重复代码，并在不修改核心业务代码的情况下进行统一增强。**

例如：

```
public void createOrder() {

    // 日志
    // 权限检查
    // 开启事务

    // 核心业务
    create();

    // 提交事务
    // 日志
}
```

AOP 可以把这些公共逻辑抽出来。

---

# 第二部分：Spring AOP 底层怎么实现？

这个是**必须掌握的⭐⭐⭐**。

核心答案：

> **Spring AOP 底层主要通过动态代理实现。**

主要有：

```
Spring AOP
    ↓
动态代理
    ↓
┌──────────────┐
│              │
JDK          CGLIB
│              │
接口           类
```

---

# 第三部分：JDK 动态代理 vs CGLIB

## JDK 动态代理

基于：

> **接口**

例如：

```
public interface UserService {
    void addUser();
}
```

代理对象实现这个接口。

---

## CGLIB

基于：

> **继承目标类**

例如：

```
UserService
     ↑
     │ extends
     │
UserService$$SpringCGLIB
```

---

## 面试直接记这个

> **JDK 动态代理基于接口，CGLIB 基于目标类生成子类。**

---

# 第四部分：Spring AOP 什么时候创建代理？

这个和你 Day 6 学的 **BeanPostProcessor** 连接起来。

Spring 创建 Bean：

```
实例化
 ↓
属性填充
 ↓
初始化前
 ↓
初始化
 ↓
BeanPostProcessor
 ↓
最终Bean
```

AOP 会在 Bean 创建过程中参与。

可以简单理解成：

```
Bean创建
   ↓
BeanPostProcessor
   ↓
AOP自动代理创建器
   ↓
判断是否需要代理
   ↓
需要
 ↓
创建代理对象
```

---

# ⭐ 这一点你一定要会

> **Spring AOP 的自动代理创建器属于 BeanPostProcessor 体系，它会在 Bean 创建过程中判断当前 Bean 是否需要 AOP 增强，如果需要，就创建代理对象。**

你不用背：

```
AnnotationAwareAspectJAutoProxyCreator
```

的源码。

知道它：

> **是 Spring AOP 自动创建代理的核心组件之一**

就够了。

---

# 第五部分：`@EnableAspectJAutoProxy` 了解即可

你刚才已经学过这条链：

```
@EnableAspectJAutoProxy
        ↓
@Import
        ↓
AspectJAutoProxyRegistrar
        ↓
注册
AnnotationAwareAspectJAutoProxyCreator
        ↓
BeanPostProcessor
        ↓
创建AOP代理
```

### 面试需要达到什么程度？

知道：

> `@EnableAspectJAutoProxy` 用于开启 Spring AOP 自动代理相关功能。

然后能够简单解释：

> 它通过 `@Import` 注册 AOP 自动代理创建器，之后 Spring 在创建 Bean 的过程中可以自动创建代理。

**到这里就够了。**

❌ 不需要背：

```
AspectJAutoProxyRegistrar
AopConfigUtils
registerAspectJAnnotationAutoProxyCreatorIfNecessary()
```

源码。

---

# 第六部分：重点来了——`@Transactional`

这是今天最重要的内容。

你之前已经学过：

> MySQL 事务

现在把它和 Spring AOP 连起来。

---

## 1. `@Transactional` 是什么？

例如：

```
@Transactional
public void createOrder() {

    createOrder();

    reduceStock();

    createPayment();
}
```

意思是：

> **这个方法需要在一个事务中执行。**

如果全部成功：

```
提交事务
```

如果发生异常：

```
回滚事务
```

---

# 2. `@Transactional` 底层是什么？

核心：

> **Spring 通过 AOP + 动态代理实现声明式事务。**

可以理解成：

```
@Transactional
       ↓
Spring AOP
       ↓
创建代理对象
       ↓
调用代理对象方法
       ↓
事务拦截器
       ↓
开启事务
       ↓
调用目标方法
       ↓
   ┌───┴────┐
   ↓        ↓
成功       异常
   ↓        ↓
提交       回滚
```

---

# ⭐⭐⭐ 这是非常值得你记住的一条链

面试官问：

> **`@Transactional` 是怎么实现的？**

你可以回答：

> `@Transactional` 底层是通过 Spring AOP 实现的。Spring 会为符合条件的 Bean 创建代理对象，当调用代理对象的方法时，事务拦截器会介入，在执行目标方法之前开启事务，方法正常执行则提交事务，如果发生符合条件的异常则回滚事务。

这个回答已经非常不错了。

---

# 第七部分：为什么有时候 `@Transactional` 会失效？

这个是**非常典型的初中级面试题**。

你至少要掌握下面几个。

---

## 情况 1：方法不是 `public`

例如：

```
@Transactional
private void createOrder() {
}
```

这种情况下容易出现事务不生效的问题。

初级阶段记：

> **通常把事务方法定义为 `public`。**

---

# 情况 2：同一个类内部调用

这个特别重要。

例如：

```
@Service
public class OrderService {

    public void create() {
        updateStock();
    }

    @Transactional
    public void updateStock() {
        // ...
    }
}
```

调用：

```
create();
```

实际上是：

```
this.updateStock()
```

而不是：

```
proxy.updateStock()
```

于是没有经过 Spring AOP 代理。

所以：

> **同类内部直接调用方法，可能导致 `@Transactional` 不生效。**

---

# 情况 3：异常被自己吃掉

例如：

```
@Transactional
public void createOrder() {

    try {
        int a = 10 / 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

异常被捕获之后没有继续抛出去。

Spring 可能无法感知到需要回滚。

---

# 情况 4：异常类型不符合回滚规则

默认情况下，Spring 对事务回滚的默认规则主要是：

> **运行时异常和 Error 默认触发回滚。**

例如：

```
throw new RuntimeException();
```

通常会回滚。

而普通受检异常：

```
throw new Exception();
```

默认情况下不一定触发回滚。

可以通过：

```
@Transactional(rollbackFor = Exception.class)
```

明确指定。

---

# 第八部分：AOP 常见注解

今天不需要深入源码。

先掌握用途。

## `@Aspect`

表示：

> 这是一个切面类。

例如：

```
@Aspect
@Component
public class LogAspect {
}
```

---

## `@Before`

目标方法执行之前执行：

```
@Before("execution(...)")
public void before() {
    System.out.println("执行前");
}
```

---

## `@After`

目标方法执行之后执行。

---

## `@Around`

非常重要。

可以理解成：

> **环绕目标方法，可以控制目标方法什么时候执行。**

例如：

```
@Around("execution(...)")
public Object around(ProceedingJoinPoint point) throws Throwable {

    System.out.println("执行前");

    Object result = point.proceed();

    System.out.println("执行后");

    return result;
}
```

执行流程：

```
进入代理
   ↓
Around 前
   ↓
proceed()
   ↓
目标方法
   ↓
Around 后
```

---

# 🎯 Day 7 最终知识地图

今天结束的时候，你脑子里应该是这张图：

```
                       Spring AOP
                           │
             ┌─────────────┴─────────────┐
             ↓                           ↓
           AOP思想                    动态代理
             │                           │
       横切关注点                  ┌──────┴──────┐
             │                    ↓             ↓
      日志/事务/权限             JDK           CGLIB
                                  │             │
                                 接口            类
                                             
                           BeanPostProcessor
                                  ↓
                         自动创建AOP代理
                                  ↓
                         代理对象调用方法
                                  ↓
                         ┌────────┴────────┐
                         ↓                 ↓
                       日志             事务
                                           ↓
                                  @Transactional
                                           ↓
                                      开启事务
                                           ↓
                                    执行目标方法
                                      ↙       ↘
                                    成功       异常
                                     ↓          ↓
                                    提交        回滚
```


---

# 🔥 今天真正需要你掌握的面试题

接下来我们就按照这个版本练，不再继续无限钻源码：

### 第一组：AOP

**1. ⭐ 什么是 AOP？解决什么问题？**

> AOP 是一种面向切面的编程思想，主要解决日志、事务、权限等横切关注点与核心业务代码耦合、重复的问题。

---

**2. ⭐⭐ Spring AOP 底层是怎么实现的？**

> Spring AOP 底层主要通过动态代理实现，包括 JDK 动态代理和 CGLIB。

---


**3. ⭐⭐ JDK 动态代理和 CGLIB 有什么区别？**

> JDK 动态代理基于接口生成代理对象，CGLIB 基于目标类生成子类来实现代理。


---


**4. ⭐⭐⭐ Spring AOP 是什么时候创建代理对象的？**

> 主要是在 Bean 初始化完成后的后置处理阶段，由自动代理创建器判断是否需要创建代理。

---

### 第二组：事务

**5. ⭐⭐⭐ `@Transactional` 为什么能够实现事务？**


> `@Transactional` 底层基于 Spring AOP。Spring 为目标 Bean 创建代理对象，调用代理方法时由事务拦截器介入，在目标方法执行前开启事务，执行成功后提交事务，发生符合回滚规则的异常时回滚事务。


---



**6. ⭐⭐ `@Transactional` 为什么会失效？**

>1.不是public  
   2.同一个类中调用当前类的事务方法
>3.异常被捕获后没有继续抛出，导致事务拦截器感知不到异常，就可能不会回滚。

---

**7. ⭐⭐ `@Transactional` 默认什么情况下回滚？**

> 默认情况下，RuntimeException 和 Error 会触发回滚；普通的受检异常 Exception 默认不会触发回滚。

---
### 第三组：注解

**8. ⭐ `@Aspect` 是什么？**

> 声明当前类是一个 AOP 切面类。

---


**9. ⭐ `@Before`、`@After`、`@Around` 有什么区别？**

> @Before:目标方法执行之前执行
> @Ater:目标方法执行之后执行
> @Around:环绕通知可以控制目标方法的执行


---


# 🔥 一、为什么同类内部调用会导致 `@Transactional` 失效？

这是非常高频的一题。

假设：

```
@Service
public class OrderService {

    public void createOrder() {
        updateStock();
    }

    @Transactional
    public void updateStock() {
        // 修改库存
    }
}
```

很多人会觉得：

> `updateStock()` 有 `@Transactional`，所以应该开启事务。

但实际上：

```
updateStock();
```

本质是：

```
this.updateStock();
```

也就是：

```
当前对象
   ↓
this.updateStock()
```

而不是：

```
代理对象
   ↓
updateStock()
```

---

## 为什么这很重要？

Spring AOP 的事务逻辑在：

```
代理对象
   ↓
事务拦截器
   ↓
目标方法
```

如果直接：

```
this.updateStock()
```

就绕过了代理。

所以：

```
代理对象
   ↓
事务拦截器 ❌
   ↓
目标方法
```

事务自然可能不生效。

### ⭐ 一句话记忆：

> **Spring AOP 是通过代理对象实现增强的，同类内部直接调用不会经过代理对象，因此可能导致事务失效。**

---

# 🔥 二、为什么事务方法一般要求 `public`？

因为 Spring AOP 默认主要针对可以被代理拦截的方法。

例如：

```
@Transactional
private void test() {
}
```

你可以简单记：

> **事务方法一般定义为 `public`，否则可能无法被 Spring AOP 正常代理拦截。**

面试不要展开太深，初级阶段这样回答就够了。

---

# 🔥 三、为什么异常被 catch 后事务可能不回滚？

例如：

```
@Transactional
public void createOrder() {

    try {
        createOrderData();

        int a = 10 / 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

发生：

```
异常
 ↓
catch
 ↓
异常被吃掉
 ↓
方法正常返回
```

事务拦截器看到的是：

```
目标方法正常结束
```

所以可能：

```
提交事务
```

而不是：

```
回滚事务
```

---

## 如果重新抛出去：

```
catch (Exception e) {
    throw e;
}
```

那么：

```
异常
 ↓
继续向外抛
 ↓
事务拦截器感知异常
 ↓
判断回滚规则
 ↓
回滚
```

---

# 🔥 四、默认什么异常会回滚？

这个一定记住。

```
默认回滚：

RuntimeException
Error

默认不回滚：

普通 Exception
```

例如：

```
throw new RuntimeException();
```

通常：

```
回滚 ✅
```

而：

```
throw new Exception();
```

默认：

```
回滚 ❌
```

如果希望普通 `Exception` 也回滚：

```
@Transactional(rollbackFor = Exception.class)
```

---

# 🔥 五、`rollbackFor` 是什么？

例如：

```
@Transactional(
    rollbackFor = Exception.class
)
```

意思：

> **指定哪些异常类型发生时需要回滚。**

所以：

```
@Transactional
```

和：

```
@Transactional(rollbackFor = Exception.class)
```

是不一样的。

后者扩大了回滚范围。

---

# 🔥 六、`@Around` 为什么特别重要？

你刚才已经知道：

```
@Around
```

可以在目标方法前后执行。

例如：

```
@Around("execution(* com.xxx.service.*.*(..))")
public Object around(ProceedingJoinPoint point)
        throws Throwable {

    System.out.println("前");

    Object result = point.proceed();

    System.out.println("后");

    return result;
}
```

关键：

```
point.proceed();
```

它表示：

> **继续执行目标方法。**

---

## 如果不调用 `proceed()` 呢？

例如：

```
@Around(...)
public Object around(ProceedingJoinPoint point) {

    System.out.println("前");

    return null;
}
```

那么：

```
进入Around
 ↓
没有 proceed()
 ↓
目标方法不会继续执行
```

所以面试如果问：

> `@Around` 和 `@Before` 最大区别？

你可以回答：

> **`@Around` 可以控制目标方法是否执行，并且可以在目标方法执行前后进行增强，而 `@Before` 只能在目标方法执行前执行。**

---

# 🔥 七、AOP 里面几个概念不要混

这个也很容易被问。

```
Aspect
Pointcut
Advice
JoinPoint
```

你可以这样记：

### Aspect

> **切面**

例如：

```
@Aspect
public class LogAspect {}
```

---

### Pointcut

> **切点：规定哪些方法需要被增强。**

例如：

```
com.xxx.service.*.*
```

---

### Advice

> **通知：具体要执行什么增强逻辑。**

例如：

```
记录日志
开启事务
权限检查
```

---

### JoinPoint

> **连接点：可以被 AOP 增强的位置。**

初级面试不用深入。

---

# 🧠 八、把整个 AOP 再串一次

你现在应该能理解：

```
              AOP
               ↓
       横切关注点抽离
               ↓
         Spring AOP
               ↓
          动态代理
         ↙         ↘
       JDK         CGLIB
        ↓            ↓
       接口          子类
               ↓
       BeanPostProcessor
               ↓
        自动创建代理
               ↓
         代理对象调用
               ↓
       ┌───────┴───────┐
       ↓               ↓
      日志            事务
                       ↓
                @Transactional
                       ↓
                  事务拦截器
                       ↓
                   开启事务
                       ↓
                   目标方法
                    ↙     ↘
                  成功     异常
                   ↓        ↓
                  提交      回滚
```