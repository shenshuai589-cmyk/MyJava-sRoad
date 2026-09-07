
# 🎯 Day 8 今日目标

今天重点掌握 7 个问题：

|优先级|问题|
|---|---|
|⭐⭐⭐|1. Spring Bean 的生命周期是什么？|
|⭐⭐⭐|2. Spring 是怎么创建 Bean 的？|
|⭐⭐⭐|3. BeanPostProcessor 在 Bean 生命周期哪个阶段执行？|
|⭐⭐⭐|4. Spring 为什么需要三级缓存？|
|⭐⭐⭐|5. 什么是循环依赖？Spring 是怎么解决的？|
|⭐⭐|6. 为什么构造器循环依赖无法通过三级缓存解决？|
|⭐⭐|7. 为什么 `@Autowired` 的循环依赖可以解决，而 `@Resource` 等情况需要具体分析？|

其中：

> **问题 1～5 是今天最重要的。**

---

# 一、先搞懂：什么是 Bean 生命周期？

面试官问：

> **Spring Bean 的生命周期是什么？**

不要一上来背几十个方法。

你先记住这条主线：

```
BeanDefinition
      ↓
实例化
      ↓
属性注入
      ↓
Aware接口
      ↓
BeanPostProcessor
      ↓
初始化
      ↓
BeanPostProcessor
      ↓
Bean准备完成
      ↓
使用
      ↓
销毁
```

这就是你今天的核心地图。

---

# 二、第一阶段：BeanDefinition

Spring 并不是一启动就：

```
new UserService();
```

而是先有：

```
UserService
    ↓
BeanDefinition
```

BeanDefinition 可以理解成：

> **Spring 用来描述“这个 Bean 应该怎么创建”的配置对象。**

里面可以包含：

```
Bean类型
作用域
是否懒加载
初始化方法
销毁方法
依赖关系
...
```

所以：

```
BeanDefinition
      ↓
Spring知道怎么创建Bean
```

---

# 三、第二阶段：实例化

例如：

```
@Service
public class UserService {
}
```

Spring 最终需要创建：

```
new UserService();
```

这一步叫：

> **实例化（Instantiation）**

注意：

### 实例化 ≠ 完整创建

这点非常重要。

```
实例化
 ↓
只是把对象创建出来
```

例如：

```
new UserService()
```

但：

```
@Autowired
private UserMapper userMapper;
```

这时候还没有完成注入。

---

# 四、第三阶段：属性注入

实例化完成：

```
UserService对象
```

然后 Spring 开始处理：

```
@Autowired
private UserMapper userMapper;
```

把：

```
UserMapper
```

注入：

```
UserService
```

所以：

```
实例化
 ↓
属性填充
 ↓
依赖注入完成
```

---

# 五、第四阶段：Aware 接口

如果 Bean 实现了：

```
BeanNameAware
```

或者：

```
ApplicationContextAware
```

Spring 会给它一些容器相关的信息。

例如：

```
@Component
public class MyBean implements BeanNameAware {

    @Override
    public void setBeanName(String name) {
        System.out.println(name);
    }
}
```

Spring 会告诉它：

> “你的 Bean 名字叫 xxx。”

初级面试：

> **知道 Aware 是让 Bean 获取 Spring 容器相关信息即可。**

不用背所有 Aware。

---

# 六、第五阶段：BeanPostProcessor

这就是你已经学过的重点。

BeanPostProcessor 有两个重要阶段：

```
BeanPostProcessor
       ↓
┌───────────────┐
│               │
初始化前       初始化后
│               │
before          after
```

也就是：

```
postProcessBeforeInitialization()
```

和：

```
postProcessAfterInitialization()
```

---

# 七、为什么 BeanPostProcessor 这么重要？

因为很多 Spring 功能都依赖它。

例如我们昨天学的：

> **AOP**

就是其中一个典型应用。

大致：

```
Bean创建
   ↓
初始化
   ↓
BeanPostProcessor
   ↓
AOP自动代理创建器
   ↓
判断是否需要代理
   ↓
创建代理对象
```

所以你现在应该形成一个非常重要的认知：

> **BeanPostProcessor 是 Spring 对 Bean 进行扩展和增强的重要入口。**

