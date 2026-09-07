
# 一、Day 6 今天到底学什么？

今天建议按照下面这个顺序：

```
Spring核心思想
      ↓
IOC是什么？
      ↓
BeanFactory
      ↓
ApplicationContext
      ↓
BeanDefinition
      ↓
refresh()
      ↓
Bean创建流程
      ↓
Bean生命周期
      ↓
三级缓存
      ↓
循环依赖
      ↓
AOP
```

其中最重要的是这 5 个：

> ⭐⭐⭐ `refresh()`

> ⭐⭐⭐ Bean 创建流程

> ⭐⭐⭐ Bean 生命周期

> ⭐⭐⭐ 三级缓存 + 循环依赖

> ⭐⭐⭐ AOP 创建代理

这几个是今天的核心。

---

# 二、第一部分：IOC 到底是什么？

很多人面试时会说：

> IOC 就是控制反转，把对象交给 Spring 管理。

这句话**没错，但是太浅了**。

我们从代码理解。

## 1. 没有 IOC

假设有：

```
public class UserService {

    private UserDao userDao = new UserDao();

}
```

那么：

```
UserService
    ↓
自己 new
    ↓
UserDao
```

`UserService` 自己负责创建 `UserDao`。

这意味着：

```
UserService
    ↓
强依赖
    ↓
UserDao
```

如果以后换成：

```
UserDaoImpl
```

你可能需要修改：

```
UserService
```

---

# 三、有 IOC 以后

变成：

```
public class UserService {

    private UserDao userDao;

}
```

然后：

```
              Spring IOC容器
                    │
          ┌─────────┴─────────┐
          ↓                   ↓
      UserDao            UserService
          │                   │
          └────── 注入 ───────┘
```

也就是说：

> **对象不再由业务代码主动创建，而是由 Spring 容器负责创建、组装和管理。**

这就是：

# IOC：控制反转

所谓“反转”：

以前：

```
程序员
  ↓
new UserDao()
  ↓
UserService
```

现在：

```
UserService
     ↓
告诉 Spring：“我需要 UserDao”
     ↓
Spring
     ↓
创建 UserDao
     ↓
注入 UserService
```

控制权：

```
原来：业务代码 → 创建对象

现在：Spring → 创建对象
```

所以叫：

> **控制反转（Inversion of Control）**

---

# 四、DI 又是什么？

这里面试非常容易问：

> IOC 和 DI 有什么区别？

### IOC

是一种**思想**：

> 对象的控制权从程序员手中交给 Spring。

### DI

是一种**实现方式**：

> Dependency Injection，依赖注入。

例如：

```
public class UserService {

    private UserDao userDao;

}
```

Spring：

```
创建 UserDao
      ↓
发现 UserService 依赖 UserDao
      ↓
把 UserDao 注入 UserService
```

所以可以记：

```
IOC
│
└── 是思想
      ↓
    控制反转

DI
│
└── 是实现方式
      ↓
    依赖注入
```

面试可以直接回答：

> IOC 是一种控制反转的思想，DI 是 IOC 的一种具体实现方式，Spring 通过依赖注入完成对象之间的依赖关系组装。

---

# 五、Spring IOC 容器到底是什么？

现在进入一个非常重要的问题：

> **Spring 到底拿什么东西管理 Bean？**

最核心的就是：

```
BeanFactory
```

---

# 六、BeanFactory

`BeanFactory` 可以理解成：

> **Spring IOC 容器最核心的接口。**

它最重要的功能就是：

```
Object getBean(String name);
```

例如：

```
UserService userService =
        beanFactory.getBean("userService");
```

Spring：

```
getBean("userService")
        ↓
IOC容器
        ↓
找到 Bean
        ↓
返回 UserService
```

所以：

> **BeanFactory 是 Spring IOC 容器的底层核心。**

---

# 七、ApplicationContext 又是什么？

这时候面试官可能马上追问：

> 那 ApplicationContext 和 BeanFactory 有什么区别？

这是非常高频的问题。

先记住关系：

```
BeanFactory
     ↑
     │
ApplicationContext
```

`ApplicationContext` 是更高级的容器。

例如：

```
BeanFactory
    ↓
负责 Bean 基本管理
```

而：

```
ApplicationContext
    ↓
Bean管理
+ 国际化
+ 事件发布
+ 资源加载
+ 自动注册各种后处理器
+ ...
```

所以：

> **BeanFactory 是 IOC 容器的核心基础，而 ApplicationContext 是在 BeanFactory 基础上提供更多企业级功能的高级容器。**

---

# 八、为什么我们平时几乎不用 BeanFactory？

因为 Spring Boot 中：

```
ApplicationContext context =
        SpringApplication.run(Application.class);
```

这个：

```
context
```

本质上就是 Spring 的 IOC 容器。

然后：

```
UserService service =
        context.getBean(UserService.class);
```

拿 Bean。

所以你平时写：

```
@Autowired
private UserService userService;
```

背后最终还是：

```
ApplicationContext
       ↓
BeanFactory
       ↓
Bean
```

---

# 九、Bean 到底是什么？

这是理解 Spring 源码的关键。

例如：

```
@Service
public class UserService {
}
```

Spring 扫描到这个类以后，并不是简单地：

```
new UserService();
```

Spring 首先会建立一份：

> **BeanDefinition**

---

# 十、BeanDefinition 是什么？

可以把它理解成：

> **Spring 用来描述一个 Bean 的“说明书”。**

例如：

```
@Service
public class UserService {
}
```

Spring 可能会记录：

```
BeanDefinition
│
├── Bean类型：UserService
├── Bean名称：userService
├── Scope：singleton
├── 是否懒加载：false
├── 是否自动注入：true
├── 初始化方法
├── 销毁方法
└── ...
```

注意：

> **BeanDefinition 不是 Bean 本身。**

这是非常重要的区别。

---

# 十一、BeanDefinition → Bean

整个过程可以先理解成：

```
UserService.class
       ↓
Spring扫描
       ↓
BeanDefinition
       ↓
Spring根据BeanDefinition创建
       ↓
UserService对象
       ↓
Bean
```

所以：

```
Class
 ↓
BeanDefinition
 ↓
Bean
```

这条线一定要记住。

---

# 十二、今天最重要的源码：refresh()

现在进入今天的核心。

Spring 容器启动的时候，会执行：

```
refresh();
```

这个方法非常重要。

面试官可能直接问：

> **Spring 的 IOC 容器启动过程是什么？**

你不能只说：

> 扫描 Bean，然后创建 Bean。

太浅。

你至少应该知道：

```
refresh()
   ↓
prepareRefresh()
   ↓
obtainFreshBeanFactory()
   ↓
prepareBeanFactory()
   ↓
postProcessBeanFactory()
   ↓
invokeBeanFactoryPostProcessors()
   ↓
registerBeanPostProcessors()
   ↓
initMessageSource()
   ↓
initApplicationEventMulticaster()
   ↓
onRefresh()
   ↓
registerListeners()
   ↓
finishBeanFactoryInitialization()
   ↓
finishRefresh()
```

今天先不用死背全部方法。

重点抓：

```
refresh()
    ↓
BeanFactory
    ↓
BeanDefinition
    ↓
BeanFactoryPostProcessor
    ↓
BeanPostProcessor
    ↓
创建 Bean
    ↓
完成 IOC 容器启动
```

| **步骤** | **阶段名称**                     | **核心职责**                                                                     | **比喻说明**            |
| ------ | ---------------------------- | ---------------------------------------------------------------------------- | ------------------- |
| **1**  | **`refresh()`**              | 触发容器刷新的入口方法，拉开初始化序幕。                                                         | 按下工厂的“启动总开关”。       |
| **2**  | **BeanFactory**              | 创建底层的 `DefaultListableBeanFactory` 容器，初始化基础环境。                               | 搭建工厂的毛坯车间与基础设施。     |
| **3**  | **BeanDefinition**           | 扫描/解析配置（注解或 XML），将类名、作用域、依赖关系等元数据注册为 `BeanDefinition`。_（此时未实例化 Bean）_        | 收集并整理所有产品的“设计图纸”。   |
| **4**  | **BeanFactoryPostProcessor** | 在 Bean 实例化之前执行，拦截并修改 `BeanDefinition`（如替换 `${...}` 占位符、解析 `@ComponentScan`）。 | “总工程师”在开工前审查并修改图纸。  |
| **5**  | **BeanPostProcessor**        | 实例化并注册所有的 Bean 后置处理器，供后续 Bean 实例化时调用。                                        | 在流水线上安排“质检员”和“装修工”。 |
| **6**  | **创建 Bean**                  | 对所有非懒加载单例 Bean 进行真正的实例化、属性注入（DI）和初始化（触发 AOP 代理）。                             | 流水线全速运转，批量生产最终产品。   |
| **7**  | **完成 IOC 容器启动**              | 清理缓存，触发 `ContextRefreshedEvent` 事件，发布容器启动完成状态。                               | 所有产品入库，工厂正式对外营业。    |

---

# 十三、refresh() 最重要的一步

其中：

```
finishBeanFactoryInitialization(beanFactory);
```

非常重要。

它会触发：

> **非懒加载单例 Bean 的创建。**

也就是说：

```
@Service
public class UserService {
}
```

如果是：

```
singleton
+
非懒加载
```

那么 Spring 启动的时候就可能创建它。

---

# 十四、Bean 创建流程

这就是今天第二个核心。

大致可以记成：

```
getBean()
   ↓
doGetBean()
   ↓
createBean()
   ↓
doCreateBean()
   ↓
createBeanInstance()
   ↓
populateBean()
   ↓
initializeBean()
   ↓
Bean完成
```

