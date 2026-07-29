
## 1. Spring是什么？

Spring 是一个**轻量级 Java 开发框架**，核心思想：

> **IOC（控制反转） + AOP（面向切面编程）**

主要解决：

- 对象创建和管理复杂
- 对象之间耦合严重
- 代码复用困难
- 事务、安全、日志等横切逻辑重复


# 2. IOC（控制反转）

## 2.1 什么是IOC？

IOC：

> Inversion Of Control（控制反转）

以前：

对象由程序员自己创建。

例如：

```
public class UserService {

    private UserDao userDao = new UserDao();

}
```

问题：

UserService 强依赖 UserDao。

如果 UserDao 改名字：

```
new UserDaoImpl()
```

所有地方都需要修改。

---

Spring IOC：

对象交给 Spring 创建。

```
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

}
```

现在：

UserService 不负责创建 UserDao。

而是：

```
Spring容器
      |
      |
创建UserDao对象
      |
      |
注入UserService
```

---

## 2.2 IOC核心

IOC实际上就是：

> 把对象创建权、依赖管理权交给Spring容器。

以前：

```
程序员
 |
创建对象
 |
使用对象
```

现在：

```
程序员
 |
申请对象
 |
Spring创建对象
 |
返回对象
```

---

# 3. DI（依赖注入）

## 什么是DI？

Dependency Injection

依赖注入。

IOC是一种思想。

DI是实现IOC的方法。

例如：

UserService依赖UserDao。

Spring：

```
@Service
public class UserService {


@Autowired
private UserDao userDao;


}
```

Spring自动：

```
创建UserDao

↓

找到UserService

↓

把UserDao注入进去
```

---

## DI三种方式

### 1. 属性注入（常用）

```
@Autowired
private UserDao userDao;
```

优点：

简单。

缺点：

隐藏依赖。

---

### 2. 构造方法注入（推荐）

```
@Service
public class UserService {


private final UserDao userDao;


public UserService(UserDao userDao){

    this.userDao=userDao;

}


}
```

优点：

- 不可变
- 方便测试
- 强制依赖

---

### 3. Setter注入

```
@Autowired
public void setUserDao(UserDao userDao){

}
```