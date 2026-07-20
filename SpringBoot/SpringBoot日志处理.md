# 1.日志概述

> 日志的核心作用是：**记录程序运行状态、复现线上 Bug、监控性能指标、留存安全审计证据**

# 2.java中的日志框架

Java 日志是一套“门面模式（接口与实现分离）”
- **门面（接口）**：`SLF4J`。
- **实现（干活）**：`Logback`（Spring Boot 默认）、`Log4j2`

**Spring Boot 的底层支撑**：依赖于 `spring-boot-starter-logging`。只要你引入了 `spring-boot-starter-web`，它就会默认自动引入这个日志 Starter，完全不需要你手动配置

# 3. 日志级别概述

Spring Boot 支持 5 种核心的日志级别，粒度由粗到细排序:
```txt
ERROR:错误，影响程序正常运行
WARN:警告，不影响运行但有潜在风险
INFO：常规信息，Spring Boot 默认的输出级别。
DEBUG:调试细节，开发时看 SQL 或请求参数用
TRACE:最详细的追踪，基本不用
```

# 4.更改日志级别

默认级别是 `INFO`，所以我们在代码里写的 `log.debug()` 默认是看不到的。如果想看更详细的日志（比如排查问题），可以在 `application.yml` 中动态更改

```yml
logging:
	level:
		root: info # 全局默认 info
		com.example.demo.controller: debug 
		# 单独将控制层改为 debug 级别，能看到请求细节
```

# 5.丰富启动日志

- **更换 Banner 颜色与图案**：在 `src/main/resources` 下新建一个 `banner.txt`，写上你自定义的艺术字。


# 6.日志的粗细粒度

这指的是你对代码中不同包、不同类的日志控制。
- **粗粒度控制**：直接设置 `root: info`，全系统一刀切。
- **细粒度控制**：针对不同的业务组件进行配置，方便在生产环境“只观察某一个有问题的模块”
```yml
logging:
  level:
    root: warn                       # 粗粒度：框架、中间件等只打印错误和警告信息
    com.example.demo.service: info   # 细粒度：业务核心层打印 info
    com.example.demo.dao: debug      # 细粒度：数据库持久层打印 debug 以便查看生成的 SQL
```

# 7.日志分组

当项目越来越大，一个个包去配置日志级别太麻烦了。Spring Boot 提供了日志分组（Group）功能，可以把相关的包捆绑在一起统一管理

```yml

logging:
	group:
	# 定义一个名为 tomcat 的分组
		tomcat: org.apache.catalina, org.apache.coyote
	# 定义一个名为 business 的业务分组
		business: com.example.demo.controller, com.example.demo.service
	
	  
	level:
		tomcat: warn # 统一控制 tomcat 相关包的级别
		business: debug # 统一将业务包的级别改为 debug
```

# 8.日志输出到文件

默认情况下，日志只打印在控制台，服务重启就没了。生产环境必须输出到文件保存

```yml

logging:
	file:
		# 方式一：只指定文件名，默认在当前项目根目录下生成
		name: app.log

		# 方式二：指定绝对路径（推荐）
		# name: /var/log/myapp/app.log
```

# ==9.滚动日志==

如果一个系统运行几个月，日志文件能达到好几个G，根本打不开。因此需要**滚动日志策略**——即按照“日期”或“文件大小”切分日志。

Spring Boot 内置了对滚动日志的简单配置（基于 Logback 规范）：
```yml
logging:
	logback:
		rollingpolicy:
# 当日志文件达到 10MB 时，自动切分压缩成类似 app-2026-06-10.0.gz 的文件
			max-file-size: 10MB
# 最多保留 30 天的日志，超期的自动删除
			max-history: 30
# 日志文件的总大小上限，超过后会清理旧日志
			total-size-cap: 2GB
```

# 10.日志框架切换（以切换到Log4j2为例）
虽然默认的 Logback 很好，但有些高并发项目为了极致的异步吞吐量，需要切换到 **Log4j2**。
切换的核心思想是：**“排除 Logback 依赖，引入 Log4j2 依赖”**。
在 `pom.xml` 中修改：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
       <!--切除logback -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!--引入Log4j2-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```
引入后，在 `resources` 目录下放入一个 `log4j2-spring.xml` 配置文件，Spring Boot 就会自动识别并切换为 Log4j2 引擎，而你在代码里写的 `@Slf4j` 和 `log.info()` 代码一行都不需要动！