
# MySQL 视图 WITH CHECK OPTION

`WITH CHECK OPTION` 是创建视图时的一个**约束选项**。

作用：

> **保证通过视图修改的数据，必须满足视图定义中的 WHERE 条件。**

简单理解：

**不允许通过视图插入或修改出视图范围的数据。**

---

## 1. 不使用 WITH CHECK OPTION

创建用户视图：

```
CREATE VIEW v_user AS
SELECT id, name, age
FROM user
WHERE age >= 18;
```

这个视图只显示成年人：

|id|name|age|
|---|---|---|
|1|张三|20|
|2|李四|25|

现在通过视图修改：

```
UPDATE v_user
SET age = 15
WHERE id = 1;
```

执行成功！

但是结果：

原表 user：

|id|name|age|
|---|---|---|
|1|张三|15|

问题：

张三已经不满足：

```
age >= 18
```

但是数据被修改成功。

这会导致：

> 数据修改后，不再属于视图范围。

---

# 2. 使用 WITH CHECK OPTION

创建视图：

```
CREATE VIEW v_user AS
SELECT id,name,age
FROM user
WHERE age >=18
WITH CHECK OPTION;
```

现在执行：

```
UPDATE v_user
SET age=15
WHERE id=1;
```

结果：

```
ERROR
```

原因：

修改后的数据：

```
age=15
```

不满足：

```
age>=18
```

所以 MySQL 拒绝修改。

---

# 3. INSERT测试

没有：

```
CREATE VIEW v_user AS
SELECT *
FROM user
WHERE age>=18;
```

执行：

```
INSERT INTO v_user
VALUES(3,'小王',10);
```

可能成功。

结果：

user表：

|id|name|age|
|---|---|---|
|3|小王|10|

但是：

```
select *
from v_user;
```

看不到。

因为：

```
age>=18 不满足
```

---

加入：

```
WITH CHECK OPTION
```

再插入：

```
INSERT INTO v_user
VALUES(3,'小王',10);
```

直接失败。

---

# 4. WITH CHECK OPTION执行时机

流程：

```
通过视图修改数据

        ↓

检查修改后的数据

        ↓

是否满足视图WHERE条件

        ↓

满足
 |
允许修改


不满足
 |
拒绝修改
```

---

# 5. CASCADED 和 LOCAL（面试重点）

语法：

```
CREATE VIEW view_name AS
SELECT ...
WITH CHECK OPTION;
```

默认：

```
WITH CASCADED CHECK OPTION
```

---

## ① CASCADED

检查：

当前视图 + 基础视图

例如：

基础视图：

```
CREATE VIEW v1 AS
SELECT *
FROM user
WHERE age>=18
WITH CHECK OPTION;
```

再创建：

```
CREATE VIEW v2 AS
SELECT *
FROM v1
WHERE age<=60
WITH CHECK OPTION;
```

v2要求：

```
18 <= age <= 60
```

修改：

```
update v2
set age=10;
```

失败。

---

## ② LOCAL

只检查当前视图条件。

例如：

v1：

```
age>=18
```

v2：

```
age<=60
WITH LOCAL CHECK OPTION;
```

修改：

```
age=10
```

检查：

v2：

```
age<=60
```

满足。

但是：

v1：

```
age>=18
```

不满足。

LOCAL可能允许。

---

# 6. 面试回答

**Q：WITH CHECK OPTION有什么作用？**

标准回答：

> WITH CHECK OPTION用于创建视图时限制数据修改操作，保证通过视图插入或更新的数据必须满足视图定义中的WHERE条件，避免修改后的数据脱离视图范围。默认使用CASCADED，会检查当前视图和依赖视图的条件。