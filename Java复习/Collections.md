
`Collections` 是 Java 提供的一个**操作集合的工具类**。

包：

```
java.util.Collections
```

特点：

- 不能创建对象（构造方法私有）
- 提供大量 **静态方法**
- 用于操作 `List、Set、Map` 等集合

源码结构：

```
public class Collections {

    private Collections() {}

}
```

使用：

```
Collections.xxx();
```

---

# 一、排序方法 sort()

## 1. 基本排序

```
List<Integer> list = new ArrayList<>();

list.add(3);
list.add(1);
list.add(2);

Collections.sort(list);

System.out.println(list);
```

输出：

```
[1,2,3]
```

底层：

JDK8：

```
public static <T extends Comparable<? super T>> 
void sort(List<T> list)
```

调用：

```
List.sort(null)
```

最终使用：

```
TimSort算法
```

---

# 二、自定义排序 Comparator

例如按照年龄排序：

```
List<User> list = new ArrayList<>();

Collections.sort(list,new Comparator<User>(){

    @Override
    public int compare(User u1,User u2){

        return u1.getAge()-u2.getAge();

    }

});
```

结果：

年龄升序。

---

## Lambda写法

```
Collections.sort(list,
    (u1,u2)->u1.getAge()-u2.getAge()
);
```

---

# 三、reverse() 反转集合

```
List<Integer> list =
        new ArrayList<>(Arrays.asList(1,2,3));


Collections.reverse(list);
```

结果：

```
[3,2,1]
```

底层：

交换首尾元素：

```
swap(list, i, j);
```

---

# 四、shuffle() 随机打乱

```
List<Integer> list =
new ArrayList<>(Arrays.asList(1,2,3,4));


Collections.shuffle(list);
```

可能：

```
[3,1,4,2]
```

底层：

随机交换元素。

应用：

- 斗地主洗牌
- 随机抽奖

---

# 五、swap() 交换元素

```
List<String> list =
new ArrayList<>();

list.add("A");
list.add("B");


Collections.swap(list,0,1);
```

结果：

```
[B,A]
```

源码：

```
E temp=list.get(i);

list.set(i,list.get(j));

list.set(j,temp);
```

---

# 六、max() 最大值

```
List<Integer> list =
Arrays.asList(10,20,30);


Integer max=Collections.max(list);
```

结果：

```
30
```

底层：

遍历比较：

```
compareTo()
```

---

# 七、min() 最小值

```
Integer min =
Collections.min(list);
```

---

# 八、binarySearch() 二分查找

```
List<Integer> list =
Arrays.asList(1,3,5,7);


int index =
Collections.binarySearch(list,5);
```

返回：

```
2
```

注意：

必须先排序：

```
Collections.sort(list);
```

否则结果不可靠。

时间复杂度：

```
O(log n)
```

---

# 九、fill() 填充集合

```
List<String> list =
new ArrayList<>();

list.add("A");
list.add("B");
list.add("C");


Collections.fill(list,"Java");
```

结果：

```
[Java,Java,Java]
```

---

# 十、copy() 集合复制

```
List<String> src =
Arrays.asList("A","B","C");


List<String> dest =
Arrays.asList("","","");


Collections.copy(dest,src);
```

结果：

```
[A,B,C]
```

注意：

目标集合长度必须 >= 源集合。

错误：

```
List<String> dest=new ArrayList<>();

Collections.copy(dest,src);
```

会报：

```
IndexOutOfBoundsException
```

---

# 十一、frequency() 统计出现次数

```
List<String> list =
Arrays.asList(
"Java",
"Java",
"MySQL"
);


int count =
Collections.frequency(list,"Java");
```

结果：

```
2
```

---

# 十二、replaceAll() 替换元素

```
List<String> list =
Arrays.asList(
"Java",
"Java",
"MySQL"
);


Collections.replaceAll(
list,
"Java",
"Spring"
);
```

结果：

```
[Spring,Spring,MySQL]
```

---

# 十三、addAll() 批量添加

```
List<String> list=new ArrayList<>();


Collections.addAll(
    list,
    "Java",
    "Spring",
    "MySQL"
);
```

等价：

```
list.add("Java");
list.add("Spring");
list.add("MySQL");
```

---

# 十四、empty集合创建

## emptyList()

返回空List：

```
List<String> list =
Collections.emptyList();
```

特点：

不可修改：

```
list.add("Java");
```

异常：

```
UnsupportedOperationException
```

还有：

```
Collections.emptySet();

Collections.emptyMap();
```

---

# 十五、singleton() 创建单元素集合

```
List<String> list =
Collections.singletonList("Java");
```

只能有一个元素。

修改：

```
list.add("Spring");
```

异常：

```
UnsupportedOperationException
```

---

# 十六、unmodifiableXXX 不可修改集合

例如：

```
List<String> list =
new ArrayList<>();

list.add("Java");


List<String> newList =
Collections.unmodifiableList(list);
```

之后：

```
newList.add("Spring");
```

异常。

---

# 十七、Collections和Collection区别（面试必问）

||Collection|Collections|
|---|---|---|
|类型|接口|工具类|
|包|java.util|java.util|
|作用|定义集合规范|操作集合|
|是否能创建对象|不能|不能|
|方法|add、remove|sort、reverse|