---

# 八、第六阶段：初始化

接下来 Bean 会进行初始化。

这里可能涉及：

```
@PostConstruct
```

以及：

```
InitializingBean
```

以及：

```
init-method
```

你现在不需要把这些全部背下来。

知道：

> **初始化阶段主要完成 Bean 正式使用前的初始化工作。**

即可。

---

# 九、第七阶段：Bean 创建完成

最后：

```
Bean创建完成
```

Spring 容器中保存的最终对象可能是：

```
原始对象
```

也可能是：

```
代理对象
```

例如昨天的 AOP：

```
UserService
     ↓
AOP代理
     ↓
最终Bean
```

所以你现在会发现：

### Day 7 和 Day 8 是连起来的。

---

# 🔥 十、现在进入今天最重要的：循环依赖

假设：

```
@Service
public class A {

    @Autowired
    private B b;
}
```

同时：

```
@Service
public class B {

    @Autowired
    private A a;
}
```

形成：

```
A → B
↑   ↓
└───┘
```

这就是：

> **循环依赖（Circular Dependency）**

---

# 十一、为什么循环依赖会有问题？

假设 Spring：

```
创建 A
```

发现 A 需要：

```
B
```

于是：

```
创建 B
```

发现 B 又需要：

```
A
```

于是：

```
创建 A
```

然后：

```
A → B → A → B → A → ...
```

如果 Spring 什么都不做：

> **无限创建。**

---

# 十二、Spring 怎么解决？

这就是经典的：

# ⭐⭐⭐ 三级缓存

Spring 单例 Bean 相关的三级缓存可以简单理解成：

```
一级缓存
singletonObjects

二级缓存
earlySingletonObjects

三级缓存
singletonFactories
```

不要急着背名字。

先理解：

> **三级缓存的核心目的，是在 Bean 还没有完全创建完成的时候，提前暴露一个“早期引用”，让另一个 Bean 可以先拿到它。**

---

# 十三、用 A → B → A 理解

现在：

```
创建 A
```

A 已经实例化：

```
A对象
```

但是还没完成：

```
属性注入
初始化
```

这时候：

```
A
↓
需要B
```

Spring 开始：

```
创建B
```

B 实例化之后：

```
B
↓
需要A
```

这时候 Spring 发现：

> A 已经实例化了，只是还没有完全初始化。

于是：

```
三级缓存
     ↓
提前暴露A的早期引用
     ↓
B拿到A
     ↓
B完成创建
     ↓
A继续完成创建
```

于是：

```
A → B
↑   ↓
└───┘
```

就被解决了。

---

# 十四、为什么需要“三级”？

这里是面试高频追问：

> **为什么一级缓存不够？为什么还要三级缓存？**

核心原因：

> **Spring 不只是要解决循环依赖，还要考虑 AOP 代理对象的创建。**

例如 A：

```
@Async
@Service
public class A {
}
```

或者：

```
A
 ↓
AOP代理
```

如果循环依赖过程中直接把：

```
原始A对象
```

暴露出去，最终容器里可能是：

```
代理A
```

但是 B 拿到的却是：

```
原始A
```

这就出现：

> **同一个 Bean 出现两个不同对象引用的问题。**

所以三级缓存中的：

```
singletonFactories
```

可以提供一个：

> **获取 Bean 早期引用的工厂。**

必要时可以在这里得到经过处理的早期引用。

---

# 十五、三级缓存怎么记？

不要死背源码。

记三个作用：

```
一级缓存
↓
完整创建好的 Bean

二级缓存
↓
提前暴露的早期 Bean

三级缓存
↓
获取早期 Bean 的工厂
```

可以简单记：

> **一级放成品，二级放半成品，三级放“半成品怎么拿”的工厂。**

这个比死记变量名好记得多。

---

# 十六、为什么构造器循环依赖解决不了？

这个是今天第二个高频追问。

例如：

```
@Service
public class A {

    private final B b;

    public A(B b) {
        this.b = b;
    }
}
```

B：

```
@Service
public class B {

    private final A a;

    public B(A a) {
        this.a = a;
    }
}
```

问题：

