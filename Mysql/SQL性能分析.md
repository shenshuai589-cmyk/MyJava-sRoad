SQL 性能分析的目的：**找到 SQL 执行慢的原因，并进行优化**。

在企业开发中，常见分析手段：

1. **慢查询日志（定位慢 SQL）**
2. **EXPLAIN 执行计划（分析 SQL 执行过程）**
3. **SQL Profiling（分析 SQL 执行耗时）**
4. **数据库监控工具**

---

# 一、慢查询日志（Slow Query Log）

## 1. 什么是慢查询？

MySQL 默认认为：

> 执行时间超过指定阈值的 SQL，就是慢 SQL。

例如：

```
select * from user where username='张三';
```

如果执行时间超过 1 秒，就会被记录。

---

## 2. 开启慢查询日志

查看：

```
show variables like 'slow_query_log';
```

开启：

```
set global slow_query_log = ON;
```

查看慢查询时间：

```
show variables like 'long_query_time';
```

设置：

```
set global long_query_time = 1;
```

表示：

> SQL 执行超过 1 秒记录。

---

## 3. 查看慢查询日志文件

```
show variables like 'slow_query_log_file';
```

例如：

```
/var/lib/mysql/mysql-slow.log
```

里面记录：

```
Query_time: 2.5

select *
from user
where name='Tom';
```

---

# 二、EXPLAIN 执行计划（重点）

实际开发中最常用。

作用：

> 查看 MySQL 如何执行 SQL。

使用：

```
EXPLAIN SQL语句;
```

例如：

```
EXPLAIN 
SELECT *
FROM user
WHERE username='zhangsan';
```

结果：

|字段|含义|
|---|---|
|id|查询编号|
|select_type|查询类型|
|table|访问表|
|type|访问类型|
|possible_keys|可能使用索引|
|key|实际使用索引|
|rows|扫描行数|
|Extra|额外信息|

---

# 三、重点字段分析

## 1. type（重要）

表示访问类型。

性能从好到差：

```
system
 ↓
const
 ↓
eq_ref
 ↓
ref
 ↓
range
 ↓
index
 ↓
ALL
```

---

## system

最快。

表中只有一条数据。

---

## const

主键或者唯一索引查询。

例如：

```
select *
from user
where id=1;
```

执行：

```
type = const
```

因为：

id 是主键。

---

## eq_ref

多表连接时使用主键。

例如：

```
select *
from user u
join order o
on u.id=o.user_id;
```

---

## ref

普通索引查询。

例如：

```
select *
from user
where username='Tom';
```

username 有普通索引。

结果：

```
type=ref
```

---

## range

范围查询。

例如：

```
select *
from user
where id > 100;
```

或者：

```
where id between 10 and 100;
```

---

## index

扫描整个索引。

例如：

```
select id
from user;
```

因为只需要索引字段。

---

## ALL（最差）

全表扫描。

例如：

```
select *
from user
where age=20;
```

但是 age 没有索引。

执行：

```
type=ALL
```

需要扫描整张表。

---

# 四、key（实际使用索引）

例如：

```
EXPLAIN
select *
from user
where username='Tom';
```

结果：

```
possible_keys:
idx_username

key:
idx_username
```

说明：

- 可能使用 username 索引
- 实际使用 username 索引

---

如果：

```
possible_keys:
idx_username

key:
NULL
```

说明：

> 有索引，但是 MySQL 没使用。

原因可能：

### 1. 数据量太少

MySQL认为全表扫描更快。

### 2. 索引失效

例如：

```
select *
from user
where username like '%Tom';
```

左模糊匹配导致索引失效。

---

# 五、rows（扫描行数）

表示：

> MySQL 预计需要扫描多少行。

例如：

```
rows=100000
```

说明：

需要扫描十万行。

优化目标：

降低 rows。

---

# 六、Extra（重要）

## 1. Using index

覆盖索引。

例如：

索引：

```
(id,name)
```

SQL：

```
select id,name
from user
where id=1;
```

结果：

```
Using index
```

说明：

只查索引，不回表。

---

## 2. Using where

表示：

使用 where 条件过滤。

---

## 3. Using filesort

重点！

表示：

发生额外排序。

例如：

```
select *
from user
order by age;
```

但是：

age没有索引。

出现：

```
Using filesort
```

优化：

给 age 建索引：

```
create index idx_age
on user(age);
```

---

## 4. Using temporary

表示：

使用临时表。

常见：

```
select name,count(*)
from user
group by name;
```

如果没有合适索引。

优化：

建立联合索引。

---

# 七、索引优化案例

## 问题 SQL：

```
select *
from user
where username='Tom'
and age=20;
```

表：

```
user

id
username
age
address
```

已有：

```
idx_username(username)
```

执行：

```
type=ref
rows=50000
```

扫描很多。

---

优化：

创建联合索引：

```
create index idx_name_age
on user(username,age);
```

再次执行：

```
type=ref

rows=1
```

性能提升。

---

# 八、SQL优化常见原则

## 1. 避免 SELECT *

不好：

```
select *
from user;
```

推荐：

```
select id,username
from user;
```

原因：

- 减少网络传输
- 可能覆盖索引
- 减少磁盘 IO

---

## 2. 避免索引失效

### 不要对字段计算

错误：

```
where id+1=10
```

正确：

```
where id=9
```

---

### 不要使用函数

错误：

```
where DATE(create_time)='2026-07-19'
```

正确：

```
where create_time 
between '2026-07-19 00:00:00'
and '2026-07-19 23:59:59'
```

---

### 不要前置模糊查询

错误：

```
like '%abc'
```

正确：

```
like 'abc%'
```

---

# 九、SQL 性能分析流程（面试重点）

```
SQL执行慢
    |
    ↓
开启慢查询日志
    |
    ↓
定位慢SQL
    |
    ↓
EXPLAIN分析
    |
    ↓
查看type
    |
    ↓
查看key是否使用索引
    |
    ↓
查看rows扫描数量
    |
    ↓
优化索引/SQL
    |
    ↓
再次EXPLAIN验证
```

---

# 十、面试回答模板

**Q：如何分析一条 SQL 的性能？**

回答：

> 首先通过慢查询日志定位执行时间较长的 SQL，然后使用 EXPLAIN 查看执行计划，重点关注 type、key、rows、Extra 等字段。type 尽量避免 ALL 全表扫描，key 判断是否使用索引，rows 判断扫描数据量，Extra 关注是否出现 Using filesort、Using temporary。最后根据分析结果优化 SQL，比如建立合理索引、避免索引失效、减少查询字段。