
## 1. String不可变的原因

### 关键成员变量是 `private final`

在 JDK 8 及以前，`String` 内部维护的是一个字符数组：

Java

```
private final char value[];
```

_在 JDK 9+ 中优化为了 `private final byte[] value;`（以节省空间），但机制一致。_

- **`private`**：禁止外部直接修改或访问这个数组。
    
- **`final`**：保证 `value` 引用一旦被初始化，就**不能再指向其他的数组对象**。
    

### 2. 类本身被声明为 `final`

Java

```
public final class String implements java.io.Serializable, Comparable<String>, CharSequence { ... }
```

`String` 类被 `final` 修饰，意味着它**无法被继承**。这防止了子类通过重写方法或引入非 final 状态来破坏不可变性。

### 3. 没有对外暴露任何修改底层数组的方法

- `String` 提供的所有修改方法（如 `replace()`、`substring()`、`toLowerCase()` 等），在底层**都不是直接修改原数组**，而是**在内存中创建一个全新的 `String` 对象**并返回。
    
- 类的构造函数在接收数组参数时，会进行深拷贝（Deep Copy），防止外部传入引用后在外部直接改变原数组内容。


---


## String为什么设计成不可变



### 1. 支持字符串常量池（String Pool）

字符串在 Java 中使用极为频繁。为了节省内存，JVM 实现了**字符串常量池**：多个相同内容的字符串变量可以共享同一个内存对象（如上图中的 `"abc"`）。

- **前提条件**：只有当 `String` 是不可变的，共享才是安全的。
    
- 如果 `String` 可变，当变量 A 改变了字符串内容，所有指向该常量的变量（B、C ...）都会被意外修改。
    

### 2. 安全性（Security）

`String` 在 Java 中被广泛用作许多关键组件的参数：

- **网络与数据库**：URL、IP 地址、数据库连接字符串等。
    
- **系统安全**：文件路径、类加载器加载类名。
    

如果 `String` 是可变的，攻击者可以在传入参数并通过安全检查后，利用多线程或异步操作在后台篡改字符串（即 **TOCTOU 问题**，Check-Time to Use-Time），从而绕过权限验证或注入恶意命令。

### 3. 绝对的线程安全（Thread Safety）

因为 `String` 对象状态一旦创建就无法更改，所以它**天生是线程安全**的。

- 在多线程环境下，多个线程可以同时读取同一个 `String` 实例，无需任何同步锁（`synchronized`）机制，大大提高了高并发场景下的性能。
    

### 4. 优化 HashCode 缓存

`String` 经常被用来作为 `HashMap`、`HashSet` 等哈希集合的 **Key**。

Java

```
private int hash; // 默认为 0
```

因为 `String` 不可变，它的 `hashCode()` 在第一次计算后就可以**直接缓存到 `hash` 成员变量中**。后续再次调用 `hashCode()` 时直接取缓存值即可，性能极高。如果字符串可变，每次内容改变都需要重新计算 Hash 值，且作为 Key 时会导致查找失败。


---


## 2. equals和hashCode

假设：

```
User u1 = new User("张三");
User u2 = new User("张三");
```

我们重写：

```
equals()
```

让：

```
u1.equals(u2)
```

返回：

```
true
```

那么 Java 有一个非常重要的约定：

> **如果两个对象 equals 相等，那么它们的 hashCode 必须相等。**

也就是：

```
u1.equals(u2) == true

↓

u1.hashCode() == u2.hashCode()
```

注意方向：

### 必须保证：

```
equals true
    ↓
hashCode 一定相同
```

但是：

```
hashCode 相同
    ↓
equals 不一定 true
```

这就是所谓的：

> **Hash 冲突**

例如：

```
对象 A → hash = 10
对象 B → hash = 10
```

不代表：

```
A.equals(B) == true
```


现在来看：

```
map.put(key, value);
```

HashMap 大致会：

```
key
 ↓
hashCode()
 ↓
hash()
 ↓
计算桶位置
 ↓
找到桶
 ↓
equals()
 ↓
判断是不是同一个 key
```

所以：

> **hashCode 负责“快速定位大概在哪”，equals 负责“最终确认是不是它”。**

