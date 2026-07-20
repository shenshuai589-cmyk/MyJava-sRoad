
### 1.什么是连接池

```
提前创建好多个数据库连接
↓
放入一个容器中
↓
程序需要连接时直接取
↓
使用完后归还
↓
而不是关闭
```

### 2. 为什么需要连接池？

传统 JDBC：
```java
Connection conn =  DriverManager.getConnection(...);
```

每次执行：
```
创建连接  
↓  
验证账号密码  
↓  
建立网络连接  
↓  
执行SQL  
↓  
关闭连接
```
频繁创建和销毁连接非常耗性能。

---
例如：
```
1000个用户访问系统  
  
不开连接池：  
创建1000次连接  
销毁1000次连接  
  
开连接池：  
只创建20个连接循环使用
```

### 3. JDBC 不使用连接池

```
Connection conn =DriverManager.getConnection(url,user,pwd);PreparedStatement ps =conn.prepareStatement(sql);ps.executeQuery();conn.close();
```

这里：

```
close()=真正关闭连接
```

---

### 4. JDBC 使用连接池

```
DataSource ds =new DruidDataSource();Connection conn =ds.getConnection();
```

使用完：

```
conn.close();
```

实际上：

```
不是关闭↓归还连接池
```

所以速度很快。

### 5. Druid 配置

## Maven依赖

```
<dependency>    <groupId>com.alibaba</groupId>    <artifactId>druid</artifactId>    <version>1.2.27</version></dependency>
```

---

## 创建连接池

```
DruidDataSource ds =new DruidDataSource();ds.setDriverClassName(        "com.mysql.cj.jdbc.Driver");ds.setUrl(        "jdbc:mysql://localhost:3306/test");ds.setUsername("root");ds.setPassword("123456");
```

---

## 获取连接

```
Connection conn =ds.getConnection();
```

---

## 归还连接

```
conn.close();
```

实际上：

```
归还连接池
```


---

### 6. Druid 配置参数

## 初始连接数

```
ds.setInitialSize(5);
```

启动时创建：

```
5个连接
```

---

## 最大连接数

```
ds.setMaxActive(20);
```

最多：

```
20个连接
```

---

## 最小空闲连接

```
ds.setMinIdle(5);
```

至少保留：

```
5个空闲连接
```

---

## 获取连接超时时间

```
ds.setMaxWait(3000);
```

表示：

```
3秒拿不到连接↓抛异常
```