```
创建A
 ↓
必须先创建B
 ↓
创建B
 ↓
必须先创建A
 ↓
创建A
 ↓
...
```

关键在于：

> **A 连对象都还没实例化出来。**

而三级缓存解决循环依赖的前提之一是：

> **Bean 至少已经完成实例化，可以提前暴露早期引用。**

构造器注入：

```
实例化A之前
 ↓
必须先拿到B
```

所以：

> **连 A 的半成品都没有，三级缓存自然没办法提前暴露 A。**

---

# ⭐ 这个一定记住

```
字段注入 / setter注入
        ↓
可以先实例化
        ↓
再注入依赖
        ↓
三级缓存有机会解决循环依赖

构造器注入
        ↓
实例化之前就必须拿到依赖
        ↓
无法提前暴露
        ↓
无法解决
```

---

# 🎯 Day 8 今天先掌握到这里

你现在不要去背源码。

先把这张图记住：

```
                  Spring Bean生命周期
                         ↓
                  BeanDefinition
                         ↓
                      实例化
                         ↓
                     属性注入
                         ↓
                     Aware
                         ↓
             BeanPostProcessor 前置
                         ↓
                       初始化
                         ↓
             BeanPostProcessor 后置
                         ↓
                      Bean完成
                         ↓
                        使用
                         ↓
                        销毁


                   循环依赖
                       ↓
                  A → B → A
                       ↓
                   三级缓存
                       ↓
             ┌─────────┼─────────┐
             ↓         ↓         ↓
           一级       二级       三级
          成品       早期对象    对象工厂
```

---

# 🧠 Day 8 第一轮测试

现在和之前一样，**不要看上面的答案**，用自己的话回答：

### 问题 1 ⭐⭐⭐

> Spring Bean 的生命周期大概是什么？

>BeanPostProcessor 是 Spring 扩展 Bean 生命周期的重要入口，而 AOP 自动代理创建器就是利用这个机制，在 Bean 创建过程中完成代理对象的创建。

---

### 问题 2 ⭐⭐⭐

> BeanPostProcessor 在 Bean 生命周期中有什么作用？为什么 AOP 会用到它？

>  BeanPostProcessor 是 Spring 提供的 Bean 后置处理机制，可以在 Bean 初始化前后对 Bean 进行处理和增强。Spring AOP 中的自动代理创建器属于 BeanPostProcessor，它会在 Bean 创建过程中判断当前 Bean 是否匹配切面，如果需要，就创建代理对象。

---

### 问题 3 ⭐⭐⭐

> 什么是循环依赖？举一个 A → B → A 的例子。

> 循环依赖是指两个或多个 Bean 之间互相依赖，形成闭环。例如 A 依赖 B，而 B 又依赖 A，就形成了 A → B → A 的循环依赖。

---


### 问题 4 ⭐⭐⭐

> Spring 为什么需要三级缓存？三级缓存分别是干什么的？

```

三级缓存不仅仅是为了循环依赖，还涉及 AOP 代理的早期引用。
一级：完整 Bean
二级：早期 Bean 引用
三级：获取早期 Bean 引用的工厂
```

### 问题 5 ⭐⭐

> 为什么构造器注入的循环依赖通常无法通过三级缓存解决？

> 三级缓存解决循环依赖的前提是 Bean 已经完成实例化，可以提前暴露早期引用。构造器注入在实例化 Bean 时就必须先获取依赖对象，因此 Bean 还没有实例化完成，就无法提前暴露到三级缓存，所以无法通过三级缓存解决。

### 三级缓存能解决所有循环依赖吗？

```

单例 + 属性注入       ✅
单例 + Setter注入     ✅

单例 + 构造器注入     ❌
原型 Bean     
```

---

好，继续。现在我们把 **“三级缓存到底怎么解决 A → B → A”** 彻底走一遍。

这部分是 Day 8 的核心，但我会控制在**初级 Java 面试需要的深度**，不钻源码细节。

---

# 一、先准备两个 Bean