```
1. getBean() / doGetBean()
    → 查仓库：“这辆车造好了吗？有现成的直接给，没有就去工厂造。”
    ↓
2. createBean() / doCreateBean()
    → 下达生产指令：“开始进入车间，准备组装。” 
    ↓ 
3. createBeanInstance() 【第一步：造车架】 
   → 用反射 new 出一个对象。（此时里面全是 null，就像一辆只有空壳、没装发动机和轮胎的车） 
   ↓ 
4. populateBean() 【第二步：装配件】 
   → 依赖注入（DI）。把 `@Autowired` 标记的各种属性、其他 Bean 给填进去。（把发动机、轮胎装上去）     ↓ 
5. initializeBean() 【第三步：精装修与上牌】 
   → 执行 `@PostConstruct` 和自定义初始化方法。 
   → **AOP 动态代理（重点）**：如果要加切面（比如日志、事务），就在这里给车喷个漆、加个镀膜（生成代理对象）。 
   ↓ 
6. Bean 完成 
   → 一辆完整可用、随时能开的车，放入**一级缓存**（仓库），交付给你使用。
   
```
这条链非常重要。

---

# 十五、createBeanInstance()

第一步：

> 创建 Bean 实例。

例如：

```
UserService userService =
        new UserService();
```

但 Spring 实际上会根据：

```
构造方法
工厂方法
Supplier
CGLIB
...
```

选择不同的实例化方式。

核心思想就是：

```
BeanDefinition
      ↓
确定怎么创建
      ↓
实例化
      ↓
原始Bean对象
```

---

# 十六、populateBean()

Bean 创建出来以后：

```
UserService userService =
        new UserService();
```

但此时：

```
userService.userDao
```

可能还是：

```
null
```

因为依赖还没注入。

所以接下来：

```
populateBean()
```

负责：

> **属性填充 / 依赖注入。**

例如：

```
@Autowired
private UserDao userDao;
```

最终：

```
UserService
     │
     └── userDao
            ↓
         UserDao对象
```

---

# 十七、initializeBean()

依赖注入完成以后：

```
实例化
 ↓
属性注入
 ↓
初始化
```

这里会涉及：

```
Aware接口
 ↓
BeanPostProcessor
 ↓
初始化方法
 ↓
@PostConstruct
 ↓
InitializingBean
 ↓
自定义init-method
```

这就是我们后面要重点研究的：

> **Bean 生命周期**

---

# 十八、Bean 生命周期先记一个大图

今天先不要死抠源码。

先建立整个生命周期：

```
前提：
1. 编写一个类实现BeanPostProcessor接口并
重写postProcessBeforeInitialization和
postProcessAfterInitialization方法

2. 在xml文件中配置后置处理器
<bean class="com.powernode.spring6.bean.LogBeanPostProcessor"/>

此后置处理器作用于该配置文件配置的所有bean
七步：
1.实例化bean
2.为bean属性赋值
3.bean前置处理器before方法运行
4.初始化bean
5.bean后置处理器after方法运行
6.使用bean
7.销毁bean

实例化bean → bean属性赋值 → Aware接口 → 前置处理器 → 初始化bean → 后置处理器 → 业务使用 → 销毁bean

```

这个一定要形成脑子里的流程图。

---

# 十九、三级缓存——今天最难的部分

三级缓存本质上是 `DefaultSingletonBeanRegistry` 类中的 **三个 ConcurrentHashMap（或 HashMap）**

| **缓存层级** | **变量名**                 | **类型**                          | **存储内容与作用**                                                        |
| -------- | ----------------------- | ------------------------------- | ------------------------------------------------------------------ |
| **一级缓存** | `singletonObjects`      | `Map<String, Object>`           | **成品 Bean**：存放经历了完整生命周期（实例化 + 属性赋值 + 初始化 + AOP）的单例 Bean。           |
| **二级缓存** | `earlySingletonObjects` | `Map<String, Object>`           | **半成品 Bean**：存放刚实例化、注入了依赖但尚未完全初始化的 Bean（或提前生成的代理对象），防止多次创建代理。      |
| **三级缓存** | `singletonFactories`    | `Map<String, ObjectFactory<?>>` | **Bean 工厂**：存放用于生成“半成品 Bean”的匿名工厂 Lambda 表达式。**核心目的是延迟生成 AOP 代理**。 |

如果你现在看到：

```
singletonObjects
earlySingletonObjects
singletonFactories
```

不要慌。

这是 Spring 解决：

> **单例 Bean 循环依赖**

的核心机制。

例如：

```
class A {
    @Autowired
    B b;
}

class B {
    @Autowired
    A a;
}
```

形成：

```
A → B
↑   ↓
└───┘
```

也就是：

```
A依赖B
B又依赖A
```

如果简单地：

```
创建A
 ↓
创建B
 ↓
创建A
 ↓
创建B
 ↓
无限循环
```

怎么办？

Spring 的三级缓存就是解决这个问题的核心机制之一。

---

# 二十、三级缓存先不要背定义

先理解三个缓存：

```
一级缓存
singletonObjects
```

存：

> **完整初始化完成的单例 Bean**

---

```
二级缓存
earlySingletonObjects
```

存：

> **提前暴露出来的 Bean**

---

```
三级缓存
singletonFactories
```

存：

> **能够生成早期 Bean 引用的 ObjectFactory**

---

# 二十一、三级缓存为什么要有三个？

这是非常好的面试题。

不要回答：

> 因为 Spring 就设计成了三级。

真正原因和：

> **AOP**

有关系。

因为 Spring 不一定最终直接把：

```
原始对象
```

放出去。

可能需要：

```
原始对象
 ↓
创建代理
 ↓
代理对象
```

三级缓存中的：

```
ObjectFactory
```

可以在需要的时候：

> **提前生成 Bean 的早期引用。**

这个“早期引用”可能是：

```
原始对象
```

也可能是：

```
代理对象
```

所以三级缓存和：

```
循环依赖
+
AOP代理
```

紧密相关。

---

# 二十二、今天你必须搞懂的核心关系

把这张图记住：

```
                  Spring IOC
                      │
                      ↓
               ApplicationContext
                      │
                      ↓
                  BeanFactory
                      │
                      ↓
                BeanDefinition
                      │
                      ↓
                  createBean
                      │
          ┌───────────┼───────────┐
          ↓           ↓           ↓
       实例化       属性注入      初始化
          │           │           │
          └───────────┼───────────┘
                      ↓
                 BeanPostProcessor
                      │
                      ↓
                    AOP
                      │
                      ↓
                   代理对象
                      │
                      ↓
                    Bean
```

这就是我们 Day 6 的主线。

---

# 二十三、今天的面试题

今天先把下面 **10 道题**搞懂：

### ⭐

**1. 什么是 IOC？**

> IOC（控制反转）是一种设计思想，将对象的创建、依赖关系维护以及生命周期管理等控制权，从原本的业务代码交给 Spring 容器。

---

### ⭐

**2. IOC 和 DI 有什么区别？**

>  DI（Dependency Injection，依赖注入）是 IOC 的一种实现方式，Spring 通过依赖注入完成 Bean 之间依赖关系的建立。

---

### ⭐

**3. BeanFactory 和 ApplicationContext 有什么区别？**

> BeanFactory 是 Spring IOC 容器的底层核心接口，负责 Bean 的创建和获取；ApplicationContext 是更高级的容器，在 BeanFactory 基础上扩展了事件、国际化、资源加载等功能，并且在容器启动时会自动完成更多组件的初始化。

---

### ⭐⭐

**4. BeanDefinition 是什么？**

> BeanDefinition 是 Bean 的定义信息，而不是 Bean 对象本身。


---
### ⭐⭐

**5. Spring 容器启动过程是什么？**

```
ApplicationContext
       ↓
refresh()
       ↓
创建 / 准备 BeanFactory
       ↓
解析 BeanDefinition
       ↓
注册 BeanPostProcessor
       ↓
执行 BeanFactoryPostProcessor
       ↓
创建非懒加载单例 Bean
       ↓
完成 IOC 容器启动
```

---
### ⭐⭐⭐

**6. refresh() 做了什么？**

```
refresh()
   ↓
BeanFactory准备
   ↓
各种PostProcessor准备
   ↓
finishBeanFactoryInitialization()
   ↓
创建非懒加载单例Bean
```

---
### ⭐⭐⭐

**7. Spring Bean 的生命周期是什么？**

```
BeanDefinition
     ↓
实例化
     ↓
属性赋值
     ↓
Aware接口
     ↓
BeanPostProcessor.before
     ↓
初始化
     ↓
BeanPostProcessor.after
     ↓
Bean创建完成
     ↓
使用
     ↓
销毁
```
---
### ⭐⭐⭐

**8. Spring 是怎么创建 Bean 的？**

```
getBean
   ↓
doGetBean
   ↓
createBean
   ↓
doCreateBean
   ↓
createBeanInstance
   ↓
populateBean
   ↓
initializeBean
   ↓
Bean
```

### ⭐⭐⭐

**9. Spring 为什么需要三级缓存？**



### ⭐⭐⭐

**10. Spring 是如何解决循环依赖的？**


---

# Day 6 · 第二阶段：refresh() 源码分析

我们先回忆一下整个入口：

```
Spring启动
    ↓
ApplicationContext
    ↓
refresh()
    ↓
Spring IOC容器初始化
```

而 `refresh()` 大致是：

```
refresh()
   ↓
prepareRefresh()
   ↓
obtainFreshBeanFactory()
   ↓
prepareBeanFactory()
   ↓
postProcessBeanFactory()
   ↓
invokeBeanFactoryPostProcessors()
   ↓
registerBeanPostProcessors()
   ↓
...
   ↓
finishBeanFactoryInitialization()
   ↓
finishRefresh()
```

今天我们先搞懂前两个：

```
refresh()
   ↓
prepareRefresh()
   ↓
obtainFreshBeanFactory()
```

---

# 一、先看 refresh() 到底长什么样

Spring 6 中，`AbstractApplicationContext` 的核心结构可以简化理解成：

