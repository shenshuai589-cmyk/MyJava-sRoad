
# 第一部分 Java 面向对象（★★★★★）

## 1、什么是面向对象？

### 面试题

> 什么是面向对象？

### 回答

面向对象（Object Oriented Programming，OOP）是一种编程思想。

核心思想：

- 万物皆对象
- 使用对象完成业务
- 对象拥有属性和行为

Java 中的对象由类创建。

例如：

```
Student student = new Student();
```

Student 是类。

student 是对象。

---

## 2、面向对象三大特性（★★★★★）

企业100%会问。

### 封装（Encapsulation）

把数据和方法封装到对象内部。

外部不能直接访问对象属性。

一般使用 private。

例如：

```
private String name;

public String getName(){
    return name;
}
```

优点：

- 数据安全
- 降低耦合
- 易维护

---

### 继承（Inheritance）

子类继承父类。

使用

```
extends
```

例如：

```
class Animal{}

class Dog extends Animal{}
```

特点：

可以复用父类代码。

Java 只支持单继承。

支持多层继承。

---

### 多态（Polymorphism）

父类引用指向子类对象。

例如：

```
Animal animal = new Dog();
```

调用：

```
animal.eat();
```

实际执行 Dog 的 eat()。

条件：

① 继承

② 方法重写

③ 父类引用指向子类对象

优点：

提高扩展性。

---

# 3、重载（Overload）和重写（Override）

★★★★★

企业必问。

|重载|重写|
|---|---|
|同一个类|父子类|
|方法名相同|方法名相同|
|参数不同|参数必须一致|
|返回值无关|返回值兼容|
|编译期决定|运行期决定|

---

# 4、接口（Interface）

为什么使用接口？

接口表示一种规范。

例如：

```
interface USB{
    void connect();
}
```

实现：

```
class Mouse implements USB{

    public void connect(){

    }

}
```

特点：

不能实例化。

Java8以后：

可以有

```
default

static
```

方法。

Java9：

private 方法。

---

# 5、抽象类（Abstract）

抽象类：

```
abstract class Animal{

    abstract void eat();

}
```

特点：

不能 new。

可以有普通方法。

可以有成员变量。

可以有构造方法。

---

# 面试：

接口和抽象类区别？

|接口|抽象类|
|---|---|
|更关注规范|更关注公共代码|
|implements|extends|
|多实现|单继承|
|没有构造|可以有构造|
|成员变量默认 public static final|普通成员变量|

---

# Object类（★★★★★）

所有类父类。

常问：

```
equals()

hashCode()

toString()

clone()

wait()

notify()

notifyAll()
```

---

## equals()

默认比较地址。

String 重写以后比较内容。

例如：

```
String a="abc";

String b="abc";

a.equals(b)
```

true

---

## == 和 equals

★★★★★

| ==       | equals |
| -------- | ------ |
| 基本类型比较值  | 比较内容   |
| 引用类型比较地址 | 可以重写   |

---

## hashCode()

返回对象哈希值。

作用：

HashMap

HashSet

HashTable

---

为什么 equals 一样 hashCode 必须一样？

因为 HashMap 先找 hash。

再找 equals。

否则集合无法正常工作。

---

# String（★★★★★）

企业非常喜欢。

String：

不可变。

原因：

```
private final char[] value;
```

JDK9以后：

```
byte[]
```

---

为什么不可变？

安全

线程安全

缓存

字符串常量池

HashMap key

---

StringBuilder

线程不安全

效率最高

---

StringBuffer

线程安全

效率低

---

区别：

|类|是否线程安全|
|---|---|
|String|是|
|StringBuilder|否|
|StringBuffer|是|

---

# 包装类

基本类型：

```
int
```

对应：

```
Integer
```

自动装箱：

```
Integer a=10;
```

自动拆箱：

```
int b=a;
```

---

缓存：

Integer：

```
-128

~

127
```

缓存。

例如：

```
Integer a=100;

Integer b=100;

a==b
```

true

但是：

```
200
```

false。

---

# 泛型（★★★★★）

作用：

编译时检查。

避免强转。

例如：

```
List<String>
```

擦除：

Java 泛型采用：

Type Erasure。

运行时：

不知道 T 是什么。

---

# 枚举（Enum）

定义：

