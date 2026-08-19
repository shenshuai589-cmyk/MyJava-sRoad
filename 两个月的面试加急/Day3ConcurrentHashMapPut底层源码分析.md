
> **从源码角度搞懂 JDK8 ConcurrentHashMap 的 put 流程。**


# 一、先看整体结构

JDK8 ConcurrentHashMap：

```
ConcurrentHashMap

        table数组

          ↓

+----+----+----+----+
| 0  | 1  | 2  | 3  |
+----+----+----+----+

每个位置：

Node

 ↓

Node

 ↓

Node


或者：

TreeBin(红黑树)
```

和 HashMap 很像：

```
数组
+
链表
+
红黑树
```

但是增加了：

```
CAS

synchronized

volatile
```

保证线程安全。

---

# 二、put入口

我们先看入口：

```
public V put(K key, V value) {
    return putVal(key, value, false);
}
```

非常简单。

调用：

```
putVal()
```

---

# 三、putVal源码结构

核心源码：

```java
final V putVal(K key, V value, boolean onlyIfAbsent) {

    if (key == null || value == null)
        throw new NullPointerException();
        

    int hash = spread(key.hashCode());

    int binCount = 0;

    for (Node<K,V>[] tab = table;;) {
    Node<K,V> f; int n, i, fh;
    
    // 【分支 1】：数组尚未初始化
    if (tab == null || tab.length == 0)
        tab = initTable(); // 利用 CAS 进行延迟初始化
        
    // 【分支 2】：目标桶是空的（无哈希冲突）
    else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
        // 利用 CAS 无锁写入新节点！如果成功，直接跳出循环
        if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
            break; // 插入成功，退出 for 循环
        // 如果 CAS 失败（说明别的线程抢先占了这个桶），继续下一次 for 循环重试
    }
    
    // 【分支 3】：发现桶节点的 hash 是 MOVED (-1)，说明 HashMap 正在扩容
    else if ((fh = f.hash) == MOVED)
        tab = helpTransfer(tab, f); // 当前线程主动加入，协助其他线程一起扩容
        
    // 【分支 4】：产生了哈希冲突（桶不为空，且没在扩容）
    else {
        V oldVal = null;
        // 仅仅锁住当前桶的头节点 f（细粒度锁）
        synchronized (f) {
            if (tabAt(tab, i) == f) { // 双重检查，确保头节点没有被改变
                if (fh >= 0) { // hash >= 0 表示这是普通的链表
                    binCount = 1;
                    for (Node<K,V> e = f;; ++binCount) {
                        K ek;
                        // 如果找到相同的 Key，覆盖 value
                        if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                            oldVal = e.val;
                            if (!onlyIfAbsent)
                                e.val = value;
                            break;
                        }
                        Node<K,V> pred = e;
                        // 到了链表末尾，尾插法插入新节点
                        if ((e = e.next) == null) {
                            pred.next = new Node<K,V>(hash, key, value, null);
                            break;
                        }
                    }
                }
                else if (f instanceof TreeBin) { // 标识这是红黑树
                    Node<K,V> p;
                    binCount = 2;
                    if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key, value)) != null) {
                        oldVal = p.val;
                        if (!onlyIfAbsent)
                            p.val = value;
                    }
                }
            }
        }
        
        // 插入完成后的后续处理
        if (binCount != 0) {
            // 如果链表节点数达到 8 个，尝试转为红黑树
            if (binCount >= TREEIFY_THRESHOLD)
                treeifyBin(tab, i);
            if (oldVal != null)
                return oldVal;
            break; // 退出 for 循环
        }
    }
}

// 退出 for 循环后：增加元素计数并检查是否需要触发扩容
addCount(1L, binCount);
return null;

}
```

我们一点一点拆。

---

# 第一部分：为什么 key 和 value 不能为 null？

源码：

```
if (key == null || value == null)
    throw new NullPointerException();
```

HashMap：

允许：

```
map.put(null,null);
```

但是 ConcurrentHashMap：

不允许。

为什么？

因为并发环境下：

null 有歧义。

---

比如：

```
map.get("user")
```

返回：

```
null
```

代表什么？

可能：

情况1：

没有这个key

情况2：

key存在，但是value就是null

多线程情况下无法区分。

所以：

ConcurrentHashMap：

禁止 null。

---

