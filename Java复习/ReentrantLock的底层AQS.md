AQS 是 **Java 并发包 `java.util.concurrent.locks` 下的核心同步器框架**，很多并发工具都是基于 AQS 实现的，例如：

- `ReentrantLock`
- `CountDownLatch`
- `Semaphore`
- `ReentrantReadWriteLock`
- `FutureTask`

它本质上是一个**锁和同步器的基础框架**。AQS 通过一个 `int` 类型的 `state` 状态变量和一个 FIFO 双向队列来管理线程竞争。[Oracle 文档](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/locks/AbstractQueuedSynchronizer.html?utm_source=chatgpt.com)

---

# 一、为什么需要 AQS？

在没有 AQS 之前，实现一个锁需要自己处理：

- 线程竞争
- 阻塞
- 唤醒
- 排队
- 公平性

非常麻烦。

例如：

```
线程A 获取锁
线程B 获取锁失败
线程C 获取锁失败
线程D 获取锁失败
```

失败的线程怎么办？

不能一直 while 循环：

```
while(true){
    if(获取锁成功){
        break;
    }
}
```

这叫：

> 自旋

会浪费 CPU。

所以 AQS 提供：

```
获取失败
    |
    ↓
加入等待队列
    |
    ↓
阻塞线程
    |
    ↓
锁释放
    |
    ↓
唤醒后继线程
```

---

# 二、AQS核心结构

AQS主要包含三个东西：

## 1. state状态变量

```
private volatile int state;
```

表示同步状态。

例如：

### ReentrantLock

state：

```
0   没有线程持有锁

1   一个线程持有锁

2   同一个线程重入两次
```

例如：

```
lock.lock();

lock.lock();
```

state：

```
0
 |
 v
1
 |
 v
2
```

释放：

```
2
 |
 v
1
 |
 v
0
```

---

state修改依靠CAS：

```
compareAndSetState()
```

底层：

```
期望值
   |
   |
当前state == 期望值？
       |
       是
       |
修改state
```

保证线程安全。

官方文档说明 AQS 使用 `getState()`、`setState()` 和 CAS 修改这个原子状态。[Oracle 文档](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/locks/AbstractQueuedSynchronizer.html?utm_source=chatgpt.com)

---

# 2. CLH同步队列

AQS内部维护一个等待队列：

结构：

```
        AQS队列


head                         tail

 ↓                           ↓

Node ---> Node ---> Node ---> Node

线程A      线程B      线程C
```

每个等待线程包装成：

```
Node节点
```

里面保存：

```
Thread thread;

Node prev;

Node next;

waitStatus;
```

例如：

线程竞争锁：

```
线程1
 |
获取成功


线程2
 |
失败

线程3
 |
失败
```

进入：

```
head
 |
 ↓
线程2
 |
 ↓
线程3
```

---

# 3. Node节点

AQS里面线程不是直接排队：

而是：

```
Thread
 |
包装
 |
Node
 |
进入队列
```

类似：

```
线程B
 ↓
Node(B)

线程C
 ↓
Node(C)
```

---

# 三、AQS两种模式

AQS支持两种同步模式：

## 1. 独占模式（Exclusive）

一个线程独占。

例如：

```
ReentrantLock
```

流程：

```
线程A

获取锁

state=1


线程B

获取失败

进入队列等待
```

---

## 2. 共享模式（Shared）

多个线程可以同时获得。

例如：

```
Semaphore
CountDownLatch
读锁
```

例如：

Semaphore：

设置：

```
Semaphore semaphore=new Semaphore(3);
```

表示：

最多三个线程进入。

```
线程1 √
线程2 √
线程3 √
线程4 等待
```

---

# 四、ReentrantLock底层就是AQS

我们常用：

```
ReentrantLock lock=new ReentrantLock();


lock.lock();

try{

}catch(Exception e){

}finally{

lock.unlock();

}
```

底层：

```
ReentrantLock

        |
        ↓

Sync

        |
        ↓

AQS
```

---

# 五、AQS获取锁流程（重点面试）

以 `lock.lock()` 为例：

## 第一步：尝试获取锁

调用：

```
tryAcquire()
```

例如：

```
state == 0
```

说明没有人持有：

CAS：

```
0 --> 1
```

成功：

```
线程成为锁拥有者
```

---

## 第二步：获取失败

例如：

```
线程A持有锁


线程B来了
```

B：

```
tryAcquire失败
```

进入：

```
addWaiter()
```

加入队列：

```
head

 |
 ↓

A

 |
 ↓

B
```

---

## 第三步：线程阻塞

调用：

```
LockSupport.park()
```

线程进入WAITING状态。

---

## 第四步：释放锁

A执行：

```
unlock()
```

调用：

```
tryRelease()
```

state：

```
1

↓

0
```

---

## 第五步：唤醒等待线程

唤醒：

```
B
```

B重新竞争：

```
tryAcquire()
```

成功：

```
state=1
```

获得锁。

---

# 六、AQS源码重要方法

面试重点：

---

## 1. acquire()

获取独占锁：

```
public final void acquire(int arg)
```

流程：

```
acquire

 ↓

tryAcquire

 ↓失败

addWaiter

 ↓

acquireQueued

 ↓

park阻塞
```

---

## 2. release()

释放锁：

```
public final boolean release(int arg)
```

流程：

```
release

 ↓

tryRelease

 ↓

unpark唤醒后继节点
```

---

# 七、AQS和synchronized区别

||synchronized|AQS|
|---|---|---|
|实现|JVM层面|Java代码实现|
|锁升级|有|没有|
|等待队列|Object Monitor|Node队列|
|可中断|否|可以|
|公平锁|不支持|支持|
|多个条件队列|不支持|支持|

---

# 八、AQS面试回答模板

面试：

> 说一下AQS？

回答：

> AQS全称AbstractQueuedSynchronizer，是Java并发包中的同步器框架。它通过一个volatile int类型的state表示同步状态，通过CAS保证状态修改的原子性，并维护一个FIFO双向链表队列保存竞争失败的线程。线程获取资源失败后会进入队列并阻塞，当资源释放后唤醒后继节点重新竞争。ReentrantLock、Semaphore、CountDownLatch等都是基于AQS实现的。