```
public void refresh() throws BeansException, IllegalStateException {

    synchronized (this.startupShutdownMonitor) {

        // 1. 准备刷新
        prepareRefresh();

        // 2. 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory =
                obtainFreshBeanFactory();

        // 3. 准备 BeanFactory
        prepareBeanFactory(beanFactory);

        // 4. 子类扩展
        postProcessBeanFactory(beanFactory);

        // 5. 执行 BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);

        // 6. 注册 BeanPostProcessor
        registerBeanPostProcessors(beanFactory);

        // ...

        // 11. 创建非懒加载单例 Bean
        finishBeanFactoryInitialization(beanFactory);

        // 12. 完成刷新
        finishRefresh();
    }
}
```

你现在先注意：

```
synchronized (this.startupShutdownMonitor)
```

然后：

```
prepareRefresh();
```

---

# 二、为什么 refresh() 里面有 synchronized？

这里可以联系你之前学过的 Java 多线程。

```
synchronized (this.startupShutdownMonitor)
```

意思是：

> **同一个 ApplicationContext 的刷新/关闭过程需要进行同步控制。**

为什么？

因为容器启动和关闭涉及大量共享状态：

```
BeanFactory
BeanDefinition
Bean
各种后处理器
事件监听器
...
```

如果两个线程同时操作：

```
线程A → refresh()
线程B → refresh()
```

就可能出现状态混乱。

所以 Spring 用：

```
startupShutdownMonitor
```

作为监视器对象。

---

# 三、进入 prepareRefresh()

现在真正开始第一步：

```
prepareRefresh();
```

这个方法名字其实已经非常直白：

> **准备刷新 ApplicationContext。**

它并不是创建 Bean。

而是在正式初始化 BeanFactory 之前：

> **把 ApplicationContext 自己的基础状态准备好。**

---

# 四、prepareRefresh() 主要干什么？

你可以先记成：

```
prepareRefresh()
       ↓
设置容器启动时间
       ↓
设置容器状态
       ↓
初始化 Environment
       ↓
初始化 properties
       ↓
准备监听器/早期事件等
```

源码里面有几个比较重要的动作。

---

# 五、第一件事：记录启动时间

类似：

```
this.startupDate = System.currentTimeMillis();
```

也就是：

> Spring 记录 ApplicationContext 从什么时候开始启动。

例如：

```
2026-08-22 22:20:01
       ↓
Spring开始启动
```

Spring 就把这个时间记录下来。

这个东西本身不是重点。

重点是：

> **prepareRefresh() 是在准备容器自身的运行状态。**

---

# 六、第二件事：设置 active 状态

可以看到类似：

```
this.closed.set(false);
this.active.set(true);
```

也就是说：

```
refresh之前：

active = false
closed = true/false
```

refresh 开始：

```
active = true
closed = false
```

可以理解成：

```
ApplicationContext
       ↓
开始进入“正在运行”状态
```

---

# 七、为什么要有 active 和 closed？

因为 ApplicationContext 是有生命周期的。

大概：

```
创建
 ↓
refresh()
 ↓
运行中
 ↓
close()
 ↓
关闭
```

所以 Spring 需要知道：

```
现在容器是不是已经启动？
现在容器是不是已经关闭？
```

这就是状态管理。

---

# 八、第三件事：初始化 Environment

这是一个很重要的概念。

源码会涉及：

```
initPropertySources();
```

然后：

```
getEnvironment().validateRequiredProperties();
```

你现在看到：

```
Environment
```

不要把它理解得太复杂。

它主要负责：

> **Spring 应用运行环境中的各种配置和属性。**

比如：

```
server.port=8080
spring.datasource.url=...
spring.datasource.username=...
```

以及：

```
环境变量
JVM系统属性
配置文件
Profile
...
```

都属于 Spring Environment 体系。

---

# 九、为什么 Spring 要在 prepareRefresh() 阶段准备 Environment？

因为后面的 Spring 初始化可能需要读取配置。

比如：

```
spring.profiles.active=dev
```

Spring 需要先知道：

> 当前到底是什么环境？

例如：

```
dev
test
prod
```

可能对应不同配置：

```
application-dev.yml
application-test.yml
application-prod.yml
```

所以：

```
prepareRefresh()
      ↓
准备 Environment
      ↓
后面其他组件初始化
      ↓
可以读取环境配置
```

---

# 十、第四件事：validateRequiredProperties()

这一行非常有意思：

```
getEnvironment().validateRequiredProperties();
```

作用简单理解：

> **检查 Spring 要求必须存在的配置是否存在。**

例如某些配置被标记为：

```
必须存在
```

结果启动时发现：

```
没有这个配置
```

那么：

```
Spring启动
   ↓
prepareRefresh()
   ↓
检查配置
   ↓
发现缺失
   ↓
启动失败
```

所以它属于：

> **启动前的环境检查。**

---

# 十一、第五件事：准备早期事件

`prepareRefresh()` 还会涉及：

```
this.earlyApplicationEvents = new LinkedHashSet<>();
```

先不用深挖。

你只需要知道：

Spring 有一套：

> **ApplicationEvent 事件机制**

比如：

```
容器启动
Bean创建
容器关闭
...
```

都可以通过事件机制通知监听器。

如果某些事件发生得比较早，而监听器还没有完全注册：

```
事件
 ↓
先暂存
 ↓
后面监听器注册完成
 ↓
再处理
```

这就是 `earlyApplicationEvents` 的意义。

---

# 十二、所以 prepareRefresh() 到底是什么？

现在你可以把它总结成：

```
prepareRefresh()
      │
      ├── 记录启动时间
      │
      ├── 设置容器状态
      │
      ├── 初始化 Environment
      │
      ├── 校验必要配置
      │
      └── 准备早期事件
```

一句话：

> **prepareRefresh() 主要负责在正式创建/初始化 BeanFactory 之前，把 ApplicationContext 自身的运行环境和状态准备好。**

---

# 十三、注意：这里还没有真正创建 Bean

这个非常重要。

我们现在的位置：

```
refresh()
   ↓
prepareRefresh()
```

这里：

❌ 不是创建 UserService

❌ 不是注入 UserDao

❌ 不是执行 `@Autowired`

❌ 不是创建 AOP 代理

而是在做：

> **Spring 容器启动前的准备工作。**

---

# 十四、进入第二步：obtainFreshBeanFactory()

准备工作结束：

```
prepareRefresh();
```

接下来：

```
ConfigurableListableBeanFactory beanFactory =
        obtainFreshBeanFactory();
```

这个名字：

```
obtain
fresh
BeanFactory
```

翻译一下：

> **获取一个新的 BeanFactory。**

这就开始进入 IOC 的核心了。

---

# 十五、BeanFactory 从哪里来？

这里涉及：

```
AbstractApplicationContext
        ↓
obtainFreshBeanFactory()
        ↓
refreshBeanFactory()
        ↓
创建/刷新 BeanFactory
```

不同的 ApplicationContext 实现方式可能不同。

例如常见的：

```
AnnotationConfigApplicationContext
ClassPathXmlApplicationContext
```

底层会有自己的 BeanFactory 创建逻辑。

---

# 十六、最关键的一点：BeanFactory 里面有什么？

现在你可以把 BeanFactory 想象成：

```
BeanFactory
│
├── BeanDefinition
│      ├── userService
│      ├── userDao
│      ├── orderService
│      └── ...
│
├── Bean实例
│      ├── userService对象
│      ├── userDao对象
│      └── ...
│
└── 各种Bean管理能力
```

不过这里要特别注意：

> **BeanDefinition 和 Bean 实例是两个不同阶段的东西。**

---

# 十七、为什么叫“Fresh” BeanFactory？

因为 Spring 的 `refresh()` 并不是简单地：

```
拿旧 BeanFactory 接着用
```

而是一个“重新刷新”的过程。

核心思想：

```
旧状态
  ↓
销毁/清理旧的 BeanFactory
  ↓
重新建立
  ↓
新的 BeanFactory
```

所以叫：

```
obtainFreshBeanFactory()
```

---

# 十八、到这里整个流程是什么？

我们现在已经把：

```
refresh()
   ↓
prepareRefresh()
   ↓
obtainFreshBeanFactory()
```

拆开了。

变成：

```
Spring启动
   ↓
refresh()
   ↓
┌────────────────────────┐
│ prepareRefresh()       │
│                        │
│ 准备容器状态            │
│ 准备Environment         │
│ 检查配置                │
│ 准备早期事件            │
└────────────────────────┘
   ↓
obtainFreshBeanFactory()
   ↓
获得新的 BeanFactory
```

接下来才是：

```
prepareBeanFactory()
```

这个就开始真正：

> **配置 BeanFactory。**

---

# 十九、这里出现一个非常重要的层次

你现在应该开始建立这个结构：

```
ApplicationContext
        │
        │ refresh()
        ↓
ApplicationContext初始化
        │
        ↓
BeanFactory
        │
        ↓
BeanDefinition
        │
        ↓
Bean
```

所以不要把：

```
ApplicationContext
BeanFactory
BeanDefinition
Bean
```

看成四个完全平行的东西。

它们实际上处于不同层次。

---

# 二十、今天先停在这里，给你一道检查题

现在不看上面的答案，自己回答：

### ① `prepareRefresh()` 主要负责什么？

### ② `prepareRefresh()` 阶段有没有开始大量创建 Bean？

### ③ `obtainFreshBeanFactory()` 是干什么的？

### ④ BeanFactory 和 BeanDefinition 是什么关系？

### ⑤ 为什么 `refresh()` 可以被称为 Spring IOC 容器启动的核心流程？


---

## **Day 6 · `prepareBeanFactory()`**。

这一部分开始真正接触 Spring 的“内部组装”。

---

### 一、先回顾我们走到哪里了

现在的路线：

```
refresh()
   ↓
prepareRefresh()              ✅
   ↓
obtainFreshBeanFactory()       ✅
   ↓
prepareBeanFactory()           ← 现在
   ↓
postProcessBeanFactory()
   ↓
invokeBeanFactoryPostProcessors()
   ↓
registerBeanPostProcessors()
   ↓
...
```