# 第二部分：hash计算

源码：

```
int hash = spread(key.hashCode());
```

这里类似 HashMap：

```
hash(key)
```

作用：

降低 hash 冲突。

---

例如：

key：

```
"abc"
```

调用：

```
hashCode()
```

得到：

```
96354
```

然后：

spread扰动：

得到：

```
最终hash
```

---

为什么？

因为：

如果hash冲突太严重：

会导致：

```
table[5]

Node

 ↓

Node

 ↓

Node
```

链表过长。

影响性能。

---

# 第三部分：为什么使用for死循环？

源码：

```
for (Node<K,V>[] tab = table;;)
```

看到：

```
;;
```

很多同学第一次看懵。

其实：

这是一个自旋。

什么意思？

就是：

不断尝试。

直到：

成功。

类似：

CAS：

失败：

重新尝试。

---

例如：

线程1：

CAS成功。

线程2：

CAS失败。

线程2：

重新进入循环。

---

# 四、进入核心判断

源码逻辑：

```
if ((tab = table) == null || tab.length == 0)

    初始化table

else if ((f = tabAt(tab,i)) == null)

    CAS插入

else

    synchronized锁桶
```

这三个分支是 ConcurrentHashMap 的灵魂。

我们分别分析。

---

# 第一种情况：table没有初始化

代码：

```
if ((tab = table) == null || tab.length == 0)
    tab = initTable();
```

什么意思？

第一次 put：

```
ConcurrentHashMap map=new ConcurrentHashMap();
```

此时：

```
table=null
```

因为：

ConcurrentHashMap采用懒加载。

---

什么叫懒加载？

不是创建对象：

马上初始化数组。

而是：

第一次put：

才创建table。

---

为什么？

因为：

如果：

```
new ConcurrentHashMap();
```

之后：

一直不用。

那么：

提前创建数组：

浪费空间。

---

# 五、初始化table为什么需要控制并发？

重点来了。

假设：

第一次put。

两个线程：

线程A：

```
put(A)
```

线程B：

```
put(B)
```

此时：

```
table=null
```

两个线程同时发现：

```
table==null
```

怎么办？

如果两个都初始化：

线程A：

创建：

```
Node[16]
```

线程B：

创建：

```
Node[16]
```

然后：

互相覆盖。

---

所以：

initTable内部需要保证：

只有一个线程初始化。

---

# 六、initTable源码思想

源码：

```
private final Node<K,V>[] initTable() {

    Node<K,V>[] tab;

    while ((tab = table) == null || tab.length == 0) {

        if (sizeCtl < 0)

            Thread.yield();

        else if (U.compareAndSwapInt(this,
                  SIZECTL,
                  sc,
                  -1)) {

              try {

                  if (table == null) {

                     table = new Node[16];

                  }

              } finally {

                  sizeCtl = sc;

              }

        }

    }

    return table;
}
```

---

这里重点看：

```
CAS SIZECTL
```

---

# 七、为什么初始化用CAS？

因为：

需要竞争一个初始化资格。

例如：

线程A：

CAS：

```
sizeCtl: 0 → -1
```

成功。

表示：

线程A：

获得初始化权。

---

线程B：

同时：

CAS：

```
0 → -1
```

失败。

说明：

有人正在初始化。

于是：

等待。

---

所以：

CAS作用：

> 保证只有一个线程执行初始化操作。

---

## 问题1：为什么 ConcurrentHashMap 不允许 key 和 value 为 null？


> ConcurrentHashMap 不允许 key 和 value 为 null，是因为在并发环境下 null 无法表示明确的含义。调用 get 方法返回 null 时，无法判断是 key 不存在，还是 key 对应的 value 本身为 null，因此为了避免歧义，ConcurrentHashMap 禁止 null。

---

# 问题2：为什么 ConcurrentHashMap 采用懒加载初始化 table？

ConcurrentHashMap 采用懒加载机制，在创建对象时不会立即初始化 table 数组，而是在第一次 put 操作时调用 initTable 创建数组。这样可以避免大量创建但未使用的 ConcurrentHashMap 对象造成内存浪费。

---


## 为什么初始化 table 需要 CAS？

