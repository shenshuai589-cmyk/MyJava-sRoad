# Java接口（Interface）

## 一、什么是接口？

接口（Interface）是 Java 面向对象中的一种引用类型，用于定义规范。

核心思想：

> 接口定义"能做什么"，实现类负责"怎么做"。

例如：

``` java
interface Fly {
    void fly();
}
```

表示：

所有具有飞行能力的类，都应该实现 `fly()` 方法。

------------------------------------------------------------------------

# 二、接口的定义

使用关键字：

``` java
interface
```

例如：

``` java
public interface Animal {

    void eat();

}
```

接口中的方法默认是：

``` java
public abstract
```

等价于：

``` java
public abstract void eat();
```

------------------------------------------------------------------------

# 三、接口的实现

使用：

``` java
implements
```

关键字。

示例：

``` java
interface Animal {

    void eat();

}


class Dog implements Animal {


    @Override
    public void eat(){

        System.out.println("狗吃骨头");

    }

}
```

实现接口的类必须实现接口中的抽象方法。

------------------------------------------------------------------------

# 四、接口中的成员变量

接口中的变量默认：

``` java
public static final
```

例如：

``` java
interface Animal {

    int AGE = 10;

}
```

实际上等价于：

``` java
interface Animal {

    public static final int AGE = 10;

}
```

特点：

-   public：公开访问
-   static：属于接口
-   final：不可修改

访问：

``` java
System.out.println(Animal.AGE);
```

------------------------------------------------------------------------

# 五、接口中的方法

## 1. 抽象方法

Java 8之前接口只能定义抽象方法：

``` java
interface Animal {

    void eat();

}
```

------------------------------------------------------------------------

## 2. default方法

Java 8新增，可以有方法实现：

``` java
interface Animal {


    default void sleep(){

        System.out.println("睡觉");

    }

}
```

实现类可以直接调用，也可以重写。

------------------------------------------------------------------------

## 3. static方法

接口可以定义静态方法：

``` java
interface Animal {


    static void test(){

        System.out.println("测试");

    }

}
```

调用：

``` java
Animal.test();
```

不能通过实现类对象调用。

------------------------------------------------------------------------

# 六、接口支持多实现

Java中：

一个类只能继承一个类。

但是：

一个类可以实现多个接口。

例如：

``` java
interface Fly {

    void fly();

}


interface Swim {

    void swim();

}


class Duck implements Fly, Swim {


    public void fly(){

    }


    public void swim(){

    }

}
```

这样一个类可以拥有多个能力。

------------------------------------------------------------------------

# 七、接口和多态

接口最重要的作用：

> 实现多态，提高代码扩展性。

例如：

支付接口：

``` java
interface Payment {

    void pay();

}
```

支付宝：

``` java
class AliPay implements Payment {


    public void pay(){

        System.out.println("支付宝支付");

    }

}
```

微信：

``` java
class WeChatPay implements Payment {


    public void pay(){

        System.out.println("微信支付");

    }

}
```

调用：

``` java
Payment payment = new AliPay();

payment.pay();
```

输出：

    支付宝支付

替换：

``` java
Payment payment = new WeChatPay();
```

业务代码无需修改。

------------------------------------------------------------------------

# 八、接口继承接口

接口之间可以使用：

``` java
extends
```

继承。

例如：

``` java
interface A {

    void a();

}


interface B extends A {

    void b();

}
```

实现B时：

``` java
class Test implements B {


    public void a(){

    }


    public void b(){

    }

}
```

需要实现所有方法。

------------------------------------------------------------------------

# 九、接口与抽象类区别

  对比       抽象类           接口
  ---------- ---------------- ---------------------
  关键字     abstract class   interface
  实现方式   extends          implements
  构造方法   有               没有
  成员变量   普通变量         public static final
  普通方法   可以有           default/static
  抽象方法   可以有           主要用于定义规范
  多继承     不支持           支持多个接口
  设计目的   描述共同属性     定义行为能力

------------------------------------------------------------------------

# 十、接口和继承的区别

## 继承

表示：

> is-a关系

例如：

``` java
Dog extends Animal
```

含义：

狗是一种动物。

------------------------------------------------------------------------

## 接口

表示：

> can-do关系

例如：

``` java
Bird implements Fly
```

含义：

鸟具有飞行能力。

------------------------------------------------------------------------

# 十一、实际开发中的接口应用

## 1. JDBC

Java数据库连接大量使用接口：

例如：

-   Connection
-   Statement
-   ResultSet

不同数据库提供不同实现。

------------------------------------------------------------------------

## 2. Spring开发

企业项目常见：

接口：

``` java
public interface UserService {

    User login();

}
```

实现类：

``` java
@Service
public class UserServiceImpl implements UserService {

}
```

优点：

-   降低耦合
-   方便扩展
-   方便测试

------------------------------------------------------------------------

# 十二、面试总结

## 1. 什么是接口？

接口是一种规范，用于定义类必须具备的行为，实现类负责具体实现。

------------------------------------------------------------------------

## 2. 接口有哪些特点？

1.  使用interface定义
2.  使用implements实现
3.  一个类可以实现多个接口
4.  接口变量默认public static final
5.  接口方法默认public abstract
6.  Java8以后支持default和static方法

------------------------------------------------------------------------

## 3. 为什么使用接口？

因为接口可以：

-   降低代码耦合
-   提高扩展性
-   实现多态
-   符合面向接口编程思想

------------------------------------------------------------------------

# 十三、常见面试题

## Q1：接口可以创建对象吗？

不能。

接口只是规范，不包含完整实现。

------------------------------------------------------------------------

## Q2：一个类可以实现多个接口吗？

可以。

例如：

``` java
class Duck implements Fly, Swim
```

------------------------------------------------------------------------

## Q3：接口中的变量为什么是final？

因为接口中的变量表示公共常量，不允许修改。

------------------------------------------------------------------------

## Q4：抽象类和接口如何选择？

-   有共同属性和部分实现：选择抽象类
-   只定义行为规范：选择接口
