
## 1. 什么是覆盖索引？

**覆盖索引指的是：查询的数据在索引中已经全部存在，不需要再回表查询数据。**

简单理解：

> 一条 SQL 查询，只通过索引就能得到所有需要的数据。

也就是：

普通索引查询：

```
索引 → 找到主键id → 回表 → 查询完整数据
```

覆盖索引：

```
索引 → 直接返回数据
```

---

# 2. 为什么需要覆盖索引？

先看一个表：

```
CREATE TABLE user(
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    email VARCHAR(100)
);
```

数据：

|id|name|age|email|
|---|---|---|---|
|1|张三|20|zs@qq.com|
|2|李四|25|ls@qq.com|

建立普通索引：

```
CREATE INDEX idx_age 
ON user(age);
```

---

## 查询：

```
SELECT id,name
FROM user
WHERE age=20;
```

执行过程：

### 第一步：查索引

索引结构：

```
idx_age

age      id
20       1
25       2
```

找到：

```
age=20
id=1
```

但是索引里面没有：

```
name
```

所以需要：

### 第二步：回表

根据主键：

```
id=1
```

去聚簇索引查询：

```
id=1
name=张三
email=zs@qq.com
```

这叫：

> 回表查询

---

# 3. 如何实现覆盖索引？

建立联合索引：

```
CREATE INDEX idx_age_name
ON user(age,name);
```

索引结构：

```
age    name      id
20     张三       1
25     李四       2
```

再次查询：

```
SELECT name
FROM user
WHERE age=20;
```

执行：

```
idx_age_name

age=20
 |
找到name=张三
```

直接返回：

```
张三
```

不需要回表。

这就是：

> 覆盖索引

---

# 4. 判断是否使用覆盖索引

使用：

```
EXPLAIN
SELECT name
FROM user
WHERE age=20;
```

查看：

```
Extra
----------------
Using index
```

表示：

✅ 使用覆盖索引

如果：

```
Extra
----------------
Using where
```

或者：

```
Using index condition
```

不一定是覆盖索引。

---

# 5. 覆盖索引和回表区别

## 普通索引

例如：

```
SELECT *
FROM user
WHERE age=20;
```

执行：

```
二级索引
    |
找到id
    |
回表
    |
聚簇索引
    |
返回全部字段
```

---

## 覆盖索引

例如：

```
SELECT id,name
FROM user
WHERE age=20;
```

索引：

```
(age,name,id)
```

执行：

```
二级索引
    |
直接返回结果
```

---

# 6. 为什么SELECT *容易导致回表？

例如：

```
SELECT *
FROM user
WHERE age=20;
```

即使有：

```
(age,name)
```

索引：

```
age
name
id
```

但是：

```
email
password
create_time
```

这些字段不在索引里。

所以：

必须：

```
索引查询
 ↓
回表
 ↓
获取其他字段
```

所以开发中：

❌ 不推荐：

```
SELECT *
```

推荐：

✅

```
SELECT id,name
FROM user
WHERE age=20;
```

---

# 7. 覆盖索引的优点

## ① 减少IO

回表需要：

```
访问二级索引
+
访问聚簇索引
```

覆盖索引：

```
只访问二级索引
```

减少磁盘IO。

---

## ② 提升查询速度

尤其是：

- 大表
- 高频查询
- 分页查询

例如：

用户列表：

```
SELECT id,name
FROM user
WHERE status=1
LIMIT 20;
```

建立：

```
CREATE INDEX idx_status_name
ON user(status,name);
```

查询速度明显提升。

---

# 8. 覆盖索引和最左匹配原则结合

联合索引：

```
(age,name,email)
```

可以覆盖：

```
SELECT name
FROM user
WHERE age=20;
```

可以：

```
SELECT email
FROM user
WHERE age=20 
AND name='张三';
```

但是：

```
SELECT email
FROM user
WHERE name='张三';
```

无法利用索引。

原因：

违反最左匹配原则。

---

# 9. 面试回答模板

**Q：什么是覆盖索引？**

答：

> 覆盖索引是指查询所需要的字段全部存在于索引中，MySQL可以直接通过索引返回结果，而不需要进行回表查询。它能够减少磁盘IO，提高查询效率。通常通过建立联合索引，让索引包含查询需要返回的字段实现。