```
enum Sex{

MAN,

WOMAN

}
```

优点：

类型安全。

代替常量。

---

# 注解（Annotation）

常见：

```
@Override

@Deprecated

@SuppressWarnings

@Autowired

@RestController
```

元注解：

```
@Target

@Retention

@Documented

@Inherited
```

---

# 反射（★★★★★）

企业：

Spring核心。

流程：

```
Class

↓

Constructor

↓

Method

↓

Field
```

获取 Class：

```
Class.forName()

对象.getClass()

类.class
```

优点：

动态。

缺点：

慢。

破坏封装。

---

# Lambda（★★★★★）

匿名函数。

例如：

```
list.forEach(

x->System.out.println(x)

);
```

底层：

函数式接口。

---

# Stream API（★★★★★）

流程：

```
stream()

↓

filter()

↓

map()

↓

sorted()

↓

distinct()

↓

collect()
```

例如：

```
list.stream()

.filter()

.collect()
```

特点：

不修改原集合。

惰性计算。

---

# Optional

避免：

NullPointerException。

例如：

```
Optional.ofNullable(user)

.ifPresent(...)
```

---

# BigDecimal

为什么不用 double？

因为：

double 精度丢失。

BigDecimal：

金融计算。

注意：

```
new BigDecimal("0.1")
```

不要：

```
new BigDecimal(0.1)
```

---

# 日期API

Java8：

```
LocalDate

LocalTime

LocalDateTime

Instant

Duration

Period
```

线程安全。

---

# 集合（★★★★★）

Collection

↓

List

↓

ArrayList

LinkedList

Vector

---

Set

↓

HashSet

LinkedHashSet

TreeSet

---

Map

↓

HashMap

LinkedHashMap

TreeMap

Hashtable

ConcurrentHashMap

---

# ArrayList（★★★★★）

底层：

数组。

特点：

查询快。

新增慢。

默认容量：

```
0
```

第一次：

```
10
```

扩容：

```
1.5倍
```

---

# LinkedList

双向链表。

插入快。

查询慢。

---

# Vector

线程安全。

效率低。

---

# HashMap（★★★★★★★★★★）

企业最喜欢。

## 底层

JDK8：

```
数组

+

链表

+

红黑树
```

```
table[]

↓

Node

↓

Node

↓

TreeNode
```

---

## 为什么长度是2的幂？

方便：

```
hash&(length-1)
```

代替：

```
%
```

效率高。

---

## Hash冲突

不同对象。

hash一样。

解决：

拉链法。

---

## 为什么树化？

链表太长。

查询：

O(n)

树：

O(logn)

---

树化条件：

```
>=8

容量>=64
```

---

退化：

```
<=6
```

恢复链表。

---

## HashMap扩容

默认：

16

负载因子：

0.75

达到：

```
16×0.75

=

12
```

扩容：

```
32
```

扩容：

2倍。

---

## resize()

重新计算：

hash。

迁移数据。

---

# fail-fast

集合迭代：

维护：

```
modCount
```

修改：

ConcurrentModificationException。

---

Iterator：

正确删除：

```
iterator.remove();
```

---

# 红黑树（★★★★★）

特点：

自平衡。

近似平衡。

查询：

```
O(logn)
```

规则：

根黑。

叶黑。

不能连续红。

路径黑节点一样。

---

# IO（★★★★★）

BIO

同步阻塞。

---

NIO

同步非阻塞。

Selector。

Channel。

Buffer。

---

AIO

异步非阻塞。

JDK7。

---

# File

创建：

```
File file
```

判断：

```
exists()

isFile()

mkdirs()
```

---

# 字节流

```
InputStream

OutputStream
```

---

# 字符流

```
Reader

Writer
```

---

# Buffered流

缓冲区。

减少IO次数。

---

# Object流

对象序列化。

```
ObjectOutputStream

ObjectInputStream
```

要求：

Serializable。

---

# 多线程（★★★★★）

生命周期：

```
NEW

RUNNABLE

BLOCKED

WAITING

TIMED_WAITING

TERMINATED
```

---

创建方式：

Thread

Runnable

Callable

FutureTask

CompletableFuture

---

# synchronized

对象锁。

类锁。

底层：

Monitor。

---

# volatile

保证：

可见性。

