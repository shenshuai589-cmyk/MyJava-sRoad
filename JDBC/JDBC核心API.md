## 1.Connection

表示数据库连接对象。
```
Connection conn =DriverManager.getConnection(url,user,pwd);
```

常用方法：
```
createStatement()
prepareStatement()
setAutoCommit(false)
commit()
rollback()close()
```

## 2. Statement

执行普通 SQL。
使用Statement会出现sql注入的问题，后续不会使用这个

```
Statement statement =connect.createStatement();
```

执行：

```
executeQuery(sql)
```

查询：

```
ResultSet rs =statement.executeQuery(sql);
```

增删改：

```
int rows =statement.executeUpdate(sql);
```

## ==3. PreparedStatement（重点）==

Statement 的预编译版本。

优势：

- 防 SQL 注入
- 执行效率高
- 可重复使用

---

### 查询

```
String sql ="select * from user where id=?";
PreparedStatement ps =conn.prepareStatement(sql);
ps.setInt(1,1);
ResultSet rs =ps.executeQuery();
```

---

### 插入

```
String sql ="insert into user(name,age) values(?,?)";
PreparedStatement ps =conn.prepareStatement(sql);
ps.setString(1,"Tom");
ps.setInt(2,20);
int rows =ps.executeUpdate();
```

# 4. SQL 注入

错误写法：

```
String username = "admin";
String password = "' or '1'='1";String sql ="select * from user where username='"+ username +"' and password='"+ password +"'";
```

生成：

```
select * from userwhere username='admin'and password='' or '1'='1'
```

永远成立。

---

解决方案：

```
PreparedStatement
```

```
String sql ="select * from user where username=? and password=?";
```

# 5. ResultSet

结果集对象。

```
ResultSet rs =ps.executeQuery();
```

---

遍历：

```
while(rs.next()){}
```

---

获取数据：

```
rs.getInt("id");
rs.getString("name");
rs.getDouble("salary");
rs.getDate("birthday");
```

也可通过列索引：

```
rs.getInt(1);
rs.getString(2);
```

但开发中推荐列名。