`Atomic` 原子类是 Java 并发包 `java.util.concurrent.atomic` 下提供的一系列**保证线程安全的操作类**。

核心思想：

> **利用 CAS（Compare And Swap）+ volatile 实现无锁线程安全操作。**

相比 `synchronized`，Atomic 类：

- 不需要加锁
- 性能更高
- 避免线程阻塞
- 适合简单变量的并发修改

---

# 1. 为什么需要 Atomic？

看一个问题：

```
public class Test {

    private static int count = 0;

    public static void main(String[] args) throws Exception {

        Thread t1 = new Thread(() -> {
            for(int i=0;i<10000;i++){
                count++;
            }
        });


        Thread t2 = new Thread(() -> {
            for(int i=0;i<10000;i++){
                count++;
            }
        });


        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(count);
    }
}
```

结果：

```
不到20000
```

原因：

`count++` 不是原子操作。

实际上：

```
count++;
```

等价于：

```
int temp = count;

temp = temp + 1;

count = temp;
```

多个线程同时执行：

```
线程1:
读取 count=0

线程2:
读取 count=0


线程1:
+1

线程2:
+1


最终:
count=1
```

发生了数据覆盖。

---

# 2. 使用 AtomicInteger

修改：

```
AtomicInteger count = new AtomicInteger(0);


count.incrementAndGet();
```

结果：

```
20000
```

不会丢失数据。

---

# 3. Atomic常见原子类

## 1）基本类型原子类

### AtomicInteger

整数原子操作：

```
AtomicInteger num =
        new AtomicInteger(10);


num.incrementAndGet();

System.out.println(num.get());
```

输出：

```
11
```

常用方法：

|方法|作用|
|---|---|
|get()|获取值|
|set()|设置值|
|getAndIncrement()|先获取再+1|
|incrementAndGet()|先+1再获取|
|decrementAndGet()|减1|
|compareAndSet()|CAS修改|

---

### AtomicLong

用于 long：

```
AtomicLong count =
        new AtomicLong();
```

---

### AtomicBoolean

布尔值：

```
AtomicBoolean flag =
        new AtomicBoolean(false);


flag.compareAndSet(false,true);
```

常用于：

- 单例初始化
- 状态控制

例如：

```
if(flag.compareAndSet(false,true)){
    //只允许一个线程进入
}
```

---

# 4. AtomicInteger底层原理（重点）

AtomicInteger核心：

```
private volatile int value;
```

内部保存：

```
value
 |
 volatile保证可见性
```

修改时：

调用：

```
incrementAndGet()
```

源码思想：

```
public final int incrementAndGet(){

    return unsafe.getAndAddInt(this,1)+1;

}
```

底层：

```
do{

    oldValue = 当前值;

    newValue = oldValue + 1;


}while(
    !CAS(oldValue,newValue)
);
```

---

# 5. CAS是什么？（面试重点）

CAS：

Compare And Swap

比较并交换。

三个参数：

```
CAS(
 地址,
 旧值,
 新值
)
```

例如：

当前：

```
value=10
```

线程A：

```
希望:
10 -> 11
```

执行：

```
CAS(10,11)
```

如果：

当前还是10：

```
修改成功
```

如果：

当前已经变成12：

```
修改失败
重新尝试
```

---

# 6. Atomic为什么线程安全？

因为：

## volatile保证可见性

比如：

线程A修改：

```
value=20
```

其他线程马上看到。

但是：

volatile不能保证原子性。

例如：

```
volatile int count;

count++;
```

依然线程不安全。

所以：

Atomic =

```
volatile
+
CAS
```

---

# 7. CAS的问题

## ① ABA问题

例如：

初始：

```
A
```

线程1：

读取：

```
A
```

线程2：

修改：

```
A -> B
```

然后：

```
B -> A
```

线程1：

发现还是A：

认为没有变化。

但是实际上：

发生过修改。

这就是：

> ABA问题

解决：

使用：

`AtomicStampedReference`

---

# 8. AtomicStampedReference（带版本号CAS）

普通CAS：

```
A
|
修改
|
A
```

无法发现变化。

加入版本：

```
A(version=1)

变成:

B(version=2)

再变:

A(version=3)
```

版本不同：

发现修改。

代码：

```
AtomicStampedReference<String> ref =
        new AtomicStampedReference<>(
            "A",
            1
        );
```

---

# 9. 数组原子类

## AtomicIntegerArray

保证数组元素线程安全：

```
int[] arr={1,2,3};


AtomicIntegerArray array =
        new AtomicIntegerArray(arr);


array.incrementAndGet(0);
```

结果：

```
[2,2,3]
```

---

# 10. 引用类型原子类

## AtomicReference

对象引用原子更新。

例如：

```
AtomicReference<User> userRef;


userRef.compareAndSet(
    oldUser,
    newUser
);
```

常用于：

- 状态机
- 配置更新
- 单例

---

# 11. AtomicInteger vs synchronized

||Atomic|synchronized|
|---|---|---|
|实现|CAS|锁|
|阻塞|不阻塞|可能阻塞|
|性能|高|较低|
|适用|简单变量|复杂业务|
|公平性|无|可控制|
|代码复杂度|简单|简单|

例如：

计数器：

推荐：

```
AtomicInteger
```

银行转账：

推荐：

```
synchronized
或者
Lock
```

因为涉及多个变量：

```
账户A余额
账户B余额
交易记录
```

Atomic无法保证整体一致性。

---

# 12. Atomic常见面试问题

## Q1：AtomicInteger为什么线程安全？

答：

> AtomicInteger内部使用volatile保存变量，保证可见性，同时通过CAS操作保证更新的原子性，因此线程安全。

---

## Q2：CAS有什么缺点？

答：

三个：

1. ABA问题
    
2. 自旋消耗CPU
    
3. 只能保证单个变量原子性
    

---

## Q3：Atomic和volatile区别？

||volatile|Atomic|
|---|---|---|
|可见性|√|√|
|原子性|×|√|
|底层|内存屏障|CAS+volatile|
|计数操作|不适合|适合|

---

# 13. Atomic在Java并发中的位置

Java并发体系：

```
线程安全
 |
 +-- synchronized
 |
 +-- Lock
 |
 +-- AQS
 |
 +-- CAS
       |
       +-- Atomic原子类
       |
       +-- ConcurrentHashMap
       |
       +-- LongAdder
```

---

# 14. Atomic和LongAdder（高频）

高并发计数：

AtomicInteger:

```
多个线程
    |
竞争同一个value
```

竞争严重。

LongAdder：

```
          Cell1
线程1 ---->

          Cell2
线程2 ---->

          Cell3
线程3 ---->
```

最后：

```
sum(Cell)
```

所以：

高并发统计：

推荐：

```
LongAdder
```

例如：

- 秒杀商品数量
- 网站访问量
- PV统计

---

## 面试总结一句话：

> Atomic原子类是Java并发包提供的无锁线程安全工具，底层基于CAS和volatile实现，通过不断比较和交换保证单个变量操作的原子性，常用于计数器、状态更新等场景。其缺点是存在ABA问题、自旋消耗CPU，并且无法保证多个变量的一致性。