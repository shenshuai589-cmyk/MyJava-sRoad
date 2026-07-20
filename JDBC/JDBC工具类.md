
### 第一版
```java
package com.atguigu.senior.util;  
  
import com.alibaba.druid.pool.DruidDataSourceFactory;  
  
import javax.sql.DataSource;  
import java.io.IOException;  
import java.io.InputStream;  
import java.sql.Connection;  
import java.sql.SQLException;  
import java.util.Properties;  
  
public class JDBCUtil {  
  
    private static DataSource dataSource;  
  
    static {  
        try {  
            Properties properties = new Properties();  
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");  
            properties.load(inputStream);  
            dataSource = DruidDataSourceFactory.createDataSource(properties);  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    public static Connection getConnection() throws Exception {  
        return dataSource.getConnection();  
    }  
  
    public static void release(Connection conn) {  
        if (conn != null) {  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}

```

### 第二版

```java
package com.atguigu.senior.util;  
  
import com.alibaba.druid.pool.DruidDataSourceFactory;  
  
import javax.sql.DataSource;  
import java.io.InputStream;  
import java.sql.Connection;  
import java.sql.SQLException;  
import java.util.Properties;  
  
public class JDBCUtilV2 {  
    private static DataSource dataSource;  
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();  
  
    static {  
        try {  
            Properties properties = new Properties();  
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");  
            properties.load(inputStream);  
            dataSource = DruidDataSourceFactory.createDataSource(properties);  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    public static Connection getConnection() throws Exception {  
        Connection connection = threadLocal.get();  
        if (connection == null) {  
            connection = dataSource.getConnection();  
            threadLocal.set(connection);  
        }  
        return connection;  
    }  
  
    public static void release(Connection conn) {  
        if (conn != null) {  
            try {  
                Connection connection = threadLocal.get();  
                if (connection != null) {  
                    threadLocal.remove();  
                    connection.close();  
                }  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
```