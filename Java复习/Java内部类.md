**内部类：定义在一个类内部的类。**

例如：

```
public class Outer {

    class Inner {
        
    }

}
```

这里：

- `Outer`：外部类
- `Inner`：内部类

内部类的主要作用：

1. **封装性更强**：某个类只被另一个类使用时，可以定义成内部类。
2. **方便访问外部类成员**：内部类可以直接访问外部类的属性和方法。
3. **实现回调、事件监听等设计模式**。

---

# 一、内部类的分类

Java内部类主要分为4种：

```
内部类
 |
 |-- 成员内部类
 |
 |-- 静态内部类
 |
 |-- 局部内部类
 |
 |-- 匿名内部类
```

---

# 1. 成员内部类（普通内部类）

## 定义

定义在外部类成员位置，没有 `static` 修饰。

```
public class Outer {

    private String name = "张三";


    class Inner {

        public void show(){
            System.out.println(name);
        }

    }

}
```

特点：

- 属于外部类对象
- 可以访问外部类所有成员（包括private）
- 创建内部类对象需要先创建外部类对象

---

## 创建对象

错误：

```
Inner inner = new Inner();
```

因为 Inner 属于 Outer。

正确：

```
Outer outer = new Outer();

Outer.Inner inner = outer.new Inner();

inner.show();
```

输出：

```
张三
```

---

# 为什么内部类可以访问private？

例如：

```
class Outer {

    private int age = 20;


    class Inner {

        void test(){
            System.out.println(age);
        }
    }
}
```

虽然 `age` 是 private。

但是：

> 内部类和外部类属于同一个类文件，Java允许内部类访问外部类私有成员。

编译后实际上会生成：

```
Outer.class

Outer$Inner.class
```

编译器会自动生成访问方法。

---

# 2. 静态内部类

## 定义

使用 `static` 修饰：

```
public class Outer {


    static class Inner {


    }


}
```

---

## 特点

和成员内部类相比：

||成员内部类|静态内部类|
|---|---|---|
|是否需要外部对象|需要|不需要|
|是否有static|没有|有|
|访问外部成员|全部|只能访问静态成员|

---

## 创建对象

成员内部类：

```
Outer.Inner inner =
        new Outer().new Inner();
```

静态内部类：

```
Outer.Inner inner =
        new Outer.Inner();
```

---

## 示例

```
public class Outer {

    private static String name="Java";


    static class Inner {

        public void show(){

            System.out.println(name);

        }

    }
}
```

可以访问：

```
Outer.Inner inner = new Outer.Inner();

inner.show();
```

输出：

```
Java
```

---

# 3. 局部内部类

定义在：

- 方法内部
- 代码块内部

例如：

```
public class Outer {


    public void test(){


        class Inner{

            public void show(){
                System.out.println("内部类");
            }

        }


        Inner inner = new Inner();

        inner.show();

    }

}
```

特点：

- 作用范围只在当前方法
- 外部无法访问
- 使用较少

---

## 注意：访问局部变量

例如：

```
public void test(){

    int num = 10;


    class Inner{

        void show(){
            System.out.println(num);
        }

    }

}
```

为什么可以访问？

因为：

> 局部内部类只能访问 final 或 effectively final 的局部变量。

例如：

```
int num = 10;

num = 20; // 修改

class Inner{

    void show(){
        System.out.println(num);
    }

}
```

会报错。

原因：

内部类对象生命周期可能比方法长。

如果方法结束：

```
test()
结束
num消失
```

但是：

```
Inner对象还存在
```

所以Java复制了一份变量。

---

# 4. 匿名内部类（重点）

匿名内部类：

> 没有名字的内部类，通常用于创建接口或抽象类对象。

格式：

```
new 类名(){

    重写方法

};
```

---

## 示例1：实现接口

接口：

```
interface Animal{

    void eat();

}
```

普通方式：

```
class Dog implements Animal{

    public void eat(){
        System.out.println("吃骨头");
    }

}


Animal animal = new Dog();
```

---

匿名内部类：

```
Animal animal = new Animal(){

    public void eat(){

        System.out.println("吃骨头");

    }

};
```

没有Dog类。

---

## 示例2：线程创建

以前：

```
class MyThread extends Thread{

    public void run(){

        System.out.println("线程执行");

    }

}


new MyThread().start();
```

匿名内部类：

```
new Thread(){

    public void run(){

        System.out.println("线程执行");

    }

}.start();
```

---

# 二、内部类访问外部类

## 1. 外部类访问内部类

必须创建内部类对象：

```
class Outer{


    class Inner{

        int num=10;

    }


    public void test(){

        Inner inner=new Inner();

        System.out.println(inner.num);

    }

}
```

---

## 2. 内部类访问外部类

直接访问：

```
class Outer{

    int age=20;


    class Inner{

        void show(){

            System.out.println(age);

        }

    }

}
```

---

# 三、this关键字区别（面试重点）

如果内部类和外部类有同名变量：

```
class Outer{


    int num=10;


    class Inner{


        int num=20;


        void show(){

            System.out.println(num);

        }

    }

}
```

输出：

```
20
```

因为：

```
this.num
```

表示内部类变量。

---

访问外部类：

```
Outer.this.num
```

例如：

```
class Outer{


    int num=10;


    class Inner{


        int num=20;


        void show(){

            System.out.println(this.num);

            System.out.println(Outer.this.num);

        }

    }

}
```

输出：

```
20
10
```

---

# 四、内部类和Lambda关系

Java 8以后：

很多匿名内部类可以使用 Lambda 简化。

匿名内部类：

```
Runnable r = new Runnable(){

    public void run(){

        System.out.println("hello");

    }

};
```

Lambda：

```
Runnable r = () -> {

    System.out.println("hello");

};
```

---

# 五、内部类面试常问

## 1. 为什么使用内部类？

答案：

> 内部类可以增强封装性，使逻辑相关的类放在一起，同时内部类可以方便访问外部类成员。

---

## 2. 成员内部类和静态内部类区别？

||成员内部类|静态内部类|
|---|---|---|
|关键字|无static|static|
|依赖外部对象|需要|不需要|
|访问外部成员|所有|静态成员|
|创建方式|outer.new Inner()|new Outer.Inner()|

---

## 3. 匿名内部类有什么特点？

- 没有类名
- 只能使用一次
- 常用于接口实现、抽象类继承
- Java8后可以使用Lambda替代部分场景