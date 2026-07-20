
# Maven是什么

`Maven 是一个Java 项目构建和依赖管理工具，核心作用就是：帮你自动下载依赖 + 编译 + 测试 + 打包 + 部署。`
 
# 2.为什么学习Maven

```
1.管理依赖
2.自动编译项目 mvn compile
3.运行测试 mvn test
4.打包项目 mvn package
5.意见构建项目 mvn clean install
打包项目并安装到本地仓库（从target目录拷贝安装到本地仓库setting.xml中配置的本地仓库）
```

# 3.Maven项目结构

```
project -->项目
 ├── src -->源码目录（分为main目录和test目录）
 │    ├── main -->主体程序目录
 │    │    ├── java -->java源代码
 │    │    └── resources -->配置文件
 │    └── test
 │         └── java
 ├── pom.xml
 
```

# 4.Maven的下载与配置

```
1. 到Maven官网下载安装包（http://maven.apache.org/）
2. windows下载.zip的文件
3. D:\Program Files\apache-maven-3.9.11\conf目录下有个settings.xml文件
4. 配置本地仓库：

<!-- localRepository
   | The path to the local repository maven will use to store artifacts.
   |
   | Default: ${user.home}/.m2/repository
  <localRepository>/path/to/local/repo</localRepository>
  -->
若不进行主动配置则有个默认的本地仓库
<localRepository>D:\\CUCN\\Maven\\repository</localRepository>

5. 配置阿里云提供的镜像仓库：（setting.xml中）

<mirrors>
	<mirror>
	    <id>nexus-aliyun</id>
	    <mirrorOf>central</mirrorOf>
	    <name>Nexus aliyun</name>
	    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
	</mirror>
</mirrors>

6. 配置Maven工程的基础JDK版本

<profile>
	<id>jdk-24</id>
	<activation>
		<activeByDefault>true</activeByDefault>
	<jdk>24</jdk>
	</activation>
	<properties>
		<!-- 编译源码用24版本语法 -->
		<maven.compiler.source>24</maven.compiler.source>
		<!-- 生成的class文件兼容24 -->
		<maven.compiler.target>24</maven.compiler.target>
		<!-- 编译器插件版本，不是JDK版本，用最新稳定版 -->
		<maven.compiler.compilerVersion>3.13.0</maven.compiler.compilerVersion>
		<!-- 可选：指定编码，避免乱码 -->
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	</properties>
</profile>
```

---
# 5. Maven 核心概念
## 5.1 坐标（Coordinate）

```
唯一标识一个项目：

groupId（公司/组织）

artifactId（项目名/模块名）

version（版本）
```

### 5.2使用命令行生成Maven工程

#### 5.2.1 创建java项目

```
1.在D:\CUCN\Maven\maven-workspace\spaceVideo路径下打开cmd
1.输入 mvn archetype:generate
```


#### 5.2.2创建Web项目

1.利用命令创建web项目

```
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-webapp -DgroupId=com.atguigu.maven -DartifactId=pro02-maven-web -DinteractiveMode=false

其中：
-DgroupId为组id，一般是公司域名反写，
-DartifactId为项目名
```

2.web工程标准的目录结构

```
pro02-maven-web -->项目名
│
├── src
│   ├── main
│   │   ├── java        （写Java代码）
│   │   ├── resources   （配置文件）
│   │   └── webapp      （Web资源）
│   │        ├── WEB-INF
│   │        │    └── web.xml
│   │        ├── index.jsp
│   │        └── 静态资源
│   │
│   └── test
│
├── pom.xml
```



### 5.3解读pom文件

```
<?xml version="1.0" encoding="UTF-8"?>
<!--根标签：project，表示对当前工程进行配置-->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <!--modelVersion标签:代表当前pom.xml所采用的标签结构-->
  <modelVersion>4.0.0</modelVersion>

 <!--坐标信息-->
 <!--groupId标签：代表公司或组织开发的某一个项目-->
  <groupId>com.atguigu.maven</groupId>
  <!-- artifactId标签：代表项目的某一个模块-->
  <artifactId>pro01-maven-java</artifactId>
  <!-- version标签：代表当前模块的版本-->
  <version>1.0-SNAPSHOT</version>
  <!-- packaging标签：打包方式-->
  <!-- 取值jar：生成jar包，说明这个是一个java工程-->
  <!-- 取值jar：生成war包，说明这个是一个web程-->
  <!-- 取值pom：说明这个工程是个管理其他工程的工程-->
  <packaging>jar</packaging>
  
  <name>pro01-maven-java</name>
  <!-- FIXME change it to the project's website -->
  <url>http://www.example.com</url>

<!-- properties标签：定义属性值-->
  <properties>
  <!--字符集-->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <dependencyManagement>
  <!--dependencies标签：配置具体依赖信息,可以包含多个dependcy-->
    <dependencies>
	<!--dependency：配置一个具体的依赖信息-->
      <dependency>
	  <!-- GAV表示当前依赖在仓库中的位置-->
        <groupId>org.junit</groupId>
        <artifactId>junit-bom</artifactId>
        <version>5.11.0</version>
        <type>pom</type>
		
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <scope>test</scope>
    </dependency>
    <!-- Optionally: parameterized tests support -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-params</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>
        <!-- clean lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#clean_Lifecycle -->
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.4.0</version>
        </plugin>
        <!-- default lifecycle, jar packaging: see https://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_jar_packaging -->
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.3.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.13.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.3.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.4.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>3.1.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>3.1.2</version>
        </plugin>
        <!-- site lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#site_Lifecycle -->
        <plugin>
          <artifactId>maven-site-plugin</artifactId>
          <version>3.12.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-project-info-reports-plugin</artifactId>
          <version>3.6.1</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>

```