禁止指令重排。

不能保证原子性。

---

# CAS

Compare And Swap。

乐观锁。

底层：

Unsafe。

---

# AQS（★★★★★）

AbstractQueuedSynchronizer。

JUC核心。

实现：

ReentrantLock

Semaphore

CountDownLatch

---

# ReentrantLock

比 synchronized 更灵活。

支持：

tryLock()

lockInterruptibly()

公平锁。

---

# ThreadLocal

线程局部变量。

每线程一份。

避免共享。

---

# CountDownLatch

倒计时。

一次性。

---

# CyclicBarrier

循环屏障。

可重复。

---

# Semaphore

信号量。

限制并发数。

---

# 线程池（★★★★★）

核心类：

```
ThreadPoolExecutor
```

七大参数：

1. corePoolSize（核心线程数）
2. maximumPoolSize（最大线程数）
3. keepAliveTime（空闲线程存活时间）
4. TimeUnit（时间单位）
5. BlockingQueue（阻塞队列）
6. ThreadFactory（线程工厂）
7. RejectedExecutionHandler（拒绝策略）

---

## 拒绝策略

- AbortPolicy（默认，直接抛异常）
- CallerRunsPolicy（由调用线程执行）
- DiscardPolicy（直接丢弃）
- DiscardOldestPolicy（丢弃队列中最旧任务）

---

# ConcurrentHashMap（★★★★★）

JDK7：Segment 分段锁。

JDK8：CAS + synchronized。

支持高并发，读操作几乎无锁。

---

# JVM（★★★★★）

## JVM内存模型

- 程序计数器（Program Counter）
- Java虚拟机栈（Stack）
- 本地方法栈（Native Method Stack）
- 堆（Heap）
- 方法区（Method Area，JDK8 后使用元空间 Metaspace）

## 堆

分代管理：

- 新生代（Eden、Survivor From、Survivor To）
- 老年代

对象通常先进入 Eden，多次 GC 后晋升到老年代。

## 垃圾回收（GC）

判断对象是否可回收：

- 引用计数法（Java 未采用）
- 可达性分析（Java 使用）

常见 GC：

- Serial
- Parallel
- CMS（低停顿，已逐渐淘汰）
- G1（JDK9+ 默认推荐）
- ZGC（超低停顿，适合大内存）

## 类加载

生命周期：

加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载

### 双亲委派模型

加载顺序：

Bootstrap ClassLoader → Platform（Extension）ClassLoader → Application ClassLoader

优点：

- 避免重复加载
- 保证核心类库安全，防止用户自定义 `java.lang.String` 等核心类覆盖 JDK 实现

## JVM 调优

常见参数：

```
-Xms   # 初始堆大小
-Xmx   # 最大堆大小
-Xss   # 栈大小
```

常用排查工具：

- `jps`
- `jstack`
- `jmap`
- `jstat`
- `jcmd`
- `jconsole`
- `VisualVM`
- `Arthas`

常见调优思路：

1. 分析 GC 日志。
2. 判断 Young GC、Full GC 是否频繁。
3. 检查内存泄漏（MAT、jmap）。
4. 根据业务调整堆大小、新生代比例和垃圾收集器。
5. 使用 G1/ZGC 等更适合低停顿场景的 GC。

---

## 面试高频 Top 20（建议重点掌握）

1. 面向对象三大特性
2. `==` 与 `equals()` 的区别
3. `hashCode()` 为什么要和 `equals()` 一起重写
4. `String` 为什么不可变
5. `ArrayList` 与 `LinkedList` 的区别
6. `HashMap` 底层原理（JDK7 vs JDK8）
7. `HashMap` 扩容机制与树化条件
8. `ConcurrentHashMap` 为什么线程安全
9. `synchronized` 与 `ReentrantLock` 的区别
10. `volatile` 能解决什么问题
11. CAS 的 ABA 问题及解决方案
12. AQS 原理及应用
13. 线程池七大参数与执行流程
14. 四种拒绝策略分别适用于哪些场景
15. `ThreadLocal` 原理及内存泄漏问题
16. JVM 内存区域划分
17. GC Root 与可达性分析
18. CMS、G1、ZGC 的区别
19. 双亲委派模型及如何打破双亲委派
20. JVM 调优步骤与常用命令