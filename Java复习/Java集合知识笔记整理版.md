# Java 集合知识笔记（整理版）

> 来源：根据用户提供的集合笔记整理，保留核心知识点并优化结构。

------------------------------------------------------------------------

# 一、Java 集合整体结构

Java 集合主要分为：

    Collection
    │
    ├── List
    │   ├── ArrayList
    │   └── LinkedList
    │
    └── Set
        ├── HashSet
        ├── LinkedHashSet
        └── TreeSet


    Map
    ├── HashMap
    ├── LinkedHashMap
    └── TreeMap

## Collection

Collection 是 List 和 Set 的父接口。

常用方法：

``` java
add(E e)             // 添加元素
remove(Object o)     // 删除元素
contains(Object o)   // 判断是否包含元素
size()               // 获取集合大小
isEmpty()            // 判断是否为空
clear()              // 清空集合
iterator()           // 获取迭代器
```

------------------------------------------------------------------------

# 二、Collection 遍历方式

## 1. Iterator 迭代器

``` java
Collection<String> list = new ArrayList<>();

Iterator<String> it = list.iterator();

while(it.hasNext()){
    String s = it.next();
    System.out.println(s);
}
```

底层：

-   iterator() 获取迭代器对象
-   hasNext() 判断是否还有元素
-   next() 获取元素

------------------------------------------------------------------------

## 2. 增强 for 循环

``` java
for(String str : list){
    System.out.println(str);
}
```

底层：

-   集合：转换为 Iterator 遍历
-   数组：转换为普通 for 循环

------------------------------------------------------------------------

## 3. forEach

``` java
list.forEach(s -> {
    System.out.println(s);
});
```

底层基于 Consumer 函数式接口。

------------------------------------------------------------------------

# 三、List 集合

特点：

-   有序
-   可重复
-   有索引

常用方法：

``` java
add(E e)

add(int index,E element)

remove(int index)

remove(Object o)

set(int index,E element)

get(int index)

size()
```

遍历：

``` java
for(int i = 0;i < list.size();i++){
    System.out.println(list.get(i));
}
```

------------------------------------------------------------------------

# 四、ArrayList

特点：

-   底层可变数组结构
-   查询快
-   增删慢

常用方法：

``` java
add()

remove()

get()

set()

size()

contains()
```

示例：

``` java
ArrayList<String> list = new ArrayList<>();

list.add("张三");
list.add("李四");

System.out.println(list.get(0));

list.remove("张三");
```

------------------------------------------------------------------------

# 五、LinkedList

特点：

-   底层双向链表
-   查询慢
-   增删快

特有方法：

``` java
addFirst()

addLast()

getFirst()

getLast()

removeFirst()

removeLast()
```

------------------------------------------------------------------------

# 六、Set 集合

特点：

-   无索引
-   不可重复

常用方法：

``` java
add()

remove()

contains()

size()

clear()
```

------------------------------------------------------------------------

# 七、HashSet

特点：

-   无序
-   不重复
-   无索引

底层：

    哈希表
    = 数组 + 链表 + 红黑树

初始化：

-   默认数组长度：16
-   加载因子：0.75
-   当元素达到 12 个时扩容
- 每次扩容自身容量的2倍

添加流程：

1.  根据 hashCode 计算存储位置
2.  如果位置为空，直接存入
3.  如果存在元素：
    -   equals 相同，不添加
    -   equals 不同，形成链表或树结构

注意：

如果存储自定义对象：

必须重写：

``` java
equals()

hashCode()
```

------------------------------------------------------------------------

# 八、LinkedHashSet

特点：

-   有序
-   不重复
-   无索引

底层：

    LinkedHashMap

    =
    哈希表 + 双向链表

特点：

保证：

    存入顺序 = 取出顺序

------------------------------------------------------------------------

# 九、TreeSet

特点：

-   无索引
-   不重复
-   可排序

底层：

    红黑树

添加元素时：

元素类需要实现：

``` java
Comparable
```

并重写：

``` java
compareTo()
```

返回：

-   负数：当前元素小
-   0：元素相同，不存入
-   正数：当前元素大


### 1. 自然排序（Natural Ordering）

- **原理**：元素所属的类 **实现 `Comparable` 接口**，并重写 `compareTo(T o)` 方法。
    
- **适用场景**：当类有默认的、固定的排序规则时使用（例如 `Integer`、`String` 等 Java 内置类默认已经实现了该接口）。
    
- **使用方式**：使用无参构造函数创建 TreeSet：`new TreeSet<>()`。
    

Java

