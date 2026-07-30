
Java线程中常用的方法主要有：

- `sleep()`
- `wait()`
- `notify()`
- `notifyAll()`
- `join()`

这些方法经常一起比较，尤其是 **sleep和wait区别**，属于多线程面试高频。

---

# 一、sleep()

## 1. 作用

`sleep()`：

> 让当前正在执行的线程暂停一段时间，进入阻塞状态。

方法：

```
Thread.sleep(long millis);
```

例如：

```
public class Test {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("开始");

        Thread.sleep(3000);

        System.out.println("结束");
    }
}
```

执行：

```
开始

等待3秒

结束
```

---

# 2. sleep()影响线程状态

调用：

```
Thread.sleep(1000);
```

线程状态：

```
RUNNABLE

      |
      |
sleep()

      |
      ↓

TIMED_WAITING

      |
      |
时间结束

      |
      ↓

RUNNABLE
```

注意：

`sleep()`不会释放CPU后永久停止。

时间到了：

重新进入就绪状态，等待CPU调度。

---

# 3. sleep()是否释放锁？（重点）

答案：

> 不释放锁。

例如：

```
public class Demo {


    private static Object lock = new Object();


    public static void main(String[] args) {


        Thread t1 = new Thread(() -> {


            synchronized(lock){

                System.out.println("线程1获得锁");


                try {

                    Thread.sleep(5000);

                } catch(Exception e){

                }


                System.out.println("线程1释放锁");
            }

        });



        Thread t2 = new Thread(() -> {


            synchronized(lock){

                System.out.println("线程2获得锁");

            }


        });


        t1.start();

        t2.start();

    }

}
```

执行：

```
线程1获得锁

等待5秒

线程2获得锁
```

原因：

虽然线程1睡眠：

但是：

```
锁还在手里
```

所以线程2必须等待。

---

# 4. 为什么sleep不释放锁？

因为：

`sleep()`只是让当前线程暂停执行。

它不知道：

- 你有没有锁
- 锁是什么

它只负责：

```
暂停线程
```

---

# 5. sleep()注意点

## （1）必须捕获异常

因为：

```
Thread.sleep()
```

会抛：

```
InterruptedException
```

所以：

```
try {

    Thread.sleep(1000);

}catch(InterruptedException e){

}
```

---

## （2）sleep是静态方法

调用：

```
Thread.sleep(1000);
```

不要：

```
thread.sleep(1000);
```

虽然可以编译：

但是容易误解。

实际上：

睡眠的是：

> 当前执行sleep的线程

例如：

```
Thread t=new Thread();


t.sleep(1000);
```

不是让t睡眠。

而是：

当前线程睡眠。

---

# 二、wait()

## 1. 作用

`wait()`：

> 让当前线程进入等待状态，直到其他线程唤醒。

方法：

```
Object.wait();
```

注意：

wait属于：

```
Object
```

不是Thread。

原因：

Java中的锁是对象。

---

# 2. wait为什么属于Object？

因为：

任何对象都可以作为锁：

例如：

```
Object lock=new Object();


synchronized(lock){

}
```

所以：

对象需要提供：

等待和唤醒机制。

因此：

wait定义在Object中。

---

# 3. wait使用要求（重点）

必须在：

```
synchronized
```

里面。

错误：

```
lock.wait();
```

直接调用：

会：

```
IllegalMonitorStateException
```

---

正确：

```
synchronized(lock){

    lock.wait();

}
```

---

# 4. wait释放锁（重点）

例如：

```
synchronized(lock){


    System.out.println("获得锁");


    lock.wait();


}
```

执行过程：

线程1：

```
获得lock锁

↓

wait()

↓

释放lock

↓

进入等待队列
```

线程2：

```
获取lock锁

↓

执行
```

---

# 5. wait状态变化

```
RUNNABLE

↓

wait()

↓

WAITING

↓

notify()

↓

BLOCKED

↓

获取锁

↓

RUNNABLE
```

注意：

notify后：

不是马上执行。

而是：

进入竞争锁状态。

---

# 三、notify()

## 1. 作用

唤醒一个等待线程。

代码：

```
lock.notify();
```

---

例如：

线程A：

```
synchronized(lock){

    lock.wait();

}
```

进入等待。

线程B：

```
synchronized(lock){

    lock.notify();

}
```

唤醒A。

---

# 2. notify注意点

## （1）必须在synchronized中

错误：

```
lock.notify();
```

正确：

```
synchronized(lock){

    lock.notify();

}
```

---

## （2）不会立即释放锁

例如：

线程B：

```
synchronized(lock){

    lock.notify();

    //继续执行

}
```

线程A：

被唤醒。

但是：

必须等待：

线程B释放锁。

---

# 四、notifyAll()

## 1. 作用

唤醒所有等待线程。

例如：

等待队列：

```
lock对象

线程A

线程B

线程C
```

调用：

```
notifyAll()
```

结果：

```
线程A
线程B
线程C

全部被唤醒
```

但是：

最终只有一个线程能获得锁。

---

# notify和notifyAll区别

||notify|notifyAll|
|---|---|---|
|唤醒数量|一个|全部|
|效率|高|低|
|风险|可能唤醒错误线程|安全|
|使用场景|明确知道线程|不确定|

---

# 五、join()

## 1. 作用

> 等待某个线程执行完成。

例如：

```
Thread t=new Thread(()->{

    System.out.println("子线程执行");

});


t.start();


t.join();


System.out.println("主线程结束");
```

执行：

```
子线程执行

↓

主线程结束
```

---

# 2. join底层原理

join内部实际上使用：

```
wait()
```

简单理解：

主线程：

等待子线程结束。

类似：

```
synchronized(thread对象){

    thread.wait();

}
```

子线程结束：

JVM调用：

```
notifyAll()
```

唤醒等待线程。

---

# 3. join应用场景

例如：

订单系统：

需要：

```
查询用户信息

查询商品信息

查询库存

↓

组合结果
```

主线程：

等待三个任务完成。

---

# 六、sleep和wait区别（面试必问）

|区别|sleep|wait|
|---|---|---|
|所属类|Thread|Object|
|释放锁|不释放|释放|
|使用位置|任何地方|必须synchronized|
|唤醒方式|时间结束|notify/notifyAll|
|状态|TIMED_WAITING|WAITING|
|目的|暂停执行|线程通信|

---

# 七、wait/notify经典生产者消费者模型

例如：

生产者：

生产商品：

```
synchronized(lock){

    while(库存满){

        lock.wait();

    }


    生产();

    lock.notifyAll();

}
```

消费者：

消费商品：

```
synchronized(lock){

    while(库存为空){

        lock.wait();

    }


    消费();

    lock.notifyAll();

}
```

流程：

```
生产者
   |
库存满
   |
 wait等待


消费者
   |
消费
   |
 notify唤醒生产者
```

这是面试经常问的：

> wait和notify有什么实际应用？

---

# 八、面试总结

## sleep()

记：

> 让线程休眠，不释放锁。

---

## wait()

记：

> 让线程等待，并释放锁，需要notify唤醒。

---

## notify()

记：

> 唤醒一个等待线程，但不会立即释放锁。

---

## notifyAll()

记：

> 唤醒所有等待线程。

---

## join()

记：

> 让当前线程等待目标线程执行完成。