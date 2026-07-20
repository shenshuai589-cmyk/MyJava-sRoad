## 1.数据准备

### 1.1 建数据库表
![t_clazz](SSM/Mybatis/图片/高级映射及延迟/001.png)
![t_stu](SSM/Mybatis/图片/高级映射及延迟/002.png)

### 1.2 创建module
![t_stu|378](SSM/Mybatis/图片/高级映射及延迟/003.png)

### 1.3 表与表之间的关系

```
多对一：
  - 多的一方是：Student
  - 一的一方是：Clazz

怎么分清主表和副表
原则：谁在前，谁是主表

多对一：多在前，那么多的就是主表
一对多：一在前，那么一就是主表
```

根据多对一高级映射ORM关系分析，修改后的Student类：
![t_stu](SSM/Mybatis/图片/高级映射及延迟/004.png)
Student
```java
package com.powernode.mybatis.pojo;  
  
public class Student {  
    private Integer sid;  
    private String sname;
    
    //新增的
    private Clazz clazz;  
  
  
    public Student() {  
    }  
  
    public Student(Integer sid, String sname) {  
        this.sid = sid;  
        this.sname = sname;  
    }  
  
    public Integer getSid() {  
        return sid;  
    }  
  
    public void setSid(Integer sid) {  
        this.sid = sid;  
    }  
  
    public String getSname() {  
        return sname;  
    }  
  
    public void setSname(String sname) {  
        this.sname = sname;  
    }  
	//新增的
    public Clazz getClazz() {  
        return clazz;  
    }  
	// 新增的
    public void setClazz(Clazz clazz) {  
        this.clazz = clazz;  
    }  
	//修改后的
    @Override  
    public String toString() {  
        return "Student{" +  
                "sid=" + sid +  
                ", sname='" + sname + '\'' +  
                ", clazz=" + clazz +  
                '}';  
    }  
}
```

## 2.高级映射--多对一

多种方式：
- 一条SQL语句，级联属性映射
- 一条SQL语句，association
- 两条SQL语句，分布查询

### 2.1 级联属性映射

- StudentMapper.java
```
Student selectById(Integer sid);
```

![t_stu](SSM/Mybatis/图片/高级映射及延迟/005.png)
- StudentMapper.xml
```
<!--    多对一映射的第一种方式：一条SQL语句，级联属性映射-->  
    <resultMap id="StudentResultMap" type="Student">  
        <id property="sid" column="sid"/>  
        <result property="sname" column="sname" />  
        <result property="clazz.cid" column="cid"/>  
        <result property="clazz.cname" column="cname"/>  
    </resultMap>  
  
    <select id="selectById" resultMap="StudentResultMap">  
        select            
	        s.sid,s.sname,c.cid,c.cname      
	    from            
		    t_stu s left join t_clazz c on s.cid = c.cid        
		where            
			s.sid = #{sid}    
	</select>
```
![t_stu|546](SSM/Mybatis/图片/高级映射及延迟/006.png)
- StudentMapperTest
```java
@Test  
public void testSelectById() {  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);  
    Student stu = mapper.selectById(3);  
    System.out.println(stu);  
    sqlSession.close();  
}
```
![t_stu|586](SSM/Mybatis/图片/高级映射及延迟/007.png)
### 2.2 association

- StudentMapper.java
```
Student selectByIdWithAssociation(Integer id);
```
![StudentMapper|539](SSM/Mybatis/图片/高级映射及延迟/008.png)
- StudentMapper.xml
```java
<!--    多对一映射的第二种方式：一条SQL语句：association-->  
    <!--association:关联-->  
    <!--        property:提供要映射的POJO类的属性名  
        javaType:用来指定要映射的java类  
    -->  
    <resultMap id="StudentResultMapWithAssociation" type="Student">  
        <id property="sid" column="sid"/>  
        <result property="sname" column="sname"/>  
        <association property="clazz" javaType="Clazz">  
            <id property="cid" column="cid"/>  
            <result property="cname" column="cname"/>  
        </association>  
    </resultMap>  
  
    <select id="selectByIdWithAssociation" resultMap="StudentResultMapWithAssociation">  
        select            s.sid,s.sname,c.cid,c.cname        from            t_stu s left join t_clazz c on s.cid = c.cid        where            s.sid = #{sid}    </select>
```
==重要部分==
```
<!--    多对一映射的第二种方式：一条SQL语句：association-->  
	   association:关联  
        - property:提供要映射的POJO类的属性名  
        - javaType:用来指定要映射的java类  
           
    <resultMap id="StudentResultMapWithAssociation" type="Student">  
        <id property="sid" column="sid"/>  
        <result property="sname" column="sname"/>  
        <association property="clazz" javaType="Clazz">  
            <id property="cid" column="cid"/>  
            <result property="cname" column="cname"/>  
        </association>  
    </resultMap>  
```
![xml|521](SSM/Mybatis/图片/高级映射及延迟/009.png)
- StudentMapperTest
```java
// association  
@Test  
public void testSelectByIdWithAssociation(){  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);  
    Student stu = mapper.selectByIdWithAssociation(2);  
    System.out.println(stu.getClazz().getCname());  
    System.out.println(stu);  
    sqlSession.close();  
}
```
![test|556](SSM/Mybatis/图片/高级映射及延迟/010.png)