# 6. web工程依赖java工程

在web工程中的pom.xml中，进行配置java工程的依赖
```
<!--配置java工程的依赖-->
	<dependency>
		<groupId>com.atguigu.maven</groupId>
		<artifactId>pro01-maven-java</artifactId>
		<version>1.0-SNAPSHOT</version>
		<scope>compile</scope>
	</dependency>
```


# 7.测试依赖的范围

标签的位置：dependencies/dependency/score
标签的可选值：compile/test/provided/system/runtime/omport

==scope 控制这个依赖 **在哪些阶段生效**==

- 编译
- 测试 
- 运行
- 打包

1. compile

```
编译用[√]
测试用[√]
运行用[√]
打包用[√]
```

2. test

```
只用于测试代码
只能作用在test文件夹中
```

3. provided

```
编译用
测试用
运行不用
打包不用
```


# ==8.依赖传递==

A依赖于-->B依赖于-->C

那么此时A可以依赖于C吗？

当B对C的依赖时compile时可以用，其他的不可以用


```bash
 我们用pro01-maven-java项目，去依赖spring-core
 
 此时pro01-maven-java对spring-core的依赖就是compile，
 那么spring-core对commons-logging的依赖也是compile，所以pro01-maven-java可以使用commons-logging
 
 +- org.junit.jupiter:junit-jupiter-api:jar:5.11.0:test
[INFO] |  +- org.opentest4j:opentest4j:jar:1.3.0:test
[INFO] |  +- org.junit.platform:junit-platform-commons:jar:1.11.0:test
[INFO] |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
[INFO] +- org.junit.jupiter:junit-jupiter-params:jar:5.11.0:test
[INFO] \- org.springframework:spring-core:jar:7.0.6:compile
[INFO]    +- commons-logging:commons-logging:jar:1.3.5:compile
[INFO]    \- org.jspecify:jspecify:jar:1.0.0:compile
[INFO] ----------------------------------------------
```

==在pom.xml文件中传递依赖时，发现了一个问题==
```text
pom.xml文件中有两个很相像的标签<dependencies>和<dependencyManagement>，那么他们的区别是什么呢?
- dependencies = 真正引入依赖（会下载、会生效）  
- dependencyManagement = 只管理版本（不引入依赖）
```

# 9.依赖的冲突和排除

什么是依赖的冲突
```text
A-->C(0.1)
B-->C(1.0)
D-->A-->B
当A依赖于C的0.1版本，B依赖于C的1.0版本时，此时D和A和B之间存在传递依赖时，那么此时就会出现以来的冲突（C的版本不一致）
```

	当出现依赖冲突时，不需要手动处理，Maven会自动处理
	当出现依赖排除时，需要手动处理
    处理以来排除,通过<exclusions>和<exclusion>来解决

```
<dependency>
		<groupId>com.atguigu.maven</groupId>
		<artifactId>pro01-maven-java</artifactId>
		<version>1.0-SNAPSHOT</version>
		<scope>compile</scope>
		
		<!--配置依赖排除-->
		<exclusions>
			<!--配置具体排除信息，让commons-logging不要传递到pro02-maven-web工程中-->
		  <exclusion>
		    <!--只需要指定artifactId和groupId-->
			<groupId>commons-logging</groupId>
		    <artifactId>commons-logging</artifactId>
		  </exclusion>
		</exclusions>
	</dependency>
```

# 10.继承

## 10.1概念

Maven工程中，A工程继承B工程
本质上是A工程的pom.xml中的配置继承了B工程中pom.xml的配置

## 10.2为什么要有继承

每个模块都要写版本号 、插件配置  、编译版本  、依赖版本，那么此时重复的代码就会比较多，再加上pom中的版本存在不同，维护起来比较困难。

## 10.2.1继承解决了什么问题

统一管理
- 统一JDK版本
- 统一依赖版本
- 统一插件版本
---
# 10.3创建工程

### 10.3.1创建父工程

```
父工程的打包方式必须是pom -->  <packaging>pom</packaging>
创建父工程 mvn archetype:generate
groupId：com.atguigu.maven
artifactId:pro03-maven-parent

```

### 10.3.2创建子程

```
创建子工程 mvn archetype:generate
groupId：com.atguigu.maven
artifactId:pro04-maven-module

```

