
索引的概念：
    索引（Index）就是数据库为了提高数据检索速度而引入的一种特殊的数据结构。

索引的优缺点
```
优点：
  - 提高数据检索的效率，降低数据库的IO成本
  - 提高索引列对数据进行排序，降低数据排序的成本，降低CPU的消耗
    
```




> 为什么MySQL索引用B+树？

回答：

> MySQL
> InnoDB索引采用B+树结构，因为B+树的非叶子节点只存储索引，不存储数据，因此一个节点可以存储更多索引，从而降低树的高度，减少磁盘IO次数。同时B+树的叶子节点通过链表连接，方便范围查询和排序查询。


# 索引的分类

主键索引：针对表中主键创建的索引   只能有一个
唯一索引：避免同一个表中某数据列中的值重复  可以有多个
常规索引：快速定位数据  可以有多个
全文索引： 全文索引查找的是文本中的关键字，而不是比较索引中的值 可以有多个


聚集索引（聚簇索引）：将数据存储和索引放到一起，索引结构的叶子节点保存行数据  必须有，而且只能有一个

二级索引：将数据与索引分开存储，索引结构的叶子节点关联的是对应的主键    可以有多个

### ==聚集索引规则==：

> 如果存在主键，主键索引就是聚集索引

> 如果不存在主键，将使用第一个唯一（unique）索引作为聚集索引

> 如果表中没有主键，也没有唯一索引，InnoDB会自动生成一个rowId作为隐藏的聚集索引


### 聚集索引

![聚集索引](Mysql/image/002-聚集索引.png)

聚集索引的叶子节点保存完整的一行数据。

### 二级索引

![二级索引](Mysql/image/003-二级索引.png)
叶子节点挂着的是id字段（聚集索引）的数据


# 索引语法

1. 创建索引
> create [unique| fulltext] index index_name on table_name (index_col_name,...);

2. 查看索引
> show index from table_name;

3. 删除索引
> drop index index_name on table_name;

# SQL性能分析

**SQL执行频率**
MySQL客户端连接成功后，通过show[session | global] status 命令可以提供服务器状态信息。通过如下指令，可以查看当前数据库的insert、update、delete、select的访问频次：
```sql
show global status like 'Com_______'; -- 七个下划线
```

**慢查询日志**
慢查询日志记录了所有执行时间超过指定参数（long_query_time，单位：秒，默认10秒）的所有SQL语句的日志。
```sql
-- 查看慢查询日志
show variables like 'slow_query_log'; 
```
MySQL的慢查询日志默认没有开启，需要在MySQL的配置文件（/etc/my.cnf）中配置信息：

```sql
-- 开启mysql慢日志开关
slow_query_log=1

-- 设置慢查询日志的时间为2秒，sql语句执行时间超过2秒，就会视为慢查询，记录慢查询日志
long_query_time=2
```

**profile**

**explain执行计划**

explain执行计划各字段含义：
```
1. id
   select查询的序列号，表示查询中执行select子句或者是操作表的顺序（id相同，执行顺序从上到下；id不同，值越大，越先执行）
   
2. select_type
   表示select的类型，常见的取值有simple（简单表，不使用表连接或子查询）、primary（主查询）
   union（union中的第二个或者后面的查询语句）、subquery（select/where之后包含了子查询）
   
3. type
   表示连接类型，性能由好到差的连接类型为system -> const -> eq_ref -> ref -> range -> index -> ALL
   
4. possible_key
   显示可能应用在这张表的索引
```


# 索引使用规则

# MySQL 索引使用规则

索引使用规则是 MySQL 面试高频内容，核心围绕：

- **索引结构（B+树）**
- **最左匹配原则**
- **索引失效情况**
- **覆盖索引**
- **索引下推**

---

# 一、最左匹配原则（重点）

## 1. 什么是最左匹配？

对于联合索引：

```
index(a,b,c)
```

MySQL 会按照索引字段顺序匹配：

```
a → b → c
```

必须从最左边开始匹配。

---

例如：

创建索引：

```
create index idx_user
on user(name, age, gender);
```

索引结构：

```
(name, age, gender)
```

---

## 可以使用索引

### 情况1：

```
select *
from user
where name='张三';
```

匹配：

```
name
```

✅ 使用索引

---

### 情况2：

```
select *
from user
where name='张三'
and age=20;
```

匹配：

```
name → age
```

✅ 使用索引

---

### 情况3：

```
select *
from user
where name='张三'
and age=20
and gender='男';
```

匹配：

```
name → age → gender
```

✅ 完整使用索引

---

