
# 一、Arrays 类概述

源码：

```
public class Arrays {
    // 工具类，全部是 static 方法
}
```

特点：

- 不能创建对象
- 方法全部是 `static`
- 底层大量使用循环和优化算法

例如：

```
int[] arr = {3,1,5,2};

Arrays.sort(arr);

System.out.println(Arrays.toString(arr));
```

输出：

```
[1, 2, 3, 5]
```

---

# 二、常用方法（面试重点）

## 1. toString()

### 作用

将数组转换成字符串输出。

---

普通数组：

```
int[] arr = {1,2,3};

System.out.println(arr);
```

输出：

```
[I@1b6d3586
```

原因：

数组继承 Object，没有重写 toString。

---

使用 Arrays：

```
System.out.println(Arrays.toString(arr));
```

输出：

```
[1, 2, 3]
```

---

源码思想：

```
public static String toString(int[] a){
    StringBuilder b = new StringBuilder();

    for(int i=0;i<a.length;i++){
        b.append(a[i]);
    }

    return b.toString();
}
```

---

# 2. sort()

## 数组排序

```
int[] arr = {5,3,1,4};

Arrays.sort(arr);

System.out.println(Arrays.toString(arr));
```

结果：

```
[1,3,4,5]
```

---

## 底层原理（面试）

不同类型数组使用不同排序：

### 基本类型数组

例如：

```
int[]
double[]
char[]
```

使用：

> Dual-Pivot QuickSort（双轴快速排序）

时间复杂度：

平均：

```
O(nlogn)
```

空间：

```
O(logn)
```

---

### 对象数组

例如：

```
String[]
Student[]
```

使用：

> TimSort（归并排序优化）

例如：

```
String[] names={
 "Tom",
 "Jack",
 "Bob"
};

Arrays.sort(names);
```

---

# 3. binarySearch()

## 二分查找

```
int[] arr={1,3,5,7,9};

int index = Arrays.binarySearch(arr,5);

System.out.println(index);
```

结果：

```
2
```

注意：

数组必须提前排序！

错误：

```
int[] arr={5,1,3};

Arrays.binarySearch(arr,3);
```

结果不确定。

---

返回规则：

找到：

```
返回下标
```

没找到：

```
-(插入位置)-1
```

例：

```
int[] arr={1,3,5};

Arrays.binarySearch(arr,4);
```

4应该插入下标2：

返回：

```
-3
```

---

# 4. copyOf()

## 数组复制

```
int[] arr={1,2,3};

int[] newArr=Arrays.copyOf(arr,5);

System.out.println(Arrays.toString(newArr));
```

结果：

```
[1,2,3,0,0]
```

---

## 源码思想

```
public static int[] copyOf(
        int[] original,
        int newLength)
{
    int[] copy=new int[newLength];

    System.arraycopy(
        original,
        0,
        copy,
        0,
        Math.min(original.length,newLength)
    );

    return copy;
}
```

---

# 5. copyOfRange()

复制指定范围

```
int[] arr={1,2,3,4,5};


int[] copy=
Arrays.copyOfRange(arr,1,4);


System.out.println(Arrays.toString(copy));
```

结果：

```
[2,3,4]
```

注意：

左闭右开：

```
[begin,end)
```

类似 String.substring()

---

# 6. fill()

填充数组

```
int[] arr=new int[5];

Arrays.fill(arr,10);


System.out.println(Arrays.toString(arr));
```

结果：

```
[10,10,10,10,10]
```

---

指定范围：

```
Arrays.fill(arr,1,4,5);
```

结果：

```
[0,5,5,5,0]
```

---

# 7. equals()

比较数组内容

```
int[] a={1,2,3};

int[] b={1,2,3};


System.out.println(a==b);
```

结果：

```
false
```

因为：

```
==比较地址
```

使用：

```
Arrays.equals(a,b);
```

结果：

```
true
```

---

底层：

逐个比较：

```
for(int i=0;i<a.length;i++){

    if(a[i]!=b[i])
        return false;

}

return true;
```

---

# 8. deepEquals()

比较二维数组

例如：

```
int[][] a={
 {1,2},
 {3,4}
};


int[][] b={
 {1,2},
 {3,4}
};


System.out.println(
Arrays.deepEquals(a,b)
);
```

结果：

```
true
```

为什么不用 equals？

因为：

```
Arrays.equals()
```

只能比较一层。

---

# 9. deepToString()

打印多维数组

```
int[][] arr={
 {1,2},
 {3,4}
};


System.out.println(
Arrays.deepToString(arr)
);
```

输出：

```
[[1, 2], [3, 4]]
```

---

# 10. asList()

数组转 List

```
String[] arr={
 "A","B","C"
};


List<String> list=
Arrays.asList(arr);
```

结果：

```
[A,B,C]
```

---

但是有坑：

## 基本类型数组

```
int[] arr={1,2,3};


List<int[]> list=
Arrays.asList(arr);
```

结果：

```
一个元素
```

原因：

泛型不支持基本类型。

正确：

```
Integer[] arr={
1,2,3
};
```

---

## asList不能修改长度

```
List<String> list=
Arrays.asList("A","B");


list.add("C");
```

异常：

```
UnsupportedOperationException
```

因为：

Arrays.asList返回的是：

> 固定长度 List


# Arrays.sort底层（面试重点）

例如：

```
Arrays.sort(arr);
```

流程：

```
Arrays.sort()
        |
        |
判断数组类型
        |
        |
基本类型
        |
DualPivotQuickSort

对象类型
        |
TimSort
```

---

# 五、Arrays常见面试题

## 1. Arrays.sort()底层是什么？

答：

- 基本类型：
    - 双轴快速排序
- 对象类型：
    - TimSort

---

## 2. Arrays.asList有什么坑？

答：

1. 返回固定长度List

```
add/remove
```

会异常。

2. 基本类型数组问题

```
int[]
```

会被认为一个元素。

---

## 3. 为什么数组打印不是内容？

因为：

数组继承 Object，没有重写 toString。

---

## 4. Arrays.equals和==区别？

||比较|
|---|---|
|==|地址|
|Arrays.equals|内容|