前两个可以记成：

```
prepareRefresh()
    ↓
准备 ApplicationContext 自己

obtainFreshBeanFactory()
    ↓
准备 BeanFactory
```

那么：

> **`prepareBeanFactory()` 是干什么的？**

一句话：

> **对刚刚拿到的 BeanFactory 进行基础配置，让它具备完整的 Spring Bean 管理能力。**

---

### 二、先看源码骨架

Spring 6 中这个方法可以抽象成：

```
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    // 1. 设置 ClassLoader
    beanFactory.setBeanClassLoader(getClassLoader());

    // 2. 设置 SpEL 表达式解析器
    beanFactory.setBeanExpressionResolver(
            new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader())
    );

    // 3. 添加属性编辑器
    beanFactory.addPropertyEditorRegistrar(
            new ResourceEditorRegistrar(this, getEnvironment())
    );

    // 4. 添加 BeanPostProcessor
    beanFactory.addBeanPostProcessor(
            new ApplicationContextAwareProcessor(this)
    );

    // 5. 忽略某些自动注入接口
    beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
    beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

    // 6. 注册特殊依赖
    beanFactory.registerResolvableDependency(
            BeanFactory.class, beanFactory
    );

    beanFactory.registerResolvableDependency(
            ResourceLoader.class, this
    );

    beanFactory.registerResolvableDependency(
            ApplicationEventPublisher.class, this
    );

    beanFactory.registerResolvableDependency(
            ApplicationContext.class, this
    );

    // ...
}
```

别被代码吓到。

今天我们重点搞懂 **4 件事**：

```
ClassLoader
SpEL
BeanPostProcessor
Aware
```

其中后面两个最重要。

---

### 三、第一件事：ClassLoader

源码：

```
beanFactory.setBeanClassLoader(getClassLoader());
```

这个很好理解。

Spring 创建：

```
UserService
```

首先得知道：

> `UserService.class` 到底在哪里？怎么加载？

这就需要：

```
ClassLoader
```

也就是：

> **类加载器。**

你之前学 JVM 的时候已经学过：

```
.java
 ↓ 编译
.class
 ↓
ClassLoader
 ↓
Class对象
 ↓
JVM
```

现在 Spring 只是把 JVM 的：

```
ClassLoader
```

利用起来。

所以：

```
Spring
 ↓
BeanDefinition
 ↓
UserService.class
 ↓
ClassLoader加载
 ↓
Class对象
 ↓
创建Bean
```

这就是为什么 `prepareBeanFactory()` 要设置 ClassLoader。

---

### 四、第二件事：SpEL

源码：

```
beanFactory.setBeanExpressionResolver(
    new StandardBeanExpressionResolver(...)
);
```

这个：

```
BeanExpressionResolver
```

是干什么的？

它主要用于解析：

> **Spring 表达式语言 SpEL。**

例如：

```
@Value("#{2 + 3}")
private int number;
```

Spring 需要计算：

```
2 + 3
 ↓
5
```

再比如：

```
@Value("${server.port}")
private int port;
```

这里又涉及属性解析。

所以：

```
BeanFactory
    ↓
ExpressionResolver
    ↓
解析 Spring 表达式
```

你现在知道它是干嘛的就够了。

---

### 五、第三件事：BeanPostProcessor ⭐⭐⭐⭐⭐

现在进入真正重要的地方：

```
beanFactory.addBeanPostProcessor(
    new ApplicationContextAwareProcessor(this)
);
```

这里出现了：

# `BeanPostProcessor`

这个东西你后面会反复看到。

---

### 六、BeanPostProcessor 到底是什么？

先不要背定义。

假设 Spring 创建：

```
UserService userService =
    new UserService();
```

Spring 并不是：

```
new完
 ↓
直接扔进容器
```

而是：

```
new UserService()
      ↓
BeanPostProcessor
      ↓
各种处理
      ↓
初始化
      ↓
BeanPostProcessor
      ↓
最终Bean
```

所以：

> **BeanPostProcessor 是 Spring 在 Bean 创建过程中提供的扩展机制，可以对 Bean 进行加工处理。**

---

### 七、为什么 Spring 需要“加工”Bean？

因为 Spring 有大量功能都不是：

```
new UserService()
```

本身提供的。

比如：

### `@Autowired`

```
@Autowired
private UserDao userDao;
```

Spring 得帮你：

```
找到 UserDao
   ↓
注入 UserService
```

---

### `@PostConstruct`

```
@PostConstruct
public void init() {
}
```

Spring 得帮你：

```
创建Bean
 ↓
找到@PostConstruct
 ↓
调用init()
```

---

### AOP

```
@Transactional
public void save() {
}
```

Spring 最终可能需要：

```
原始对象
 ↓
创建代理
 ↓
代理对象
```

这些都离不开各种：

```
BeanPostProcessor
```

---

### 八、注意：这里的 ApplicationContextAwareProcessor 是什么？

你刚才看到：

```
new ApplicationContextAwareProcessor(this)
```

这个名字很长。

拆开：

```
ApplicationContext
+
Aware
+
Processor
```

它是一个：

> **BeanPostProcessor**

专门处理各种 `Aware` 接口。

这就进入下一个重要知识点。

---

### 九、Aware 是什么？

Spring 里面有一系列：

```
xxxAware
```

例如：

```
BeanNameAware
BeanFactoryAware
ApplicationContextAware
EnvironmentAware
ResourceLoaderAware
ApplicationEventPublisherAware
```

它们的核心思想：

> **让 Bean 能够感知 Spring 容器提供的某些对象或信息。**

---

### 十、举个最简单的例子

比如：

```
@Component
public class UserService implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
```

那么 Spring 创建 `UserService` 时：

```
UserService
    ↓
发现实现了 ApplicationContextAware
    ↓
把 ApplicationContext 注入进去
```

最终：

```
UserService
     │
     └── ApplicationContext
```

所以叫：

> **Aware：感知 / 获取 Spring 容器提供的对象。**

---

### 十一、那 ApplicationContextAwareProcessor 干了什么？

核心逻辑可以简单理解成：

```
Bean创建
   ↓
ApplicationContextAwareProcessor
   ↓
判断Bean是不是实现了Aware
   ↓
如果是
   ↓
把对应对象传给它
```

例如：

```
实现ApplicationContextAware
        ↓
注入ApplicationContext

实现EnvironmentAware
        ↓
注入Environment

实现ResourceLoaderAware
        ↓
注入ResourceLoader
```

所以：

```
ApplicationContextAwareProcessor
             ↓
        处理各种Aware
```

---

### 十二、为什么不直接用 `@Autowired`？

这是一个很好的思考。

例如：

```
@Autowired
private ApplicationContext context;
```

当然也可以获取。

但是 Spring 自己内部有很多基础设施 Bean，需要在非常底层的阶段就能够获取：

```
BeanFactory
ApplicationContext
Environment
ResourceLoader
...
```

所以 Spring 提供了：

```
Aware
```

这种更加底层的机制。

---

### 十三、第四件事：ignoreDependencyInterface()

现在源码还有：

```
beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
```

这个第一次看特别容易懵。

它不是：

> Spring 不支持 `ApplicationContextAware`

恰恰相反。

它是在说：

> **这些接口不应该按照普通的自动装配规则去寻找依赖。**

为什么？

因为：

```
ApplicationContextAware
```

应该由：

```
ApplicationContextAwareProcessor
```

专门处理。

而不是让普通的：

```
@Autowired
```

依赖解析机制去处理。

所以：

```
普通依赖
    ↓
@Autowired等机制

Aware依赖
    ↓
ApplicationContextAwareProcessor
```

这是两条不同的处理路径。

---

### 十四、第五件事：registerResolvableDependency()

这个更加有意思。

源码类似：

```
beanFactory.registerResolvableDependency(
    BeanFactory.class,
    beanFactory
);
```

还有：

```
ApplicationContext.class
        ↓
ApplicationContext实例
```

它的意思可以简单理解为：

> **告诉 BeanFactory：如果有人需要这些特殊类型的对象，直接给我指定的实例。**

例如：

```
@Autowired
private ApplicationContext applicationContext;
```

Spring 发现：

```
需要 ApplicationContext
```

然后：

```
BeanFactory
    ↓
ResolvableDependency
    ↓
ApplicationContext
    ↓
直接拿当前 ApplicationContext
```

而不是：

```
再去创建一个ApplicationContext Bean
```

---

### 十五、所以 prepareBeanFactory() 到底干了什么？

现在可以总结：

```
prepareBeanFactory()
       │
       ├── 设置 ClassLoader
       │
       ├── 设置 SpEL 解析器
       │
       ├── 注册资源编辑器
       │
       ├── 注册 BeanPostProcessor
       │       ↓
       │   ApplicationContextAwareProcessor
       │
       ├── 配置 Aware 接口
       │
       └── 注册特殊依赖
```

一句话：

> **`prepareBeanFactory()` 负责对 BeanFactory 进行基础能力配置，使它具备后续管理和创建 Spring Bean 所需要的基础设施。**

---

### 十六、现在把三个方法串起来

这是今天最重要的图：

```
refresh()
   ↓
prepareRefresh()
   │
   ├── 准备容器状态
   ├── 准备Environment
   └── 校验配置
   ↓
obtainFreshBeanFactory()
   │
   └── 获取/刷新BeanFactory
   ↓
prepareBeanFactory()
   │
   ├── ClassLoader
   ├── SpEL
   ├── BeanPostProcessor
   ├── Aware
   └── 特殊依赖
```

你会发现：

```
prepareRefresh()
```

准备的是：

> **ApplicationContext**

而：

```
prepareBeanFactory()
```

准备的是：

> **BeanFactory**

这两个不要混。

---

### 十七、现在有一个非常重要的区别

你现在已经遇到：

```
BeanFactoryPostProcessor
```

和：

```
BeanPostProcessor
```

