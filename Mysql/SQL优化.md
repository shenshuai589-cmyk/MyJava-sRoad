
# 一. 插入数据优化（INSERT优化）

## 1. 批量插入

### 不推荐：

```
INSERT INTO user VALUES(1,'张三');

INSERT INTO user VALUES(2,'李四');

INSERT INTO user VALUES(3,'王五');
```

执行：

```
SQL发送3次
事务提交3次
```

效率低。

---

### 推荐：

```
INSERT INTO user VALUES
(1,'张三'),
(2,'李四'),
(3,'王五');
```

优势：

- 减少网络交互
- 减少事务提交次数
- 提高吞吐量

---

# 2. 手动控制事务

默认：

```
INSERT一条
提交一次
```

优化：

```
START TRANSACTION;


INSERT INTO user VALUES(1,'张三');
INSERT INTO user VALUES(2,'李四');
INSERT INTO user VALUES(3,'王五');


COMMIT;
```

变成：

```
3次insert
1次commit
```

---

# 3. 大量数据导入使用LOAD DATA

例如：

100万数据。

不要：

```
insert 100万次
```

使用：

```
LOAD DATA INFILE 'user.txt'
INTO TABLE user;
```

速度非常快。

**load指令**
```sql
# 客户端连接服务器时，加上参数 --local-infile
mysql --local-infile -u root -p

# 设置全局参数local_infile为1，开启从本地加载文件导入数据的开关
set global local_infile=1;

# 执行load指令将准备好的数据，加载到表结构中
load data local infile '/root/sql.log' into table 'tb_user' fields terminated by ',' lines terminated by '\n';
```


# 二. 主键优化

## 1. 主键长度不要太大

错误：

```
id VARCHAR(255)
```

推荐：

```
id BIGINT
```

原因：

InnoDB：

- 主键就是聚集索引
- 二级索引保存主键值

主键越大：

二级索引越大。

---

## 2. 推荐自增主键

推荐：

```
id BIGINT AUTO_INCREMENT
```

原因：

InnoDB B+树：

```
1
2
3
4
5
```

顺序插入。

---

如果：

UUID：

```
f83kd93
a72hd82
z92kd11
```

随机插入：

导致：

- 页分裂
- 磁盘IO增加
- 性能下降

---

## 3. 避免主键乱序插入

不好：

```
100
50
200
10
```

好：

```
1
2
3
4
```


# 3.order by优化

SQL：

```
select *
from user
order by age;
```

MySQL排序：

两种方式：

---

# 方式1：Using filesort

没有索引：

```
查询数据

↓

内存排序

↓

返回
```

执行计划：

```
Using filesort
```

效率低。

---

# 方式2：利用索引排序

创建：

```
create index idx_age
on user(age);
```

执行：

```
select *
from user
order by age;
```

直接：

```
索引已经有序
```

无需排序。

---

## 联合索引排序

索引：

```
(name,age)
```

可以：

```
order by name;
```

可以：

```
order by name,age;
```

不能：

```
order by age;
```

原因：

最左匹配原则。

# 4. group by优化

SQL：

```
select age,count(*)
from user
group by age;
```

没有索引：

```
查询全部数据

↓

排序

↓

分组
```

---

优化：

建立：

```
create index idx_age
on user(age);
```

执行：

```
索引有序

↓

直接分组
```

---

## group by 和 order by

不要：

```
group by age
order by age;
```

因为：

group by默认排序。

MySQL 8以后：

可以：

```
group by age
order by null;
```

取消排序。


# 5. limit优化

常见：

```
select *
from user
limit 100000,10;
```

问题：

MySQL：

先扫描：

```
100010条
```

然后丢弃：

```
100000条
```

只返回10条。

---

## 优化方式1：覆盖索引

例如：

```
select id
from user
limit 100000,10;
```

先找到id。

---

## 优化方式2：子查询

原：

```
select *
from user
limit 100000,10;
```

优化：

```
select *
from user
where id >=
(
select id
from user
limit 100000,1
)
limit 10;
```

速度提升明显。


# 6. count优化

常见：

```
select count(*)
from user;
```

---

## count区别

### count(*)

统计行数：

最快。

---

### count(id)

统计id不为空。

---

### count(字段)

忽略null。

例如：

数据：

|id|name|
|---|---|
|1|张三|
|2|null|

```
count(name)
```

结果：

```
1
```

---

## 优化方案

不要：

```
select count(*) from 大表;
```

频繁执行。

可以：

维护：

```
统计表
```

例如：

```
user_count

count=1000000
```

# 7. update优化

## 问题：

更新大量数据：

```
update user
set age=20;
```

可能：

锁很多数据。

---

## 优化1：根据索引更新

不要：

```
update user
set name='张三'
where name='李四';
```

如果name无索引：

全表扫描。

建立：

```
create index idx_name
on user(name);
```

---

## 优化2：分批更新

不要：

```
update user
set status=1;
```

改：

```
update user
set status=1
where id between 1 and 10000;
```

循环执行。