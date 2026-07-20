
在数据库中，我们一般将表的主键设置为自动递增，那么这时我们不知道该条数据的主键值，这是我们就需要进行主键回显获取主键值

**主键回显的定义**;主键回显是指执行插入操作后，获取数据库自动生成的主键值，并自动回填到 Java 对象对应属性中的过程。

```java
    @Test  
    public void testJDBCPrimaryKey() throws Exception {  
        //1.获取连接对象  
        Connection connection = DriverManager.getConnection(url, username, password);  
        //2.预编译sql  
        String sql = "insert into t_emp(emp_name,emp_salary,emp_age) values (?,?,?)";  
        //6.开启主键回显  
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);  
  
        //3.为占位符赋值并执行sql语句  
        Employee employee = new Employee(null,"jack",666.66,30);  
        preparedStatement.setString(1, employee.getEmpName());  
        preparedStatement.setDouble(2, employee.getEmpSalary());  
        preparedStatement.setInt(3, employee.getEmpAge());  
  
        int result = preparedStatement.executeUpdate();  
  
        //4.判断是否执行成功  
        if (result > 0) {  
            //7.获取回显的主键值  
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();  
            while (generatedKeys.next()) {  
                Integer empId = generatedKeys.getInt(1);  
                employee.setEmpId(empId);  
            }  
            System.out.println(employee);  
        }else{  
            System.out.println("失败");  
        }  
        //5.释放资源  
        preparedStatement.close();  
        connection.close();  
  
    }  
}
```

修改后的版本
```java
@Test  
public void testJDBCPrimaryKey() throws Exception {  
    //1.获取连接对象  
    Connection connection = DriverManager.getConnection(url, username, password);  
    //2.预编译sql  
    String sql = "insert into t_emp(emp_name,emp_salary,emp_age) values (?,?,?)";  
    //6.开启主键回显  
    PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);  
  
    //3.为占位符赋值并执行sql语句  
    Employee employee = new Employee(null,"jack",666.66,30);  
    preparedStatement.setString(1, employee.getEmpName());  
    preparedStatement.setDouble(2, employee.getEmpSalary());  
    preparedStatement.setInt(3, employee.getEmpAge());  
  
    int result = preparedStatement.executeUpdate();  
    ResultSet resultSet = null;  
    //4.判断是否执行成功  
    if (result > 0) {  
        //7.获取回显的主键值  
        resultSet = preparedStatement.getGeneratedKeys();  
        while (resultSet.next()) {  
            Integer empId = resultSet.getInt(1);  
            employee.setEmpId(empId);  
        }  
        System.out.println(employee);  
    }else{  
        System.out.println("失败");  
    }  
    //5.释放资源  
    if (resultSet != null) {  
    resultSet.close();  
    }
    preparedStatement.close();  
    connection.close();  
}
```