我们必须把它们彻底分开。

### BeanFactoryPostProcessor

处理：

```
BeanDefinition
```

发生在：

```
Bean真正创建之前
```

---

### BeanPostProcessor

处理：

```
Bean对象
```

发生在：

```
Bean创建过程中
```

可以这样记：

```
BeanFactoryPostProcessor
        ↓
     改说明书

BeanPostProcessor
        ↓
     加工成品
```

这个比喻非常好记。

---

### 十八、现在考你 6 个问题

不要看上面的答案，自己回答。

 ① `prepareBeanFactory()` 和 `prepareRefresh()` 最大的区别是什么？

 ② 为什么 `prepareBeanFactory()` 要设置 ClassLoader？

 ③ `BeanPostProcessor` 是干什么的？

 ④ `ApplicationContextAware` 是干什么的？

⑤ `ApplicationContextAwareProcessor` 和 `ApplicationContextAware` 是什么关系？

 ⑥ `BeanFactoryPostProcessor` 和 `BeanPostProcessor` 最大区别是什么？

---

# `invokeBeanFactoryPostProcessors()`

前面我们已经走完：

```
refresh()
   ↓
prepareRefresh()                 ✅
   ↓
obtainFreshBeanFactory()          ✅
   ↓
prepareBeanFactory()              ✅
   ↓
postProcessBeanFactory()
   ↓
invokeBeanFactoryPostProcessors() ← 现在
```

从这里开始，Spring 源码会真正回答一个很重要的问题：

> **我们写的 `@Component`、`@Configuration`、`@Bean`、`@ComponentScan`，到底是怎么被 Spring 识别并注册成 BeanDefinition 的？**

---

# 一、先回忆 BeanDefinition

我们之前说：

```
@Component
public class UserService {
}
```

Spring 最终需要得到：

```
BeanDefinition
│
├── BeanClass = UserService
├── BeanName = userService
├── Scope = singleton
└── ...
```

但是问题来了：

> **谁负责把这些配置转换成 BeanDefinition？**

这里就要登场：

```
BeanFactoryPostProcessor
```

---

# 二、`invokeBeanFactoryPostProcessors()` 是干什么的？

先给你一句话：

> **`invokeBeanFactoryPostProcessors()` 负责执行容器中的 BeanFactoryPostProcessor，让这些后置处理器有机会在 Bean 实例化之前修改、解析和注册 BeanDefinition。**

注意几个关键词：

```
BeanFactoryPostProcessor
        ↓
BeanDefinition
        ↓
Bean实例化之前
```

---

# 三、为什么一定要在 Bean 创建之前？

假设：

```
@Component
public class UserService {
}
```

Spring 如果先：

```
创建 UserService
```

然后才发现：

```
哦，原来它是一个@Component
```

就晚了。

所以顺序必须是：

```
发现配置
   ↓
生成 BeanDefinition
   ↓
处理 BeanDefinition
   ↓
最后创建 Bean
```

而不是：

```
创建 Bean
   ↓
再研究怎么创建
```

所以：

# BeanFactoryPostProcessor 发生在 Bean 实例化之前

---

# 四、最重要的一个角色：ConfigurationClassPostProcessor ⭐⭐⭐⭐⭐

如果你现在问：

> Spring 中最重要的 BeanFactoryPostProcessor 是谁？

非常重要的一个就是：

```
ConfigurationClassPostProcessor
```

它负责处理大量 Spring 配置相关的东西。

比如：

```
@Configuration
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserService();
    }
}
```

还有：

```
@ComponentScan("com.xxx")
```

以及：

```
@Import(...)
```

这些都和：

```
ConfigurationClassPostProcessor
```

密切相关。

---

# 五、它到底干了什么？

假设你写：

```
@Configuration
@ComponentScan("com.example.service")
public class AppConfig {
}
```

Spring 一开始看到的只是：

```
AppConfig.class
```

然后：

```
ConfigurationClassPostProcessor
        ↓
解析@Configuration
        ↓
发现@ComponentScan
        ↓
扫描指定包
        ↓
发现UserService
        ↓
注册UserService的BeanDefinition
```

最终：

```
BeanDefinitionRegistry
│
├── appConfig
├── userService
├── userDao
├── orderService
└── ...
```

这就是一个非常重要的过程。

---

# 六、所以 `@Component` 是什么时候变成 BeanDefinition 的？

例如：

```
@Component
public class UserService {
}
```

最终会经历：

```
UserService.class
      ↓
组件扫描
      ↓
发现@Component
      ↓
创建BeanDefinition
      ↓
注册到BeanDefinitionRegistry
      ↓
以后创建UserService Bean
```

注意：

> **这里主要还是在注册 BeanDefinition，不是创建 UserService 对象。**

这一点非常重要。

---

# 七、BeanDefinitionRegistry 又是什么？

名字很长：

```
BeanDefinitionRegistry
```

拆开：

```
BeanDefinition
+
Registry
```

就是：

> **BeanDefinition 注册中心。**

你可以想象成：

```
BeanDefinitionRegistry
│
├── userService → BeanDefinition
├── userDao → BeanDefinition
├── orderService → BeanDefinition
└── ...
```

它主要负责：

```
注册
查询
删除
BeanDefinition
```

---

# 八、所以 Spring 启动其实可以分成两个大阶段

这是你现在非常应该形成的认识。

## 第一阶段：准备 BeanDefinition

```
配置
 ↓
@Component
@Configuration
@Bean
@ComponentScan
@Import
 ↓
BeanDefinition
```

---

## 第二阶段：根据 BeanDefinition 创建 Bean

```
BeanDefinition
      ↓
实例化
      ↓
属性注入
      ↓
初始化
      ↓
AOP
      ↓
Bean
```

所以：

```
id="4w8q8r"
配置 → BeanDefinition → Bean
```

这是 Spring IOC 的核心主线之一。

---

# 九、`invokeBeanFactoryPostProcessors()` 为什么这么复杂？

你以后看源码会发现：

```
PostProcessorRegistrationDelegate
    .invokeBeanFactoryPostProcessors(...)
```

里面有很多：

```
PriorityOrdered
Ordered
普通
```

看起来很复杂。

其实核心是在解决：

> **这些 BeanFactoryPostProcessor 到底应该按照什么顺序执行？**

因为不同后处理器之间存在依赖关系。

---

# 十、Spring 为什么要区分 PriorityOrdered / Ordered？

假设有：

```
Processor A
Processor B
Processor C
```

如果随便执行：

```
A → C → B
```

可能出问题。

所以 Spring 提供排序机制：

```
PriorityOrdered
      ↓
Ordered
      ↓
普通
```

简单理解：

```
优先级最高
    ↓
PriorityOrdered
    ↓
Ordered
    ↓
普通
    ↓
优先级最低
```

---

# 十一、这里有一个非常重要的面试点

不要把：

```
BeanFactoryPostProcessor
```

和：

```
BeanPostProcessor
```

混起来。

我们刚刚已经学过：

### BeanFactoryPostProcessor

```
BeanDefinition
      ↓
处理
      ↓
Bean创建之前
```

### BeanPostProcessor

```
Bean对象
      ↓
处理
      ↓
Bean创建过程中
```

所以现在：

```
invokeBeanFactoryPostProcessors()
```

处理的是：

> **BeanFactoryPostProcessor**

不是 BeanPostProcessor。

---

# 十二、那 `postProcessBeanFactory()` 是干嘛的？

在：

```
invokeBeanFactoryPostProcessors()
```

之前还有：

```
postProcessBeanFactory(beanFactory)
```

这个方法是：

> **给具体的 ApplicationContext 子类一个扩展机会，让它可以在 BeanFactoryPostProcessor 执行前，对 BeanFactory 做额外处理。**

你现在不用深挖。

因为这个方法本身在不同 ApplicationContext 中可能有不同实现。

目前只记：

```
postProcessBeanFactory()
    ↓
ApplicationContext给子类的扩展点
```

---

# 十三、现在把这一段完整串起来

到目前为止：

```
refresh()
   ↓
prepareRefresh()
   │
   └── 准备ApplicationContext
   ↓
obtainFreshBeanFactory()
   │
   └── 获取BeanFactory
   ↓
prepareBeanFactory()
   │
   └── 配置BeanFactory
   ↓
postProcessBeanFactory()
   │
   └── 给子类扩展
   ↓
invokeBeanFactoryPostProcessors()
   │
   └── 处理BeanDefinition
```

尤其是最后：

```
invokeBeanFactoryPostProcessors()
             ↓
ConfigurationClassPostProcessor
             ↓
解析配置类
             ↓
@ComponentScan
@Bean
@Import
@Configuration
             ↓
注册BeanDefinition
```

---

# 十四、一个完整例子

假设你写：

```
@Configuration
@ComponentScan("com.example")
public class AppConfig {
}
```

然后：

```
@Component
public class UserService {
}
```

Spring 启动：

```
ApplicationContext
       ↓
refresh()
       ↓
invokeBeanFactoryPostProcessors()
       ↓
ConfigurationClassPostProcessor
       ↓
解析 AppConfig
       ↓
发现 @ComponentScan
       ↓
扫描 com.example
       ↓
发现 UserService
       ↓
创建 UserService 的 BeanDefinition
       ↓
注册到 BeanDefinitionRegistry
```

此时：

```
UserService对象
```

**还不一定创建出来。**

现在只有：

```
UserService BeanDefinition
```

这是非常关键的。

---

# 十五、你可以把它理解成“施工图”

这个比喻非常适合你现在理解 Spring：

```
配置类 / 注解
      ↓
解析
      ↓
BeanDefinition
      ↓
施工图
      ↓
Spring按照施工图建房子
      ↓
Bean对象
```

也就是说：

```
BeanDefinition = 施工图

Bean = 建好的房子
```

而：

```
BeanFactoryPostProcessor
```

就是：

> **在正式施工之前修改/完善施工图的人。**

---

