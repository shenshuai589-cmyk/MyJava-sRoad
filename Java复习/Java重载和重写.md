### 1. 重载的概念

重载（overload）：是在**同一个类**里，方法名相同，但参数不同。
重载的核心规则：
```
1.方法名必须相同
2.参数列表必须不同
  - 参数的个数不同
  - 类型不同
  - 顺序不同
3.与返回值类型、访问修饰符无关
```
例子：
```java
class Calculator {
    // 两个整数相加
    public int add(int a, int b) {
        return a + b;
    }
    
    // 重载：三个整数相加（参数个数不同）
    public int add(int a, int b, int c) {

        return a + b + c;

    }
    
    // 重载：两个小数相加（参数类型不同）
    public double add(double a, double b) {
        return a + b;
    }
}
```

### 2.重写（Override）
重写发生在继承关系中。当子类继承了父类的方法，但觉得父类的方法实现得不够好、或者不符合自己的需求时，子类可以重新写一遍这个方法。

### 核心规则：
```
- 方法名、参数列表、返回值类型必须完全相同。
- 访问权限不能比父类更低（例如：父类是 public，子类不能改成 private）。
- 声明抛出的异常不能比父类更大。
- 特别提醒：被 `final`、`static`、`private` 修饰的方法不能被重写。
```

