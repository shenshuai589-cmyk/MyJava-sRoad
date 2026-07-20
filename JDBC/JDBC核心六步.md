
JDBC 是 Java 提供的一套数据库操作标准接口，用于 Java 程序访问 MySQL、Oracle、SQL Server 等关系型数据库。

# JDBC 开发步骤（重点）

## 1.第一步：导入依赖

```xml
<dependency>  
<groupId>com.mysql</groupId>  
<artifactId>mysql-connector-j</artifactId>  
<version>9.3.0</version>  
</dependency>
```
## 第二步：注册驱动

```java
Class.forName("com.mysql.cj.jdbc.Driver");
```


## 第三步：获取连接

```java
String url = "jdbc:mysql://127.0.0.1:3306/atguigu";  
String username = "root";  
String password = "250712";  
Connection connection = DriverManager.getConnection(url, username, password);
```

## 第四步执行

```java
Statement stmt = conn.createStatement();  
String sql =  "select * from user";  
ResultSet rs = stmt.executeQuery(sql);
```

## 第五步：处理结果集
```java
while(rs.next()){  
System.out.println(  
rs.getInt("id")  
+ "\t"  
+ rs.getString("name")  
);  
}
```

## 第六步：释放资源

```java
rs.close();
stmt.close();
conn.close();
```