ConcurrentHashMap 初始化 table 时需要使用 CAS，是为了保证多个线程同时第一次 put 时，只有一个线程能够完成 table 初始化。如果没有 CAS，多个线程可能同时创建数组，导致数组覆盖甚至数据丢失。通过 CAS 修改初始化状态，成功的线程负责初始化，失败线程等待，从而保证初始化过程线程安全。

---


# 二、CAS插入空桶源码分析

源码：

```java
else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {

    if (casTabAt(tab, i, null,
        new Node<K,V>(hash, key, value)))
        
	    // table[i]，表示当前桶头节点位置，null期望当前的值为null，new Node()表示要插入的数据
        break;
}
```

这一段就是：

> **桶为空时，ConcurrentHashMap 不加锁，而是使用 CAS 插入。**

---

# 1. 先看这行代码

```
i = (n - 1) & hash
```

作用：

计算桶的位置。

和 HashMap 一样。

---

假设：

当前数组：

```
table长度 n = 16
```

也就是：

```
table[0]

table[1]

...

table[15]
```

---

key:

```
"A"
```

经过：

```
hash = spread(key.hashCode())
```

得到：

```
hash = 101010
```

计算：

```
(16-1)&hash
```

也就是：

```
15 & hash
```

得到：

```
5
```

所以：

这个key应该放：

```
table[5]
```

---

# 2. tabAt(tab,i)

源码：

```
(f = tabAt(tab,i))
```

作用：

获取当前桶的节点。

例如：

现在：

```
table


[0]


[1]


[2]


[3]


[4]


[5] ---> null


[6]
```

执行：

```
tabAt(tab,5)
```

返回：

```
null
```

说明：

桶为空。

---

# 3. 为什么桶为空可以不用锁？

这是重点。

假设：

如果使用：

```
synchronized
```

需要：

```
synchronized(什么对象?)
```

问题来了：

现在：

```
table[5]=null
```

没有对象。

你锁谁？

---

例如：

```
synchronized(table[5]){

}
```

实际上：

等价：

```
synchronized(null){

}
```

直接异常。

---

所以：

空桶情况下：

没有锁对象。

ConcurrentHashMap选择：

CAS。

---

# 4. casTabAt()

源码：

```
casTabAt(tab,i,null,new Node(...))
```

拆开：

三个核心参数：

```
当前位置

期望值

新值
```

也就是：

```
table[i]

当前是不是 null

如果是

替换成 Node
```

---

例如：

当前：

```
table[5]=null
```

线程A：

执行：

```
CAS(
table[5],
null,
Node(A)
)
```

线程B：

执行：

```
CAS(
table[5],
null,
Node(B)
)
```

---

# 5. 两个线程同时CAS会怎样？

这是面试重点。

初始：

```
table[5]=null
```

---

## 线程A先执行

CAS：

比较：

```
当前值 == null?
```

结果：

是。

所以：

替换：

```
null

↓

Node(A)
```

成功：

```
table[5]=Node(A)
```

---

## 线程B执行

CAS：

它期待：

```
null
```

但是现在：

```
Node(A)
```

比较失败：

所以：

```
CAS失败
```

---

注意：

线程B不会覆盖线程A。

这就是 CAS 和普通赋值的区别。

---

# 6. CAS失败之后怎么办？

源码：

```
for(;;)
```

为什么前面说它是自旋？

就是这里。

线程B：

CAS失败：

重新进入循环。

重新判断：

```
table[5]
```

发现：

已经不是null。

进入：

```
else
```

也就是：

后面的：

```
synchronized(f)
```

---

流程：

```
第一次put


table为空

↓

CAS抢占


成功：

结束


失败：

重新循环

↓

发现桶已经有数据

↓

synchronized处理
```

---

# 7. 为什么不用 CAS 解决所有问题？

你可能会想到：

> 既然CAS这么好，为什么链表插入不用CAS？

原因：

链表修改复杂。

例如：

现在：

```
table[5]


Node A

 ↓

Node B

 ↓

Node C
```

插入：

Node D

需要修改：

```
A.next

B.next
```

可能涉及多个引用变化。

CAS：

适合：

简单状态修改。

例如：

```
null

↓

Node
```

但是复杂结构：

CAS实现困难。

---

所以：

ConcurrentHashMap设计：

## 空桶：

简单：

```
null → Node
```

使用：

```
CAS
```

## 非空桶：

复杂：

```
Node → Node → Node
```

使用：

```
synchronized
```

