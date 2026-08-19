
# Day4 学习目标

今天解决这些问题：

1. 为什么 ConcurrentHashMap 扩容比 HashMap 复杂？
2. 为什么 ConcurrentHashMap 扩容可以多个线程一起参与？
3. `sizeCtl` 到底是什么？
4. `ForwardingNode` 是干什么的？
5. 扩容过程中，get还能正常执行吗？
6. transfer源码流程

---

# 一、先回顾 HashMap 扩容

我们先对比一下。

HashMap：

```
旧数组

table


[0]

Node


[1]

Node


[2]

Node


       ↓ resize


新数组

newTable
```

扩容：

创建新数组：

```
Node<K,V>[] newTab = new Node[newCap];
```

然后：

把旧数据搬过去。

---

但是问题：

如果数据很多：

例如：

```
旧table:

16个桶

100万个Node
```

扩容需要：

遍历所有节点。

如果单线程：

耗时。

---

# 二、ConcurrentHashMap的设计

ConcurrentHashMap：

考虑：

> 能不能让多个线程一起搬数据？

答案：

可以。

例如：

现在：

```
旧table


0 1 2 3 4 5 6 7
```

扩容：

线程1负责：

```
0-1号桶
```

线程2负责：

```
2-3号桶
```

线程3：

```
4-5号桶
```

最后：

一起完成扩容。

这就是：

## 协助扩容机制

| `sizeCtl` 的值 | 对应的 Map 状态 / 核心含义 |
| :--- | :--- |
| **`0`** | **默认初始状态**。数组（`table`）尚未初始化，初始化时将使用默认容量 `16`。 |
| **`> 0`** | **两种情况**：<br>1. 如果数组还没初始化，表示**指定的初始容量**。<br>2. 如果数组已经初始化，表示**下一次触发扩容的阈值（Threshold）**，计算公式为 $\text{capacity} \times 0.75$。 |
| **`-1`** | **正在初始化**。某个线程正在抢占执行 `initTable()` 初始化数组，其他线程看到 `-1` 会调用 `Thread.yield()` 让出 CPU 时间片挂起等待。 |
| **`< -1`** | **正在并发扩容（Transfer）**。多个线程正在一起协同扩容/搬迁数据。高位记录扩容邮戳，低位记录参与扩容的线程数。 |
# 三、扩容入口：addCount()

ConcurrentHashMap什么时候扩容？

和HashMap一样：

元素数量达到阈值。

例如：

```
map.put()
```

之后：

会调用：

```
addCount()
```

作用：

统计元素数量。

---

源码：

```
private final void addCount(long x, int check)
```

里面：

判断：

```
if (check >= 0)
```

然后：

判断是否需要扩容。

---

核心变量：

```
sizeCtl
```

来了。

🔥 面试重点。

---

# 四、sizeCtl是什么？

这是 ConcurrentHashMap 最重要的变量之一。

定义：

```
private transient volatile int sizeCtl;
```

它控制：

- 初始化
- 扩容
- 扩容线程数量

不同状态：

---

## 情况1：初始化之前

```
sizeCtl = 0
```

表示：

还没有初始化。

---

第一次put：

执行：

```
initTable()
```

设置：

```
sizeCtl = 16
```

表示：

下一次扩容阈值。

---

## 情况2：正常状态

例如：

```
sizeCtl=12
```

表示：

扩容阈值。

因为：

默认：

负载因子：

0.75

所以：

16容量：

阈值：

```
16 * 0.75 = 12
```

---

## 情况3：正在扩容

重点来了。

扩容期间：

```
sizeCtl < 0
```

例如：

```
sizeCtl=-2145779710
```

表示：

正在扩容。

而且：

里面包含：

两个信息：

1. 扩容标识
    
2. 当前参与扩容线程数量
    

---

所以：

看到：

```
sizeCtl < 0
```

说明：

> 当前正在扩容。


---


# 五、为什么sizeCtl需要CAS？

因为：

可能多个线程同时发现：

需要扩容。

例如：

线程1：

put

发现：

超过阈值。

线程2：

put

也发现：

超过阈值。

如果没有控制：

两个线程：

同时：

```
创建newTable
```

造成：

重复扩容。

---

所以：

通过CAS：

竞争扩容资格。

只有一个线程：

负责创建新数组。

其他线程：

加入扩容。

---

# 六、扩容核心方法 transfer()

源码：

```
private final void transfer(Node<K,V>[] tab,
                             Node<K,V>[] nextTab)
```