```

创建子工程 mvn archetype:generate
groupId：com.atguigu.maven
artifactId:pro05-maven-module

```

```

创建子工程 mvn archetype:generate
groupId：com.atguigu.maven
artifactId:pro06-maven-module

```

### 10.4父子工程都创建好后

==父工程==

```
父工程中的pom.xml文件中会出现 -->
  <modules>
    <module>pro04-maven-module</module>
    <module>pro05-maven-module</module>
    <module>pro06-maven-module</module>
  </modules>
```

==子工程==

```
子工程中的pom.xml文件中会出现父工程的信息 -->
  <parent>
    <artifactId>pro03-maven-parent</artifactId>
    <groupId>com.atguigu.maven</groupId>
    <version>1.0-SNAPSHOT</version>
  </parent>
```

==注意点==
```text
当子工程的groupId和父工程的groupId相同时，可以省略
当子工程的version和父工程的version相同时，可以省略
<parent>
    <artifactId>pro03-maven-parent</artifactId>
    <groupId>com.atguigu.maven</groupId>
    <version>1.0-SNAPSHOT</version>
  </parent>
	<!--当子工程的groupId和父工程的groupId相同时，可以省略-->
  <!--
  <groupId>com.atguigu.maven</groupId>
  -->
  <artifactId>pro04-maven-module</artifactId>
  <!--
  <version>1.0-SNAPSHOT</version>
  <!--
```

==关于子父版本配置问题==

```
对于已经在父工程配置了对依赖的管理，子工程需要使用具体哪一个依赖还是要明确配置
	情况1 确实省略了version标签：子工程采纳的就是父工程管理的版本
	情况2 没有省略version标签：
		A:这里配置了version和复工程管理的版本一致，最终还是采纳这个版本
		B:这里配置的version版本和父工程版本不一致，那么子工程配置的版本会覆盖掉父工程中的版本并使用。

```

### 10.5父工程自定义属性标签

父工程中的自定义属性标签通常写在 ==properties ==标签中，用来统一管理版本号、编码格式等配置，子工程可以直接继承使用。

pro03-maven-parent 父工程的pom.xml:

```
// 自定义属性
  <properties>
	创建自定义的属性标签
	标签名就是属性名，标签值就是属性值
	通过引用属性表达式设定版本号，这样版本号就成了一个动态值
	<atguigu.spring.version>4.1.0.RELEASE</atguigu.spring.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>17</maven.compiler.release>
  </properties>
```

调用方式：${自定义标签名} --> ${atguigu.spring.version}

pro03-maven-parent中的pom.xml

```
    <dependencies>
	  <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-core</artifactId>
          <version>${atguigu.spring.version}</version>
      </dependency>

        <!-- Spring Beans  Bean管理与依赖注入 -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
        <version>${atguigu.spring.version}</version>
      </dependency>

        <!-- Spring Context 应用上下文，扩展核心容器 -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>${atguigu.spring.version}</version>
      </dependency>

        <!-- Spring Expression Language Spring表达式语言 -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-expression</artifactId>
        <version>${atguigu.spring.version}</version>
      </dependency>

        <!-- Spring AOP 面向切面编程支持 -->
      <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aop</artifactId>
        <version>${atguigu.spring.version}</version>
      </dependency>
    </dependencies>

```

# 11.Maven聚合

### 11.1 什么是Maven聚合

在Maven中，聚合是指父工程统一管理多个子模块，一次构建全部项目

**聚合 = 一个父工程+多个子模块**
```text
父工程  
│  
├── user-module  
├── order-module  
├── product-module

```

### 11.2聚合的核心标签 ==modules==

pro03-maven-parent项目的pom.xml

```text
  <modules>
    <module>pro04-maven-module</module>
    <module>pro05-maven-module</module>
    <module>pro06-maven-module</module>
  </modules>
```

### 11.3聚合的作用

#### 11.3.1一次构建全部模块

只需要在父工程执行一次 --->`mvn clean install`那么其余子工程也会全部编译打包

Example：

```bash
D:\CUCN\Maven\maven-workspace\spaceVideo\pro03-maven-parent>mvn clean install

[INFO] pro03-maven-parent ................................. SUCCESS [  0.231 s]
[INFO] pro04-maven-module ................................. SUCCESS [  2.345 s]
[INFO] pro05-maven-module ................................. SUCCESS [  1.035 s]
[INFO] pro06-maven-module ................................. SUCCESS [  0.963 s]

```

如果我此时让pro05依赖pro06，让pro04依赖pro05，此时打包再进行打包，那么他们的打包顺序就会由此不同

```bash
D:\CUCN\Maven\maven-workspace\spaceVideo\pro03-maven-parent>mvn clean install
-- 此时的打包顺序：
[INFO] pro03-maven-parent ................................. SUCCESS [  0.232 s]
[INFO] pro06-maven-module ................................. SUCCESS [  2.347 s]
[INFO] pro05-maven-module ................................. SUCCESS [  1.052 s]
[INFO] pro04-maven-module ................................. SUCCESS [  0.993 s]
```