
`Object` 是 Java 中**所有类的根类（顶级父类）**。

源码：

```
public class Object
```

任何 Java 类，如果没有显式继承其他类，默认继承 Object。

例如：

```
public class User {

}
```

实际上等价于：

```
public class User extends Object {

}
```

所以：

```
User user = new User();

System.out.println(user.toString());
```

可以调用 Object 中的方法。

---

# 一、Object 常用方法

Object 中有 11 个方法：

```
public final native Class<?> getClass();

public native int hashCode();

public boolean equals(Object obj);

protected native Object clone();

public String toString();

public final native void notify();

public final native void notifyAll();

public final native void wait();

public final native void wait(long timeout);

public final native void wait(long timeout,int nanos);

protected void finalize();
```

面试主要考：

> equals、hashCode、toString、getClass、clone、wait/notify

---

# 二、getClass()

## 作用

获取对象运行时的 Class 对象。

方法：

```
public final native Class<?> getClass()
```

例如：

```
User user = new User();

Class clazz = user.getClass();

System.out.println(clazz);
```

输出：

```
class User
```

---

## 面试：

### getClass() 和 instanceof 区别？

### instanceof

判断是否属于某个类型：

```
user instanceof User
```

返回：

```
true
```

### getClass()

严格判断运行时类型：

```
user.getClass()==User.class
```

---

区别：

||instanceof|getClass|
|---|---|---|
|判断|是否属于某类型|真实类型|
|支持继承|支持|不支持|
|返回|boolean|Class对象|

---

# 三、equals()（★★★★★）

## 作用

比较两个对象是否相等。

方法：

```
public boolean equals(Object obj)
```

---

## Object 默认实现

Object中的equals：

```
public boolean equals(Object obj){

    return this == obj;

}
```

也就是：

> 默认比较地址。

例如：

```
User u1=new User();
User u2=new User();


System.out.println(u1.equals(u2));
```

结果：

```
false
```

因为：

```
u1地址 != u2地址
```

---

# 四、为什么 String equals 返回 true？

因为 String 重写了 equals。

例如：

```
String s1=new String("abc");

String s2=new String("abc");


System.out.println(s1.equals(s2));
```

结果：

```
true
```

String 的 equals：

比较：

```
字符数组内容
```

而不是地址。

---

# 五、重写 equals 的规则（面试重点）

如果重写 equals，需要满足：

## 1. 自反性

对象自己等于自己：

```
a.equals(a)==true
```

---

## 2. 对称性

```
a.equals(b)

必须等于

b.equals(a)
```

---

## 3. 传递性

```
a.equals(b)

b.equals(c)

那么：

a.equals(c)
```

---

## 4. 一致性

对象没有改变：

多次调用结果一致。

---

## 5. 非空性

```
a.equals(null)==false
```

---

# 六、hashCode()（★★★★★）

## 作用

返回对象哈希值。

方法：

```
public native int hashCode()
```

主要用于：

- HashMap
- HashSet
- Hashtable

---

## HashMap 为什么需要 hashCode？

例如：

```
map.put(user,"张三");
```

查找：

```
map.get(user);
```

流程：

```
key
 |
hashCode()
 |
定位数组位置
 |
equals()
 |
确认对象
```

---

# 七、为什么重写 equals 必须重写 hashCode？

面试必问。

因为：

HashMap 判断 key：

第一步：

```
hashCode()
```

第二步：

```
equals()
```

规定：

如果：

```
a.equals(b)==true
```

那么必须：

```
a.hashCode()==b.hashCode()
```

否则：

HashMap 可能找不到数据。

---

错误：

```
class User{

    String name;


    public boolean equals(Object o){

        return name.equals(
            ((User)o).name
        );

    }

}
```

只重写 equals。

问题：

两个相同User：

equals：

```
true
```

hashCode：

```
不同
```

HashMap异常。

---

正确：

```
class User{

    String name;


    @Override
    public boolean equals(Object o){

        if(this==o)
            return true;

        if(!(o instanceof User))
            return false;

        User u=(User)o;

        return name.equals(u.name);

    }



    @Override
    public int hashCode(){

        return name.hashCode();

    }

}
```

---

# 八、toString()（★★★★★）

## 作用

对象转字符串。

默认：

```
User user=new User();

System.out.println(user);
```

输出：

```
User@5e2de80c
```

实际上：

```
类名 + @ + 十六进制hashCode
```

---

## 为什么重写？

方便打印对象信息。

例如：

```
@Override
public String toString(){

    return "User{name='张三'}";

}
```

输出：

```
User{name='张三'}
```

---

# 九、clone()（★★★★）

## 作用

对象复制。

Object：

```
protected native Object clone()
```

使用：

必须实现：

```
Cloneable
```

接口。

例如：

```
class User implements Cloneable{


    public User clone()
            throws CloneNotSupportedException{

        return (User)super.clone();

    }

}
```

---

## 浅拷贝

复制对象：

```
对象地址不同

内部引用相同
```

例如：

```
User
 |
 Address
```

复制：

```
User1
 |
 Address


User2
 |
 Address
```

两个User共享Address。

---

## 深拷贝

复制：

```
对象
 +
内部引用对象
```

完全独立。

---

# 十、wait() / notify()（★★★★）

用于线程通信。

## wait()

让当前线程等待。

```
object.wait();
```

线程状态：

```
RUNNING

↓

WAITING
```

---

## notify()

唤醒一个等待线程。

```
object.notify();
```

---

## notifyAll()

唤醒所有等待线程。

```
object.notifyAll();
```

---

注意：

必须在 synchronized 中使用：

```
synchronized(lock){

    lock.wait();

}
```

---

# 十一、Object 面试高频问题

## 1. Object有哪些方法？

答：

```
equals()
hashCode()
toString()
getClass()
clone()
wait()
notify()
notifyAll()
```

---

## 2. == 和 equals区别？

答：

- == 比较地址（引用类型）
- equals 默认比较地址，可以重写比较内容

---

## 3. 为什么重写equals必须重写hashCode？

答：

因为HashMap、HashSet通过hashCode定位，再通过equals判断。

---

## 4. Object为什么是所有类父类？

答：

Java为了统一所有对象行为，提供公共方法。

---

## 5. final、finally、finalize区别？

||作用|
|---|---|
|final|修饰变量、方法、类|
|finally|异常处理代码块|
|finalize|GC前调用的方法（已废弃）|