这句话你一定要记住。


---



## 为什么需要hash和equals

假设你有：

```
HashMap<String, User>
```

里面有：

```
100000 个 User
```

现在：

```
map.get("张三");
```

如果没有 hash：

```
一个一个找
↓
张三？
↓
不是
↓
张三？
↓
不是
...
```

效率很差。

所以 HashMap 首先通过：

```
key.hashCode()
```

计算 hash。

然后确定：

> **这个 key 大概率应该去哪一个桶。**

**因为可能发生：

> **Hash 冲突**

例如：

```
key1
 ↓
hash = 100


key2
 ↓
hash = 100
```

那么：

```
┌─────────────┐
│   bucket    │
├─────────────┤
│ key1        │
│ key2        │
└─────────────┘
```

所以：

```
hashCode()
    ↓
定位桶
    ↓
equals()
    ↓
判断具体是哪一个对象
```

这就是：

> **HashMap 为什么同时需要 hashCode 和 equals。****


---



## 为什么HashMap需要红黑树


假设大量 key 发生 hash 冲突：

```
数组
 ↓
一个桶
 ↓
Node
 ↓
Node
 ↓
Node
 ↓
Node
 ↓
Node
 ↓
Node
...
```

如果一直是链表：

> 查询可能退化成 O(n)。

所以 Java 8 引入：

```
链表
 ↓
过长
 ↓
红黑树
```

让查询效率在理想情况下从：

```
O(n)
```

改善到：

```
O(log n)
```


---


## 为什么 HashMap 需要先计算 hash？

你的回答：

> HashMap 通过计算 hash 值来找到该元素应该存储的桶位置。

### ✅ 正确

可以再提高一个层次：

```
key
 ↓
hashCode()
 ↓
hash()
 ↓
计算桶下标
 ↓
找到对应桶
```

HashMap 不可能每次都把所有元素遍历一遍，所以先通过 hash **快速定位到可能存放 key 的桶**。

你面试时可以说：

> **HashMap 首先根据 key 的 hashCode 计算 hash，然后通过 hash 快速定位元素所在的桶，从而避免遍历整个数组。**

这个回答就比较标准了。

---

# 为什么计算完 hash 后还要调用 equals？

你的回答：

> 因为存在 hash 冲突的情况，需要使用 equals 方法来判断该元素是否和 hash 冲突的元素值相等。

### ✅ 完全正确

这就是 HashMap 的核心逻辑：

```
             key
              ↓
          hashCode()
              ↓
             hash
              ↓
          定位桶位置
              ↓
        ┌─────┴─────┐
        ↓           ↓
     没有节点      有节点
        ↓           ↓
      直接放      equals()
                    ↓
              判断是不是同一个key
```

这里你一定要记住一句：

> **hashCode 负责快速定位，equals 负责最终确认。**

还有一个非常重要的关系：

```
equals 相等
    ↓
hashCode 必须相等

hashCode 相等
    ↓
equals 不一定相等
```

也就是：

### hash 相同 ≠ 对象一定相同

这就是**哈希冲突**。


---

## 为什么 HashMap 的数组长度通常是 2 的幂？


核心就在 HashMap 的数组下标计算：

```
(n - 1) & hash
```

其中：

```
n = 数组长度
```

例如：

```
n = 16
```

那么：

```
n - 1 = 15
```

二进制：

```
16 = 10000
15 = 01111
```

于是：

```
hash & 01111
```

就可以快速得到：

```
0 ~ 15
```

范围内的数组下标。


---

## 为什么 HashMap 不直接使用 `%` 计算数组下标？

如果数组长度是 16：

传统思路：

```
hash % 16
```

而 HashMap 可以利用：

```
hash & (16 - 1)
```

得到相同的效果。

也就是：

```
hash % 16

≈

hash & 15
```

这样做的一个重要好处是：

> **位运算效率高，而且 HashMap 可以通过这种方式快速计算桶下标。**

所以：

```
数组长度是 2 的幂
        ↓
n - 1 的二进制全是 1
        ↓
(hash & (n - 1))
        ↓
快速计算数组下标
```