# 十六、接下来会进入 `registerBeanPostProcessors()`

这一步非常重要，因为前面我们一直说：

```
BeanPostProcessor
```

现在终于要正式注册它们了：

```
invokeBeanFactoryPostProcessors()
        ↓
处理BeanDefinition
        ↓
registerBeanPostProcessors()
        ↓
注册BeanPostProcessor
        ↓
后面创建Bean时使用
```

所以这里实际上形成了一个非常漂亮的先后关系：

```
① 先处理 BeanDefinition

        ↓

② 注册 BeanPostProcessor

        ↓

③ 最后创建 Bean

        ↓

④ BeanPostProcessor 参与 Bean 创建
```

这就是 Spring 为什么要把：

```
invokeBeanFactoryPostProcessors()
```

放在：

```
registerBeanPostProcessors()
```

前面的原因。

---

# 十七、现在你来回答 6 个问题

这一轮建议你认真回答，因为已经进入 Spring 源码的核心区域了。

 ① `invokeBeanFactoryPostProcessors()` 主要干什么？

 ② 为什么 BeanFactoryPostProcessor 必须在 Bean 实例化之前执行？

 ③ `ConfigurationClassPostProcessor` 主要负责什么？

 ④ `@ComponentScan` 和 BeanDefinition 有什么关系？

 ⑤ BeanDefinitionRegistry 是干什么的？

 ⑥ 为什么 Spring 要先执行 BeanFactoryPostProcessor，再注册 BeanPostProcessor？


---


# Day 6 · `registerBeanPostProcessors()`

这是 `refresh()` 里面非常重要的一步。

---

## 一、先看我们现在的位置

```
refresh()
   ↓
prepareRefresh()                       ✅
   ↓
obtainFreshBeanFactory()                ✅
   ↓
prepareBeanFactory()                    ✅
   ↓
postProcessBeanFactory()                ✅
   ↓
invokeBeanFactoryPostProcessors()       ✅
   ↓
registerBeanPostProcessors()            ← 现在
   ↓
initMessageSource()
   ↓
...
   ↓
finishBeanFactoryInitialization()
```

前面：

```
invokeBeanFactoryPostProcessors()
```

主要解决：

> **BeanDefinition 准备好了吗？**

现在：

```
registerBeanPostProcessors()
```

主要解决：

> **Bean 创建过程中需要的 BeanPostProcessor 准备好了吗？**

---

# 二、先理解为什么需要这一步

假设我们有：

```
@Component
public class UserService {

    @Autowired
    private UserDao userDao;
}
```

Spring 后面要创建：

```
UserService
```

但是创建的时候需要很多“加工工具”：

```
@Autowired
@PostConstruct
AOP
事务
...
```

这些功能背后大量依赖：

```
BeanPostProcessor
```

所以 Spring 必须先把这些：

```
BeanPostProcessor
```

注册到 BeanFactory。

于是：

```
registerBeanPostProcessors()
```

就来了。

---

# 三、这一步到底干什么？

一句话：

> **找到 Spring 容器中定义的 BeanPostProcessor，并按照一定顺序实例化、注册到 BeanFactory 中。**

最终变成：

```
BeanFactory
   │
   ├── BeanPostProcessor A
   ├── BeanPostProcessor B
   ├── BeanPostProcessor C
   └── ...
```

以后创建普通 Bean 时：

```
UserService
   ↓
BeanPostProcessor们
   ↓
处理
```

---

# 四、这里有一个很重要的细节

你可能会问：

> **BeanPostProcessor 自己也是 Bean 啊，谁来处理它？**

这就是 Spring 源码里一个很有意思的地方。

`registerBeanPostProcessors()` 本身会先找到：

```
BeanPostProcessor类型的Bean
```

然后把它们提前创建出来并注册。

也就是说：

```
BeanPostProcessor
        ↓
提前准备
        ↓
注册到BeanFactory
        ↓
以后处理其他Bean
```

---

# 五、为什么一定要提前注册？

想象一下：

```
创建 UserService
      ↓
需要 BeanPostProcessor
      ↓
发现还没有注册
```

那就麻烦了。

所以必须：

```
先注册 BeanPostProcessor
       ↓
再创建普通 Bean
```

这也是为什么：

```
registerBeanPostProcessors()
```

发生在：

```
finishBeanFactoryInitialization()
```

之前。

---

# 六、这和我们之前讲的 Bean 生命周期连起来了

现在你应该能把两部分连接起来：

### refresh() 阶段

```
registerBeanPostProcessors()
       ↓
把处理器准备好
```

然后真正创建 Bean：

```
new UserService()
       ↓
BeanPostProcessor
       ↓
属性填充
       ↓
BeanPostProcessor
       ↓
初始化
       ↓
BeanPostProcessor
```

所以：

> **`registerBeanPostProcessors()` 是“准备加工工具”。**

而 Bean 生命周期中的：

> **`postProcessBeforeInitialization()` / `postProcessAfterInitialization()` 是“真正使用加工工具”。**

这两个不要混。

---

# 七、这里会遇到一个非常重要的接口：`BeanPostProcessor`

例如：

```
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(
            Object bean,
            String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(
            Object bean,
            String beanName) {
        return bean;
    }
}
```

你可以看到两个非常重要的方法：

```
postProcessBeforeInitialization()
```

以及：

```
postProcessAfterInitialization()
```

---

# 八、这两个方法什么时候执行？

假设：

```
UserService
```

创建：

```
实例化
 ↓
属性填充
 ↓
Aware
 ↓
postProcessBeforeInitialization()
 ↓
初始化方法
 ↓
postProcessAfterInitialization()
 ↓
最终Bean
```

注意：

> **BeanPostProcessor 并不是只在实例化之后执行一次。**

它实际上会围绕初始化过程发挥作用。

---

# 九、为什么 AOP 经常出现在 `postProcessAfterInitialization()`？

这是非常经典的面试问题。

假设：

```
@Service
public class UserService {

    @Transactional
    public void save() {
    }
}
```

Spring 最终希望得到：

```
UserService原始对象
       ↓
判断需要代理
       ↓
创建代理对象
       ↓
最终放入容器
```

所以很多 AOP 相关处理器会在 Bean 创建后进行判断：

```
postProcessAfterInitialization()
       ↓
是否需要代理？
       ↓
需要
       ↓
创建代理
```

最终：

```
原始Bean
   ↓
代理Bean
   ↓
放入IOC容器
```

这就是为什么我们以后学习 AOP 源码的时候，会一直看到：

```
BeanPostProcessor
```

---

# 十、`registerBeanPostProcessors()` 里面为什么又有排序？

和前面的：

```
BeanFactoryPostProcessor
```

一样。

BeanPostProcessor 也不是随便执行的。

Spring 会根据：

```
PriorityOrdered
Ordered
普通
```

进行排序。

大致可以理解成：

```
PriorityOrdered
      ↓
Ordered
      ↓
普通 BeanPostProcessor
```

因为不同处理器之间可能存在顺序要求。

---

# 十一、一个非常重要的处理器：`AutowiredAnnotationBeanPostProcessor`

你之前学过：

```
@Autowired
private UserDao userDao;
```

那么：

> **是谁发现 `@Autowired` 并完成注入的？**

一个非常重要的角色就是：

```
AutowiredAnnotationBeanPostProcessor
```

它就是一个：

```
BeanPostProcessor
```

所以整个过程可以理解成：

```
registerBeanPostProcessors()
       ↓
注册 AutowiredAnnotationBeanPostProcessor
       ↓
以后创建 UserService
       ↓
发现 @Autowired
       ↓
完成依赖注入
```

这就把你以前学过的：

```
@Autowired
```

和 Spring 源码真正连接起来了。

---

# 十二、还有一个重要处理器：`CommonAnnotationBeanPostProcessor`

例如：

```
@PostConstruct
public void init() {
}
```

背后也有相关的：

```
CommonAnnotationBeanPostProcessor
```

所以：

```
@Autowired
        ↓
AutowiredAnnotationBeanPostProcessor
```

```
@PostConstruct
        ↓
CommonAnnotationBeanPostProcessor
```

你现在先建立这种“功能 → 处理器”的映射。

---

# 十三、现在你会发现 Spring 的很多注解突然串起来了

以前你可能觉得：

```
@Autowired
@PostConstruct
@Transactional
@Component
```

都是“Spring 魔法”。

现在开始能看到背后的结构：

```
Spring启动
    ↓
准备 BeanDefinition
    ↓
注册 BeanPostProcessor
    ↓
创建 Bean
    ↓
BeanPostProcessor
    ↓
处理各种注解和功能
```

所以 Spring 并不是：

> “看到注解自动就懂了。”

而是：

> **Spring 提前准备了一套处理器，在合适的生命周期阶段处理这些注解。**

这个思想非常重要。

---

# 十四、到这里，refresh() 又前进了一步

现在变成：

```
refresh()
   ↓
prepareRefresh()
   ↓
obtainFreshBeanFactory()
   ↓
prepareBeanFactory()
   ↓
postProcessBeanFactory()
   ↓
invokeBeanFactoryPostProcessors()
       ↓
       BeanDefinition准备
   ↓
registerBeanPostProcessors()
       ↓
       BeanPostProcessor准备
   ↓
...
```

你可以发现一个非常漂亮的顺序：

```
先准备“说明书”
        ↓
BeanDefinition

再准备“加工工具”
        ↓
BeanPostProcessor

最后真正“生产”
        ↓
Bean
```

---

# 十五、接下来就开始真正创建 Bean 了

后面的核心方法：

```
finishBeanFactoryInitialization()
```

这是我们后面一定要重点讲的。

因为这里最终会进入：

```
preInstantiateSingletons()
```

然后：

```
BeanDefinition
    ↓
实例化
    ↓
populateBean
    ↓
依赖注入
    ↓
Aware
    ↓
初始化
    ↓
BeanPostProcessor
    ↓
AOP
    ↓
最终Bean
```

也就是说：

