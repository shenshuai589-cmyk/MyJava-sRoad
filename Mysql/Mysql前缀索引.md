## 1. 什么是前缀索引？

**前缀索引就是对字符串字段的前一部分内容建立索引，而不是对整个字段建立索引。**

例如：

用户表：

```
CREATE TABLE user(
    id BIGINT PRIMARY KEY,
    email VARCHAR(255)
);
```

数据：

|id|email|
|---|---|
|1|zhangsan123@qq.com|
|2|lisi456@qq.com|
|3|wangwu789@qq.com|

如果直接建立索引：

```
CREATE INDEX idx_email
ON user(email);
```

那么索引存储：

```
zhangsan123@qq.com
lisi456@qq.com
wangwu789@qq.com
```

字符串较长，占用空间大。

---

如果使用前缀索引：

```
CREATE INDEX idx_email_prefix
ON user(email(8));
```

表示：

只对 email 前 8 个字符建立索引：

```
zhangsan
lisi456@
wangwu78
```

这就是：

> 前缀索引

---

# 2. 为什么需要前缀索引？

主要解决：

## 字符串字段太长导致索引占空间大

例如：

```
url VARCHAR(1000)
```

如果建立普通索引：

```
完整URL
↓
索引空间巨大
```

而：

```
CREATE INDEX idx_url
ON website(url(20));
```

只保存前20个字符：

```
https://www.xxx
```

减少索引大小。

---

# 3. 前缀索引的优点

## ① 减少索引空间

例如：

字段：

```
email VARCHAR(255)
```

完整索引：

```
255字符
```

前缀：

```
email(10)
```

只保存：

```
10字符
```

索引体积明显减少。

---

## ② 提高索引维护效率

索引越小：

- 创建速度更快
- 更新成本更低
- 占用内存更少

---

# 4. 前缀索引的缺点

## ① 降低索引选择性

例如：

手机号：

```
13800138000
13800138001
13800138002
```

建立：

```
phone(3)
```

索引：

```
138
138
138
```

大量重复。

查询：

```
WHERE phone='13800138000'
```

索引无法快速定位。

---

## ② 无法用于覆盖索引

例如：

建立：

```
CREATE INDEX idx_name
ON user(name(10));
```

查询：

```
SELECT name
FROM user
WHERE name='zhangsan';
```

不能算覆盖索引。

原因：

索引里面只有：

```
name前10个字符
```

不是完整name。

需要：

```
回表查询完整字段
```

---

# 5. 如何选择前缀长度？

核心：

> 前缀长度越长，区分度越高，但是索引越大。

需要找到平衡。

---

例如：

查看不同长度的选择性：

### 完整字段：

```
SELECT COUNT(DISTINCT email)
FROM user;
```

结果：

```
100000
```

---

测试前缀：

```
SELECT COUNT(DISTINCT LEFT(email,5))
FROM user;
```

例如：

结果：

```
90000
```

说明：

5个字符已经有较高区分度。

继续：

```
SELECT COUNT(DISTINCT LEFT(email,10))
FROM user;
```

结果：

```
99900
```

那么：

10可能更合适。

---

# 6. 前缀索引语法

创建：

```
CREATE INDEX idx_name
ON user(name(10));
```

删除：

```
DROP INDEX idx_name 
ON user;
```

查看：

```
SHOW INDEX FROM user;
```

---

# 7. 前缀索引和普通索引区别

||普通索引|前缀索引|
|---|---|---|
|索引字段|完整字段|字段前N个字符|
|空间|较大|较小|
|查询速度|快|可能降低|
|选择性|高|取决于长度|
|覆盖索引|支持|通常不支持|
|适合|短字段|长字符串|

---

# 8. 面试重点

### Q：为什么不用前缀索引替代普通索引？

回答：

> 因为前缀索引虽然可以减少索引空间，但是会降低索引选择性，同时无法支持覆盖索引。如果前缀长度过短，会导致大量索引冲突，查询效率下降。