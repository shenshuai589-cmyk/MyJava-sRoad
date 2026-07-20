
## 1.\<if>标签

```
<if test=""></if>
1.if标签中的test属性是必须的，不可以省略
2.if标签中的属性的值是false或者true
3.如果test是true，则if标签中的sql语句就会拼接，如果为false，则不会拼接
4.test属性中可以使用的是：
	- 当使用了@Param注解，那么test中要出现的是@Param注解指定的参数，例如:
	  @Param("brand"),那么test中出现的必须得是brand
	- 当没使用@Param注解，那么Test中要出现的是：arg0,arg1...param1,param2...
	- 当使用的是POJO对象，那么test中出现的是POJO类的属性名

```

```xml 
完整形式
<select id="selectByMultiCondition" resultType="Car">  
    select * from t_car    where 1=1        
	    <if test="brand !=null and brand !=''">  
            and brand like "%${brand}%"        
        </if>  
        <if test="guidePrice !=null and guidePrice !=''">  
            and guide_price like "%${guidePrice}%"        
        </if>  
        <if test="carType != null and carType !=''">  
            and car_type like "%${carType}%"        
        </if>  
</select>
```

## 2.\<where>标签

- 可以更只能的控制where子句，让where子句更加动态只能
- 所有条件都为空时，where标签保证不会生成where子句
- 自动去除某些条件前面的and或or
```
<select id="selectByMultiConditionWithWhere" resultType="Car">  
    select * from t_car    
	    <where>  
	        <if test="brand != null and brand!=''">  
	            brand like "%${brand}%"        
	        </if>  
	        <if test="guidePrice !=null and guidePrice !=''">  
	            and guide_price like "%${guidePrice}%"        
	        </if>  
	        <if test="carType != null and carType !=''">  
	            and car_type like "%${carType}%"        
	        </if>  
    </where>  
</select>
```
==注意，\<where>标签只能去掉语句前面的and，语句后面的and去不了

## 3.\<trim>标签

```
<trim prefix="" prefixOverrides="" suffix="" suffixOverrides=""></trim>
	 - prefix:在前面加
	 - prefixOverrides:去掉前面的
	 - suffix:在后面加
	 - suffixOverrides：去掉后面的

```

```xml
<select id="selectByMultiConditionWithTrim" resultType="Car">  
    select * from t_car    
    <trim prefix="where" prefixOverrides="" suffix="" suffixOverrides="and|or">  
        <if test="brand != null and brand!=''">  
            brand like "%${brand}%"        
        </if>  
        <if test="guidePrice !=null and guidePrice !=''">  
            and guide_price like "%${guidePrice}%"        
        </if>  
        <if test="carType != null and carType !=''">  
            and car_type like "%${carType}%"        
        </if>  
    </trim>  
</select>
```


## 4.\<set>标签

- set标签主要使用在update语句当中，用来生成set关键字，同时可以去掉多余的 “，”
```xml
<update id="updateByIdWithSet">  
    update t_car    <set>  
        <if test="carNum != null and carNum != '' ">  
            car_num = #{carNum},        
        </if>  
        <if test="brand != null and brand != '' ">  
            brand = #{brand},        
        </if>  
        <if test="guidePrice != null and guidePrice != '' ">  
            guide_price = #{guidePrice},        
        </if>  
        <if test="produceTime != null and produceTime != '' ">  
            produce_time = #{produceTime},       
        </if>  
        <if test="carType != null and carType != '' ">  
            car_type = #{carType},        
        </if>  
    </set>  
    where id = #{id}
</update>
```
![set标签](SSM/Mybatis/图片/我的第一个Mybatis/082.png)

## 5.choose when otherwise

```
choose、when、otherwise三个一半联合起来使用
只有一个分支可以被选择
<choose>
	<when></when>
	<when></when>
	<when></when>
	<otherwise></otherwise>
</choose>
类似于
if(){

}else if(){

}else if(){

}else{

}
```
![CarMapper](SSM/Mybatis/图片/我的第一个Mybatis/083.png)
![Mapper.xml](SSM/Mybatis/图片/我的第一个Mybatis/084.png)
```
@Test  
public void testSelectWithChoose(){  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    CarMapper mapper = sqlSession.getMapper(CarMapper.class);  
    List<Car> cars = mapper.selectWithChoose("volvo", 30.0, "燃油车");  
    cars.forEach(System.out::println);  
    sqlSession.close();  
}
当三个（"volvo", 30.0, "燃油车），都不为空时，sql语句：
 select * from t_car WHERE brand like "%volvo%"
当第一个为空时（"", 30.0, "燃油车），sql语句：
select * from t_car WHERE guide_price = ?
当前两个都为空时（"", null, "燃油车"），sql语句：
select * from t_car WHERE car_type = ?
```


## 6.\<forEach>标签

```
<foreach collection="" item="" separator=""></foreach>
foreach中的属性;
 - collection:表示指定的数组或集合
 - item:代表数组中的元素
 - separator:循环之间的分隔符
```

`int deleteByForeach(@Param("ids") Long[] ids);`
![foreach|587](SSM/Mybatis/图片/我的第一个Mybatis/085.png)
```java
public void testDeleteByForeach(){  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    CarMapper mapper = sqlSession.getMapper(CarMapper.class);  
    Long[] ids = {80L,81L,82L};  
    mapper.deleteByForeach(ids);  
    sqlSession.commit();  
    sqlSession.close();  
}
```
原本的数据库：
![foreach|587](SSM/Mybatis/图片/我的第一个Mybatis/086.png)
执行后的数据库：
![foreach|587](SSM/Mybatis/图片/我的第一个Mybatis/087.png)
```
<foreach collection="" item="" separator="" open="" close=""></foreach>
此时就可以不用写(),而是open="(" close=")"
```

## 7.sql片段复用\<sql>和\<include>

1.先定义\<sql>
```xml
<sql id="Base_Column_List">  
 
id,  
car_num,  
brand,  
guide_price,  
produce_time,  
car_type  
</sql>
```
2.使用\<include>
```xml
<select id="selectAll"  
resultType="Car">
select  
<include refid="Base_Column_List"/>  
from t_car  
</select>

注意：refid中ref表示引用，id则是\<sql>的id，那么这里就是将\<sql>中的id写到这
```

