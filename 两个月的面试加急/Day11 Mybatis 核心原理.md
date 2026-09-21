

今天我们按这个顺序来：

1. ⭐ MyBatis 是什么
2. ⭐ MyBatis 执行 SQL 的基本流程
3. ⭐ `SqlSessionFactory`
4. ⭐ `SqlSession`
5. ⭐ Mapper 接口为什么没有实现类却可以直接调用
6. ⭐⭐⭐ Mapper 动态代理
7. ⭐ `#{}` 和 `${}` 的区别
8. ⭐ MyBatis 一级缓存
9. ⭐⭐ MyBatis 二级缓存
10. ⭐ MyBatis 参数映射
11. ⭐ MyBatis 插件机制（了解）

## 一、MyBatis 是什么？

MyBatis 是一个持久层框架，主要用于 Java 程序与数据库之间的交互。

它可以帮助我们：

- 执行 SQL
    
- 处理 SQL 参数
    
- 将查询结果映射成 Java 对象
    
- 管理 Mapper
    
- 提供缓存
    
- 提供动态 SQL
    

可以简单理解为：

```
Java代码
   ↓
MyBatis
   ↓
JDBC
   ↓
数据库
```

---

# 二、Mapper 接口为什么没有实现类也可以调用？

例如：

```
@Mapper
public interface UserMapper {

    User selectById(Long id);
}
```

项目中没有：

```
UserMapperImpl
```

但是却可以：

```
UserMapper mapper = sqlSession.getMapper(UserMapper.class);

User user = mapper.selectById(1L);
```

### 原因

MyBatis 会为 Mapper 接口创建**动态代理对象**。

调用：

```
userMapper.selectById(1L);
```

实际上调用的是 Mapper 的动态代理对象。

核心：

```
Mapper接口
    ↓
动态代理
    ↓
MapperProxy
    ↓
拦截方法调用
    ↓
执行对应SQL
```

### 面试回答

> MyBatis 会为 Mapper 接口创建动态代理对象，不需要我们手动编写实现类。调用 Mapper 方法时，由代理对象拦截方法调用，然后根据 Mapper 方法找到对应的 SQL 并执行。

---

# 三、SqlSessionFactory 和 SqlSession

## 1. SqlSessionFactory

`SqlSessionFactory` 是 MyBatis 的核心工厂。

主要作用：

> 保存 MyBatis 的配置和映射信息，并负责创建 SqlSession。

可以理解成：

```
SqlSessionFactory
       ↓
创建
       ↓
SqlSession
```

---

## 2. SqlSession

`SqlSession` 可以理解成：

> 一次数据库操作会话。

它可以：

- 执行 SQL
    
- 获取 Mapper
    
- 提交事务
    
- 回滚事务等
    

例如：

```
SqlSession session = sqlSessionFactory.openSession();

UserMapper mapper = session.getMapper(UserMapper.class);
```

### 面试回答

> SqlSessionFactory 保存 MyBatis 的配置和映射信息，并负责创建 SqlSession；SqlSession 可以理解为一次数据库操作会话，可以执行 SQL、获取 Mapper 等。

---

# 四、MapperRegistry、MapperProxyFactory、MapperProxy

执行：

```
UserMapper mapper = sqlSession.getMapper(UserMapper.class);
```

大致过程：

```
SqlSession
    ↓
MapperRegistry
    ↓
MapperProxyFactory
    ↓
MapperProxy
    ↓
JDK动态代理
    ↓
UserMapper代理对象
```

---

## 1. MapperRegistry

`MapperRegistry`：

> 负责管理和注册 Mapper。

MyBatis 启动时会将 Mapper 接口注册到 `MapperRegistry`。

---

## 2. MapperProxyFactory

`MapperProxyFactory`：

> 负责创建 Mapper 的代理对象。

---

## 3. MapperProxy

`MapperProxy`：

> Mapper 动态代理的核心，负责拦截 Mapper 方法调用。

例如：

```
userMapper.selectById(1L);
```

最终会被 MapperProxy 接收到。

---

# 五、namespace + id

Mapper XML：

```
<mapper namespace="com.example.mapper.UserMapper">

    <select id="selectById" resultType="User">
        SELECT * FROM user WHERE id = #{id}
    </select>

</mapper>
```

这里：

```
namespace = com.example.mapper.UserMapper
id = selectById
```

可以唯一定位对应的 SQL 映射。

调用：

```
userMapper.selectById(1L);
```

MyBatis 会根据：

```
namespace + id
```

