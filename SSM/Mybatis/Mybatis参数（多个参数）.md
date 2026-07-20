
- 当mybatis接口方法中的参数个数是多个时，Mapper文件中的参数应该如何写呢？
```
IStudentMapper中：

int selectByNameAndSex(String name,Character sex);

此时，StudentMapper.xml映射文件中的参数应该写什么呢？

当方法的参数超过一个时，mybatis会自动生成一个Map数组，里面存放的就是参数的信息，
那么它的键和值是这样的：

map （key是arg或者param）

arg0,name
arg1,sex
...

param1,name
param2,sex
....

所以我们映射文件中的#{}应该写：#{arg0},#{arg1}

<!--    多个参数：-->  
    <select id="selectByNameAndSex" resultType="Student">  
        select * from t_student where name = #{arg0} and sex=#{arg1};    </select>
```

由于arg和param的可读性太差，所以mybatis推出了@Param注解的方式：

```
List<Student> selectByNameAndSex2(@Param("name") String name,@Param("sex") Character sex);
```
原本的@param（value=""）,但是value可以省略，所以可以直接写成@Param("");
使用了param注解之后，arg方式的就失效了，但是param方式的还可以用。

**1.==当我们在查询数据时，不知道要封装成什么，且没有对应的对象时，可以使用Map返回==**
![resultTpye="map'](SSM/Mybatis/图片/我的第一个Mybatis/079.png)

**==2. 当返回的是多小数据，且没有对应的对象作为返回值时，那么我们就可用List\<Map<String,Object>>==**

3.返回Map<String,Map<String,Object>>
![@MapKey()](SSM/Mybatis/图片/我的第一个Mybatis/081.png)

```
/*  
* 查询多条car信息，返回一个大Map集合  
* Map集合的key是每一条记录的主键值（id）  
*@MapKey("id") -->里面是id，表示将数据中的id值作为map集合的key值  
* */  
  
@MapKey("id")  
Map<Long,Map<String,Object>> selectAllCar();
```