### 2.3 分布查询

1.先在StudentMapper.java中写方法
```java
//分布查询  
Student selectByIdStep1(Integer id);
```
2.然后再去StudentMapper.xml中写select相关内容
```xml
    <resultMap id="StudentResultMapSteps" type="Student">  
        <id property="sid" column="sid"/>  
        <result property="sname" column="sname"/>  
<!--        select：需要指定另一补sql语句-->
<!--        column：表示另一条sql语句查询的条件，在这也就是cid-->  
        <association property="cid" select="com.powernode.mybatis.mapper.ClazzMapper.selectByIdStep2" column="cid"/>  
  
    </resultMap>  
    <select id="selectByIdStep1" resultMap="StudentResultMapSteps">  
        select sid,sname,cid from t_stu where sid = #{sid}    
    </select>

```
3.接着去ClazzMapper.java中写方法
```java
Clazz selectByIdStep2(Integer cid);
```
4.然后去ClazzMapper.xml文件中写相关的sql语句
```xml
<select id="selectByIdStep2" resultType="Clazz">  
    select cid,cname from t_clazz where cid = #{cid}
</select>
```
5.在主表的test程序中写测试代码
```java
@Test  
public void testSelectByIdSteps(){  
    SqlSession sqlSession = SqlSessionUtil.openSession();  
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);  
    Student stu = mapper.selectByIdStep1(5);  
    System.out.println(stu);  
    sqlSession.close();  
}
```
6.执行步骤
```
Preparing: select sid,sname,cid from t_stu where sid = ?
Parameters: 5(Integer)
Preparing: select cid,cname from t_clazz where cid = ?
Parameters: 1001(Integer)
```

### 懒加载（fetchType =“lazy”）

fetchType是association标签中的一个属性，fetchType="lazy"表示开启了懒加载，调用哪个SQL语句就执行哪个SQL语句，优化了运行

- 当开启了懒加载：
![lazy](SSM/Mybatis/图片/高级映射及延迟/011.png)
运行结果：
```
Preparing: select sid,sname,cid from t_stu where sid = ?
Parameters: 5(Integer)

只执行了一条sql语句
```
- 当未开启懒加载：
![未开启lazy](SSM/Mybatis/图片/高级映射及延迟/012.png)
运行结果：
```
Preparing: select sid,sname,cid from t_stu where sid = ?
Parameters: 5(Integer)
Preparing: select cid,cname from t_clazz where cid = ?
Parameters: 1001(Integer)
```

==注意：该懒加载只对当前association的sql语句起作用，若想要其他sql语句也生效，可以配置全局的懒加载==

![懒加载的全局设置|609](SSM/Mybatis/图片/高级映射及延迟/013.png)
配置了这个之后，只要是分布映射都可以使用懒加载机制


==fetchType="eager",取消延迟加载 

## 3. 一对多

一对多的逻辑关系梳理：
![一对多|612](SSM/Mybatis/图片/高级映射及延迟/014.png)

主表对应的class文件修改
```java
package com.powernode.mybatis.pojo;  
  
import java.util.List;  
  
public class Clazz {  
    private Integer cid;  
    private String cname;  
	//后加成员变量
    private List<Student> students;  
  
  
    public Clazz() {  
    }  
  
    public Clazz(Integer cid, String cname) {  
        this.cid = cid;  
        this.cname = cname;  
    }  
  
    public Integer getCid() {  
        return cid;  
    }  
  
    public void setCid(Integer cid) {  
        this.cid = cid;  
    }  
  
    public String getCname() {  
        return cname;  
    }  
  
    public void setCname(String cname) {  
        this.cname = cname;  
    }  
  
  // 新加的get和set方法
  
    public List<Student> getStudents() {  
        return students;  
    }  
  
    public void setStudents(List<Student> students) {  
        this.students = students;  
    }  
  //修改后的toString方法
    @Override  
    public String toString() {  
        return "Clazz{" +  
                "cid=" + cid +  
                ", cname='" + cname + '\'' +  
                ", students=" + students +  
                '}';  
    }  
}
```

