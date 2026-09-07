
# 一、Day 9 今天学什么？

按照初级 Java 面试的要求，我们重点掌握：

### ⭐⭐⭐ 第一部分：事务传播

1. 什么是事务传播行为？
2. `REQUIRED`
3. `REQUIRES_NEW`
4. `NESTED`

### ⭐⭐ 第二部分：事务隔离

5. Spring 事务隔离级别
6. MySQL 隔离级别和 Spring 隔离级别的关系

### ⭐⭐⭐ 第三部分：事务失效

7. `@Transactional` 为什么会失效？
8. 同类调用为什么导致事务失效？
9. `private` / `final` 方法为什么可能无法被代理？

今天**不建议你去背 Spring 源码中的几十个类**。

---

# 二、什么叫事务传播行为？

先看一个最简单的例子。

```
@Service
public class OrderService {

    @Transactional
    public void createOrder() {

        saveOrder();

        paymentService.pay();
    }
}
```

假设：

```
paymentService.pay()
```

也有：

```
@Transactional
public void pay() {
}
```

那么问题来了：

```
createOrder()
      ↓
      pay()
```

到底：

```
A事务
 ↓
B加入A事务
```

还是：

```
A事务
 ↓
B重新创建一个事务
```

这就是：

# ⭐ 事务传播行为

简单说：

> **当一个事务方法调用另一个事务方法时，后者应该如何参与当前事务。**

---

# 三、`REQUIRED` —— 最重要 ⭐⭐⭐

这是 Spring 默认的事务传播行为。

```
@Transactional(
    propagation = Propagation.REQUIRED
)
```

它的意思：

> **如果当前已经存在事务，就加入当前事务；如果不存在，就创建一个新事务。**

这是面试必须会的。

---

## 情况一：外面已经有事务

```
A()
@Transactional
 ↓
开启事务 T1
 ↓
B()
@Transactional(REQUIRED)
```

因为 B 发现：

> 当前已经存在 T1。

所以：

```
A
└── T1
     ↓
     B加入T1
```

不是：

```
T1
 ↓
T2 ❌
```

而是：

```
T1
├── A
└── B
```

---

# 四、情况二：外面没有事务

如果：

```
public void A() {
    B();
}
```

B：

```
@Transactional
public void B() {
}
```

如果 B 的调用经过 Spring 代理，那么：

```
A
 ↓
B
 ↓
发现没有事务
 ↓
创建T1
```

所以：

> `REQUIRED` = **有就加入，没有就创建。**

### 🧠 一句话记忆：

> **REQUIRED：有事务就加入，没有就新建。**

这个一定记住。

---

# 五、`REQUIRES_NEW` —— 第二个重点 ⭐⭐⭐

```
@Transactional(
    propagation = Propagation.REQUIRES_NEW
)
```

意思：

> **不管当前有没有事务，都创建一个新的事务。**

例如：

```
A()
@Transactional
 ↓
T1
 ↓
B()
@Transactional(REQUIRES_NEW)
```

B 不加入 T1。

而是：

```
T1
 ↓
暂时挂起
 ↓
创建T2
 ↓
执行B
 ↓
T2提交/回滚
 ↓
恢复T1
```

所以：

```
T1
 └── A

T2
 └── B
```

是**两个独立事务**。

---

# 六、为什么 `REQUIRES_NEW` 很有用？

举一个非常实际的例子：

```
用户下单
 ↓
订单事务
 ↓
记录操作日志
```

假设：

```
订单创建失败
```

你可能仍然希望：

> **操作日志保存下来。**

那么日志可以使用：

```
@Transactional(
    propagation = Propagation.REQUIRES_NEW
)
public void saveLog() {
}
```

于是：

```
订单事务 T1
      ↓
   保存订单
      ↓
   保存日志 T2
      ↓
   T2独立提交
      ↓
T1后来回滚
```

最终：

```
订单 ❌ 回滚
日志  ✅ 保留
```

这就是 `REQUIRES_NEW` 的实际意义。

