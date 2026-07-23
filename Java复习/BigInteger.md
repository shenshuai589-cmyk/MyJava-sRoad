## 1. 为什么需要 BigInteger？

Java 基本整数类型有范围限制：

|类型|范围|
|---|---|
|int|-2³¹ ~ 2³¹-1|
|long|-2⁶³ ~ 2⁶³-1|

如果超过 long：

```
long num = 9999999999999999999L;
```

会溢出。

所以使用：

```
BigInteger
```

---

# 二、BigInteger 常用 API

导入：

```
import java.math.BigInteger;
```

---

## 1. 创建 BigInteger

### 方式1：字符串（推荐）

```
BigInteger a =
    new BigInteger("999999999999999999999");
```

为什么用字符串？

因为：

```
new BigInteger(999999999999999999)
```

这个数字本身已经超过 Java 表示范围。

---

## 2. 加法 add()

```
BigInteger a =
    new BigInteger("100");

BigInteger b =
    new BigInteger("200");


BigInteger c = a.add(b);

System.out.println(c);
```

输出：

```
300
```

---

## 3. 减法 subtract()

```
a.subtract(b);
```

例如：

```
200 - 100
```

---

## 4. 乘法 multiply()

```
a.multiply(b);
```

---

## 5. 除法 divide()

```
a.divide(b);
```

例如：

```
10 / 3
```

结果：

```
3
```

只保留整数部分。

---

## 6. 取余 remainder()

```
a.remainder(b);
```

例如：

```
10 % 3
```

结果：

```
1
```

---

## 7. 比较 compareTo()

BigInteger 不能使用：

```
>
<
==
```

例如：

错误：

```
a > b
```

---

使用：

```
a.compareTo(b)
```

返回：

|结果|含义|
|---|---|
|大于0|a>b|
|等于0|a=b|
|小于0|a<b|

示例：

```
BigInteger a =
new BigInteger("100");

BigInteger b =
new BigInteger("200");


System.out.println(
    a.compareTo(b)
);
```

结果：

```
-1
```


## 8. valueOf方法

valueOf方法底层对-16~16进行了优化：
```
当使用了valueOf方法输入的数满足 [-16~16],之间，那么BigInteger会直接从底层的数组中拿出这个数，而不是new一个新的BigInteger对象

BigInteger bd1 = BigInteger.valueOf(16);
BigInteger bd2 = BigInteger.valueOf(16);

System.out.println(bd1 == bd2); // true
```

# BigInteger 底层原理

## 1. BigInteger 为什么能表示超大整数？

普通：

```
int
long
```

底层：

```
固定大小二进制
```

例如：

```
long
64 bit
```

超过：

```
9223372036854775807
```

就溢出。

---

BigInteger：

底层使用：

```
int[] mag
```

保存数字。

简单理解：

例如：

数字：

```
123456789012345
```

拆分：

```
123
456
789
012
345
```

保存：

```
int[] mag = {
    123,
    456,
    789,
    12,
    345
};
```

所以：

数字多大都可以，只要内存够。

---

# 2. BigInteger 核心属性

JDK源码：

```
public class BigInteger 
    extends Number 
    implements Comparable<BigInteger>
{

    final int signum;

    final int[] mag;

}
```

两个核心：

---

## signum

表示符号：

```
1    正数

0    0

-1   负数
```

例如：

```
-12345
```

保存：

```
signum = -1

mag = [12345]
```

---

## mag

Magnitude：

表示绝对值。

例如：

```
123456
```

保存：

```
signum = 1

mag = [123456]
```