---

# 8. 这一段面试总结

如果面试问：

### 为什么 ConcurrentHashMap 空桶插入使用 CAS？

回答：

> 因为空桶不存在锁对象，无法通过 synchronized 加锁，同时空桶插入只是一次简单的引用替换操作，非常适合使用 CAS。多个线程同时插入时，CAS保证只有一个线程成功，失败线程重新竞争，从而避免锁带来的性能消耗。


---


```
else {

    synchronized (f) {

        if (tabAt(tab, i) == f) {

            if (fh >= 0) {

                // 链表处理

            }
            else if (f instanceof TreeBin) {

                // 红黑树处理

            }

        }
    }
}
```

---

# 一、什么时候进入这个 else？

前面我们分析了：

```
else if ((f = tabAt(tab, i)) == null)
```

表示：

当前桶为空。

那么现在：

进入 else：

说明：

```
table[i] != null
```

也就是：

桶里面已经有节点。

例如：

```
table[5]


       Node(A)

          ↓

       Node(B)

          ↓

       Node(C)
```

现在：

又来了：

```
put("D",4)
```

计算：

```
index=5
```

发现：

```
table[5]!=null
```

所以进入：

```
synchronized(f)
```

---

# 二、为什么非空桶需要 synchronized？

因为：

现在不是简单插入。

如果链表：

```
A

↓

B

↓

C
```

我要插入：

D

需要修改：

```
C.next=D
```

也就是：

改变已有节点引用。

---

假设没有锁：

两个线程：

线程1：

插入：

```
D
```

线程2：

插入：

```
E
```

原链表：

```
A

↓

B

↓

C
```

---

线程1：

读取：

```
C.next=null
```

准备：

```
C.next=D
```

---

线程2：

也读取：

```
C.next=null
```

准备：

```
C.next=E
```

---

结果：

可能：

```
A

↓

B

↓

C

↓

E
```

D丢失。

所以：

需要保证：

同一个桶：

同一时间只能一个线程修改。

---

# 三、为什么锁的是 f？

源码：

```
synchronized(f)
```

这里的：

```
f
```

是什么？

前面：

```
f = tabAt(tab,i)
```

所以：

f就是：

```
桶的头节点
```

例如：

```
table[5]


f

↓

Node(A)

↓

Node(B)

↓

Node(C)
```

锁：

```
synchronized(Node(A))
```

---

# 四、为什么不锁整个 table？

比如：

```
synchronized(table)
```

？

因为：

太大。

假设：

table：

```
[0]

Node


[1]

Node


[2]

Node


[3]

Node
```

线程1：

操作：

```
桶1
```

线程2：

操作：

```
桶3
```

如果锁table：

```
整个Map暂停
```

两个线程不能并发。

---

而锁头节点：

```
table[1]

Node(A) 🔒


table[3]

Node(B) 🔒
```

两个锁对象不同。

所以：

可以同时执行。

---

# 五、为什么需要这个判断？

源码：

```
if (tabAt(tab,i)==f)
```

什么意思？

我们前面：

```
f=tabAt(tab,i)
```

得到：

桶头节点。

但是：

从获取 f 到加锁：

中间可能发生变化。

例如：

时间线：

---

线程1：

读取：

```
f = Node(A)
```

---

线程2：

修改：

```
table[5]

Node(A)

↓

Node(B)
```

---

线程1：

开始：

```
synchronized(f)
```

但是：

此时：

f已经不是当前桶头节点。

所以：

需要重新确认：

```
tabAt(tab,i)==f
```

---

如果不相等：

说明：

桶已经变化。

重新循环处理。

---

# 六、进入链表处理

源码：

```
if (fh >= 0)
```

什么意思？

Node里面：

有：

```
hash
```

字段。

普通节点：

hash >=0

例如：

```
Node

hash=12345

key=A

value=1
```

所以：

表示：

这是普通链表。

---

结构：

```
table[5]


Node(hash=10)

 ↓

Node(hash=20)

 ↓

Node(hash=30)
```

---

开始遍历：

类似 HashMap：

```
for(Node e=f;;){

}
```

---

# 七、遍历链表寻找相同key

逻辑：

```
if(e.hash==hash &&
   (e.key==key || key.equals(e.key)))
```

什么意思？

和 HashMap 一样：

先比较：

