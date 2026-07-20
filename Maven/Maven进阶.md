
# 1.创建工程，引入依赖

## 1.1架构的概念

架构本质上就是[<span style = "color:blue">项目的结构</span>]

## 1.2 单一架构

单一架构也叫[all-in-one]结构，就是所有代码、配置文件、各种资源都在同一个工程
- 一个项目包含一个工程
- 导出一个war包
- 放在一个tomcat上运行

# 2.创建工程


![[创建all-in-one工程.png]]
 
# 3.引入依赖

## 3.1搜索依赖信息网站

<a href="https://mvnrepository.com/">https://mvnrepository.com/</a>

## 3.2怎么选择

- 确定技术选型：确定我们项目中要使用哪些技术
- 到mvnrepository网站搜索具体技术对应的具体依赖信息
- 确定这个技术使用版本依赖
	- 考虑因素1：看是否有别的技术要求这里必须用某一个版本
	- 考虑因素2：如果没有硬性要求，那么选择较高版本或者下载量大的版本

①持久化层所需依赖
- mysql:mysql-connector-java:5.1.37
- com.alibaba:druid:1.2.28
- commons-dbutils:commons-dbutils:1.6

②表达层所需依赖
- javax.servlet:javax.servlet-api:3.1.0
- org.thymeleaf:thymeleaf:3.0.11.RELEASE

③辅助功能所需依赖
- junit:junit:4.12
- ch.qos.logback:logback-classic:1.2.3

```xml
<!-- Source: https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.47</version>
    <scope>compile</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.28</version>
    <scope>compile</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/commons-dbutils/commons-dbutils -->
<dependency>
    <groupId>commons-dbutils</groupId>
    <artifactId>commons-dbutils</artifactId>
    <version>1.6</version>
    <scope>compile</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf -->
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.1.1.RELEASE</version>
    <scope>compile</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/junit/junit -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>

<!-- Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.13</version>
    <scope>compile</scope>
</dependency>
```

# 4.搭建环境

## 4.1.持久化层
### 4.1.1物理建层

①在idea中连接数据库，并创建相关数据

```text
1.创建t_emp表
2.创建t_memorials表
3.创建实体类Emp,Memorials
4.配置jdbc文件
```

![t_emp表](图片/t_emp表.png)
![t_memorials](图片/t_memorials表.png)
![两个实体类](图片/实体类.png)

![jdbc文件配置](图片/jdbc文件配置.png)
![jdbc文件地址](图片/jdbc文件地址.png)

### 4.1.2获取数据库连接
