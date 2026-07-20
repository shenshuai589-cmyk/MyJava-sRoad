```mysql
select  
    id,    
    car_num as carNum,    
    brand,    
    guide_price as guidePrice,    
    produce_time as produceTime,    
    car_type as carType
from
	t_car  
where  
    id = #{id}  
```
这样书写，太过繁琐，那么resultMap就可以解决这一困惑
- resultMap就是手动（数据库的列对应java类中的变量）进行配置并告诉mybatis

```
<resultMap id="" type=""></resultMap>
    1.专门定义一个结果映射，在这个结果映射当中指定数据库表的字段名和java类的属性名的对应关系  
    2.type属性:用来指定POJO类的类名  
    3.id属性：resultMap的唯一标识，这个id会在select标签中使用
    
<result property="" column=""/>
    - property:用来填写POJO类的属性名
    -  column:用来填写数据库对应的字段名
<id property="id" column="id"/>     
注意:可以将数据库中的主键列单独配置出来，可以提高mybatis的效率
```


```
<!--  
    resultMap；  
    1.专门定义一个结果映射，在这个结果映射当中指定数据库表的字段名和java类的属性名的对应关系  
    2.type属性:用来指定POJO类的类名  
    3.id属性：resultMap的唯一标识，这个id会在select标签中使用  
-->  
    <resultMap id="CarResultMap" type="Car">  
<!--        property:用来填写POJO类的属性名-->  
<!--        column:用来填写数据库对应的字段名-->  
<!--        注意:可以将数据库中的主键列单独配置出来，可以提高mybatis的效率-->  
        <id property="id" column="id" /> 
        <result property="carNum" column="car_num"></result> 
        如果property和column一样时可以不配置
        <result property="brand" column="brand"></result>  
        <result property="guidePrice" column="guide_price"></result>  
        <result property="produceTime" column="produce_time"></result>  
        <result property="carType" column="car_type"></result>  
    </resultMap>  
  
    <select id="selectAllRetResultMap" resultMap="">  
        select * from t_car;    </select>
```

## 开启自动映射

开启自动映射的前置条件
```
属性名遵守java的命名规范，数据库表的列名遵循SQL的命名规范
 - java命名规范：首字母小写，遵循驼峰命名方式
 - SQL命名规范：全部小写，单词之间采用下划线分割

```
在mybatis-config.xml文件中配置
```
	<settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/>  
    </settings>
```

那么此时就不需要手动配置\<resultMap>标签了