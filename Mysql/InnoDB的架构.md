
InnoDB 是 MySQL 默认的存储引擎，它的架构主要分为：

```
                 MySQL Server层
                       |
                       ↓
              SQL接口 / SQL解析器
                       |
                       ↓
              InnoDB存储引擎
                       |
        --------------------------------
        |                              |
        ↓                              ↓
     内存结构                         磁盘结构

 Buffer Pool                    Tablespace
 Change Buffer                  Redo Log
 Log Buffer                     Undo Log
 Adaptive Hash Index
```


![InnoDB的架构](Mysql/image/007-InnoDB的架构.png)

简单理解：

> **内存负责提高访问速度，磁盘负责持久化保存。**


# 一、InnoDB 内存结构

## 1. Buffer Pool（缓冲池）⭐⭐⭐⭐⭐

这是 InnoDB 最核心的内存区域。

作用：

> 缓存磁盘中的数据页和索引页，减少磁盘 IO。

因为：

磁盘速度：

```
慢
```

内存：

```
快
```

所以：

```
查询数据

第一次：
磁盘
 ↓
读取Page
 ↓
放入Buffer Pool


第二次：
Buffer Pool
 ↓
直接读取
```

例如：

查询：

```
select * from user where id=1;
```

流程：

```
磁盘 user.ibd

        ↓

读取数据页 Page

        ↓

Buffer Pool缓存

        ↓

返回数据
```

---

## Buffer Pool 存储什么？

主要存：

### ① 数据页

例如：

```
user表数据
```

### ② 索引页

例如：

```
B+树节点
```

### ③ Undo页

回滚日志。

### ④ 自适应哈希索引

后面讲。

---

# 2. Change Buffer（写缓冲）

作用：

> 缓存对非唯一普通索引页的修改。

场景：

例如：

有普通索引：

```
create index idx_name 
on user(name);
```

执行：

```
update user
set name='张三'
where id=1;
```

如果对应索引页不在内存：

传统：

```
读取索引页
 ↓
修改
 ↓
写回磁盘
```

Change Buffer：

```
修改记录先放Change Buffer

        ↓

以后合适时合并
```

减少磁盘 IO。

注意：

**只针对普通索引。**

不适用于：

- 主键索引
- 唯一索引

原因：

唯一索引需要判断唯一性，必须立即查询。

---

# 3. Log Buffer（日志缓冲区）

作用：

> 暂存 Redo Log。

事务修改数据：

```
修改Buffer Pool中的数据

        ↓

产生Redo Log

        ↓

先写Log Buffer

        ↓

刷入Redo Log文件
```

---

# 4. Adaptive Hash Index（自适应哈希索引）

InnoDB 自动创建的哈希索引。

作用：

提高查询速度。

例如：

经常执行：

```
select *
from user
where id=100;
```

InnoDB 发现：

这个查询非常频繁。

可能建立：

```
B+树

     ↓

Hash索引
```

之后：

```
Hash查找
O(1)
```

速度更快。

---

# 二、InnoDB 磁盘结构

## 1. System Tablespace（系统表空间）

文件：

```
ibdata1
```

存储：

- 数据字典
- Undo日志（旧版本）
- 系统信息

---

## 2. File-Per-Table Tablespace（独立表空间）

现在默认开启。

每张表：

```
user.ibd

order.ibd

product.ibd
```

里面保存：

```
表数据
索引
```

---

## 3. Redo Log（重做日志）⭐⭐⭐⭐⭐

作用：

> 保证事务的持久性，实现崩溃恢复。

比如：

事务：

```
update user
set money=900
where id=1;
```

正常流程：

```
修改Buffer Pool

        ↓

写Redo Log

        ↓

提交事务

        ↓

后台刷磁盘
```

如果突然断电：

```
Buffer Pool数据丢失

但是Redo Log还在

        ↓

恢复数据
```

所以：

Redo Log保证：

```
Durability（持久性）
```

---

# 4. Undo Log（回滚日志）⭐⭐⭐⭐⭐

作用：

两个：

## ① 事务回滚

例如：

```
begin;

update user
set money=0;

rollback;
```

恢复：

```
修改前的数据
```

---

## ② MVCC实现

多个事务读取数据时：

通过 Undo Log 保存历史版本。

例如：

原数据：

```
money=100
```

事务A修改：

```
money=50
```

Undo Log保存：

```
旧版本：

money=100
```

事务B仍然可以看到旧数据。

---

# 三、InnoDB 一次更新流程（面试必问）

例如：

```
update user
set age=20
where id=1;
```

完整流程：

```
1. 查询数据页

        ↓

2. 从磁盘加载到Buffer Pool

        ↓

3. 修改Buffer Pool中的数据

        ↓

4. 写Undo Log
   (保存旧版本)

        ↓

5. 写Redo Log Buffer

        ↓

6. Redo Log刷盘

        ↓

7. 事务提交

        ↓

8. 后台线程将脏页刷入磁盘
```

---

# 四、InnoDB 后台线程

## 1. Master Thread

核心后台线程。

负责：

- 刷脏页
- 合并Change Buffer
- 回收Undo

---

## 2. IO Thread

负责：

磁盘IO。

包括：

- Read Thread
- Write Thread

---

## 3. Purge Thread

负责：

清理无用Undo。

例如：

事务结束后：

```
旧版本数据
        ↓
删除
```

---

# 五、InnoDB 架构总结图

```
                    InnoDB

                      |
        --------------------------------

        内存区域                    磁盘区域

        Buffer Pool                表空间
            |                         |
     数据页/索引页                 .ibd文件

        Change Buffer              Undo Log
            |
        Log Buffer                 Redo Log


        Adaptive Hash Index
```

---

# 面试回答版

**Q：介绍一下 InnoDB 架构？**

答：

> InnoDB 架构主要分为内存结构和磁盘结构。内存结构主要包括 Buffer Pool、Change Buffer、Log Buffer 和 Adaptive Hash Index，其中 Buffer Pool 用于缓存数据页和索引页，提高访问效率。磁盘结构主要包括表空间、Redo Log 和 Undo Log。Redo Log 用于保证事务持久性和崩溃恢复，Undo Log 用于事务回滚和 MVCC。通过这些机制，InnoDB 实现了高性能、高可靠的事务支持。