
`final` 是 Java 中一个**限制修改**的关键字，可以修饰：

1. **变量**
2. **方法**
3. **类**
4. **成员变量**
5. **参数**

核心思想：

> **final 修饰的东西，不能被再次改变。**

# 一、final 修饰变量

## 1. 基本数据类型

```
final int age = 20;

age = 30; // 编译错误
```

因为 `age` 的值不能修改。

---

## 2. 引用类型

例如：

```
final User user = new User();

user = new User(); // 错误
```

原因：

final 限制的是**引用地址不能改变**。

但是对象内部属性可以改变：

```
final User user = new User();

user.setName("张三"); // 可以
```

内存：

```
user
 |
 | 不能变
 ↓
User对象
{
 name="张三"
}
```

所以：

|情况|是否允许|
|---|---|
|重新指向新对象|❌|
|修改对象属性|✅|

---

# 二、final 修饰成员变量

例如：

```
public class Student {

    final String name;

}
```

final成员变量必须初始化。

有三种方式：

---

## 方式1：声明时初始化

```
class Student {

    final String name = "Tom";

}
```

---

## 方式2：构造方法初始化

常用：

```
class Student {

    final String name;


    public Student(String name){
        this.name = name;
    }

}
```

---

## 方式3：代码块初始化

```
class Student {

    final String name;


    {
        name = "Tom";
    }

}
```

---

# 三、final 修饰方法

表示：

> 方法不能被子类重写。

父类：

```
class Animal {

    public final void eat(){
        System.out.println("吃东西");
    }

}
```

子类：

```
class Dog extends Animal{

    public void eat(){

    }

}
```

报错：

```
Cannot override final method
```

---

## 为什么需要final方法？

防止子类修改核心逻辑。

例如：

```
public final void login(){

    // 验证用户
    // 生成token

}
```

不希望子类改变登录流程。

---

# 四、final 修饰类

表示：

> 类不能被继承。

例如：

```
final class String{

}
```

所以：

```
class MyString extends String{

}
```

错误。

Java中的：

```
public final class String
```

就是这样设计的。

原因：

保证字符串不可变、安全。

# 五、final、finally、finalize区别（面试常问）

|关键字|作用|
|---|---|
|final|修饰类、方法、变量|
|finally|异常处理代码块|
|finalize|对象销毁前调用的方法（已废弃）|

例如：

```
try{

}catch(Exception e){

}finally{

    //一定执行

}
```

---

# 六、final 和 static 区别

经常一起出现：

```
public static final String NAME="Java";
```

表示：

- static：属于类
- final：不能修改

例如：

```
Math.PI
```

源码：

```
public static final double PI
```

含义：

```
Math类共享一个PI
并且不能修改
```

---

# 七、面试总结

### final变量

> final修饰变量后只能赋值一次，基本类型值不可变，引用类型地址不可变但对象内容可以改变。

---

### final方法

> 防止子类重写。

---

### final类

> 防止继承。

---

### String为什么不可变？

> String类被final修饰，内部value数组也是final，同时没有提供修改字符串的方法，因此String对象不可变。

---

### 高频面试题：

**Q：final修饰引用类型对象，对象还能修改吗？**

答：

> 可以。final只保证引用地址不变，不保证对象内部状态不变。

---

**Q：String s1="abc"; String s2="abc"; s1==s2？**

答：

> true，因为两个引用指向字符串常量池中的同一个对象。

---

**Q：String s1=new String("abc"); String s2=new String("abc"); s1==s2？**

答：

> false，因为两个new出来的是不同对象。

