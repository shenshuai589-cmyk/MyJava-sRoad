
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

