
# 1.PageHelper

### 1.1引入依赖

```
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.3.2</version>
    <scope>compile</scope>
</dependency>
```
### 1.2 在mybatis-config.xml文件中配置插件
```
<plugins>
  <plugin interceptor="com.github.pagehelper.PageInterceptor"></plugin>
</plugins>

```
### 1.3 在Mapper接口和SQL映射文件中编写Java代码
```
List<Car> selectAll();
```

```
<select id="selectAll" resultType="Car">
  select * from t_car
</select>
```

**==1.4开启分页功能==**
```
注意：
一定要早执行SQl语句之前开启分页
PageHelper.startPage(pageNum,pageSize)
pageNum:当前页面数
pageSize:每页数据条数
```
### 获取PageInfo对象

```java
@Test  
public void testSelectAll() {  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    CarMapper mapper = sqlSession.getMapper(CarMapper.class);  
    PageHelper.startPage(2,3);  
    List<Car> cars = mapper.selectAll();
    //获取PageInfo对象
    PageInfo<Car> carPageInfo = new PageInfo<>(cars, 3);  
    System.out.println(carPageInfo);  
    cars.forEach(System.out::println);  
    sqlSession.close();  
}
```
```
运行结果：
PageInfo{pageNum=1, pageSize=1, size=1, startRow=0, endRow=0, total=1, pages=1, list=[Car{id=79, carNum='12345', brand='volvo', guidePrice='30.0', produceTime='2026-5-13', carType='燃油车'}], prePage=0, nextPage=0, isFirstPage=true, isLastPage=true, hasPreviousPage=false, hasNextPage=false, navigatePages=3, navigateFirstPage=1, navigateLastPage=1, navigatepageNums=[1]}
```
