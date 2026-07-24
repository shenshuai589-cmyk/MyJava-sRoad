# 1. Lambda是什么？

## 面试回答

> Lambda 表达式是 Java 8 引入的一种函数式编程特性，它可以简化匿名内部类的代码，本质是向函数式接口传递一个实现。

简单理解：

以前：

```
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("执行");
    }
};
```

Lambda：

```
Runnable r = () -> {
    System.out.println("执行");
};
```

---

## Lambda组成

格式：

```
(参数列表) -> { 方法体 }
```

例如：

```
(a,b) -> a+b
```

解释：

|部分|作用|
|---|---|
|(a,b)|方法参数|
|->|Lambda符号|
|a+b|方法实现|

---

# 2. Lambda使用条件？

## 核心条件：

> Lambda只能用于函数式接口。

例如：

```
@FunctionalInterface
interface Calculator {

    int add(int a,int b);

}
```

使用：

```
Calculator c =
(a,b)->a+b;
```

---

但是：

```
interface Test{

    void method1();

    void method2();

}
```

不能使用 Lambda。

原因：

Lambda不知道实现哪个方法。

---

# 3. 什么是函数式接口？

## 定义：

> 函数式接口就是只有一个抽象方法的接口。

例如：

```
@FunctionalInterface
interface MyInterface{

    void run();

}
```

可以有：

### 一个抽象方法

```
void run();
```

---

### 多个默认方法

```
default void test(){

}
```

---

### 多个静态方法

```
static void show(){

}
```

---

但是：

不能有两个抽象方法。

---

## @FunctionalInterface作用

作用：

1. 标识这是函数式接口
2. 编译器检查是否符合规则

例如：

```
@FunctionalInterface
interface A{

    void a();

    void b();

}
```

编译报错：

```
Multiple non-overriding abstract methods
```

---

# 4. Consumer / Supplier / Function / Predicate区别

这些都在：

```
java.util.function
```

包。

---

# ① Consumer 消费型接口

特点：

> 有参数，没有返回值

源码：

```
void accept(T t);
```

例：

```
Consumer<String> c =
s -> System.out.println(s);


c.accept("Java");
```

执行：

```
Java
```

应用：

集合遍历：

```
list.forEach(
    s -> System.out.println(s)
);
```

---

# ② Supplier 供给型接口

特点：

> 没有参数，有返回值

源码：

```
T get();
```

例：

```
Supplier<String> s =
() -> "hello";


String str=s.get();
```

结果：

```
hello
```

应用：

创建对象：

```
User user =
supplier.get();
```

---

# ③ Function 函数型接口

特点：

> 一个输入，一个输出

源码：

```
R apply(T t);
```

例：

```
Function<String,Integer> f =
s -> s.length();


int len=f.apply("Java");
```

结果：

```
4
```

应用：

数据转换：

```
String
    ↓
Integer
```

---

# ④ Predicate 判断型接口

特点：

> 一个参数，返回boolean

源码：

```
boolean test(T t);
```

例：

```
Predicate<Integer> p =
x -> x>10;


System.out.println(
p.test(20)
);
```

输出：

```
true
```

应用：

过滤：

```
stream.filter(
x -> x>10
);
```

---

## 四种接口总结

|接口|参数|返回值|用途|
|---|---|---|---|
|Consumer|有|无|消费数据|
|Supplier|无|有|提供数据|
|Function|有|有|转换数据|
|Predicate|有|boolean|判断过滤|

---

# 5. Lambda 和匿名内部类区别？

## ① 语法区别

匿名内部类：

```
new Runnable(){

    public void run(){

    }

}
```

Lambda：

```
()->{

}
```

Lambda更简洁。

---

## ② this指向不同（重点）

匿名内部类：

```
new Runnable(){

    public void run(){

        System.out.println(this);

    }

}
```

this：

> 指向匿名内部类对象

---

Lambda：

```
()->{

    System.out.println(this);

}
```

this：

> 指向外部类对象

---

## ③ 编译方式不同

匿名内部类：

生成class文件：

例如：

```
Test$1.class
```

---

Lambda：

不会直接生成匿名内部类。

使用：

```
invokedynamic
```

动态生成实现。

---

# 6. Lambda底层原理（invokedynamic）

这是面试高级问题。

例如：

```
Runnable r =
()->System.out.println("run");
```

编译后：

不是：

```
Runnable$1.class
```

而是：

```
invokedynamic
```

流程：

```
Lambda表达式
        |
        |
Java编译器
        |
        |
invokedynamic指令
        |
        |
LambdaMetafactory
        |
        |
动态生成实现类
        |
        |
执行方法
```

---

## 为什么这样设计？

优点：

### 1. 性能更好

JVM可以动态优化。

### 2. 更灵活

不用提前生成大量class。

---

# 7. Lambda结合Stream使用

这是企业代码最常见场景。

例如：

集合：

```
List<String> list =
Arrays.asList(
"Java",
"Python",
"Go"
);
```

---

需求：

找长度大于3的字符串。

传统：

```
for(String s:list){

    if(s.length()>3){

        System.out.println(s);

    }

}
```

---

Stream：

```
list.stream()
    .filter(
        s -> s.length()>3
    )
    .forEach(
        s -> System.out.println(s)
    );
```

执行流程：

```
List
 |
stream()
 |
filter()
 |
forEach()
 |
输出
```

---

常见组合：

## map转换

例如：

字符串转大写：

```
list.stream()
.map(String::toUpperCase)
.forEach(System.out::println);
```

---

## filter过滤

```
list.stream()
.filter(
s -> s.startsWith("A")
);
```

---

## collect收集

```
List<String> result =
list.stream()
.collect(Collectors.toList());
```

---

# 8. 方法引用

## 定义：

> 方法引用是 Lambda 的简写形式，当 Lambda 只是调用一个已有方法时，可以使用方法引用。

格式：

```
对象::方法名
```

---

## ① 静态方法引用

Lambda：

```
x -> Integer.parseInt(x)
```

方法引用：

```
Integer::parseInt
```

---

## ② 实例方法引用

Lambda：

```
s -> System.out.println(s)
```

方法引用：

```
System.out::println
```

---

## ③ 构造方法引用

Lambda：

```
()->new User()
```

方法引用：

```
User::new
```