> **前面我们一直在“搭建 Spring 工厂”，这里才真正开始大量生产 Bean。**

---

# 十六、你现在先回答这 5 个问题

不用追求一字不差。

### ① `registerBeanPostProcessors()` 主要干什么？

### ② 为什么 BeanPostProcessor 要在创建普通 Bean 之前注册？

### ③ `@Autowired` 背后一个重要的 BeanPostProcessor 是谁？

### ④ `BeanPostProcessor` 的两个核心方法叫什么？

### ⑤ `registerBeanPostProcessors()` 和 Bean 生命周期里的 BeanPostProcessor 执行有什么区别？


---

# `finishBeanFactoryInitialization()`

这一部分就是：

> **Spring 真正开始创建 Bean 的地方。**

---

# 一、先看 refresh 最后的流程

现在完整流程：

```
refresh()

↓
prepareRefresh()
准备ApplicationContext

↓
obtainFreshBeanFactory()
获取BeanFactory

↓
prepareBeanFactory()
配置BeanFactory

↓
invokeBeanFactoryPostProcessors()
处理BeanDefinition

↓
registerBeanPostProcessors()
注册BeanPostProcessor

↓
finishBeanFactoryInitialization()
⭐⭐ 创建Bean

↓
finishRefresh()
完成刷新
```

注意：

之前所有步骤其实都是：

> 准备工作

现在：

```
finishBeanFactoryInitialization()
```

才是真正：

> 生产 Bean

---

# 二、这个方法名字怎么理解？

拆开：

```
finish
BeanFactory
Initialization
```

意思：

> 完成 BeanFactory 的初始化。

但是它具体做什么？

核心一句：

> **实例化所有非懒加载的单例 Bean。**

---

# 三、什么叫非懒加载单例 Bean？

Spring 默认：

```
@Component
public class UserService {

}
```

默认：

```
scope = singleton
```

也就是：

整个 Spring 容器：

```
UserService对象
只有一个
```

---

但是如果：

```
@Lazy
@Component
public class UserService {

}
```

表示：

懒加载：

```
启动时不创建

第一次使用时创建
```

---

所以：

```
finishBeanFactoryInitialization()
```

创建的是：

```
singleton
+
非lazy
```

例如：

```
UserService
OrderService
UserDao
```

这些。

---

# 四、源码里面最关键的一句

进入：

```
finishBeanFactoryInitialization(beanFactory)
```

里面最终会调用：

```
beanFactory.preInstantiateSingletons();
```

这个方法非常重要。

记住：

```
finishBeanFactoryInitialization()

          ↓

preInstantiateSingletons()

          ↓

创建单例Bean
```

---

# 五、preInstantiateSingletons() 做什么？

名字：

```
pre
Instantiate
Singletons
```

翻译：

> 提前实例化单例对象。

为什么叫提前？

因为默认情况下：

Spring启动时：

```
把所有单例Bean提前创建好
```

而不是：

```
第一次调用才创建
```

---

例如：

有：

```
@Service
public class UserService {
}
```

启动：

```
Spring启动
    ↓
发现UserService
    ↓
提前创建
    ↓
放入IOC容器
```

之后：

```
@Autowired
UserService userService;
```

直接拿。

---

# 六、真正创建 Bean 的入口

继续往下：

```
preInstantiateSingletons()

        ↓

getBean()

        ↓

doGetBean()

        ↓

createBean()

        ↓

doCreateBean()
```

这就是 Bean 创建链。

非常重要：

记住：

```
getBean()
    ↓
doGetBean()
    ↓
createBean()
```

---

# 七、Spring 创建 Bean 的完整过程

终于把之前学的生命周期串起来。

整体：

```
BeanDefinition

        ↓

实例化 Bean

        ↓

属性填充

        ↓

Aware接口处理

        ↓

BeanPostProcessor
before

        ↓

初始化方法

        ↓

BeanPostProcessor
after

        ↓

AOP代理

        ↓

最终Bean
```

---

# 八、第一步：实例化 Bean

源码大概：

```
createBeanInstance()
```

作用：

创建对象。

比如：

```
UserService userService =
        new UserService();
```

此时：

```
对象存在了
```

但是：

```
属性还没有注入
```

例如：

```
@Autowired
private UserDao userDao;
```

现在：

```
userDao = null
```

---

# 九、第二步：属性填充 populateBean()

源码：

```
populateBean()
```

作用：

> 给 Bean 注入属性。

例如：

原来：

```
UserService
{
    UserDao userDao=null;
}
```

执行：

```
populateBean()
```

之后：

```
UserService
{
    UserDao userDao=xxx;
}
```

---

这里就会使用：

```
AutowiredAnnotationBeanPostProcessor
```

处理：

```
@Autowired
```

所以：

```
@Autowired
        ↓
AutowiredAnnotationBeanPostProcessor
        ↓
完成注入
```

---

# 十、第三步：Aware 回调

比如：

```
public class UserService
implements ApplicationContextAware
```

Spring：

创建对象之后：

发现：

```
实现Aware接口
```

然后：

调用：

```
setApplicationContext()
```

把容器给它。

---

流程：

```
Bean
 ↓
判断Aware
 ↓
调用Aware方法
 ↓
注入Spring内部对象
```

---

# 十一、第四步：BeanPostProcessor before

执行：

```
postProcessBeforeInitialization()
```

例如：

```
Bean
 ↓
前置处理
```

---

# 十二、第五步：初始化方法

这里包括：

### 1. InitializingBean

例如：

```
public class UserService
implements InitializingBean {

@Override
public void afterPropertiesSet(){

}

}
```

---

### 2. @PostConstruct

例如：

```
@PostConstruct
public void init(){

}
```

---

### 3. init-method

XML：

```
<bean init-method="init">
```

---

这些都属于：

初始化阶段。

---

# 十三、第六步：BeanPostProcessor after

执行：

```
postProcessAfterInitialization()
```

这里非常重要：

因为：

## AOP代理通常发生这里

例如：

```
@Transactional
public void save(){

}
```

Spring：

```
原始UserService

        ↓

判断需要代理

        ↓

创建代理对象

        ↓

返回代理
```

所以：

最终放入容器的可能不是：

```
UserService
```

而是：

```
UserService代理对象
```

---

# 十四、现在完整串起来

这是今天最重要的一张图：

```
BeanDefinition

      ↓

createBeanInstance()
创建对象

      ↓

populateBean()
依赖注入

      ↓

Aware接口

      ↓

BeanPostProcessor.before

      ↓

初始化方法

      ↓

BeanPostProcessor.after

      ↓

AOP代理

      ↓

最终Bean
```

---

# 十五、回到你的疑问

之前你问：

> Bean生命周期中后置处理器不是在实例化之后吗？

现在就完全对应上了：

因为：

```
registerBeanPostProcessors()
```

只是：

> 提前把处理器准备好

而：

```
BeanPostProcessor.before/after
```

才是真正：

> 在Bean创建过程中执行

所以：

```
准备工具
        ↓
registerBeanPostProcessors()

使用工具
        ↓
Bean生命周期
```

---

# 十六、今天到这里，你应该掌握：

✅ `finishBeanFactoryInitialization()` 的作用

✅ `preInstantiateSingletons()`

✅ getBean 创建链

```
getBean
 ↓
doGetBean
 ↓
createBean
 ↓
doCreateBean
```

✅ Bean生命周期完整流程


---


## **Day 6 最后一块核心：三级缓存与循环依赖**。

这一部分是 Spring 面试中非常高频的内容，也是很多人“会背但不会解释”的地方。

---

# 一、什么是循环依赖？

先看一个例子：

```
@Service
public class A {

    @Autowired
    private B b;

}
```

```
@Service
public class B {

    @Autowired
    private A a;

}
```

关系：

```
 id="a8c3f2"
A
↓
需要B

B
↓
需要A
```

形成：

```
 id="v8k1p6"
A → B → A → B → ...
```

这就是：

> **循环依赖**

---

# 二、如果没有 Spring，怎么创建？

假设手动创建：

```
new A()
```

A需要：

```
B
```

于是：

```
new B()
```

B需要：

```
A
```

于是：

```
new A()
```

然后无限循环：

```
 id="k5n3r7"
new A()
 ↓
new B()
 ↓
new A()
 ↓
new B()
```

最终：

```
StackOverflowError
```

---

# 三、Spring 为什么能解决？

关键：

> **Spring 不会等对象完全创建完成才放入容器。**

它会：

先创建一个“半成品”。

例如：

```
 id="u7x2m9"
A对象
{
    b = null
}
```

对象已经存在。

只是：

```
B属性还没注入
```

然后：

把这个提前暴露出去。

这就是：

## 提前暴露

---

# 四、Spring Bean 创建过程（循环依赖版本）

正常：

```
 id="q4z8s1"
创建A
 ↓
注入B
 ↓
创建B
 ↓
完成B
 ↓
完成A
```

循环：

```
 id="p2m9x5"
创建A
 ↓
发现需要B
 ↓
创建B
 ↓
发现需要A
 ↓
发现A正在创建
 ↓
提前拿到A
 ↓
B完成
 ↓
A完成
```

关键：

```
 id="d7y1k8"
发现A正在创建
       ↓
不能重新new A
       ↓
拿之前那个A
```

---

# 五、Spring 如何知道 A 正在创建？

靠：

```
 id="g8m2x4"
singletonsCurrentlyInCreation
```

这个集合。

简单理解：

```
 id="r9p3k6"
正在创建：

[A]
```

当创建：

```
 id="w6n1q5"
B
```

发现：

```
需要A
```

Spring检查：

```
A是不是正在创建？
```

发现：

```
是
```

于是：

进入提前暴露流程。

---

# 六、三级缓存是什么？

Spring 默认有三个 Map：

## 一级缓存

```
 id="z4h7n2"
singletonObjects
```

存：

> 完全初始化完成的 Bean

例如：

```
 id="m8p3x9"
A对象
B对象
```

