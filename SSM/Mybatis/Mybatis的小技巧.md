
## 1.  ==#{} 和${}的区别==

1. #{}
```
==>  Preparing: select id, car_num as carNum, brand, guide_price as guidePrice, produce_time as produceTime, car_type as carType from t_car where car_type = ?
==> Parameters: 新能源(String)
```

2. ${}
```
==>  Preparing: select id, car_num as carNum, brand, guide_price as guidePrice, produce_time as produceTime, car_type as carType from t_car where car_type = 新能源
==> Parameters: 

org.apache.ibatis.exceptions.PersistenceException: 
### Error querying database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column '新能源' in 'where clause'
### The error may exist in CarMapper.xml
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: select             id,             car_num as carNum,             brand,             guide_price as guidePrice,             produce_time as produceTime,             car_type as carType         from             t_car         where             car_type = 新能源
### Cause: java.sql.SQLSyntaxErrorException: Unknown column '新能源' in 'where clause'

```

#{} 和 ${}的区别
```
#{}：底层使用PreparedStatement.特点：先对sql语句进行编译，然后给SQL语句的占位符（?）传值，可以避免SQL注入的风险。

${}:底层使用的是Statement.特点：先进行SQL语句的拼接，然后再对SQL语句进行编译，存在SQL注入的风险
```

==${}的使用场景==

例：
name = "张三"
select * from user where name = #{name}
```
那么select * from user where name = #{name}编译的结果则是：
select * from user where name = '张三'
```

select * from user where name = ${name}
```
那么select * from user where name = ${name}编译的结果则是：
select * from user where name = 张三
```

使用#{ }时，会将sql语句自动加上一对 '  '
使用${}时，会原样不懂的将\${ }里面的内容拼接到sql语句中

在进行模糊查询的时候：
使用#{}时
```
<select id="selectByCarLike" resultType="com.powernode.mybatis.pojo.Car">  
    select        
	    id, ar_num as carNum, brand,guide_price as guidePrice,produce_time as produceTime,car_type as carType    
	    from  t_car  where  brand like '%#{brand}%'</select>
```
这种情况下，#{}被包裹在 ' ' 中，会直接被当成字符串,那么解决方法：
- 第一种：用${}
```
<select id="selectByCarLike" resultType="com.powernode.mybatis.pojo.Car">  
    select        
	    id, ar_num as carNum, brand,guide_price as guidePrice,produce_time as produceTime,car_type as carType    
	    from  t_car  where  brand like '%${brand}%'</select>
```

 - 第二种:用concat进行拼接

## 2. 别名机制

mybatis-config.xml文件中的\<typeAliases>\</typeAliases>标签中配置别名

![起别名](SSM/Mybatis/图片/我的第一个Mybatis/072.png)
```
type:表示指定给那个起别名
alias:表示别名
注意：typeAliases标签必须写在configuration标签里面，properties、settings标签下面
2. 别名不区分大小写
   
注意：namespace不可以起别名，必须写全限定接口名称
```

## 3. Mybatis-config.xml文件中的Mappers标签

\<Mapper>标签的三个属性：
```
1. <mapper resource = "CarMapper.xml"/>
2. <mapper url="file:///d:/CarMapper.xml"/>
3. <mapper class=""/>
   
resource:这种方式是从类的根路径下开始查找资源。采用这种方式，配置文件放到类路径当中才行

url: 这种方式是一种绝对路径的方式，这种方式不要求配置在类路径下，哪里都行，只要提供一个绝对路径就行。这种方式使用极少，因为移植性太差。路径开头必须是（file:///） 

class: 这个位置提供的是mapper（dao）接口的全限定接口名，必须带有包名的。
	Mapper标签的作用是指定SqlMapper.xml文件的路径，指定接口名有什么用？
	<mapper class="com.powernode.mybatis.mapper.CarMapper"/>
	如果class指定的是com.powernode.mybatis.mapper.CarMapper，
	那么mybatis框架会自动去com.powernode.mybatis.mapper目录下查找CarMapper.xml文件
	也就是说，如果使用这种方式，那么必须保证CarMapper.xml文件必须和CarMapper接口在同一个目录下
```

更先进点的方式\<package>

![package标签|250](SSM/Mybatis/图片/我的第一个Mybatis/075.png)

```
<!--        <mapper resource="CarMapper.xml"/>-->  
<!--        现在的项目大多使用package标签的形式去配置XxxMapper.xml文件  
            前提是CarMapper.xml和CarMapper接口必须是在同包下  
-->  
        <package name="com.powernode.mybatis.mapper"/>
```

## ==4.配置模板==

![配置模板|620](SSM/Mybatis/图片/我的第一个Mybatis/076.png)
![例子|618](SSM/Mybatis/图片/我的第一个Mybatis/077.png)
![新键|618](SSM/Mybatis/图片/我的第一个Mybatis/078.png)



