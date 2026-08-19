## 第一部分：为什么需要 ConcurrentHashMap

## 第二部分：JDK7 ConcurrentHashMap 原理

## 第三部分：JDK8 ConcurrentHashMap 核心结构

## 第四部分：put 源码分析

（今天重点）

---

# 一、为什么需要 ConcurrentHashMap？

先看 HashMap。

普通 HashMap：

```
HashMap<String,Integer> map = new HashMap<>();
```

单线程：

没问题。

但是多线程：

```
线程1              线程2

put(A,1)           put(B,2)

        ↓

    修改table数组
```

两个线程同时修改：

可能出现问题。

---

## 问题1：数据覆盖

例如：

初始：

```
table[5]

null
```

线程1：

```
put("A",1)
```

线程2：

```
put("B",2)
```

两个线程同时判断：

```
table[5]==null
```

然后：

线程1：

```
table[5]=A
```

线程2：

```
table[5]=B
```

结果：

```
A丢失
```

---

## 问题2：扩容死循环（JDK7）

HashMap 在 JDK7 扩容时使用：

> 头插法

多线程扩容可能导致：

```
Node1

↓

Node2

↓

Node1
```

形成循环链表。

查询：

```
get()
```

可能：

无限循环。

---

所以：

HashMap：

> 线程不安全。

---

# 二、ConcurrentHashMap是什么？

ConcurrentHashMap：

就是：

> 支持高并发访问的 HashMap。

目标：

不是简单加锁。

因为：

如果：

```
synchronized
```

包住整个 HashMap：

```
synchronized put(){

}
```

那么：

所有线程排队。

效率很低。

---

所以 ConcurrentHashMap 的设计思想：

> 尽可能缩小锁的范围。

---

# 三、JDK7 ConcurrentHashMap

先看老版本。

JDK7：

ConcurrentHashMap 采用：

# Segment 分段锁

结构：

```
ConcurrentHashMap


        Segment数组


       ↓

+-------+-------+-------+
| Seg0  | Seg1  | Seg2  |
+-------+-------+-------+


每个Segment里面：

HashEntry数组
```

---

类似：

大 HashMap：

拆成很多小 HashMap。

---

例如：

假设：

```
ConcurrentHashMap

Segment[16]
```

线程：

线程1：

操作：

```
Segment0
```

线程2：

操作：

```
Segment1
```

因为：

锁不同。

所以：

可以同时执行。

---

# 四、Segment是什么？

Segment 本质：

继承：

```
ReentrantLock
```

也就是说：

Segment 自带锁。

结构：

```
Segment

{

    HashEntry[] table;

    ReentrantLock;

}
```

---

所以：

JDK7：

加锁位置：

```
Segment
```

---

# 五、JDK7 put流程

假设：

```
map.put("abc",100);
```

流程：

```
put()

 ↓

计算hash

 ↓

定位Segment

 ↓

获取Segment锁

 ↓

定位HashEntry

 ↓

插入节点

 ↓

释放锁
```

---

重点：

锁住的不是整个 Map。

而是：

某一个 Segment。

---

例如：

三个线程：

线程1：

```
Segment0
```

线程2：

```
Segment1
```

线程3：

```
Segment2
```

可以同时执行。

---

# 六、JDK7的问题

Segment方案很好。

但是：

缺点：

## 1. 锁粒度还是比较大

例如：

一个 Segment：

```
Segment0

   |
   |
HashEntry数组

[0]
[1]
[2]
[3]
```

如果两个线程：

同时操作：

```
Segment0
```

仍然需要竞争。

---

## 2. Segment数量固定

默认：

```
16
```

并发度有限。

最多：

16个线程同时修改。

---

所以 JDK8 进行了重大改进。

---

# 七、JDK8 ConcurrentHashMap结构

重点来了。

JDK8：

放弃 Segment。

改成：

> CAS + synchronized

结构：

和 HashMap 类似：

```
ConcurrentHashMap


Node数组


table[]

[0]

[1]

[2]

...
```

也就是：

```
数组
+
链表
+
红黑树
```

---

和 HashMap 很像。

但是：

区别：

HashMap：

```
不安全
```

ConcurrentHashMap：

```
线程安全
```

---

# 八、JDK8 为什么不用 Segment？

因为：

CAS。

JDK8：

把锁控制到了：

> 桶（Node链表头）

也就是：

以前：

```
Segment锁

      ↓

一大片区域
```

现在：

```
Node桶锁

      ↓

一个桶
```

锁粒度更小。

---

# 九、JDK8 put核心思想

源码：

```
public V put(K key, V value) {

    return putVal(key,value,false);

}
```

进入：

```
putVal()
```

核心流程：

```
put()

 ↓

计算hash

 ↓

判断table是否初始化

 ↓

计算桶位置

 ↓

桶为空？

 ↓
CAS插入


桶不为空？

 ↓

synchronized锁住桶头节点


 ↓

链表/红黑树插入
```

---

# 十、重点：CAS插入

假设：

```
table[5]

null
```

现在两个线程：

线程1：

put(A)

线程2：

put(B)

---

如果使用 synchronized：

两个都要抢锁。

但是：

桶为空时：

根本没有竞争。

所以：

JDK8：

直接 CAS。

---

源码思想：

```
if(tab[i]==null){

    CAS设置Node

}
```

---

CAS：

Compare And Swap

比较并交换。

三个参数：

```
内存地址

旧值

新值
```

---

例如：

当前：

```
table[5]=null
```

线程1：

CAS：

```
null → Node(A)
```

成功：

```
table[5]=A
```

线程2：

CAS：

```
null → Node(B)
```

发现：

实际已经：

```
A
```

失败。

然后：

重新处理。

---

# 十一、为什么CAS安全？

因为：

CAS是原子操作。

不会出现：

```
读取

↓

修改

↓

写回
```

被打断。

---

普通：

```
x=x+1;
```

其实：

三个步骤：

```
读取x

计算

写回
```

多线程会出问题。

CAS：

一步完成。


