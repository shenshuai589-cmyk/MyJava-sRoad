
## 1. 什么是视图？

**视图（View）是一种虚拟存在的表。**

它的数据并不真正存储在数据库中，而是基于 **SQL查询结果动态生成**。

可以理解为：

> 视图 = 保存了一条 SQL 查询语句的虚拟表

---

例如：

有用户表：

### user

|id|name|age|gender|
|---|---|---|---|
|1|张三|20|男|
|2|李四|22|男|
|3|小红|21|女|

现在经常需要查询女生：

```
select *
from user
where gender='女';
```

可以创建视图：

```
create view girl_user as
select *
from user
where gender='女';
```

以后：

```
select *
from girl_user;
```

效果：

|id|name|age|gender|
|---|---|---|---|
|3|小红|21|女|

---

# 2. 视图的特点

## （1）不存储数据

普通表：

```
磁盘
 |
表数据
```

视图：

```
磁盘
 |
SQL定义
```

执行：

```
select *
from view_name;
```

实际上执行：

```
视图对应的SQL
```

---

## （2）简化复杂SQL

例如：

订单查询：

```
select 
u.name,
o.order_no,
o.price
from user u
join orders o
on u.id=o.user_id;
```

这个SQL很长。

创建：

```
create view order_view as

select 
u.name,
o.order_no,
o.price

from user u
join orders o
on u.id=o.user_id;
```

以后：

```
select *
from order_view;
```

简单很多。

---

## （3）提高安全性

例如：

用户表：

```
user

id
username
password
phone
address
```

不希望普通员工看到密码。

创建视图：

```
create view user_info as

select
id,
username,
phone

from user;
```

员工只能：

```
select *
from user_info;
```

看不到：

```
password
address
```

---

# 3. 创建视图

语法：

```
CREATE VIEW 视图名称 AS
SELECT语句;
```

例如：

```
create view emp_view as

select
id,
name,
salary

from employee;
```

---

# 4. 查看视图

## 查看所有视图

```
show tables;
```

视图也会显示。

---

## 查看视图结构

```
desc emp_view;
```

---

## 查看创建语句

```
show create view emp_view;
```

结果：

```
CREATE VIEW emp_view AS
SELECT id,name,salary
FROM employee;
```

---

# 5. 修改视图

方式1：

删除重新创建

```
drop view emp_view;


create view emp_view as
select...
```

---

方式2：

```
create or replace view emp_view as

select
id,
name

from employee;
```

---

# 6. 删除视图

```
drop view 视图名称;
```

例如：

```
drop view emp_view;
```

---

# 7. 视图的数据修改

视图可以执行：

```
insert
update
delete
```

但是有限制。

---

例如：

简单视图：

```
create view v_user as

select id,name
from user;
```

可以：

```
update v_user
set name='张三'
where id=1;
```

实际上修改：

```
user表
```

---

但是复杂视图一般不能修改。

例如：

包含：

- group by
- 聚合函数
- distinct
- union
- join

例如：

```
create view v_count as

select
gender,
count(*)

from user

group by gender;
```

不能：

```
update v_count
set count=100;
```

因为不知道修改哪个原表数据。

---

# 8. 视图和普通表区别（面试）

|区别|普通表|视图|
|---|---|---|
|是否存储数据|存储|不存储|
|数据来源|真实数据|查询结果|
|占用空间|需要|基本不需要|
|修改数据|直接修改|受限制|
|安全性|低|高|
|维护复杂SQL|不方便|方便|

---

# 9. 视图和索引区别（高频）

很多人容易混淆。

||视图|索引|
|---|---|---|
|作用|简化查询、安全控制|提高查询速度|
|存储|SQL定义|B+Tree结构|
|是否占空间|几乎没有|需要空间|
|影响查询|方便开发|提升性能|

例如：

视图：

```
select * from user_view;
```

索引：

```
create index idx_name
on user(name);
```

---

# 10. 面试回答模板

**Q：什么是MySQL视图？**

回答：

> 视图是基于SQL查询结果生成的一张虚拟表，本身不存储数据，只保存查询定义。它可以简化复杂SQL，提高数据安全性，例如隐藏敏感字段。视图的数据会随着基表数据变化而变化。