## 1. hash值

```
hash相同
```

因为：

不同hash：

一定不是同一个key。

---

## 2. ==

判断：

引用地址。

例如：

```
String a="abc";

String b=a;
```

那么：

```
a==b
```

true。

---

## 3. equals()

判断：

内容。

例如：

```
String a=new String("abc");

String b=new String("abc");
```

地址不同：

但是：

```
a.equals(b)
```

true。

---

所以：

判断key相同：

必须：

```
hash相同

&&

(key引用相同 || equals相同)
```

---

# 八、如果key存在怎么办？

例如：

原来：

```
map.put("A",100)
```

现在：

```
map.put("A",200)
```

找到：

```
Node(A,100)
```

然后：

修改：

```
oldValue=100

value=200
```

---

这就是：

HashMap put覆盖。

---

# 九、如果key不存在？

例如：

原：

```
A

↓

B
```

put：

```
C
```

遍历结束：

```
e.next=null
```

执行：

```
pred.next=new Node(...)
```

变成：

```
A

↓

B

↓

C
```

---

# 十、这一段源码总结

现在 ConcurrentHashMap put流程：

```
put()


计算hash


↓

计算桶位置


↓

桶为空？

    ↓

    CAS插入


桶不为空？

    ↓

    synchronized锁桶头节点


    ↓

    判断链表/红黑树


    ↓

    遍历key


    ↓

    存在：修改value

    不存在：插入节点
```



---

## 问题1：

为什么 ConcurrentHashMap 非空桶需要 synchronized？

```
非空桶中可能存在链表或红黑树结构，插入、删除、修改节点都会涉及多个引用关系变化，因此需要 synchronized 保证同一个桶内结构修改的线程安全，避免数据覆盖。
```

## 问题2：

为什么锁的是桶头节点，而不是整个数组？

```
锁整个 table 会导致锁粒度过大，降低并发性能，而锁桶头节点只会影响当前桶的操作，其他桶仍然可以并发访问，提高了并发能力。
```

## 问题3：

为什么 synchronized(f) 后还需要判断：```
```
tabAt(tab,i)==f
```

```
synchronized(f) 后再次判断 tabAt(tab,i)==f，是为了防止在获取锁之前桶结构已经发生变化。通过判断引用是否仍然一致，确保当前线程加锁的是当前桶对应的头节点，避免操作错误的数据。
```


---


# 四、链表插入源码分析

核心代码：

```
if (fh >= 0) {

    binCount = 1;

    for (Node<K,V> e = f;; ++binCount) {

        K ek;

        if (e.hash == hash &&
            ((ek = e.key) == key ||
             (key != null && key.equals(ek)))) {

            oldVal = e.val;

            if (!onlyIfAbsent)
                e.val = value;

            break;
        }

        Node<K,V> pred = e;

        if ((e = e.next) == null) {

            pred.next = new Node<K,V>(hash,key,value);

            break;
        }
    }
}
```

我们拆开。

---

# 1. 为什么判断 `fh >= 0`？

先看：

```
if (fh >= 0)
```

这里的：

```
fh
```

就是：

```
f.hash
```

也就是桶头节点的 hash。

普通Node：

```
static final int MOVED = -1;
static final int TREEBIN = -2;
```

所以：

普通链表节点：

```
hash >= 0
```

例如：

```
Node

hash=12345

key=user

value=Tom
```

---

特殊节点：

## 扩容节点

```
hash=-1
```

表示：

```
ForwardingNode
```

代表：

> 当前桶正在迁移。

---

## 红黑树节点

```
hash=-2
```

表示：

```
TreeBin
```

代表：

> 当前桶已经转换为红黑树。

---

所以：

```
fh >=0
```

就是：

> 当前桶是普通链表结构。

---

# 2. 遍历链表

源码：

```
for(Node<K,V> e=f;;++binCount)
```

什么意思？

从头节点开始遍历：

例如：

```
table[5]


f

↓

Node(A)

↓

Node(B)

↓

Node(C)
```

第一次：

```
e = Node(A)
```

第二次：

```
e = Node(B)
```

第三次：

```
e = Node(C)
```

---

# 3. 判断key是否已经存在

源码：

```
if(e.hash == hash &&
   ((ek=e.key)==key ||
   key.equals(ek)))
