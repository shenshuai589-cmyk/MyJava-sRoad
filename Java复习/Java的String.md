### 1. String的特点


特点：
- 是一个 **final类**
- 不可被继承
- 字符串对象不可变（Immutable）
- 底层使用字符数组存储（JDK8）
- JDK9以后改为 `byte[] + 编码标识`

**String的部分源码**

```java
@Stable  
private final byte[] value;  // 这是String的存储底层：是一个字符数组


```

### 2. String对象一旦创建内容不能改变

String不可变主要是因为String类被final修饰，不能被继承，同时内部保存字符串内容的数组value也是private final，并且String没有提供修改内部内容的方法，所以String对象创建后内容无法改变。任何修改操作都会创建新的String对象。


### 3. String创建方式

#### 3.1 直接赋值
```java
String str1 = "hello";
```

==存储位置：字符串常量池==

#### 3.2 new创建

```java
String str2 = new String("hello");
```

在堆中开辟一个地址用来存储（存储在堆中）


### 4 字符串常量池

JVM为了减少字符串对象创建数量，在堆中维护的一块特殊区域。

![字符串常量池](Java复习/image/001-字符串常量池.png)
例如：

```
String a="hello";
String b="hello";
```

内存：

```
字符串常量池

"hello"

 ↑    ↑

 a    b
 
 a和b指向的是同一个
```

### ==5. 字符串的比较==

## 1. == 比较

比较地址。

例如：

```
String a="hello";

String b="hello";


System.out.println(a==b);
```

结果：

```
true
```

因为：

```
a ----
       \
        "hello"
       /
b ----
```

## 2. new对象比较

```
String a=new String("hello");

String b=new String("hello");


System.out.println(a==b);
```

结果：

```
false
```

原因：

地址不同。

**Object中equals的源码**

```java
public boolean equals(Object obj) { 
	return (this == obj); 
}
```
**`this == obj`**：默认情况下，`equals` 比较的是**内存地址**（引用等价性）。


**String类中的equals源码**
```java
public boolean equals(Object anObject) {
    // 1. 检查是否指向同一个内存地址。如果是，直接返回 true（效率最高）
    if (this == anObject) {
        return true;
    }
    
    // 2. 检查传入的对象是否是 String 类型
    if (anObject instanceof String) {
        String aString = (String)anObject;
        
        // 3. 比较两个字符串的长度（在 String 的底层，通常存为 byte[] 或 char[]）
        // 4. 循环逐个字符进行比较，一旦发现不一致，立刻返回 false
        if (coder() == aString.coder()) {
            return isLatin1() ? StringLatin1.equals(value, aString.value)
                              : StringUTF16.equals(value, aString.value);
        }
    }
    
    // 5. 类型不匹配或内容不一致，返回 false
    return false;
}
```


### 6. 字符串常用方法

#### 6.1. length()

获取长度：

```
String s="hello";

s.length();
```

结果：

```
5
```

---

#### 6.2. charAt()

获取指定字符：

```
String s="hello";

s.charAt(1);
```

结果：

```
e
```

---

#### 6.3. substring()

截取字符串：

```
String s="hello";


String s2=s.substring(1,4);
```

结果：

```
ell
```

注意：

左闭右开：

```
[1,4)
```

---

## 4. contains()

判断包含：

```
"hello".contains("he");
```

结果：

```
true
```

#### 6.5. equals()

内容比较：

```
"a".equals("a");
```

---

#### 6.6. equalsIgnoreCase()

忽略大小写：

```
"HELLO".equalsIgnoreCase("hello");
```
---

#### 6.7. trim()

去除两端空格：

```
" hello ".trim();
```

结果：

```
hello
```

---

#### 6.8. split()

分割：

```
String s="a,b,c";

String[] arr=s.split(",");
```

结果：

```
[a,b,c]
```

---

#### 6.9. replace()

替换：

```
String s="hello";

s.replace("h","H");
```

结果：

```
Hello
```

---

#### 6.10. startsWith()

判断开头：

```
"hello".startsWith("he");
```

---

#### 6.11. endsWith()

判断结尾：

```
"hello".endsWith("lo");
```

---
### 7、String、StringBuilder、StringBuffer区别

这是Java面试必问。

String                 不可变                    安全                低                  少量字符串

StringBuilder      可变                      不安全              最高              大量拼接

StringBuffer        可变                        安全                较低               多线程环境

---

#### 8.String拼接问题

例如：

```
String s="";

for(int i=0;i<10000;i++){

    s+=i;

}
```

底层：

每次创建新对象。

类似：

```
""
"0"
"01"
"012"
...
```

产生大量垃圾。

---

推荐：

```
StringBuilder sb=new StringBuilder();


for(int i=0;i<10000;i++){

    sb.append(i);

}


String s=sb.toString();
```

---

### 9. StringBuilder原理

底层：

```
char[]
```

(JDK8)

默认容量：

```
16
```

例如：

```
StringBuilder sb=new StringBuilder();
```

内部：

```
char[16]
```

超过容量：

扩容：

```
newCapacity =
oldCapacity*2+2
```