状态：

```
完整Bean
```

---

## 二级缓存

```
 id="k6t1w4"
earlySingletonObjects
```

存：

> 提前暴露的 Bean

也就是：

半成品。

例如：

```
 id="s3v8q2"
A对象

但是：
b属性还没注入
```

---

## 三级缓存

```
 id="f5n9x7"
singletonFactories
```

存：

> Bean工厂

不是 Bean 本身。

形式：

```
 id="u2m6c8"
ObjectFactory
```

作用：

需要的时候：

```
三级缓存
 ↓
执行工厂
 ↓
创建提前暴露对象
```

---

# 七、为什么需要三级缓存？

这是面试重点。

很多人会问：

> 为什么不能只有一级缓存和二级缓存？

答案：

因为：

## AOP代理问题

假设：

```
@Service
@Transactional
class A
```

最终 Spring 放入容器的不是：

```
原始A对象
```

而是：

```
代理A对象
```

所以提前暴露的时候：

不能简单暴露：

```
原始对象
```

需要：

> 有机会创建代理对象。

于是三级缓存出现。

---

# 八、三级缓存流程

假设：

A依赖B。

创建A：

第一步：

实例化A：

```
 id="e7v4m1"
A原始对象
```

第二步：

放入三级缓存：

```
 id="c3m8x2"
singletonFactories

A → ObjectFactory
```

注意：

不是放A。

放的是：

```
创建A的工厂
```

---

第三步：

创建B。

B需要A：

Spring发现：

一级：

```
没有
```

二级：

```
没有
```

三级：

```
有A的工厂
```

于是：

执行：

```
ObjectFactory.getObject()
```

得到：

```
A提前引用
```

---

如果A需要AOP：

这里可以返回：

```
A代理对象
```

而不是原始A。

这就是三级缓存最大的价值。

---

# 九、为什么不能直接放二级缓存？

假设只有：

```
 id="h9k2p4"
二级缓存
```

存：

```
A对象
```

问题：

如果后面发现：

```
A需要代理
```

怎么办？

已经暴露原对象了。

但是最终：

IOC里面应该放：

```
代理A
```

不是：

```
原A
```

所以需要：

```
三级缓存
```

延迟决定：

> 到底返回原对象还是代理对象。

---

# 十、完整循环依赖流程

现在串起来：

```
 id="n7c5m2"
创建A

↓

实例化A

↓

A放入三级缓存

↓

填充A属性

↓

发现需要B

↓

创建B

↓

B需要A

↓

三级缓存获取A

↓

执行ObjectFactory

↓

得到A引用

↓

B完成

↓

A完成

↓

A放入一级缓存
```

最终：

```
 id="q1v8s4"
一级缓存：

A
B
```

三级缓存：

删除。

---

# 十一、为什么三级缓存只解决单例循环依赖？

面试经常问。

因为 Spring 的三级缓存：

```
singletonObjects
```

只管理：

> 单例 Bean

如果：

```
@Scope("prototype")
```

原型 Bean：

每次创建新对象。

没有统一容器缓存。

所以无法解决。

---

# 十二、Spring 循环依赖限制

三级缓存解决的是：

✅

```
单例
+
setter属性注入
```

例如：

```
@Autowired
private B b;
```

---

不能解决：

❌ 构造器循环依赖

例如：

```
class A {

    public A(B b){

    }
}
```

因为：

创建A之前：

必须先有B。

创建B之前：

必须先有A。

没有机会提前暴露。

---

# 十三、面试回答模板 ⭐⭐⭐

如果面试官问：

> Spring 为什么需要三级缓存？

可以这样回答：

> Spring 为了解决单例 Bean 的循环依赖问题，引入了三级缓存。一级缓存存放完整初始化后的 Bean，二级缓存存放提前暴露的 Bean，三级缓存存放 Bean 创建工厂。三级缓存的作用是延迟决定提前暴露的是原始对象还是代理对象，从而支持 AOP 场景下的循环依赖。

这个答案已经达到中高级面试水平。

---

# 十四、Day6 到这里基本完成

整个 Spring IOC refresh 流程：

```
refresh()

↓

prepareRefresh()
准备环境

↓

obtainFreshBeanFactory()
获取BeanFactory

↓

prepareBeanFactory()
配置BeanFactory

↓

invokeBeanFactoryPostProcessors()
处理BeanDefinition

↓

registerBeanPostProcessors()
注册处理器

↓

finishBeanFactoryInitialization()
创建Bean

↓

Bean生命周期

↓

三级缓存解决循环依赖

↓

finishRefresh()
完成启动
```


---

# 一、面试问题

## ⭐⭐⭐ Spring IOC 容器启动过程是什么？

这是 Spring 面试非常高频的问题。

---

# 二、普通回答（初级）

如果面试官问：

> Spring 容器启动过程？

不要一上来背 13 个方法。

先说整体：

> Spring IOC 容器启动的核心流程就是 ApplicationContext 的 refresh() 方法。refresh() 负责完成 BeanFactory 创建、BeanDefinition 加载、后置处理器注册、Bean 创建以及容器刷新完成等工作。

然后展开。

---

# 三、标准面试回答

可以这样说：

---

## 第一步：创建 BeanFactory

```
obtainFreshBeanFactory()
```

作用：

> 创建并获取 BeanFactory，同时加载 BeanDefinition。

这里得到：

```
BeanFactory

里面保存：

BeanDefinition
```

关系：

```
BeanFactory
     |
     |
 BeanDefinition
```

---

## 第二步：准备 BeanFactory

```
prepareBeanFactory()
```

作用：

给 BeanFactory 设置基础能力：

比如：

- ClassLoader
- BeanExpressionResolver
- 添加一些系统 BeanPostProcessor

简单说：

> 对 BeanFactory 做基础配置。

---

## 第三步：执行 BeanFactoryPostProcessor

方法：

```
invokeBeanFactoryPostProcessors()
```

作用：

> 在 Bean 创建之前修改 BeanDefinition。

例如：

```
@Configuration
@Bean
@ComponentScan
```

这些配置：

通过：

```
ConfigurationClassPostProcessor
```

解析。

最终：

```
配置
 ↓
BeanDefinition
```

---

## 第四步：注册 BeanPostProcessor

方法：

```
registerBeanPostProcessors()
```

作用：

提前准备 Bean 创建过程中需要的处理器。

例如：

```
@Autowired

↓

AutowiredAnnotationBeanPostProcessor
```

还有：

```
AOP

↓

AbstractAutoProxyCreator
```

---

## 第五步：创建 Bean

方法：

```
finishBeanFactoryInitialization()
```

进入：

```
preInstantiateSingletons()
```

开始创建单例 Bean。

流程：

```
BeanDefinition

↓

实例化

↓

属性注入

↓

Aware回调

↓

BeanPostProcessor前置

↓

初始化方法

↓

BeanPostProcessor后置

↓

AOP代理

↓

最终Bean
```

---

## 第六步：完成容器刷新

方法：

```
finishRefresh()
```

作用：

> 发布容器刷新完成事件，标志 Spring IOC 容器启动完成。

---

# 四、面试完整答案（背这个）

你可以直接这样说：

> Spring IOC 容器启动的核心方法是 ApplicationContext 的 refresh() 方法。首先通过 obtainFreshBeanFactory 获取 BeanFactory，并加载 BeanDefinition，然后 prepareBeanFactory 对 BeanFactory 做基础配置。之后执行 BeanFactoryPostProcessor，对 BeanDefinition 进行修改和解析，例如处理@Configuration、@Bean、@ComponentScan等配置。接着注册 BeanPostProcessor，为后续 Bean 创建过程提供扩展能力。最后通过 finishBeanFactoryInitialization 创建非懒加载单例 Bean，在 Bean 创建过程中完成实例化、依赖注入、初始化以及 AOP 代理，最终通过 finishRefresh 完成整个 IOC 容器启动。

这个回答：

- 初级开发 ✅
- 1～3年经验面试 ✅
- Spring源码方向 ✅

---

# 五、现在把 Day6 知识压缩成一张图

你以后复习看这个：

```
                 refresh()
                    |
                    |
        ---------------------------
        |                         |
   准备阶段                    创建阶段
        |                         |
        |                         |
BeanFactory准备             finishBeanFactoryInitialization
        |                         |
        |                         |
BeanDefinition              Bean生命周期
        |                         |
        |                         |
BeanFactoryPostProcessor     实例化
        |                         |
        |                         |
BeanPostProcessor准备       属性注入
                              |
                              |
                         初始化
                              |
                              |
                         AOP代理
                              |
                              |
                           Bean
```

---

# 六、Day6 最容易错的 5 个点（面试陷阱）

## 1. refresh 是创建 Bean 吗？

❌ 错

正确：

> refresh 是 IOC 容器启动总入口，其中后半部分才触发 Bean 创建。

---

## 2. BeanFactoryPostProcessor 和 BeanPostProcessor一样吗？

❌ 错

记：

```
BeanFactoryPostProcessor

改图纸

BeanPostProcessor

加工成品
```

---

## 3. BeanPostProcessor什么时候执行？

不是：

> 注册的时候执行

而是：

> Bean 创建过程中执行。

---

## 4. 三级缓存解决什么？

不是：

> 所有循环依赖

而是：

> 单例 Bean 的 setter 属性循环依赖。

---

## 5. 为什么三级缓存不是二级缓存？

因为：

> 三级缓存可以延迟创建代理对象，解决 AOP 代理情况下的循环依赖。

---

# 七、Day6完成情况

现在你的 Spring IOC 源码部分：

```
IOC思想                 ✅
DI                      ✅
BeanFactory             ✅
ApplicationContext      ✅
BeanDefinition          ✅
refresh流程             ✅
BeanFactoryPostProcessor ✅
BeanPostProcessor        ✅
Bean生命周期             ✅
Aware机制               ✅
三级缓存                 ✅
循环依赖                 ✅
```

Day6 正式完成。