两个参数：

---

## tab

旧数组：

```
oldTable
```

---

## nextTab

新数组：

```
newTable
```

---

如果：

第一次扩容：

```
nextTab == null
```

创建：

```
new Node[newCap]
```

---

结构：

例如：

扩容：

16 → 32

旧：

```
table


0

1

2

3
```

新：

```
nextTable


0

1

2

3

4

5

6

7
```

---

# 七、扩容最大的创新：ForwardingNode

这是今天最重要的知识。

源码：

```
static final class ForwardingNode<K,V>
extends Node<K,V>
```

它是什么？

简单理解：

> 一个标记节点，表示当前桶已经迁移完成。

---

例如：

原来：

```
oldTable[5]


Node(A)

↓

Node(B)
```

线程1迁移：

搬到：

```
newTable[5]
```

然后：

旧位置：

放入：

```
ForwardingNode
```

变成：

```
oldTable[5]


ForwardingNode
```

表示：

这里已经搬完了。

---

为什么需要？

因为：

扩容期间：

另一个线程：

可能执行：

```
get(key)
```

它看到：

```
oldTable[5]

ForwardingNode
```

知道：

数据已经迁移。

于是：

去新数组找。

---

所以：

扩容期间：

ConcurrentHashMap：

仍然可以读。

这是HashMap没有的能力。

---

# 八、今天先掌握整体流程

整个扩容：

```
put


↓

addCount


↓

发现超过阈值


↓

检查sizeCtl


↓

CAS竞争扩容


↓

transfer()


↓

创建nextTable


↓

多个线程协助迁移


↓

桶迁移完成


↓

放ForwardingNode


↓

table指向新数组
```

---

# 今天第一轮理解结束。

现在问你三个问题（源码面试）：

### 问题1：

为什么 ConcurrentHashMap 扩容可以让多个线程一起参与，而 HashMap 不行？

> ConcurrentHashMap通过协助扩容机制允许多个线程共同迁移桶数据。它通过sizeCtl控制扩容状态，通过ForwardingNode标记已经迁移完成的桶，使多个线程能够安全参与扩容。而HashMap没有这些并发控制机制，因此不能支持多线程扩容。

---

### 问题2：

`sizeCtl` 有什么作用？为什么扩容的时候它会变成负数？

> sizeCtl是ConcurrentHashMap中控制初始化和扩容的重要变量。初始化前为0，初始化后表示扩容阈值。当扩容进行时，会设置为负数，表示当前正在扩容，并且其中记录了扩容标识和参与扩容的线程数量。

---

### 问题3：

为什么扩容完成的桶要放一个 `ForwardingNode`，直接设置成 null 不行吗？

> ForwardingNode用于标记当前桶已经完成迁移。如果直接设置为null，其他线程在扩容期间访问时无法判断该桶是否已经迁移，可能误认为数据不存在。ForwardingNode可以让读操作发现扩容状态，并跳转到新数组继续查询。


---


# transfer()源码分析

这是 ConcurrentHashMap 面试中非常高频的一块。

先看源码入口：

```
private final void transfer(Node<K,V>[] tab,
                             Node<K,V>[] nextTab)
```

作用：

> 将旧table中的Node迁移到新的nextTable中。

---

# 一、扩容前后的结构

假设当前：

```
oldTable

容量：4


0       1       2       3

A       B       C       D
```

现在扩容：

```
4 → 8
```

创建：

```
newTable

容量：8


0 1 2 3 4 5 6 7
```

然后把旧数据搬过去。

---

# 二、为什么扩容不是一次迁移全部？

HashMap：

```
一个线程：

遍历整个数组

全部搬完
```

但是 ConcurrentHashMap：

可能：

```
线程1

搬0、1号桶


线程2

搬2、3号桶


线程3

搬4、5号桶
```

所以需要一个东西：

记录：

> 哪些桶已经被线程领取。

这个东西就是：

# transferIndex

---

# 三、transferIndex是什么？

源码：

```
private transient volatile int transferIndex;
```

作用：

记录：

> 下一批需要迁移的桶位置。

---

在 `ConcurrentHashMap` 扩容时，旧数组被分割成若干个子区间（区间大小由 `stride` 决定，最小为 16 个槽位）。由于多个线程可能同时参与扩容，需要依靠 `transferIndex` 来**无锁（CAS）按块切分并领取任务**。

- **初始值**：等于旧数组的长度 $n$（例如 64）。
    
- **分配方向**：从后往前（从高地址到低地址）倒序切分。
    
