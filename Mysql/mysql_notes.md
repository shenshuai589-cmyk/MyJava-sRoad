# MySQL 笔记（含面试常问点）

---

## 1. 数据库与表操作

### 1.1 数据库操作
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS school;

-- 使用数据库
USE school;

-- 删除数据库
DROP DATABASE IF EXISTS school;
```

### 1.2 表操作
```sql
-- 创建表
CREATE TABLE IF NOT EXISTS Student (
    student_id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    gender CHAR(1),
    class INT
);

-- 删除表
DROP TABLE IF EXISTS Student;

-- 查看表结构
DESCRIBE Student;
```

### 1.3 插入、更新、删除数据
```sql
-- 插入数据
INSERT INTO Student(student_id, name, age, gender, class)
VALUES (1, '张三', 20, 'M', 101);

-- 更新数据
UPDATE Student SET age = 21 WHERE student_id = 1;

-- 删除数据
DELETE FROM Student WHERE student_id = 1;
```

---

## 2. 基础查询

### 2.1 简单查询
```sql
SELECT * FROM Student;
SELECT name, age FROM Student;
SELECT DISTINCT class FROM Student;
```

### 2.2 条件查询
```sql
SELECT * FROM Student WHERE age >= 20;
SELECT * FROM Student WHERE age >= 20 AND gender = 'M';
SELECT * FROM Student WHERE name LIKE '张%';
SELECT * FROM Student WHERE age BETWEEN 18 AND 25;
SELECT * FROM Student WHERE class IN (101, 102);
```

### 2.3 排序与分页
```sql
SELECT * FROM Student ORDER BY age DESC;
SELECT * FROM Student LIMIT 5 OFFSET 10;
```

---

## 3. 聚合函数与分组

### 3.1 聚合函数
```sql
SELECT COUNT(*) FROM Student;
SELECT AVG(age) FROM Student;
SELECT MAX(age), MIN(age) FROM Student;
SELECT SUM(class) FROM Student;
```

### 3.2 分组与筛选
```sql
-- 按班级统计人数
SELECT class, COUNT(*) AS student_count
FROM Student
GROUP BY class;

-- 筛选人数大于等于2的班级
SELECT class, COUNT(*) AS student_count
FROM Student
GROUP BY class
HAVING COUNT(*) >= 2;
```
> ⚠️ 注意：
> - `WHERE` 在分组前筛选行
> - `HAVING` 在分组后筛选聚合结果

---

## 4. 多表查询

### 4.1 内连接（INNER JOIN）
```sql
SELECT s.name, e.course_id, e.grade
FROM Student s
JOIN Enrollment e ON s.student_id = e.student_id;
```

### 4.2 左连接（LEFT JOIN）
```sql
SELECT s.name, e.course_id, e.grade
FROM Student s
LEFT JOIN Enrollment e ON s.student_id = e.student_id;
```

### 4.3 聚合多表
```sql
-- 查询每个学生选课数量 >=2
SELECT s.name, COUNT(e.course_id) AS course_count
FROM Student s
JOIN Enrollment e ON s.student_id = e.student_id
GROUP BY s.student_id, s.name
HAVING COUNT(e.course_id) >= 2;
```

---

## 5. 子查询
```sql
-- 查询选修课程数量最多的学生
SELECT name
FROM Student
WHERE student_id = (
    SELECT student_id
    FROM Enrollment
    GROUP BY student_id
    ORDER BY COUNT(course_id) DESC
    LIMIT 1
);
```

---

## 6. 窗口函数（MySQL 8+）
```sql
-- 每个学生按成绩排名
SELECT
    s.name,
    e.course_id,
    e.grade,
    RANK() OVER (PARTITION BY e.course_id ORDER BY e.grade DESC) AS rank
FROM Student s
JOIN Enrollment e ON s.student_id = e.student_id;

-- 求每个学生平均成绩
SELECT
    s.name,
    AVG(e.grade) OVER (PARTITION BY s.student_id) AS avg_grade
FROM Student s
JOIN Enrollment e ON s.student_id = e.student_id;
```

---

## 7. 索引与性能
```sql
-- 创建索引
CREATE INDEX idx_name ON Student(name);

-- 删除索引
DROP INDEX idx_name ON Student;

-- 查看执行计划
EXPLAIN SELECT * FROM Student WHERE name = '张三';
```
> ⚠️ 提示：
> - 主键和唯一索引自动创建索引  
> - 联合索引顺序影响查询效率  
> - 避免对索引列进行函数操作，否则无法使用索引  

---

## 8. 常用技巧
```sql
-- 获取当前时间
SELECT NOW(), CURDATE();

-- 字符串拼接
SELECT CONCAT(name, '-', class) FROM Student;

-- 判断空值
SELECT * FROM Student WHERE age IS NULL;

-- 删除重复行
DELETE t1
FROM Student t1
INNER JOIN Student t2
WHERE t1.student_id > t2.student_id AND t1.name = t2.name;
```

---

## 9. 面试常问内容

### 9.1 基础问题
- MySQL 常用数据类型有哪些？  
- `CHAR` 与 `VARCHAR` 区别？  
- `TEXT` 与 `VARCHAR` 区别？  
- `NULL` 与空字符串的区别？  

### 9.2 索引与优化
- 主键索引、唯一索引、普通索引、全文索引区别  
- B-Tree 索引与 Hash 索引区别  
- 如何优化查询性能？  

### 9.3 SQL 相关
- `INNER JOIN`、`LEFT JOIN`、`RIGHT JOIN` 区别  
- `GROUP BY` 与 `ORDER BY` 区别  
- `WHERE` 与 `HAVING` 区别  
- 子查询与连接查询的优劣  

### 9.4 事务与锁
- 事务四大特性（ACID）  
- MySQL 支持的隔离级别及区别  
- 行锁与表锁的区别  
- 乐观锁与悲观锁  

### 9.5 实战题
- 查出每个学生选课数量最多的课程  
- 查询选课人数大于 3 的课程  
- 找出成绩排名前 3 的学生  
- 删除重复数据，保留一条  

---

## 10. 学习建议
1. 多用 `EXPLAIN` 分析 SQL 执行计划  
2. 多练 JOIN、子查询和窗口函数  
3. 熟悉索引优化和事务特性  
4. 面试题建议自己动手写 SQL，理解背后的逻辑  