### 3.1 collection标签

1.在ClazzMapper接口中写方法：
![sql|602](SSM/Mybatis/图片/高级映射及延迟/016.png)
2.在ClazzMapper映射文件中写相应的sql语句
![sql|615](SSM/Mybatis/图片/高级映射及延迟/015.png)
```
<collection property="" ofType="">
property:表示类中的属性名
ofType:表示该属性名的参数类型
```

### 3.2 分布查询

1.在ClazzMapper接口中写如第一条sql语句方法

```java
Clazz selectByStep1(Integer cid);
```
2.在ClazzMapper映射文件中编写响应的代码
![sql映射文件](SSM/Mybatis/图片/高级映射及延迟/017.png)
注意：
 - 一对多的resultMap中使用的是collection标签，而不是association标签
 - collection标签中:
    - property:表示类中的属性名称
    - select：表示另一条sql语句对应的方法全包路径
    - column:表示外键对应class文件的属性名
3.在StudentMapper接口中写相应的方法
```java
List<Student> selectionByCidStep2(Integer cid);
```
4.在StudentMapper映射文件中写相应的代码：
```xml
<select id="selectionByCidStep2" resultType="Student">  
    select * from t_stu where cid = #{cid}
</select>
```
5.写测试代码

## 4.多对多

1.多对多就是要分成两个一对多
需要通过中间表进行拆解

Student
```java
public class Student {

    private String sid;

    private String sname;

    // 一个学生对应多个课程
    private List<Course> courses;
    public Student() {
    }

    public Student(String sid, String sname) {
        this.sid = sid;
        this.sname = sname;
    }
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getSname() {
        return sname;
    }
    public void setSname(String sname) {
        this.sname = sname;
    }
    public List<Course> getCourses() {
        return courses;
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    @Override
    public String toString() {
        return "Student{" +
                "sid='" + sid + '\\'' +
                ", sname='" + sname + '\\'' +
                ", courses=" + courses +
                '}';
    }
}
```
student表结构
![t_stu|586](SSM/Mybatis/图片/高级映射及延迟/019.png)

Course
```java
public class Course {

    private String cid;
    private String cname;
    public Course() {
    }

    public Course(String cid, String cname) {
        this.cid = cid;
        this.cname = cname;
    }
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getCname() {
        return cname;
    }
    public void setCname(String cname) {
        this.cname = cname;
    }
    @Override
    public String toString() {
        return "Course{" +
                "cid='" + cid + '\\'' +
                ", cname='" + cname + '\\'' +
                '}';
    }
}
```
表结构：
![t_course|582](SSM/Mybatis/图片/高级映射及延迟/020.png)
t_stu_course sc
![t_stu_course sc|547](SSM/Mybatis/图片/高级映射及延迟/021.png)

Mapper接口：
```java
public interface StudentMapper {  
  
Student selectStudentWithCourse(String sid);  
  
}
```
SQL映射文件
```xml
<resultMap id="studentMap" type="Student">
    <id property="sid" column="sid"/>
    <result property="sname" column="sname"/>
    <!-- 多对多最终还是 collection -->
    <collection property="courses"
                ofType="Course">
        <id property="cid" column="cid"/>
        <result property="cname" column="cname"/>
    </collection>
</resultMap>

<select id="selectStudentWithCourse"
        resultMap="studentMap">
    select
        s.sid,
        s.sname,
        c.cid,
        c.cname
    from t_stu s
    left join t_stu_course sc
        on s.sid = sc.sid
    left join t_course c
        on sc.cid = c.cid
    where s.sid = #{sid}
</select>
```
测试代码
```java
@Test
public void testSelectStudentWithCourse(){
    SqlSession sqlSession =
            SqlSessionUtil.openSession();
    StudentMapper mapper =
            sqlSession.getMapper(StudentMapper.class);
    Student student =
            mapper.selectStudentWithCourse("S001");
    System.out.println(student);
    sqlSession.close();
}
```