---

# 七、`REQUIRED` 和 `REQUIRES_NEW` 一定要分清

||REQUIRED|REQUIRES_NEW|
|---|---|---|
|已存在事务|加入|创建新事务|
|没有事务|创建|创建|
|事务数量|通常一个|两个独立事务|
|当前事务|使用|挂起|
|新事务|无|有|

### 🧠 记忆：

> **REQUIRED：跟着大部队走。**

> **REQUIRES_NEW：我自己开一桌。**

这个记法非常好用。

---

# 八、`NESTED` 是什么？

第三个常见传播行为：

```
@Transactional(
    propagation = Propagation.NESTED
)
```

它和 `REQUIRES_NEW` 很容易混。

`NESTED` 的核心：

> **如果当前存在事务，在当前事务内部创建一个嵌套事务，通过 Savepoint 实现。**

例如：

```
T1
 ↓
A
 ↓
NESTED B
 ↓
Savepoint
```

如果 B 失败：

```
B失败
 ↓
回滚到Savepoint
 ↓
T1仍然可以继续
```

所以：

```
T1
├── A
└── B
```

它们仍然属于：

> **同一个物理事务**

只是 B 有一个保存点。

---

# 九、`NESTED` 和 `REQUIRES_NEW` 最大区别

这个非常重要。

### `REQUIRES_NEW`

```
T1
 ↓
挂起T1
 ↓
T2
 ↓
B
```

两个独立事务。

### `NESTED`

```
T1
 ↓
Savepoint
 ↓
B
```

仍然是一个事务，只是有保存点。

---

## 举个场景

```
订单事务 T1

保存订单
 ↓
保存商品
 ↓
保存优惠券
```

优惠券操作用了：

```
NESTED
```

如果优惠券失败：

```
回滚到优惠券之前
```

而：

```
订单
商品
```

可以继续。

但如果最后：

```
T1整体回滚
```

那么：

> **优惠券的操作也会跟着整体回滚。**

因为它本质上还是 T1 的一部分。

---

# 🔥 十、现在来一个面试经典题

假设：

```
@Transactional
public void A() {

    B();
}
```

B：

```
@Transactional(
    propagation = Propagation.REQUIRES_NEW
)
public void B() {

}
```

问：

> **A 和 B 是几个事务？**

答案：

```
A → T1

B → T2
```

两个独立事务。

执行：

```
T1开始
 ↓
A
 ↓
T1挂起
 ↓
T2开始
 ↓
B
 ↓
T2提交
 ↓
恢复T1
 ↓
A继续
 ↓
T1提交
```

---

# ⚠️ 但是这里马上有一个坑

你可能会想：

> “那如果 A 和 B 在同一个类里面呢？”

例如：

```
@Service
public class TestService {

    @Transactional
    public void A() {
        B();
    }

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    public void B() {
    }
}
```

然后：

```
A();
```

这时候：

```
B();
```

实际上相当于：

```
this.B();
```

没有经过 Spring 代理。

于是：

> **B 上面的 `@Transactional` 以及它的传播行为可能都不会生效。**

也就是说，别急着说：

```
A → T1
B → T2
```

**必须先确认 B 的调用是否经过代理。**

这就是我们 Day 7 学过的：

> **同类内部调用导致事务失效。**


---


### 问题 1 ⭐⭐⭐

> **什么是事务传播行为？**

### 问题 2 ⭐⭐⭐

> **`REQUIRED` 和 `REQUIRES_NEW` 有什么区别？**

### 问题 3 ⭐⭐

> **`NESTED` 和 `REQUIRES_NEW` 有什么区别？**

### 问题 4 ⭐⭐⭐

> A 方法开启了事务，调用 B 方法，B 使用 `REQUIRED`，那么 A 和 B 是几个事务？为什么？

### 问题 5 ⭐⭐⭐

> A 方法开启事务，在同一个类中直接调用 B，而 B 使用 `REQUIRES_NEW`，这时候 B 会创建新的事务吗？为什么？


