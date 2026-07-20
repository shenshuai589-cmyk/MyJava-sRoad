
核心思想：

> **抽象类用于描述一类事物的共同特征，抽象方法用于规定子类必须实现的行为。**


> **抽象类中不一定有抽象方法，抽象方法的类一定是抽象类**

---

# 一、抽象方法（abstract method）

## 1. 什么是抽象方法？

抽象方法：

> **只有方法声明，没有方法体的方法。**

格式：

```
[访问权限] abstract 返回值类型 方法名(参数);
```

例如：

```
public abstract void eat();
```

注意：

没有：

```
{

}
```

没有具体实现。

---

普通方法：

```
public void eat(){

    System.out.println("吃东西");

}
```

抽象方法：

```
public abstract void eat();
```

---

# 二、抽象类（abstract class）

## 1. 什么是抽象类？

使用 `abstract` 修饰的类：

```
public abstract class Animal {

}
```

就是抽象类。

---

抽象类的特点：

1. **不能创建对象**
2. 可以包含普通方法
3. 可以包含抽象方法
4. 可以有成员变量
5. 可以有构造方法

---

例如：

```
public abstract class Animal {

    String name;


    public void sleep(){

        System.out.println("睡觉");

    }


    public abstract void eat();

}
```

这个类：

有：

- 属性 name
- 普通方法 sleep()
- 抽象方法 eat()

---

# 三、为什么需要抽象类？

假设：

动物都有吃饭行为。

```
class Dog{

    public void eat(){

        System.out.println("狗吃骨头");

    }

}


class Cat{

    public void eat(){

        System.out.println("猫吃鱼");

    }

}
```

发现：

狗和猫都有：

```
eat()
```

但是具体行为不同。

所以可以抽取父类：

```
abstract class Animal{

    public abstract void eat();

}
```

然后：

```
class Dog extends Animal{


    @Override
    public void eat(){

        System.out.println("狗吃骨头");

    }

}


class Cat extends Animal{


    @Override
    public void eat(){

        System.out.println("猫吃鱼");

    }

}
```

---

# 四、抽象类不能创建对象

错误：

```
Animal animal = new Animal();
```

编译错误：

```
Animal is abstract; cannot be instantiated
```

原因：

抽象类是不完整的。

比如：

```
Animal animal;
```

你不知道：

- 是狗？
- 是猫？
- 是鸟？

所以不能直接创建。

---

# 五、子类必须实现抽象方法

例如：

父类：

```
abstract class Animal{


    public abstract void eat();


}
```

子类：

```
class Dog extends Animal{

}
```

错误：

```
Dog is not abstract and does not override abstract method eat()
```

因为：

父类要求：

> 所有动物必须吃东西

但是狗没有告诉怎么吃。

---

正确：

```
class Dog extends Animal{


    @Override
    public void eat(){

        System.out.println("狗吃骨头");

    }

}
```

---

# 六、如果子类不实现怎么办？

可以：

让子类继续抽象。

例如：

```
abstract class Dog extends Animal{

}
```

因为Dog也是抽象类。

---

# 七、抽象类中的构造方法

很多人误认为：

> 抽象类不能有构造方法 ❌

错误。

抽象类可以有构造方法。

例如：

```
abstract class Animal{


    public Animal(){

        System.out.println("Animal构造");

    }


}
```

子类：

```
class Dog extends Animal{


}
```

创建：

```
Dog dog = new Dog();
```

执行：

```
Animal构造
```

原因：

子类对象创建时，会先调用父类构造。

# 九、抽象类和接口区别（面试重点）

||抽象类|接口|
|---|---|---|
|关键字|abstract class|interface|
|继承数量|只能继承一个|可以实现多个|
|成员变量|普通变量|默认public static final|
|构造方法|有|没有|
|普通方法|可以有|Java8以后可以有default/static|
|设计目的|描述"is a"关系|定义行为规范|

---

例如：

抽象类：

```
abstract class Animal{

    String name;

    abstract void eat();

}
```

表示：

> 狗是一种动物

```
Dog extends Animal
```

---

接口：

```
interface Fly{

    void fly();

}
```

表示：

> 某个东西具有飞行能力

```
class Bird implements Fly{

    public void fly(){

    }

}
```

---

# 十、实际开发中的使用

比如支付系统：

```
abstract class Payment{


    public abstract void pay(double money);


}
```

支付宝：

```
class AliPay extends Payment{


    public void pay(double money){

        System.out.println("支付宝支付");

    }

}
```

微信：

```
class WeChatPay extends Payment{


    public void pay(double money){

        System.out.println("微信支付");

    }

}
```

调用：

```
Payment payment = new AliPay();

payment.pay(100);
```

这就是：

**父类引用指向子类对象 → 多态**

---

# 面试总结

### 什么是抽象类？

> 使用abstract修饰的类，不能实例化，可以包含抽象方法和普通方法，用于作为其他类的父类。

---

### 什么是抽象方法？

> 使用abstract修饰、没有方法体的方法，强制子类进行实现。

---

### 抽象类有什么特点？

1. 不能创建对象
2. 可以有构造方法
3. 可以有普通方法
4. 可以有抽象方法
5. 子类必须实现抽象方法

---

### 抽象类存在的意义？

> 提供统一的父类规范，让子类按照规定实现具体行为，同时结合多态提高代码扩展性。