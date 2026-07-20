## 1.什么是CRUD
```text
C : Create 增
R : Retrieve 查
U : Update 改
D ： Delete 删
```

## 2.依次实现CRUD

### 2.1 insert

**2.1.1在我的第一个Mybatis中，我们写入插入数据的时候是这样的：**
```text
<insert id = "insertCar">  
    insert into t_car(id,car_num,brand,guide_price,produce_time,car_type)    values(null,'1003','丰田霸道',30.0,'2000-10-11','燃油车')  
</insert>
```
**2.1.2这样写的问题是什么？**
	值 写死到配置文件中，在真正的开放当中是不存在的，而是通过前端的表单进行传输的。

例如：JDBC的代码是怎么写的？
```text
String sql = "insert into t_car(id,car_num,brand,guide_price,produce_time,car_type) values(null, ? ,?, ?, ?, ?)";
ps.setString(1,xxx);
ps.setString(2,yyy);
```

**2.1.3在JDBC中，占位符使用的是? 在Mybatis中的占位符是什么呢？**

	和?是等效的：#{}
	在Mybatis当中不能使用？占位符，必须使用#{}来代替JDBC当中的
![mybatis的占位符](033.png)
==**2.1.4 mybatis占位符赋值**==

==**1. 我们先用Map集合进行数据的封装**==
![Map集合放入数据|492](034.png)
==在这我们要注意==
insert（）方法中的参数有两个
1. 第一个参数：sqlId，从CarMapper.xml文件中复制
2. 第二个参数：封装数据的对象，这里我们用的是map

**占位符赋值**
![占位符赋值|479](035.png)
`注意：每一个#{}中填入一个Map的key值，要见名知意`

**2.java通过pojo类为占位符赋值**
```text
java程序中使用POJO类给SQL语句的占位符传值
Car car = new Car(null,"333","比亚迪秦",30.0,"2020-11-11","新能源");
注意：占位符#{}，大括号里面写：pojo类的属性名
<insert id="insertCarWithPojo">  
    insert into t_car(id,car_num,brand,guide_price,produce_time,car_type)    values(null,#{carNum},#{brand},#{guidePrice},#{produceTime},#{carType});</insert>
    
占位符里面写的其实是get方法后去除get并且把get后面第一个大写字母变小写的变量名。例：
getName() --> #{name}

```
![pojo类|697](036.png)


![测试代码](037.png)
![Mapper.xml的设置](038.png)

### 2.2 delete

1. 编写test代码
![编写test代码|627](039.png)

2. 在Mapper.xml文件中添加delete标签
**注意：当占位符只有一个的时候，大括号里可以传任何东西，但是不能为空**
![添加delete标签|538](040.png)
3. 运行代码并查看数据库表
![查看表数据|553](041.png)

### 2.3 Update

**注意id此时不能传null了，要传具体的值**
![test语句|580](042.png)
![update标签|581](043.png)

![数据库表数据|627](044.png)


### 2.4 Selete
#### 2.4.1查询一条数据

<span style = "color:purple">select要指定结果要封装的java对象类型告诉mybatis</span>
![resultType|488](046.png)
如果不添加resultType,那么会报错
![报错信息|489](047.png)

==添加resultType后，是可以运行了，但是运行结果有点问题==

```
Car{id=1, carNum='null', brand='宝马520Li', guidePrice='null', produceTime='null', carType='null'}
```
那为什么会出现这样的结果呢？

```text
<select id="selectById" resultType="com.powernode.mybatis.pojo.Car">  
    select * from t_car where id = #{id};
</select>
    
我们上面是根据Car对象里面的属性值对对应的#{}赋值，那这时我们用*进行查询，匹配不上对应的字段，只有id和brand碰巧和Car的id、brand同名才会输出正确的数据

数据库中的：
+----+---------+-----------+-------------+--------------+----------+
| id | car_num | brand     | guide_price | produce_time | car_type |
+----+---------+-----------+-------------+--------------+----------+
|  1 | 1001    | 宝马520Li |       10.00 | 2020-10-11    | 燃油车   |
+----+---------+-----------+-------------+--------------+----------+

Car类中的

id carNum brand guidePrice produceTime carType

解决方法："起别名"
<select id="selectById" resultType="com.powernode.mybatis.pojo.Car">  
    select id,car_num as carNum,brand,guide_price as guidePrice,produce_time as produceTime,car_type as carType from t_car where id = #{id};</select>
    
运行结果：
Car{id=1, carNum='1001', brand='宝马520Li', guidePrice='10.0', produceTime='2020-10-11', carType='燃油车'}
```
![运行结果|493](049.png)

起完别名后再次运行：
![运行结果|493](050.png)


#### 2.4.2查询全部数据

利用==selectList（）==方法返回一个集合，那么这里就可以查询全部数据
![查询全部数据|570](051.png)
**select标签配置**
![select配置|604](052.png)

### 3 namespace

#### 3.1 什么是namespace

namespace是mapper标签下的一个属性 <mapper namespace=

### 3.2 namespace的作用

例如现在我们有两个XxxMapper.xml文件,分别是CarMapper.xml和UserMapper.xml

![两个Mapper.xml文件](053.png)

里面的内容分别是
![两个Mapper.xml文件](054.png)

![两个Mapper.xml文件](055.png)
```text
CarMapper.xml中的namespace= "suibianxie",UserMapper.xnl中的namespace="zhendesuibianxiema"

但是CarMapper.xml和UserMapper.xml中的id是相同的，那么这时我们进行查询。
```
![两个Mapper.xml文件](051.png)
```text
那么此时就会报错，因为两个id相同，那么mybatis就不知道你要查询的是哪个，此时namespace就派上用场了。通过namespace.id进行调用

```
![namespace.id](056.png)
那么此时冲突就解决了