# 二、违反最左匹配原则

## 1. 跳过第一个字段

SQL：

```
select *
from user
where age=20;
```

索引：

```
(name,age,gender)
```

因为没有：

```
name
```

所以：

❌ 无法使用索引

原因：

B+树结构：

```
张三
 |
   20
     男

李四
 |
   18
     女
```

age 在 B+树中不是有序的。

---

## 2. 跳过中间字段

SQL：

```
select *
from user
where name='张三'
and gender='男';
```

匹配：

```
name
```

跳过：

```
age
```

结果：

```
name 使用索引
gender无法使用索引
```

原因：

索引顺序：

```
name
 |
 age
 |
 gender
```

不知道 age，无法快速定位 gender。

---

# 三、范围查询影响索引

例如：

索引：

```
(name,age,gender)
```

SQL：

```
select *
from user
where name='张三'
and age>20
and gender='男';
```

执行：

```
name √
age √
gender ×
```

原因：

age 是范围查询：

```
20以上
```

B+树无法继续精确定位 gender。

口诀：

> 范围查询右边的索引全部失效。

---

# 四、索引字段不要进行计算

## 错误：

```
select *
from user
where id+1=10;
```

原因：

索引存储：

```
id
1
2
3
...
```

但是计算后：

```
id+1
```

无法直接匹配。

正确：

```
where id=9;
```

---

# 五、不要在索引列使用函数

错误：

```
select *
from user
where YEAR(create_time)=2026;
```

索引：

```
index(create_time)
```

原因：

MySQL需要：

```
每一行执行 YEAR()
```

导致索引失效。

优化：

```
where create_time 
between 
'2026-01-01'
and
'2026-12-31';
```

---

# 六、LIKE 模糊查询

## 1. 后缀匹配

```
where name like '张%';
```

结果：

✅ 使用索引

因为：

```
张三
张伟
张强
```

仍然有序。

---

## 2. 前缀模糊

```
where name like '%张';
```

结果：

❌ 索引失效

因为：

不知道从哪里开始查。

---

## 3. 两边模糊

```
where name like '%张%';
```

❌ 索引失效

---

# 七、OR导致索引失效

SQL：

```
select *
from user
where name='张三'
or age=20;
```

如果：

```
name 有索引
age 没有索引
```

结果：

可能：

❌ 不使用索引

原因：

OR 两边必须都能走索引。

优化：

拆成：

```
select *
from user
where name='张三'

union

select *
from user
where age=20;
```

---

# 八、类型转换导致索引失效

例如：

字段：

```
phone varchar(20)
```

错误：

```
where phone=123456;
```

MySQL会：

```
varchar → int
```

导致索引失效。

正确：

```
where phone='123456';
```

---

# 九、覆盖索引

## 什么是覆盖索引？

查询字段全部包含在索引中。

例如：

索引：

```
index(name,age)
```

SQL：

```
select name,age
from user
where name='张三';
```

执行：

```
索引查询
 ↓
直接返回数据
```

不需要回表。

Extra:

```
Using index
```

---

# 十、索引下推（ICP）

MySQL 5.6以后支持。

例如：

索引：

```
(name,age)
```

SQL：

```
select *
from user
where name='张三'
and age=20;
```

没有索引下推：

```
索引找到所有张三
        |
        ↓
回表
        |
        ↓
判断age
```

有索引下推：

```
索引阶段判断age
        |
        ↓
只回表符合的数据
```

减少回表次数。

---

# 十一、联合索引字段顺序设计

原则：

## 1. 区分度高的字段放前面

例如：

用户表：

```
gender
```

只有：

```
男
女
```

区分度低。

username：

```
100万个不同值
```

区分度高。

所以：

推荐：

```
index(username,gender)
```

而不是：

```
index(gender,username)
```

---

## 2. 经常查询字段放前面

例如：

经常：

```
where user_id=? 
and status=?
```

建立：

```
index(user_id,status)
```

---

# 十二、索引使用口诀（面试）

```
联合索引最左匹配

不要跳过最左字段

范围查询右侧失效

不要计算索引列

不要使用函数

不要前置模糊查询

OR两边都要有索引

类型必须一致

尽量使用覆盖索引
```

---

# 十三、EXPLAIN 判断索引是否生效

重点看：

|字段|判断|
|---|---|
|type|避免 ALL|
|key|是否使用索引|
|rows|扫描数量|
|Extra|是否回表、排序|

优秀：

```
type=ref
key=idx_xxx
rows=10
Extra=Using index
```

差：

```
type=ALL
key=NULL
rows=100000
Extra=Using filesort
```