- **终止条件**：`transferIndex <= 0` 表示旧数组的所有槽位都已经分配给了某个线程，不再有新的区间可供领取。

---

例如：

旧数组：

长度：

```
16
```

开始：

```
transferIndex=16
```

线程1进入：

领取任务：

比如：

```
16-13号桶
```

然后：

修改：

```
transferIndex=12
```

线程2进入：

看到：

```
transferIndex=12
```

领取：

```
12-9号桶
```

---

所以：

多个线程通过 CAS 修改：

```
transferIndex
```

来领取不同任务。

---

# 四、为什么需要stride（任务批次）？

> stride表示每个线程分配的桶数（步长）

源码：

```
int n = tab.length;

int stride = (NCPU > 1) ?
    (n >>> 3) / NCPU :
    n;
```

这里：

stride：

表示：

每个线程一次搬多少桶。

---

为什么不能：

一个线程搬一个桶？

例如：

16万个桶：

如果：

一个线程一个桶：

CAS竞争非常严重。

所以：

批量领取。

例如：

```
线程1：

100-80


线程2：

79-60


线程3：

59-40
```

效率更高。


---

# 五、transfer核心循环

源码：

```
for (int i = transferIndex; i > 0;) {

}
```

意思：

不断领取桶。

流程：

```
while

↓

还有桶没迁移？

↓

领取一批桶

↓

迁移

↓

标记完成

↓

继续
```

---

# 六、迁移一个桶的过程

重点来了。

假设：

旧数组：

```
oldTable[5]


Node(A)

↓

Node(B)

↓

Node(C)
```

线程拿到：

```
i=5
```

---

第一步：

判断桶：

```
if (tabAt(tab,i)==null)
```

如果为空：

直接放：

```
ForwardingNode
```

表示：

这个桶处理完成。

---

如果不是空：

进入迁移。

---

# 七、迁移链表

ConcurrentHashMap扩容和HashMap有一个优化：

它不会重新计算hash。

因为：

扩容都是2倍。

例如：

原容量：

```
16
```

新容量：

```
32
```

节点的新位置：

只需要看：

hash某一位。

---

例如：

原：

```
table长度16
```

某节点：

hash：

```
0101
```

扩容：

32：

看新增的一位。

---

所以节点只会：

两种去向：

## 原位置

或者：

## 原位置 + oldCap

例如：

oldCap：

16

节点：

可能：

```
5
```

或者：

```
5+16=21
```

所以迁移非常快。

---


# 八、lastRun优化（面试重点）

ConcurrentHashMap迁移链表时：

不会简单一个个复制。

它会找：

最后一段连续相同位置的节点。

例如：

旧链表：

```
A

↓

B

↓

C

↓

D

↓

E
```

计算位置：

```
A -> 5

B -> 5

C -> 21

D -> 21

E -> 21
```

发现：

后面：

```
C-D-E
```

都去：

21

那么：

直接复用：

```
C-D-E
```

减少Node创建。

---

这也是JDK8优化。

---

# 九、迁移完成为什么放ForwardingNode？

迁移完成：

```
setTabAt(tab,i,new ForwardingNode(nextTab));
```

旧桶：

变：

```
ForwardingNode
```

表示：

```
这个桶已经完成
```

---

现在：

oldTable:

```
0

1

2

3

ForwardingNode

5
```

其他线程看到：

```
hash == MOVED
```

知道：

去新数组。

---

# 十、整个transfer流程总结

现在你应该能画出来：

```
扩容触发

    ↓

创建nextTable

    ↓

设置sizeCtl负数

    ↓

多个线程CAS领取桶

    ↓

迁移链表/红黑树

    ↓

当前桶放ForwardingNode

    ↓

继续领取任务

    ↓

所有桶迁移完成

    ↓

table指向nextTable

    ↓

恢复sizeCtl
```

---

### 问题1：

ConcurrentHashMap扩容时，为什么线程之间不会迁移同一个桶？

提示：

想想 `transferIndex`。

> oncurrentHashMap扩容时通过transferIndex记录未迁移桶的位置，每个线程通过CAS领取一批桶进行迁移，领取成功后更新transferIndex，因此不同线程负责不同范围的桶，避免重复迁移。

---

### 问题2：

ConcurrentHashMap扩容时，为什么不用重新计算hash，而是根据hash某一位决定新位置？

> 因为ConcurrentHashMap扩容容量一定扩大2倍，根据Hash计算规律，节点扩容后只可能保持原索引或者移动到原索引+旧容量的位置，因此无需重新计算hash，只需要判断hash新增位即可。