---


---

# 一、为什么同类调用会导致事务失效？⭐⭐⭐

先看代码：

```
@Service
public class OrderService {

    @Transactional
    public void createOrder() {
        saveOrder();
    }

    @Transactional
    public void saveOrder() {
        // 保存订单
    }
}
```

调用：

```
orderService.createOrder();
```

这里的 `createOrder()` 是通过 Spring 代理对象调用的：

```
调用
 ↓
Spring代理
 ↓
事务拦截器
 ↓
开启事务
 ↓
createOrder()
```

所以 `createOrder()` 的事务生效。

但是 `createOrder()` 内部调用：

```
this.saveOrder();
```

这时候：

```
this.saveOrder()
```

是**目标对象自己直接调用自己**。

没有经过：

```
Spring代理
 ↓
事务拦截器
```

所以 `saveOrder()` 上的：

```
@Transactional
```

不会被重新解析。

---

# 二、一定要理解这个图

正常调用：

```
Controller
   ↓
代理对象
   ↓
事务拦截器
   ↓
真实OrderService
```

同类调用：

```
真实OrderService
   ↓
this.saveOrder()
   ↓
真实saveOrder()
```

绕过了代理。

所以：

> **不是 `@Transactional` 注解失效了，而是这次调用根本没有经过 Spring AOP 代理。**

这个说法面试非常加分。

---

# 三、第二种：异常被自己捕获 ⭐⭐⭐

例如：

```
@Transactional
public void createOrder() {

    try {
        saveOrder();
        int a = 10 / 0;
    } catch (Exception e) {
        System.out.println("出现异常");
    }
}
```

发生异常：

```
10 / 0
 ↓
Exception
 ↓
catch捕获
 ↓
没有继续抛出
```

事务拦截器怎么办？

它通常是根据：

> **方法执行过程中有没有异常从方法中抛出来**

来判断是否回滚。

但是现在：

```
异常
 ↓
被catch
 ↓
方法正常返回
```

对于事务拦截器来说：

> **这个方法正常执行结束了。**

于是可能：

```
提交事务 ❌
```

---

# 四、怎么解决？

### 方法一：继续抛异常

```
@Transactional
public void createOrder() {

    try {
        saveOrder();
    } catch (Exception e) {
        throw e;
    }
}
```

这样：

```
异常
 ↓
继续向外抛
 ↓
事务拦截器发现异常
 ↓
回滚
```

---

### 方法二：明确告诉 Spring 回滚

```
@Transactional
public void createOrder() {

    try {
        saveOrder();
    } catch (Exception e) {
        TransactionAspectSupport
            .currentTransactionStatus()
            .setRollbackOnly();
    }
}
```

初级面试不需要背这个代码。

知道：

> **捕获异常后如果还希望事务回滚，需要继续抛出异常或者手动标记回滚。**

即可。

---

# 五、第三种：默认情况下，什么异常会回滚？⭐⭐⭐

这个特别容易被问。

很多人会说：

> “发生异常就回滚。”

❌ 不准确。

Spring `@Transactional` **默认情况下**：

```
RuntimeException
Error
   ↓
回滚
```

而：

```
Checked Exception
   ↓
默认不回滚
```

例如：

```
@Transactional
public void test() throws IOException {
    throw new IOException();
}
```

`IOException` 属于：

> Checked Exception

默认情况下：

> **不会因为这个异常自动回滚。**

---

# 六、如果我希望 Checked Exception 也回滚？

可以：

```
@Transactional(rollbackFor = Exception.class)
```

例如：

```
@Transactional(rollbackFor = Exception.class)
public void createOrder() throws Exception {
    ...
}
```

这样：

```
Exception
 ↓
回滚
```

---

# 七、这个面试题一定记住

面试官：

> **`@Transactional` 默认什么情况下回滚？**

你回答：

