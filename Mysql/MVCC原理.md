
MVCC 全称：

> **Multi-Version Concurrency Control（多版本并发控制）**

作用：

> 在不加锁的情况下，让多个事务同时访问数据库，提高并发性能。

MySQL InnoDB 默认隔离级别：

```
REPEATABLE READ（可重复读）
```

就是通过：

```
MVCC + Next-Key Lock
```

实现的。

---

# 一、为什么需要 MVCC？

假设没有 MVCC：

事务A：

```
begin;

select * from user where id=1;
```

事务B：

```
begin;

update user 
set name='李四'
where id=1;

commit;
```

如果事务A再次查询：

```
select * from user where id=1;
```

可能看到：

第一次：

```
name=张三
```

第二次：

```
name=李四
```

这就是：

## 不可重复读

解决方案：

加锁：

```
select *
from user
where id=1
for update;
```

但是：

锁会降低并发。

所以 InnoDB 使用：

> MVCC保存多个版本的数据，让查询读取历史版本。

---

# 二、MVCC核心组成

MVCC主要依赖三个东西：

```
1. 隐藏字段
2. Undo Log版本链
3. Read View
```

---

# 三、隐藏字段

InnoDB每一行数据都有隐藏字段。

例如表：

```
CREATE TABLE user(
 id int,
 name varchar(20)
);
```

实际上InnoDB保存：

```
id
name

隐藏字段：
DB_TRX_ID
DB_ROLL_PTR
DB_ROW_ID
```

---

## 1. DB_TRX_ID

事务ID：

表示：

> 最近一次修改这条记录的事务ID

例如：

事务100修改：

```
update user
set name='张三'
```

那么：

```
DB_TRX_ID = 100
```

---

## 2. DB_ROLL_PTR

回滚指针：

指向 Undo Log

作用：

形成版本链。

---

## 3. DB_ROW_ID

隐藏主键。

如果没有定义主键：

InnoDB会自动生成。

---

# 四、Undo Log版本链

假设：

原始数据：

```
id=1
name=王五
```

事务A：

```
update user
set name='张三'
where id=1;
```

生成新版本：

```
当前数据：

id=1
name=张三
trx_id=200

        |
        |
        ↓

Undo Log

id=1
name=王五
trx_id=100
```

如果继续修改：

事务C：

```
update user
set name='李四'
```

版本链：

```
最新版本

name=李四
trx_id=300

      ↓

Undo Log

name=张三
trx_id=200

      ↓

Undo Log

name=王五
trx_id=100
```

这就是：

## 数据多版本

---

# 五、Read View（核心）

MVCC最核心：

> Read View决定当前事务能看到哪个版本的数据。

什么时候创建？

分情况：

---

## 快照读

普通查询：

```
select * from user;
```

创建Read View。

---

## 当前读

例如：

```
select *
from user
where id=1
for update;
```

不会使用MVCC。

会直接读取最新数据，并加锁。

---

# 六、Read View里面有什么？

Read View主要包含：

## 1. m_ids

当前活跃事务ID列表。

例如：

现在：

事务100：

```
正在执行
```

事务200：

```
正在执行
```

生成：

```
m_ids=[100,200]
```

---

## 2. min_trx_id

最小事务ID：

```
100
```

---

## 3. max_trx_id

下一个要分配的事务ID。

例如：

当前最大：

```
200
```

那么：

```
max_trx_id=201
```

---

## 4. creator_trx_id

创建Read View的事务ID。

---

# 七、MVCC版本判断规则（重点）

当事务查询数据时：

拿当前版本：

```
trx_id
```

和 Read View比较。

---

## 情况1：

```
trx_id < min_trx_id
```

说明：

这个事务已经提交。

可见。

例如：

```
数据trx_id=50

当前活跃:
100,200
```

50之前完成：

能看到。

---

## 情况2：

```
trx_id >= max_trx_id
```

说明：

这个版本是在当前事务开启之后产生的。

不可见。

---

## 情况3：

```
min_trx_id <= trx_id < max_trx_id
```

判断：

是否在m_ids里面。

如果：

```
trx_id在m_ids
```

说明：

事务还没提交。

不可见。

如果：

```
不在m_ids
```

说明：

已经提交。

可见。

---

# 八、完整案例（面试常考）

数据库：

```
name=张三
trx_id=100
```

事务A：

```
begin;
select name from user;
```

创建Read View：

```
m_ids=[200]
```

此时：

事务B：

```
update user
set name='李四';

commit;
```

生成：

```
当前版本：

name=李四
trx_id=200


Undo:

name=张三
trx_id=100
```

事务A再次查询。

判断：

当前版本：

```
trx_id=200
```

因为：

```
200 在 m_ids
```

说明：

事务B当时未提交。

不可见。

沿Undo Log找：

```
name=张三
trx_id=100
```

100：

```
< min_trx_id
```

可见。

所以：

事务A看到：

```
张三
```

这就是：

可重复读。

---

# 九、MVCC解决了什么问题？

|问题|MVCC是否解决|
|---|---|
|脏读|✅|
|不可重复读|✅|
|幻读|部分解决|

---

为什么幻读不是完全靠MVCC？

因为：

普通查询：

```
select *
from user
where id>10;
```

是快照读。

MVCC解决。

但是：

```
select *
from user
where id>10
for update;
```

属于：

当前读。

需要：

```
Next-Key Lock
```

解决。

---

# 十、MVCC整体流程图

```
              SQL查询
                 |
                 |
          判断是否当前读
          /             \
       快照读            当前读
        |                |
     Read View          加锁
        |
        |
   找最新版本
        |
        |
  判断trx_id是否可见
        |
        |
      可见？
     /     \
   是       否
   |         |
 返回数据   查Undo Log
             |
             |
          找历史版本
```

---

# 十一、面试总结版

面试官：

> InnoDB的MVCC是怎么实现的？

回答：

> InnoDB的MVCC主要通过隐藏字段、Undo Log版本链和Read View实现。每条记录都有事务ID和回滚指针，数据修改时不会直接删除旧版本，而是通过Undo Log保存历史版本，形成版本链。事务执行普通查询时生成Read View，通过比较记录的trx_id和Read View中的事务信息判断当前版本是否可见，如果不可见则沿Undo Log回溯找到可见版本。MVCC实现了读写不阻塞，提高了数据库并发性能。