找到对应的 `MappedStatement`。

可以理解为：

```
UserMapper
    +
selectById
    ↓
com.example.mapper.UserMapper.selectById
    ↓
MappedStatement
```

### 面试重点

> namespace + id 是定位 MappedStatement 的重要标识。

---

# 六、MappedStatement

## 什么是 MappedStatement？

`MappedStatement` 是：

> MyBatis 对一条 SQL 映射信息的封装。

它不是简单的一条 SQL 字符串。

其中可以包含：

- SQL 语句
    
- SQL 类型：SELECT、INSERT、UPDATE、DELETE
    
- 参数信息
    
- 返回值信息
    
- 参数映射
    
- SqlSource 等
    

关系：

```
namespace + id
       ↓
定位
       ↓
MappedStatement
       ↓
SQL及相关映射信息
```

### 面试回答

> MappedStatement 是 MyBatis 对 Mapper 方法对应 SQL 映射信息的封装。namespace + id 可以定位对应的 MappedStatement，其中保存了 SQL、参数类型、返回值类型等信息。

---

# 七、MyBatis 核心执行流程

执行：

```
userMapper.selectById(1L);
```

大致流程：

```
Mapper接口
    ↓
动态代理
    ↓
MapperProxy
    ↓
namespace + 方法名
    ↓
MappedStatement
    ↓
Executor
    ↓
StatementHandler
    ↓
ParameterHandler
    ↓
JDBC
    ↓
数据库
    ↓
ResultSet
    ↓
ResultSetHandler
    ↓
Java对象
```

这是 MyBatis 源码面试中非常重要的一条链。

---

# 八、Executor

## Executor 是什么？

Executor 是 MyBatis 中负责执行 SQL 的核心组件之一。

可以简单理解：

> Executor = SQL 执行者

它负责：

- 执行 SQL
    
- 查询
    
- 更新
    
- 部分缓存处理
    

流程：

```
MappedStatement
      ↓
Executor
      ↓
JDBC
      ↓
数据库
```

---

# 九、三种 Executor

MyBatis 常见的 Executor：

```
Executor
├── SimpleExecutor
├── ReuseExecutor
└── BatchExecutor
```

## 1. SimpleExecutor

每执行一次 SQL，就创建一个新的 Statement。

```
SQL1 → 创建Statement → 执行
SQL2 → 创建Statement → 执行
SQL3 → 创建Statement → 执行
```

核心：

> 每次重新创建。

---

## 2. ReuseExecutor

重复使用 Statement。

```
第一次 → 创建Statement
第二次 → 复用Statement
第三次 → 复用Statement
```

核心：

> 复用 Statement。

---

## 3. BatchExecutor

用于批量执行 SQL。

例如：

```
for (int i = 0; i < 1000; i++) {
    userMapper.insert(user);
}
```

适合批量操作。

核心：

> 批量执行，减少数据库交互。

### 记忆口诀

```
Simple：重新建
Reuse：重复用
Batch：一起做
```

---

# 十、MyBatis 四大核心组件

MyBatis 常见的四个核心组件：

```
Executor
StatementHandler
ParameterHandler
ResultSetHandler
```

---

## 1. Executor

负责：

> 整体执行 SQL。

---

## 2. StatementHandler

负责：

> 创建和处理 JDBC Statement。

可以理解为：

```
Executor
   ↓
StatementHandler
   ↓
PreparedStatement
   ↓
JDBC
```

---

## 3. ParameterHandler

负责：

> 给 SQL 中的 `?` 设置参数。

例如：

```
SELECT * FROM user WHERE id = ?
```

传入：

```
1L
```

ParameterHandler 会负责将：

```
? → 1
```

设置到 PreparedStatement 中。

---

## 4. ResultSetHandler

负责：

> 处理数据库返回的 ResultSet，并映射成 Java 对象。

例如数据库返回：

```
id = 1
name = 张三
age = 20
```

最终转换成：

```
User user
```

---

# 十一、MyBatis 中 #{} 和 ${}

这是非常高频的面试题。

## 1. #{}

`#{}` 使用预编译参数。

例如：

```
SELECT * FROM user WHERE id = #{id}
```

最终类似：

```
SELECT * FROM user WHERE id = ?
```

然后通过 ParameterHandler 设置参数。

特点：

- 使用 `?`
    
- 参数绑定
    
- 可以防止 SQL 注入
    
- 推荐使用
    

记忆：

> `#` = 占位符

---

## 2. ${}

`${}` 是字符串直接替换。

