`synchronized` 是 Java 中最常用的**线程同步机制**，用于解决多个线程访问共享资源时产生的**线程安全问题**。

它可以保证：

1. **互斥性（排他性）**
    - 同一时间只有一个线程可以执行同步代码。
2. **可见性**
    - 一个线程修改的数据，对其他线程立即可见。
3. **有序性**
    - 防止指令重排序影响线程执行结果。

---

# 1. 为什么需要 synchronized？

看一个线程不安全案例：

```
public class Ticket {

    private int count = 100;

    public void sale() {
        if(count > 0) {
            count--;
            System.out.println("卖出一张票");
        }
    }
}
```

两个线程同时执行：

```
线程A                 线程B

读取count=1           读取count=1

count--              count--

写回0                写回0
```

结果：

```
卖出了两张票
但是库存只减少了一次
```

原因：

`count--` 不是一个原子操作。

实际上：

```
count--;
```

等价于：

```
int temp = count;
temp = temp - 1;
count = temp;
```

多个线程同时执行会产生问题。

---

# 2. synchronized基本使用

## 方式一：同步代码块

语法：

```
synchronized(锁对象){

    // 临界区代码

}
```

例如：

```
public void sale(){

    synchronized(this){

        if(count > 0){
            count--;
        }

    }

}
```

表示：

当前线程进入代码块前，需要获取 `this` 对象的锁。

---

# 3. synchronized锁的是什么？

重点：

> synchronized锁的是对象，不是代码。

例如：

```
synchronized(this){

}
```

锁：

```
当前对象实例
```

也就是：

```
对象头 Mark Word
        |
        |
    Monitor锁
```

---

# 4. synchronized的三种使用方式

## ① 修饰实例方法

例如：

```
public synchronized void sale(){

    count--;

}
```

等价于：

```
public void sale(){

    synchronized(this){

        count--;

    }

}
```

锁：

```
当前对象this
```

---

## ② 修饰静态方法

例如：

```
public synchronized static void test(){

}
```

锁：

```
类对象 Class对象
```

等价：

```
synchronized(Test.class){

}
```

例如：

```
public class Demo {


    public synchronized static void method(){

    }

}
```

锁：

```
Demo.class
```

---

## ③ 同步代码块

```
synchronized(lock){

}
```

锁：

```
lock对象
```

例如：

```
Object lock = new Object();


synchronized(lock){

}
```

---

# 5. synchronized底层原理（重点）

面试高频：

> synchronized底层是怎么实现的？

答案：

`synchronized` 基于 **Monitor监视器锁** 实现。

对象在 JVM 中：

```
对象
 |
对象头
 |
Mark Word
 |
Monitor
```

Mark Word保存：

- 锁状态
- 哈希值
- GC年龄

---

当线程进入：

```
synchronized(obj){

}
```

执行：

```
线程
 |
尝试获取obj对象Monitor
 |
成功
 |
执行代码
 |
释放Monitor
```

其他线程：

```
等待Monitor释放
```

---

# 6. synchronized锁升级机制（重点）

JDK1.6之后，synchronized进行了优化。

锁不会直接变重量级锁。

升级过程：

```
无锁
 |
 |
偏向锁
 |
 |
轻量级锁
 |
 |
重量级锁
```

---

## ① 无锁状态

对象刚创建：

```
Mark Word

000
```

没有线程竞争。

---

## ② 偏向锁

场景：

> 只有一个线程访问

例如：

```
synchronized(obj){

}
```

第一次线程进入：

JVM记录线程ID。

以后：

```
发现是同一个线程

直接进入

不用CAS
```

优点：

减少无竞争情况下的开销。

---

## ③ 轻量级锁

场景：

> 两个线程交替访问

使用：

CAS自旋

例如：

线程A：

```
获取锁
```

线程B：

```
发现锁被占用

自旋等待
```

不会马上阻塞。

---

## ④ 重量级锁

场景：

> 大量线程竞争

升级：

```
Monitor
```

线程进入：

```
阻塞状态
```

需要操作系统调度。

---

# 7. synchronized和Lock区别（面试必问）

||synchronized|Lock|
|---|---|---|
|实现|JVM层面|Java代码实现|
|释放锁|自动释放|需要unlock|
|获取锁|自动|手动|
|可中断|否|可以|
|公平锁|不支持|支持|
|多个条件队列|不支持|支持|
|性能|JDK优化后很好|更灵活|

---

例如：

Lock：

```
Lock lock = new ReentrantLock();


lock.lock();

try{

    //业务代码

}finally{

    lock.unlock();

}
```

---

# 8. synchronized和volatile区别

||synchronized|volatile|
|---|---|---|
|原子性|保证|不保证|
|可见性|保证|保证|
|有序性|保证|保证|
|加锁|是|否|
|性能|较低|较高|

例如：

```
volatile int count;
```

不能保证：

```
count++;
```

线程安全。

---

# 9. synchronized能防止死锁吗？

不能。

例如：

```
synchronized(a){

    synchronized(b){

    }

}
```

线程1：

```
拿a
等待b
```

线程2：

```
拿b
等待a
```

形成：

```
死锁
```

---

# 10. synchronized可重入性

重点：

> synchronized是可重入锁。

什么意思？

同一个线程可以重复获得同一把锁。

例如：

```
public synchronized void method1(){

    method2();

}


public synchronized void method2(){

}
```

执行：

```
线程A

获取method1锁

↓

调用method2

↓

再次获取同一把锁

↓

成功
```

不会死锁。

---

# 11. synchronized的内存语义

JMM：

线程：

```
工作内存
   |
   |
主内存
```

加锁：

```
获取锁

↓

清空工作内存

↓

读取主内存最新数据

↓

执行

↓

释放锁

↓

刷新主内存
```

所以保证：

## 可见性

---

# 12. synchronized面试回答模板

面试：

> 说一下synchronized原理？

回答：

```
synchronized是Java提供的线程同步关键字，
可以保证线程安全。

它底层基于Monitor监视器锁实现，
每个对象都有一个Monitor。

线程进入synchronized代码块时，
需要获取对象Monitor，
获取成功执行代码，
执行完成释放Monitor。

JDK1.6以后对synchronized进行了优化，
锁升级过程包括：
无锁、偏向锁、轻量级锁、重量级锁。

同时synchronized具有可重入性，
能够保证原子性、可见性和有序性。
```