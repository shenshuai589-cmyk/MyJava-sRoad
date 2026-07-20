# JDBC ORM（对象关系映射）

## 1. 什么是 ORM

ORM（Object Relational Mapping）

中文：

> 对象关系映射

作用：

```text
Java对象 ←→ 数据库表
```

即建立 Java 类与数据库表之间的映射关系。

---

## 2. ORM 映射关系

| Java | 数据库 |
|--------|--------|
| Class（类） | Table（表） |
| Object（对象） | Row（记录） |
| Field（属性） | Column（字段） |

例如：

数据库表：

```sql
CREATE TABLE student(
    id INT PRIMARY KEY,
    name VARCHAR(20),
    age INT
);
```

对应 Java 类：

```java
public class Student {

    private Integer id;
    private String name;
    private Integer age;

}
```

映射关系：

```text
Student类
    ↓
student表

id
    ↓
id字段

name
    ↓
name字段

age
    ↓
age字段
```

---

## 3. JDBC 中的数据封装

传统 JDBC 查询：

```java
String sql = "select * from student";

PreparedStatement ps =
        conn.prepareStatement(sql);

ResultSet rs =
        ps.executeQuery();

while(rs.next()){

    Student student = new Student();

    student.setId(rs.getInt("id"));
    student.setName(rs.getString("name"));
    student.setAge(rs.getInt("age"));

}
```

特点：

- 手动创建对象
- 手动封装数据
- 重复代码较多
- 开发效率低

---

## 4. ORM 的作用

ORM 框架自动完成：

```text
数据库记录
      ↓
对象映射
      ↓
Java对象
```

例如：

```java
Student student =
studentMapper.selectById(1);
```

ORM 自动完成：

```text
执行SQL
↓
获取ResultSet
↓
创建Student对象
↓
属性赋值
↓
返回对象
```

开发人员无需手动封装对象。

---

## 5. Java 常见 ORM 框架

### 5.1 MyBatis（半ORM）

特点：

```text
SQL自己写
对象自动封装
```

Mapper：

```xml
<select id="selectById"
        resultType="Student">

    select * from student
    where id = #{id}

</select>
```

调用：

```java
Student student =
mapper.selectById(1);
```

MyBatis 自动完成对象封装。

#### 为什么叫半 ORM？

因为：

```text
SQL需要开发人员自己编写
```

仅负责对象映射。

---

### 5.2 Hibernate（全ORM）

特点：

```text
面向对象操作数据库
```

示例：

```java
Student student =
session.get(Student.class,1);
```

Hibernate 自动生成 SQL：

```sql
select * from student where id = 1;
```

开发人员通常不需要手写 SQL。

---

### 5.3 JPA

JPA（Java Persistence API）

含义：

```text
Java ORM 标准规范
```

类似于：

```text
JDBC 是数据库访问规范

JPA 是 ORM 规范
```

常见实现：

- Hibernate
- EclipseLink

SpringBoot 中常见组合：

```text
Spring Data JPA
        ↓
       JPA
        ↓
    Hibernate
```

---


## 6. JDBC 与 ORM 对比

| 对比项 | JDBC | ORM |
|----------|----------|----------|
| SQL编写 | 手动 | 自动或半自动 |
| 对象封装 | 手动 | 自动 |
| 开发效率 | 较低 | 较高 |
| 学习成本 | 低 | 中 |
| 灵活性 | 高 | 较高 |
| 企业应用 | 较少直接使用 | 广泛使用 |

---

## 8. 面试题：什么是 ORM？

标准回答：

> ORM（Object Relational Mapping）即对象关系映射，是一种将关系型数据库中的表与 Java 对象建立映射关系的技术。ORM 框架能够自动完成数据库记录与 Java 对象之间的转换，从而减少 JDBC 中大量手动封装 ResultSet 的代码，提高开发效率和代码可维护性。

---

## 9. Java 后端 ORM 技术体系

```text
JDBC
│
├── 手动封装对象
│
└── ORM思想
      │
      ├── MyBatis（半ORM）
      │
      ├── Hibernate（全ORM）
      │
      └── Spring Data JPA
```

---
