**Spring6启用Log4j2日志框架**

第一步：引入Log4j2的依赖
```xml
<!--log4j2的依赖-->
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.19.0</version>
</dependency>
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-slf4j2-impl</artifactId>
  <version>2.19.0</version>
</dependency>
```
第二步：编写log4j2.xml
必须在根路径下创建名字必须叫做log4j2.xml的文件
```xml
<?xml version="1.0" encoding="UTF-8"?>

<configuration>
    <loggers>
        <!--
            level指定日志级别，从低到高的优先级：
                ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
        -->
        <root level="DEBUG">
            <appender-ref ref="spring6log"/>
        </root>
    </loggers>
    <appenders>
        <!--输出日志信息到控制台-->
        <console name="spring6log" target="SYSTEM_OUT">
            <!--控制日志输出的格式-->
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss SSS} [%t] %-3level %logger{1024} - %msg%n"/>
        </console>
    </appenders>
</configuration>
```
那么下面就可以使用log4j2日志了

**我自己如何使用log4j2记录日志信息呢**
第一步：创建日志记录器对象
```java
Logger logger = LoggerFactory.getLogger(FirstSpringTest.class);

```
第二步：记录日志，根据不同级别输出日志
```java
logger.info("我是一条消息")
logger.debug("我是一条调试消息")
logger.error("我是一条错误消息")
```
注意：
日志也是有等级的：
level指定日志级别，从低到高的优先级：
ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
等级越低，输出的东西越多，例如当前日志等级为DEBUG，那么INFO、WARN、ERROR、FATAL、OFF这几种信息都可以输出



