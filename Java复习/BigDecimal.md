
# BigDecimal（高精度小数）

## 1. 为什么需要 BigDecimal？

因为 double 有精度问题。

例如：

```
double a=0.1;
double b=0.2;

System.out.println(a+b);
```

结果：

```
0.30000000000000004
```

原因：

计算机二进制无法精确表示部分小数。

---

# 四、BigDecimal 常用 API

导入：

```
import java.math.BigDecimal;
```

---

# 1. 创建 BigDecimal（★★★★★）

## 推荐方式：

```
BigDecimal a =
    new BigDecimal("0.1");
```

---

不要：

```
new BigDecimal(0.1);
```

原因：

double已经产生误差。

例如：

```
System.out.println(
new BigDecimal(0.1)
);
```

可能：

```
0.100000000000000005551...
```

---

# 2. 加法 add()

```
BigDecimal a =
new BigDecimal("0.1");

BigDecimal b =
new BigDecimal("0.2");


BigDecimal result =
a.add(b);


System.out.println(result);
```

输出：

```
0.3
```

---

# 3. 减法 subtract()

```
a.subtract(b);
```

---

# 4. 乘法 multiply()

```
a.multiply(b);
```

---

# 5. 除法 divide()（★★★★★）

例如：

```
BigDecimal a =
new BigDecimal("10");

BigDecimal b =
new BigDecimal("3");


a.divide(b);
```

会报：

```
ArithmeticException
```

为什么？

因为：

```
10 / 3
=
3.333333...
```

无限循环小数。

---

正确：

```
a.divide(
    b,
    2,
    RoundingMode.HALF_UP
);
```

结果：

```
3.33
```

参数：

```
2       保留两位小数

HALF_UP 四舍五入
```

---

# 6. 比较 compareTo()

不能：

```
a > b
```

不能：

```
a == b
```

使用：

```
a.compareTo(b)
```

例如：

```
BigDecimal a =
new BigDecimal("10.0");

BigDecimal b =
new BigDecimal("10.00");


System.out.println(
    a.compareTo(b)
);
```

结果：

```
0
```

表示数值相等。

---

# 7. equals() 注意（面试重点）

BigDecimal：

```
new BigDecimal("10.0")
```

和：

```
new BigDecimal("10.00")
```

equals：

```
false
```

为什么？

因为 equals 比较：

```
数值 + 精度(scale)
```

例如：

```
10.0
scale=1

10.00
scale=2
```

不同。

---

compareTo：

```
0
```

只比较数值。

所以：

金额比较：

推荐：

```
compareTo()
```


## 8. valueOf

BigDecimal的valueOf方法底层对0-10之间的整数做了优化

```
当你使用了BigDecimal的valueOf方法并输入了[0~10]之间的整数，那么他不是直接new BifDecimal，而是从底层数组中获取该值

BigDecimal bd1 = BigDecimal.valueOf(8);
BigDecimal bd2 = BigDecimal.valueOf(8);
System.out.println(bd1==bd2); //true
```