假设：

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
A → B → A
```

---

# 二、正常情况下 Spring 怎么创建 A？

Spring 开始：

```
创建 A
```

第一步：

```
实例化 A
```

也就是相当于：

```
new A();
```

此时：

```
A对象已经存在
```

但注意：

```
A的 b 属性还没有注入
A还没有完成初始化
```

所以 A 是一个：

> **“半成品”**

---

# 三、关键：提前暴露 A

这时候 Spring 会提前准备：

```
A 的早期引用
```

三级缓存：

```
singletonFactories
```

里面放的是一个：

```
ObjectFactory
```

你现在不要纠结源码。

简单理解：

> **三级缓存保存了一个“以后可以拿到 A 早期引用”的工厂。**

所以：

```
三级缓存
   ↓
A的ObjectFactory
```

---

# 四、Spring 接着给 A 注入属性

A 需要：

```
@Autowired
private B b;
```

所以 Spring 开始：

```
创建 B
```

---

# 五、B 也开始创建

首先：

```
实例化 B
```

相当于：

```
new B();
```

然后 B 需要：

```
@Autowired
private A a;
```

于是：

> **B 现在需要 A。**

---

# 六、关键时刻来了

Spring 发现：

> A 正在创建，但是还没有完成。

于是它去找 A。

查找大致可以理解成：

```
一级缓存？
 ↓
没有

二级缓存？
 ↓
没有

三级缓存？
 ↓
有！
```

于是：

```
三级缓存
   ↓
A的ObjectFactory
   ↓
获取A的早期引用
```

---

# 七、A 被提前拿出来了

于是 B 得到了：

```
A的早期引用
```

然后：

```
B
 ↓
注入 A
 ↓
B完成初始化
 ↓
B创建完成
```

最终：

```
A → B → A
```

这个环终于被打破。

---

# 八、A 继续完成

现在 B 已经好了：

```
B创建完成
```

Spring 回到 A：

```
A
 ↓
注入 B
 ↓
初始化
 ↓
A创建完成
```

最终：

```
A
 ↓
B
 ↓
A的早期引用
```

整个循环依赖解决。

---

# 九、把缓存变化画出来

这个最重要。

## 开始创建 A

```
一级缓存：
空

二级缓存：
空

三级缓存：
A → ObjectFactory
```

---

## 创建 B

```
一级缓存：
空

二级缓存：
空

三级缓存：
A → ObjectFactory
B → ObjectFactory
```

---

## B 需要 A

Spring：

```
一级：没有
 ↓
二级：没有
 ↓
三级：找到 A
```

于是：

```
三级
A → ObjectFactory
       ↓
    A早期引用
```

然后 A 的早期引用会进入：

```
二级缓存
```

可以理解成：

```
二级：
A → A早期引用
```

同时三级中的对应工厂会被移除。

---

## 最终 A、B 创建完成

最终：

```
一级缓存：

A → A完整对象
B → B完整对象
```

而：

```
二级
三级
```

中对应的临时数据会被清理。

---

# 🔥 十、为什么不是直接放二级缓存？

这就是三级缓存最经典的问题。

面试官：

> **“既然二级缓存可以存早期对象，为什么不直接把 A 放进去？为什么需要三级缓存？”**

答案：

> **因为 Spring 需要通过三级缓存中的 ObjectFactory 延迟获取 Bean 的早期引用，并且这个过程中可以对 Bean 进行 AOP 等处理，从而有机会得到代理对象，而不是简单暴露原始对象。**

举个例子：

```
A
 ↓
需要AOP代理
 ↓
最终应该是
A代理对象
```

如果直接把：

```
原始A
```

放到二级缓存：

```
B → 原始A
```

但最终 Spring 容器中：

```
A → A代理对象
```

就可能出现：

```
B拿到原始A

容器拿到代理A
```

同一个 Bean：

> **出现两个不同的引用。**

所以三级缓存给 Spring 留出了：

> **生成/获取早期引用的机会。**

---

# 十一、这里有一个容易被误解的地方

不要简单理解成：

> “三级缓存就是专门用来创建 AOP 代理的。”

不是。

更准确：

> **三级缓存主要是为了支持单例 Bean 的循环依赖解决，并通过 ObjectFactory 提供早期引用，从而兼顾 Bean 的早期暴露和代理对象的处理。**

---

# 十二、三级缓存真正的核心

你现在可以把它总结成：

```
一级缓存
singletonObjects
↓
完整 Bean

