
### 1.硬编码方式
```java
@Test  
public void testHardCodeDruid() throws SQLException {  
    /*  
    * 硬编码  
    * */    //1.创建DruidDataSource连接池对象  
    DruidDataSource dataSource = new DruidDataSource();  
    // 2.设置连接池的配置文件  
    // 2.1必须设置  
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");  
    dataSource.setUrl("jdbc:mysql://localhost:3306/atguigu");  
    dataSource.setUsername("root");  
    dataSource.setPassword("123456");  
    //2.2非必须设置  
    dataSource.setInitialSize(10); //初始化连接数  
    dataSource.setMaxActive(20); //设置最大连接数  
  
    //3.通过连接池获取连接对象  
    Connection connection = dataSource.getConnection();  
    //基于conncetion进行crud  
    //4.回收连接  
    connection.close();  
}
```

### 2.Druid软编码方式

![[JDBC/图片/1.png|536]]

```
driverClassName=com.mysql.cj.jdbc.Driver  
url=jdbc:mysql://localhost:3306/atguigu  
username=root  
password=250712  
initialSize=10  
maxActive=20
```

```java
@Test  
public void testResources() throws Exception {  
    //1.创建properties集合，用于存储外部资源文件的key和value值  
    Properties properties = new Properties();  
  
    //2,读取外部资源文件，获取说入流，加载到Properties集合中  
    InputStream inputStream = DruidTest.class.getClassLoader().getResourceAsStream("db.properties");  
    properties.load(inputStream);  
  
    //3.基于Properties集合构建DruidDataSource连接池  
    DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);  
  
    //4.通过连接池获取连接对象  
    Connection connection = dataSource.getConnection();  
    System.out.println(connection);  
    //5.开发crud  
  
    //6.回收连接  
    connection.close();  
}
```