例如：

```
SELECT * FROM user
ORDER BY ${orderBy}
```

如果：

```
orderBy = age
```

最终：

```
SELECT * FROM user ORDER BY age
```

特点：

- 直接替换 SQL 片段
    
- 灵活
    
- 存在 SQL 注入风险
    

因此不能直接让用户输入任意 `${}` 内容。

### 为什么还需要 `${}`？

因为某些 SQL 结构不能使用 `?` 参数。

例如：

```
ORDER BY ?
```

通常不能把 `?` 当作列名。

所以可以使用：

```
ORDER BY ${column}
```

但是必须进行白名单控制。

### 面试回答

> `#{}` 使用预编译的 `?` 占位符，通过参数绑定传值，可以防止 SQL 注入；`${}` 是字符串直接替换，可以用于动态表名、列名、排序字段等 SQL 结构，但存在 SQL 注入风险。

---

# 十二、MyBatis 一级缓存

一级缓存是：

> SqlSession 级别的缓存。

默认开启。

例如：

```
User u1 = mapper.selectById(1L);
User u2 = mapper.selectById(1L);
```

在缓存没有失效的情况下：

```
第一次查询
    ↓
数据库
    ↓
一级缓存

第二次查询
    ↓
一级缓存
```

所以：

> 同一个 SqlSession 中，相同查询可能命中一级缓存。

---

# 十三、MyBatis 二级缓存

二级缓存：

> Mapper / namespace 级别的缓存。

多个 SqlSession 可以共享。

例如：

```
SqlSession A
      ↓
   二级缓存
      ↑
SqlSession B
```

所以：

> 不同 SqlSession 之间可以共享二级缓存。

### 一级缓存 vs 二级缓存

||一级缓存|二级缓存|
|---|---|---|
|作用范围|SqlSession|Mapper / namespace|
|默认开启|是|否，通常需要配置|
|是否跨 SqlSession|否|是|

### 记忆

```
一级缓存：一个 SqlSession
二级缓存：多个 SqlSession
```

---

# 十四、动态 SQL

MyBatis 动态 SQL 常见标签：

```
<if>
<where>
<set>
<foreach>
<choose>
```

---

# 十五、<if>

`<if>`：

> 根据条件决定 SQL 片段是否加入。

例如：

```
<select id="selectUser">
    SELECT * FROM user
    <where>

        <if test="name != null">
            AND name = #{name}
        </if>

        <if test="age != null">
            AND age = #{age}
        </if>

    </where>
</select>
```

如果：

```
name = null
age = 20
```

最终：

```
WHERE age = ?
```

---

# 十六、<where>

`<where>`：

> 动态生成 WHERE，并帮助处理前面的 AND / OR。

例如：

```
<where>
    <if test="name != null">
        AND name = #{name}
    </if>

    <if test="age != null">
        AND age = #{age}
    </if>
</where>
```

如果只有 age：

```
WHERE age = ?
```

而不会出现：

```
WHERE AND age = ?
```

核心：

> `<if>` 控制条件；`<where>` 处理动态 WHERE。

---

# 十七、<set>

`<set>`：

> 用于动态 UPDATE，可以自动添加 SET，并处理多余的逗号。

例如：

```
<update id="updateUser">

    UPDATE user

    <set>

        <if test="name != null">
            name = #{name},
        </if>

        <if test="age != null">
            age = #{age},
        </if>

    </set>

    WHERE id = #{id}

</update>
```

如果：

```
name = 张三
age = null
id = 1
```

最终：

```
UPDATE user
SET name = ?
WHERE id = ?
```

即使 XML 中：

```
name = #{name},
```

有逗号，`<set>` 也会处理末尾多余逗号。

---

# 十八、<foreach>

`<foreach>`：

> 遍历集合，并生成 SQL 中对应的内容。

例如：

```
<foreach collection="ids"
         item="id"
         open="("
         separator=","
         close=")">
    #{id}
</foreach>
```

如果：

```
ids = [10, 20, 30]
```

最终 SQL 结构：

```
(?,?,?)
```

然后：

```
? → 10
? → 20
? → 30
```

---

## foreach 属性

|属性|含义|
|---|---|
|collection|要遍历的集合|
|item|当前遍历到的元素|
|open|开始时添加的内容|
|separator|元素之间添加的内容|
|close|结束时添加的内容|

例如：

```
collection="ids"
item="id"
open="("
separator=","
close=")"
```

意思：