二级缓存
earlySingletonObjects
↓
早期 Bean 引用

三级缓存
singletonFactories
↓
获取早期引用的工厂
```

然后：

```
A → B → A
```

发生循环依赖：

```
A实例化
 ↓
提前暴露A
 ↓
创建B
 ↓
B需要A
 ↓
三级缓存获取A早期引用
 ↓
B完成
 ↓
A继续完成
 ↓
A、B都完成
```

---

# ⭐ 十三、你面试的时候不要讲得太复杂

如果面试官问：

> **Spring 是怎么解决循环依赖的？**

初级岗位你可以这样回答：

> **Spring 主要通过三级缓存解决单例 Bean 的属性注入循环依赖。当 A 依赖 B、B 又依赖 A 时，A 实例化完成但还没有完成属性注入时，Spring 会通过三级缓存提前暴露 A 的早期引用。创建 B 时发现需要 A，就可以从三级缓存获取 A 的早期引用，完成 B 的创建，然后 A 再继续完成初始化。**

如果继续追问：

> **为什么是三级缓存？**

再补：

> **三级缓存中保存的是获取早期引用的 ObjectFactory，可以在需要的时候获取早期引用，并兼顾 AOP 代理等 Bean 后处理逻辑。**

这就够了。


---

# ⭐ 三级缓存到底能解决哪些循环依赖？

先记住一个结论：

> **三级缓存主要解决的是单例 Bean 的属性注入循环依赖，并不是所有循环依赖都能解决。**

---

## 1. 字段注入：可以解决

例如：

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

流程：

```
创建 A
 ↓
A 实例化完成
 ↓
A 可以提前暴露
 ↓
发现需要 B
 ↓
创建 B
 ↓
B 发现需要 A
 ↓
获取 A 的早期引用
 ↓
B 创建完成
 ↓
A 注入 B
 ↓
A 创建完成
```

所以：

> **字段注入的单例循环依赖，Spring 可以通过三级缓存解决。**

---

# 2. Setter 注入：通常也可以解决

例如：

```
@Service
public class A {

    private B b;

    @Autowired
    public void setB(B b) {
        this.b = b;
    }
}
```

B：

```
@Service
public class B {

    private A a;

    @Autowired
    public void setA(A a) {
        this.a = a;
    }
}
```

原因和字段注入类似：

```
先实例化
 ↓
再注入依赖
```

所以 Bean 已经有了一个“半成品对象”，可以提前暴露。

---

# 3. 构造器注入：无法解决

例如：

```
@Service
public class A {

    private final B b;

    public A(B b) {
        this.b = b;
    }
}
```

B：

```
@Service
public class B {

    private final A a;

    public B(A a) {
        this.a = a;
    }
}
```

创建 A：

```
创建 A
 ↓
发现构造器需要 B
 ↓
创建 B
 ↓
发现构造器需要 A
 ↓
创建 A
 ↓
又需要 B
 ↓
……
```

问题就在这里：

> **A 还没有实例化完成。**

三级缓存能提前暴露的是：

```
已经实例化的 Bean
```

而现在：

```
A
↓
还卡在构造器
↓
连 A 对象都没有
```

所以没有办法提前把 A 暴露出去。

---

# 4. 一个非常好记的区别

你可以这么理解：

### 属性注入

```
先有对象
 ↓
再找依赖
```

所以：

```
A对象已经出生了
 ↓
可以把这个“半成品A”给别人
```

### 构造器注入

```
先找依赖
 ↓
才能出生
```

所以：

```
A还没出生
 ↓
没东西可以提前暴露
```

这就是为什么构造器循环依赖无法通过三级缓存解决。

---

# 5. 再补一个面试容易问的：原型 Bean

如果面试官问：

> **三级缓存能解决原型 Bean 的循环依赖吗？**

答案：

> **不能。Spring 的三级缓存主要用于单例 Bean，原型 Bean 不会使用这种单例缓存机制来解决循环依赖。**

所以你可以记：

```
单例 + 属性注入
        ↓
可以解决常见循环依赖

原型 Bean
        ↓
不能依赖三级缓存解决
```


