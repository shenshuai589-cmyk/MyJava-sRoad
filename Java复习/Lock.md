
# Java Lock锁

## 1. 为什么需要Lock？

Java早期使用：

```
synchronized
```

来实现线程同步。

但是 synchronized 功能比较简单：

- 自动加锁
- 自动释放锁
- 不支持手动控制
- 无法尝试获取锁
- 无法中断等待锁的线程

所以 Java 5 引入了：

```
java.util.concurrent.locks.Lock
```

提供更强大的锁控制。

---

# 2. Lock接口

Lock是一个接口：

```
public interface Lock {

    void lock();

    void lockInterruptibly();

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit);

    void unlock();

}
```

主要方法：

|方法|作用|
|---|---|
|lock()|获取锁|
|unlock()|释放锁|
|tryLock()|尝试获取锁，不阻塞|
|tryLock(timeout)|规定时间内尝试获取锁|
|lockInterruptibly()|可被线程中断|

---

# 3. 基本使用

例如：

```
Lock lock = new ReentrantLock();


public void add(){

    lock.lock();

    try{

        count++;

    }finally{

        lock.unlock();

    }
}
```

执行过程：

```
线程A
 |
获取Lock
 |
执行代码
 |
释放Lock


线程B
 |
等待Lock
 |
获取Lock
 |
执行代码
```

---

# 4. 为什么unlock必须放finally？

错误：

```
lock.lock();

count++;

lock.unlock();
```

问题：

如果：

```
count++;
```

出现异常：

```
获取锁

发生异常

没有释放锁
```

结果：

其他线程永久等待。

正确：

```
lock.lock();

try{

    count++;

}finally{

    lock.unlock();

}
```

保证一定释放。

---

# 5. ReentrantLock（重点）

实际开发最常用：

```
ReentrantLock
```

它是Lock接口的实现类。

结构：

```
Lock
 |
ReentrantLock
```

例如：

```
Lock lock = new ReentrantLock();
```

---

# 6. ReentrantLock底层原理

核心：

```
AQS
(AbstractQueuedSynchronizer)
```

结构：

```
ReentrantLock

       |
       v

      AQS

       |
 -----------------
 |               |
state          CLH队列
锁状态          等待线程
```

---

## state表示锁状态

AQS内部：

```
private volatile int state;
```

例如：

没有线程获取：

```
state = 0
```

线程A获取：

```
state = 1
```

如果可重入：

线程A再次获取：

```
state = 2
```

---

# 7. 什么叫可重入锁？

同一个线程可以重复获得同一把锁。

例如：

```
public synchronized void method1(){

    method2();

}


public synchronized void method2(){

}
```

不会死锁。

因为：

线程A：

```
method1()
  |
  获取锁

method2()
  |
  再次获取锁
```

因为是同一个线程，所以允许。

ReentrantLock：

名字：

```
Reentrant
可重入
Lock
锁
```

---

# 8. synchronized和Lock区别（面试重点）

||synchronized|Lock|
|---|---|---|
|类型|关键字|接口|
|实现|JVM实现|Java代码实现|
|释放锁|自动|手动|
|异常释放|自动|finally|
|公平锁|不支持|支持|
|尝试获取锁|不支持|tryLock|
|可中断|不支持|支持|
|多个条件队列|不支持|支持Condition|

---

# 9. tryLock()

避免线程一直等待：

```
Lock lock = new ReentrantLock();


if(lock.tryLock()){

    try{

        System.out.println("获得锁");

    }finally{

        lock.unlock();
    }

}else{

    System.out.println("获取失败");

}
```

效果：

```
线程A
获取锁


线程B
尝试获取

失败

继续执行其他逻辑
```

---

# 10. 公平锁和非公平锁

ReentrantLock默认：

```
new ReentrantLock();
```

是：

```
非公平锁
```

---

## 非公平锁

谁抢到谁执行：

```
线程A等待

线程B来了

直接抢锁

线程A继续等待
```

优点：

效率高。

---

## 公平锁

创建：

```
new ReentrantLock(true);
```

按照等待顺序：

```
线程A
线程B
线程C


A执行

B执行

C执行
```

优点：

不会线程饥饿。

缺点：

性能低。

---

# 11. Condition条件队列

Lock支持多个等待队列。

synchronized：

只有：

```
wait()
notify()
```

一个等待队列。

Lock：

可以创建多个：

```
Condition condition =
        lock.newCondition();
```

例如生产者消费者：

```
生产者
 |
await()


消费者
 |
signal()
```

---

# 12. Lock和AQS关系（面试重点）

面试回答：

> ReentrantLock底层基于AQS实现，AQS维护一个volatile state变量表示锁状态，同时通过CLH队列管理等待锁的线程。当线程获取锁失败时，会进入同步队列等待，被唤醒后再次竞争锁。

---

# 13. 常见Lock实现

Java并发包：

```
Lock
 |
 +-- ReentrantLock
 |
 +-- ReentrantReadWriteLock
 |
 +-- StampedLock
```

---

## ReentrantLock

普通互斥锁：

```
一个线程执行
其他等待
```

---

## ReentrantReadWriteLock

读写锁：

```
读：
多个线程可以同时读


写：
只能一个线程写
```

适合：

```
读多写少
```

例如：

缓存。

---

## StampedLock

Java8新增：

支持：

- 乐观读
- 悲观读
- 写锁

性能更高。

---

# 面试常问

### 1. Lock和synchronized区别？

答：

> synchronized是JVM关键字，Lock是Java提供的接口。Lock提供了更灵活的锁控制，比如可中断获取锁、超时获取锁、公平锁等。

---

### 2. ReentrantLock为什么叫可重入？

答：

> 同一个线程获取锁后，可以再次获取同一把锁，内部通过state记录重入次数。

---

### 3. ReentrantLock底层？

答：

> 基于AQS实现，通过state表示锁状态，通过同步队列维护等待线程。

---

### 4. Lock为什么必须手动释放？

答：

> Lock不是JVM关键字，无法像synchronized一样由JVM自动释放，所以必须finally中调用unlock。