```
// 自定义类实现 Comparable 接口
public class Student implements Comparable<Student> {
    private String name;
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Student o) {
        // 按年龄升序排序
        return Integer.compare(this.age, o.age);
    }
}

// 使用：
Set<Student> set = new TreeSet<>();
set.add(new Student("张三", 20));
set.add(new Student("李四", 18));
```

### 2. 比较器排序（Comparator Ordering）

- **原理**：在创建 TreeSet 时，传入一个自定义的 **`Comparator` 比较器对象**，重写 `compare(T o1, T o2)` 方法。
    
- **适用场景**：
    
    1. 元素所属的类没有实现 `Comparable` 接口，且你**无法修改其源码**。
        
    2. 元素类已经有了默认排序规则，但你需要**临时或特殊的排序规则**（如按字符串长度排序、降序排序等）。
        
- **使用方式**：使用带参构造函数创建 TreeSet：`new TreeSet<>(Comparator)`。
    

Java

```
// 使用 Lambda 表达式或匿名内部类传入 Comparator
Set<Student> set = new TreeSet<>((s1, s2) -> {
    // 按年龄降序排序（与自然排序方向相反）
    return Integer.compare(s2.getAge(), s1.getAge());
});

set.add(new Student("张三", 20));
set.add(new Student("李四", 18));
```

### 两种方式对比与核心注意点

|**特性**|**自然排序 (Comparable)**|**比较器排序 (Comparator)**|
|---|---|---|
|**重写方法**|`compareTo(T o)`|`compare(T o1, T o2)`|
|**代码位置**|写在**元素类**内部|写在** TreeSet 构造方法**参数中|
|**灵活性**|规则单一，修改不便|灵活性高，可自由更换不同比较逻辑|
|**优先级**|较低|**较高**（若同时存在，以 Comparator 为准）|

------------------------------------------------------------------------

# 十、Map 集合

特点：

双列集合：

    key=value

特点：

-   key 不可重复
-   value 可以重复

常用方法：

``` java
put(K key,V value)

get(K key)

remove(K key)

containsKey(K key)

containsValue(V value)

size()

clear()
```

------------------------------------------------------------------------

# 十一、Map 遍历方式

## 1. keySet()

``` java
Set<String> keys = map.keySet();

for(String key : keys){

    String value = map.get(key);

}
```

------------------------------------------------------------------------

## 2. entrySet()

推荐方式：

``` java
Set<Map.Entry<String,String>> entries =
        map.entrySet();

for(Map.Entry<String,String> entry : entries){

    System.out.println(
        entry.getKey()+":"+entry.getValue()
    );
}
```

------------------------------------------------------------------------

## 3. Lambda

``` java
map.forEach((key,value)->{
    System.out.println(key+":"+value);
});
```

------------------------------------------------------------------------

# 十二、HashMap

底层：

    数组 + 链表 + 红黑树

默认：

-   数组长度 16
-   加载因子 0.75

put流程：

1.  根据 key 计算 hash
2.  找到数组位置
3.  如果为空，直接存储
4.  如果存在：
    -   key相同：覆盖value
    -   key不同：挂入链表/树

常用方法：

``` java
put()

get()

remove()

containsKey()

containsValue()
```

------------------------------------------------------------------------

# 十三、LinkedHashMap

特点：

-   有序
-   不重复
-   无索引

底层：

    HashMap + 双向链表

保证：

    存储顺序 = 取出顺序

------------------------------------------------------------------------

# 十四、TreeMap

特点：

-   不重复
-   无索引
-   可排序

底层：

    红黑树

排序依据：

key

------------------------------------------------------------------------

# 十五、Collections 工具类

特点：

1.  构造方法私有化
2.  方法全部 static

常用方法：

``` java
Collections.addAll()

Collections.sort()

Collections.shuffle()

Collections.reverse()

Collections.max()

Collections.min()
```

------------------------------------------------------------------------

# 十六、Arrays 工具类

常用方法：

``` java
Arrays.sort()

Arrays.toString()

Arrays.copyOf()

Arrays.binarySearch()
```

------------------------------------------------------------------------

# 十七、面试重点方法

## Collection

重点：

    add()
    remove()
    contains()
    size()

## List

重点：

    add()
    get()
    set()
    remove()

## Map

重点：

    put()
    get()
    remove()
    containsKey()

------------------------------------------------------------------------

# 十八、学习优先级

⭐⭐⭐⭐⭐

1.  ArrayList
2.  HashMap
3.  HashSet
4.  LinkedList
5.  TreeMap

------------------------------------------------------------------------

# 十九、开发常用选择

普通列表：

    List → ArrayList

普通去重：

    Set → HashSet

键值存储：

    Map → HashMap

需要排序：

    TreeSet
    TreeMap

需要保持插入顺序：

    LinkedHashSet
    LinkedHashMap