```

这里和 HashMap 完全一样。

判断条件：

三个部分：

---

## 第一：

```
e.hash == hash
```

先比较hash。

为什么？

因为：

不同hash：

一定不是同一个key。

例如：

```
key=A

hash=10


key=B

hash=20
```

直接排除。

---

## 第二：

```
ek == key
```

比较引用。

例如：

```
String a="abc";

String b=a;
```

那么：

```
a==b
```

true。

---

## 第三：

```
key.equals(ek)
```

比较内容。

例如：

```
String a=new String("abc");

String b=new String("abc");
```

地址不同：

但是：

```
a.equals(b)
```

true。

---

所以：

HashMap和ConcurrentHashMap判断key相同：

都是：

```
hash相同

&&

(地址相同 || equals相同)
```

---

# 4. 找到相同key怎么办？

例如：

已有：

```
map.put("name","张三")
```

结构：

```
Node

key=name

value=张三
```

现在：

```
map.put("name","李四")
```

---

找到节点：

```
e.key=name
```

执行：

```
oldVal=e.val;
```

保存旧值：

```
张三
```

然后：

```
e.val=value;
```

修改：

```
李四
```

---

所以：

put相同key：

不是新增节点。

而是：

> 修改原节点value。

---

# 5. 如果key不存在怎么办？

例如：

当前：

```
A

↓

B

↓

C
```

put：

```
D
```

遍历：

A：

不是

B：

不是

C：

不是

继续：

```
if((e=e.next)==null)
```

说明：

到了链表尾部。

此时：

```
pred.next = new Node(...)
```

---

变成：

```
A

↓

B

↓

C

↓

D
```

---

注意：

这里为什么需要锁？

因为修改：

```
C.next
```

如果两个线程同时修改：

可能丢失数据。

---

# 6. 为什么这里使用尾插？

HashMap JDK8：

也是尾插。

以前 JDK7：

头插。

原因：

JDK7头插扩容时可能产生链表死循环。

JDK8：

保持链表顺序。

ConcurrentHashMap：

同样采用尾插。

---

# 7. binCount有什么作用？

源码：

```
++binCount
```

这个变量非常重要。

例如：

链表：

```
A

↓

B

↓

C

↓

D
```

遍历次数：

```
binCount=4
```

后面会用它判断：

是否需要树化。

---

后面源码：

```
if(binCount >= TREEIFY_THRESHOLD-1)

    treeifyBin(tab,i);
```

也就是：

链表长度达到：

```
8
```

准备转换红黑树。

---

# 到这里，我们理解了：

ConcurrentHashMap链表put流程：

```
进入synchronized桶锁

        ↓

判断普通链表

        ↓

遍历Node

        ↓

hash + == + equals判断key

        ↓

key存在

    修改value


key不存在

    尾部插入Node

        ↓

统计binCount

        ↓

判断是否树化
```

---

现在问你三个问题（面试模拟）：

### 问题1：

为什么 ConcurrentHashMap 判断 key 是否相同时，也需要：

```
hash == hash &&
(key==key || equals)
```

而不是直接 equals？

---

### 问题2：

为什么链表插入之后需要统计 `binCount`？

---

### 问题3：

ConcurrentHashMap 为什么也需要红黑树？它和 HashMap 的原因一样吗？



---

昨天我们停在这里：

```
else if (f instanceof TreeBin) {

    TreeBin<K,V> t = (TreeBin<K,V>)f;

    TreeNode<K,V> r = t.putTreeVal(hash,key,value);

}
```

也就是：

> 当前桶已经不是链表，而是红黑树结构时，ConcurrentHashMap如何插入。

---

# 一、为什么 ConcurrentHashMap 需要红黑树？

这个你刚才已经回答了：

> 链表长度过长，会导致查询效率下降，所以转换红黑树。

完全正确。

我们回顾一下：

普通链表：

```
table[5]


Node(A)

↓

Node(B)

↓

Node(C)

↓

Node(D)

↓

Node(E)
```

查询：

```
O(n)
```

如果冲突严重：

```
Node1

↓

Node2

↓

...

↓

Node100
```

查询非常慢。

---

红黑树：

```
          D

       /     \

      B       F

    /  \     /  \

   A    C   E    G