> **默认情况下，Spring 遇到 RuntimeException 和 Error 会回滚，对于普通的 Checked Exception 默认不会回滚。如果希望 Checked Exception 也回滚，可以通过 `rollbackFor` 指定异常类型。**

这个回答已经够初级 Java 面试用了。

---

# 八、第四种：方法不是 public

例如：

```
@Transactional
private void save() {
}
```

或者：

```
@Transactional
protected void save() {
}
```

在 Spring 常规代理事务机制下，不应该把事务注解放在这种非 `public` 方法上期待它像公开业务方法一样工作。

你之前已经记住：

> **事务方法一般应该定义为 public。**

为什么？

因为 Spring AOP 的代理机制需要拦截方法调用，而 Spring 的事务代理对公开业务方法的支持是最标准、最可靠的。

所以面试简单说：

> **`@Transactional` 通常应该加在 public 方法上，非 public 方法可能无法被事务代理正确拦截。**

---

# 九、第五种：Bean 没有交给 Spring 管理

例如：

```
public class OrderService {
    
    @Transactional
    public void createOrder() {
    }
}
```

然后你自己：

```
OrderService service = new OrderService();
service.createOrder();
```

这时候：

```
自己new
 ↓
Spring不知道
 ↓
没有Spring代理
 ↓
没有事务拦截器
 ↓
@Transactional不生效
```

所以：

> **`@Transactional` 依赖 Spring 的事务代理机制，如果对象不是 Spring 管理的 Bean，自然无法生效。**

---

# 十、今天最后一个：数据库本身不支持事务

这个比较容易理解。

比如你的 Spring：

```
@Transactional
 ↓
开启事务
```

但是底层数据库表使用了：

> **不支持事务的存储引擎**

那么 Spring 再怎么控制，也无法真正实现数据库层面的事务回滚。

MySQL 里我们之前学过：

```
InnoDB
 ↓
支持事务

MyISAM
 ↓
不支持事务
```

所以面试可以说：

> **如果数据库或存储引擎本身不支持事务，Spring 的声明式事务也无法实现真正的事务回滚。**

---

# 十一、现在把所有事务失效原因整理起来

你不要死背一大串。

把它分成三个大类：

## ① 没经过代理

```
同类内部调用
对象自己new
```

---

## ② 异常处理有问题

```
异常被catch后没有继续抛
Checked Exception默认不回滚
```

---

## ③ 基础条件不满足

```
非public方法
Bean没有交给Spring管理
数据库不支持事务
```

---

# 🔥 十二、Day 9 最重要的一张图

你现在把事务整个过程串起来：

```
调用@Transactional方法
        ↓
Spring代理对象
        ↓
事务拦截器
        ↓
判断传播行为
        ↓
开启/加入/挂起事务
        ↓
执行目标方法
        ↓
       ┌──────────────┐
       ↓              ↓
    正常返回        抛出异常
       ↓              ↓
     提交           判断是否回滚
                      ↓
               RuntimeException
               / Error
                      ↓
                     回滚
```

如果：

```
同类调用
   ↓
绕过代理
   ↓
事务拦截器没机会执行
```

这就是为什么 **Day 7 的 AOP** 对理解 **Day 9 的事务**非常重要。

---

# 🧪 Day 9 最后一轮面试题

现在你来回答这 6 个：

### 问题 6 ⭐⭐⭐

**`@Transactional` 为什么会失效？**

### 问题 7 ⭐⭐⭐

**为什么同类内部调用会导致 `@Transactional` 失效？**

### 问题 8 ⭐⭐⭐

**`@Transactional` 默认什么异常会回滚？Checked Exception 会不会默认回滚？**

### 问题 9 ⭐⭐

**如果异常被 `try-catch` 捕获了，为什么事务可能不会回滚？**

### 问题 10 ⭐⭐

**为什么 `@Transactional` 方法一般应该定义为 `public`？**

### 问题 11 ⭐⭐

**如果一个对象是通过 `new` 创建出来的，而不是 Spring Bean，`@Transactional` 会生效吗？为什么？**