> 遍历 ids，每次取一个元素叫 id，开头加 `(`，元素之间加 `,`，最后加 `)`。

---

# 十九、resultType

`resultType`：

> 指定查询结果最终要封装成什么 Java 类型。

例如：

```
<select id="selectUser"
        resultType="com.example.pojo.User">

    SELECT id, name, age
    FROM user

</select>
```

适合：

> 简单的自动映射。

---

# 二十、resultMap

`resultMap`：

> 自己定义数据库字段和 Java 属性之间的映射关系。

例如数据库：

```
user_name
```

Java：

```
private String username;
```

可以使用：

```
<resultMap id="userMap" type="User">

    <id property="id" column="id"/>

    <result property="username"
            column="user_name"/>

</resultMap>
```

表示：

```
数据库字段 user_name
        ↓
Java属性 username
```

---

# 二十一、resultType vs resultMap

||resultType|resultMap|
|---|---|---|
|映射方式|自动映射|自定义映射|
|使用难度|简单|更灵活|
|字段名一致|适合|也可以|
|字段名不一致|不够灵活|适合|
|复杂映射|有限|更适合|

### 面试回答

> resultType 适用于简单的自动映射，直接指定返回的 Java 类型；resultMap 可以自定义数据库字段与 Java 属性之间的映射关系，适合字段名不一致以及复杂映射场景。

---

# 二十二、下划线转驼峰

例如：

数据库：

```
user_name
```

Java：

```
username
```

默认情况下，MyBatis 不一定能自动完成这种映射。

可以开启：

```
mybatis.configuration.map-underscore-to-camel-case=true
```

开启后：

```
user_name
    ↓
username
```

可以自动映射。

---

# 二十三、MyBatis 核心知识总流程

把 Day11 最重要的内容串起来：

```
UserMapper
    ↓
动态代理
    ↓
MapperProxy
    ↓
namespace + method
    ↓
MappedStatement
    ↓
Executor
    ↓
StatementHandler
    ↓
ParameterHandler
    ↓
PreparedStatement
    ↓
JDBC
    ↓
数据库
    ↓
ResultSet
    ↓
ResultSetHandler
    ↓
Java对象
```

---

# 二十四、MyBatis 面试重点总结

## ⭐⭐⭐ 必须掌握

### 1. Mapper 动态代理

> Mapper 接口没有实现类，MyBatis 通过动态代理生成代理对象。

### 2. SqlSessionFactory / SqlSession

> SqlSessionFactory 保存配置和映射信息并创建 SqlSession；SqlSession 是一次数据库操作会话。

### 3. namespace + id

> 用于定位 Mapper 方法对应的 MappedStatement。

### 4. MappedStatement

> 对 SQL 映射信息的封装。

### 5. Executor

> MyBatis 的 SQL 执行核心组件。

### 6. #{} vs ${}

> `#{}` 使用预编译参数，防 SQL 注入；`${}` 直接字符串替换，存在 SQL 注入风险。

### 7. 一级缓存 / 二级缓存

> 一级缓存是 SqlSession 级别；二级缓存是 Mapper / namespace 级别。

### 8. 动态 SQL

重点：

```
<if>
<where>
<set>
<foreach>
```

### 9. resultType / resultMap

> resultType 主要用于简单自动映射；resultMap 用于自定义字段与属性的映射。

---

# 二十五、容易混淆的地方

## 1. MapperRegistry 和 MapperProxyFactory

```
MapperRegistry
    ↓
管理 Mapper
```

```
MapperProxyFactory
    ↓
创建 Mapper 代理
```

---

## 2. MappedStatement 和 namespace + id

错误：

> MappedStatement 就是 namespace + id。

正确：

> namespace + id 用于定位 MappedStatement；MappedStatement 是 SQL 映射信息的封装。

---

## 3. #{} 和 ${}

错误：

> #{} 直接拼接。

正确：

```
#{} → ?
```

```
${} → 直接字符串替换
```

---

## 4. resultType 和 resultMap

不要简单理解成：

```
resultType = 全路径
resultMap = 字段
```

真正的区别：

```
resultType
→ 自动映射

resultMap
→ 自定义映射
```

---

# 二十六、Day11 一句话总结

> **MyBatis 通过 Mapper 动态代理拦截方法调用，根据 namespace + id 找到 MappedStatement，再通过 Executor、StatementHandler、ParameterHandler 等组件执行 JDBC 操作，最后由 ResultSetHandler 将数据库结果映射成 Java 对象。**

这句话可以作为 Day11 的最终总纲。