```

查询：

```
O(log n)
```

---

所以：

HashMap和ConcurrentHashMap都引入红黑树。

但是：

ConcurrentHashMap有一个特殊问题：

> 多线程环境下，如何安全操作红黑树？

---

# 二、为什么 ConcurrentHashMap 不直接使用 HashMap 的 TreeNode？

这是重点。

HashMap：

结构：

```
table[i]

    TreeNode

        ↓

    TreeNode

        ↓

    TreeNode
```

但是 ConcurrentHashMap：

不是：

```
table[i]

TreeNode
```

而是：

```
table[i]

TreeBin

   ↓

TreeNode

   ↓

TreeNode
```

多了一层：

```
TreeBin
```

---

# 三、TreeBin是什么？

简单理解：

> TreeBin 是红黑树的管理节点。

它负责：

1. 保存红黑树根节点
    
2. 管理读写锁
    
3. 控制并发访问
    

结构：

```
table[i]


TreeBin

   |

   |

 root

   |

 TreeNode

   |

 TreeNode
```

---

为什么需要TreeBin？

因为：

红黑树修改不是简单操作。

例如：

插入节点：

```
        10

       /

      5
```

插入：

```
3
```

可能发生：

- 左旋
- 右旋
- 重新染色

涉及：

多个节点变化。

如果多个线程同时修改：

可能破坏树结构。

---

# 四、为什么不用 synchronized 锁整个TreeBin？

你可能想到：

直接：

```
synchronized(TreeBin){

}
```

不行吗？

其实可以保证安全。

但是：

ConcurrentHashMap追求：

> 更高并发。

来看一个场景：

现在红黑树：

```
TreeBin

        10

      /    \

     5      15
```

线程1：

查询：

```
get(5)
```

线程2：

查询：

```
get(15)
```

两个都是读操作。

如果：

全部加锁：

```
线程1读

↓

线程2等待
```

浪费。

所以：

TreeBin设计了：

读写控制。

---

# 五、TreeBin内部锁机制

TreeBin里面有：

```
volatile int lockState;
```

表示锁状态。

主要有：

```
WRITER = 1

WAITER = 2

READER = 4
```

---

简单理解：

## 写锁

修改红黑树：

例如：

put

remove

需要：

独占。

类似：

```
一个线程修改

其他线程等待
```

---

## 读锁

查询：

get

多个线程：

可以同时读取。

类似：

```
线程1读

线程2读

线程3读

允许
```

---

这就是：

ConcurrentHashMap比HashMap复杂的地方。

---

# 六、什么时候链表转换红黑树？

源码：

前面：

```
if(binCount >= TREEIFY_THRESHOLD -1)

    treeifyBin(tab,i);
```

这里：

```
TREEIFY_THRESHOLD
```

值：

```
8
```

---

但是注意：

不是达到8一定树化。

还要：

```
数组容量 >= 64
```

---

为什么？

和HashMap一样。

因为：

如果数组太小：

优先扩容。

例如：

当前：

```
table长度=16
```

但是：

某个桶：

```
链表长度=8
```

可能原因：

数组太小。

扩容：

16 → 32 → 64

重新hash之后：

元素可能分散。

所以：

没必要马上树化。

---

# 七、treeifyBin源码思想

源码：

```
private final void treeifyBin(Node<K,V>[] tab,int index)
```

流程：

大概：

```
判断数组容量


        |

        |

小于64？

        |

        |

扩容


大于64？

        |

        |

链表转换TreeBin
```

---

所以：

这里和HashMap一样：

树化之前先判断容量。

---

# 八、面试总结

## 问题1：

为什么 ConcurrentHashMap 使用红黑树？

答案：

> 因为大量 hash 冲突时，链表查询效率降低，红黑树可以将查询复杂度从 O(n) 降低到 O(log n)，提高访问效率。

---

## 问题2：

为什么 ConcurrentHashMap 有 TreeBin，而 HashMap 没有？

答案：

> HashMap 是单线程结构，而 ConcurrentHashMap 需要考虑并发修改红黑树的问题，因此增加 TreeBin 作为红黑树管理节点，负责维护根节点以及控制读写并发。

---

## 问题3：

为什么链表长度达到8还不一定树化？

答案：

> 因为ConcurrentHashMap会优先考虑扩容，如果数组容量小于64，说明当前冲突可能来自数组过小，扩容后可能降低冲突，所以优先扩容而不是树化。