---

### 问题3：

迁移完成后为什么不直接把旧桶设置为null，而是设置ForwardingNode？

> ForwardingNode用于标记桶已经完成迁移。如果设置为null，其他线程无法区分桶是不存在还是已经迁移完成，可能导致查询不到数据。ForwardingNode可以让线程感知扩容状态，并跳转到新数组查询。



----


# 一、扩容什么时候算结束？

看源码：

```
if (i <= 0 || i >= n)
```

这里是判断：

> 当前线程是否已经没有任务可以迁移。

---

我们回顾：

旧数组：

```
id="0p4q5m"
oldTable

0 1 2 3 4 5 6 7
```

线程领取：

例如：

线程1：

```
7 6
```

线程2：

```
5 4
```

每次领取后：

```
transferIndex -= stride;
```

例如：

开始：

```
transferIndex = 8
```

线程1：

领取：

```
7-6
```

变：

```
transferIndex = 6
```

继续：

线程2：

领取：

```
5-4
```

变：

```
transferIndex = 4
```

直到：

```
transferIndex <=0
```

说明：

> 所有桶已经被线程领取。

---

但是注意：

**领取完 ≠ 扩容完成**

为什么？

因为：

可能还有线程正在执行：

```
桶迁移
```

所以需要最后确认。

---

# 二、为什么最后需要 finishing 状态？

源码：

```
boolean finishing = false;
```

这个变量非常关键。

流程：

第一次：

```
finishing=false
```

表示：

> 普通迁移阶段

---

当发现：

所有桶已经处理：

进入：

```
if (!finishing)
```

---

设置：

```
finishing=true;
```

意思：

进入：

> 最后的检查阶段

---

为什么需要再检查一次？

因为：

可能存在：

线程刚刚完成迁移，但是还没有设置ForwardingNode。

例如：

线程A：

正在：

```
oldTable[5]

Node

↓

newTable
```

线程B：

发现：

所有任务领取完。

如果直接结束：

可能：

漏掉。

所以：

最后线程需要：

再次遍历检查。

---

# 三、为什么最后一个线程负责完成table替换？

源码：

```
nextTable = null;
table = nextTab;
sizeCtl = sc;
```

这里是重点。

---

## 1. 为什么不是扩容开始就：

```
table = nextTab;
```

？

因为：

此时：

新数组是不完整的。

例如：

oldTable:

```
id="6v2a1v"
0

NodeA

1

NodeB

2

NodeC
```

迁移一半：

newTable:

```
id="1r4n3x"
0

NodeA


1

空


2

NodeC
```

如果此时：

```
table=newTable
```

那么：

线程get：

可能访问：

```
newTable[1]
```

发现：

空。

数据丢失。

---

所以：

必须：

所有桶迁移完成。

最后：

```
table=nextTab
```

---

# 四、为什么需要最后一个线程？

因为：

ConcurrentHashMap允许：

多个线程一起扩容。

例如：

线程：

A

B

C

都参与迁移。

那么谁负责：

最后：

```
table=newTable
```

答案：

最后完成扩容的线程。

---

源码：

```
if (finishing) {

    nextTable = null;

    table = nextTab;

    sizeCtl = sc;

    return;

}
```

---

# 五、sizeCtl恢复

扩容过程中：

```
sizeCtl < 0
```

例如：

```
-2146238465
```

表示：

正在扩容。

完成后：

恢复：

```
sizeCtl=sc
```

例如：

16容量：

扩容后：

32

新的阈值：

```
32*0.75=24
```

所以：

```
sizeCtl=24
```

表示：

下一次达到24继续扩容。

---

# 六、为什么nextTable最后置null？

源码：

```
nextTable=null;
```

原因：

释放引用。

因为：

扩容完成：

真正使用：

```
table
```

旧的：

```
nextTable
```

已经没有意义。

让GC可以回收。

---

# 七、完整ConcurrentHashMap扩容流程总结

现在整个Day4可以串起来：

```
put()

↓

addCount()

↓

元素超过阈值

↓

检查sizeCtl

↓

CAS设置扩容状态

↓

创建nextTable

↓

transfer()

↓

transferIndex分配任务

↓

多个线程迁移桶

↓

迁移完成放ForwardingNode

↓

所有桶完成

↓

最后线程：

table = nextTable

↓

nextTable=null

↓

sizeCtl恢复阈值
```

---

# 八、面试高频问题总结