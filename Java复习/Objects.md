
`Objects` 是 Java 提供的一个**对象工具类**，位于：

```
java.util.Objects
```

作用：

> 提供一些处理对象的静态方法，主要用于**空值判断、对象比较、生成哈希值**。

源码：

```
public final class Objects
```

特点：

- 工具类
- 方法都是 `static`
- Java 7 引入

---

# 一、Objects 常用 API

## 1. Objects.equals()（★★★★★）

### 作用

安全比较两个对象是否相等。

方法：

```
public static boolean equals(Object a, Object b)
```

---

## 普通 equals 的问题

例如：

```
String s1 = null;
String s2 = "Java";

System.out.println(s1.equals(s2));
```

结果：

```
NullPointerException
```

因为：

```
s1 == null
```

调用方法会报错。

---

## 使用 Objects.equals()

```
String s1 = null;
String s2 = "Java";


System.out.println(
    Objects.equals(s1,s2)
);
```

结果：

```
false
```

不会空指针。

---

源码逻辑：

```
public static boolean equals(Object a,Object b){

    return (a==b) || 
           (a!=null && a.equals(b));

}
```

执行过程：

### 情况1：

```
a == b
```

直接 true。

### 情况2：

a不是null：

调用：

```
a.equals(b)
```

---

# 二、Objects.deepEquals()

## 作用

深度比较两个对象。

```
Objects.deepEquals(a,b)
```

常用于：

- 数组比较
- 嵌套对象比较

例如：

```
int[] a={1,2,3};
int[] b={1,2,3};


System.out.println(
    Objects.deepEquals(a,b)
);
```

结果：

```
true
```

---

区别：

|方法|作用|
|---|---|
|equals()|普通对象比较|
|deepEquals()|深度比较|

---

# 三、Objects.isNull()

判断是否为空：

```
Objects.isNull(obj)
```

例如：

```
User user=null;


if(Objects.isNull(user)){

    System.out.println("为空");

}
```

等价：

```
user == null
```

---

# 四、Objects.nonNull()

判断非空：

```
Objects.nonNull(obj)
```

例如：

```
if(Objects.nonNull(user)){

    user.getName();

}
```

等价：

```
user != null
```

---

# 五、Objects.requireNonNull()（★★★★★）

## 作用

检查对象是否为空。

如果为空：

直接抛出：

```
NullPointerException
```

例如：

```
public void test(User user){

    Objects.requireNonNull(user);

}
```

如果：

```
user=null
```

异常：

```
NullPointerException
```

---

## 带提示信息

```
Objects.requireNonNull(
    user,
    "用户不能为空"
);
```

异常：

```
NullPointerException: 用户不能为空
```

---

## Spring源码中大量使用

例如：

```
public BeanFactory(Object factory){

    this.factory =
        Objects.requireNonNull(factory);

}
```

作用：

提前发现错误。

---

# 六、Objects.hash()

## 作用

生成哈希值。

```
Objects.hash(Object... values)
```

例如：

```
String name="张三";
int age=20;


int hash=
Objects.hash(name,age);
```

---

常用于重写：

```
hashCode()
```

例如：

```
class User{

    String name;

    int age;


    @Override
    public int hashCode(){

        return Objects.hash(name,age);

    }

}
```

---

# 七、Objects.toString()

## 作用

安全调用 toString。

普通：

```
user.toString();
```

如果：

```
user=null
```

报：

```
NullPointerException
```

---

Objects：

```
Objects.toString(user);
```

返回：

```
null
```

---

## 指定默认值

```
Objects.toString(
    user,
    "未知用户"
);
```

如果为空：

返回：

```
未知用户
```

---

# 八、Objects.compare()

比较两个对象：

```
Objects.compare(a,b,comparator)
```

例如：

```
Objects.compare(
    10,
    20,
    Integer::compare
);
```

结果：

```
-1
```

---

# 九、Objects 和 Object 区别（面试必问）

||Object|Objects|
|---|---|---|
|类型|父类|工具类|
|包|java.lang|java.util|
|是否需要创建对象|对象实例|静态方法|
|作用|所有对象基础方法|操作对象|
|方法|equals/hashCode/toString|equals/isNull/hash等|

---

# 十、Objects 和 ==、equals 区别

例如：

```
String a=null;
String b="abc";
```

---

## ==

比较地址：

```
a==b
```

---

## equals()

容易空指针：

```
a.equals(b)
```

错误。

---

## Objects.equals()

安全：

```
Objects.equals(a,b)
```

推荐。

---

# 十一、面试重点总结

## 高频 API：

| 方法               | 重要程度  | 用途     |
| ---------------- | ----- | ------ |
| Objects.equals() | ★★★★★ | 安全比较   |
| requireNonNull() | ★★★★★ | 参数校验   |
| hash()           | ★★★★  | 生成hash |
| isNull()         | ★★★★  | 判空     |
| nonNull()        | ★★★★  | 非空判断   |
| toString()       | ★★★   | 安全转换   |
| deepEquals()     | ★★★   | 深度比较   |