
# 1. static

**`static`（静态）** 关键字意味着一个成员（变量、方法、代码块或内部类）**属于类本身**，而不是属于该类的某个具体对象.

## 1.1 静态变量

静态变量属于**类**，而不是某个对象。

特点：
- 所有对象共享一份数据
- 类加载时创建
- 推荐通过类名访问

## 1.2 静态方法

静态方法属于类。
调用时不用创建对象
```java
class MathUtil {

    static int add(int a, int b) {
        return a + b;
    }

}

int result = MathUtil.add(3, 5);
```

静态方法**不能直接访问实例成员**。

原因：
静态方法属于类，而 `name` 属于对象。

```txt
静态方法中只有：

- 静态变量
- 静态方法

可以直接访问。
```

## 1.3 静态代码块

格式：
```java
class Test {

    static {
        System.out.println("静态代码块");
    }

}
```

特点：

- 类加载时执行
- 只执行一次
- 通常用于初始化静态资源

## 1.4 静态内部类

```java
class Outer {

    static class Inner {

        void show() {
            System.out.println("Hello");
        }

    }

}

// 创建对象
Outer.Inner inner = new Outer.Inner(); 
inner.show();
```

**1. static 可以修饰哪些成员？**

- 成员变量
- 成员方法
- 代码块
- 内部类

**2. 为什么 `main` 方法必须是 static？**

因为 JVM 在程序启动时还没有创建任何对象，需要直接通过**类名**调用 `main` 方法，因此必须声明为：

```
public static void main(String[] args)
```

**3. static 方法中为什么不能使用 `this`？**

`this` 代表当前对象，而静态方法属于类，在调用时可能根本没有对象存在，因此不能使用 `this` 或 `super`。



