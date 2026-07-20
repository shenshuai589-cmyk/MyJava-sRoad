# Java 后端开发八股文 2026 版（初级 + 中高级）

适用范围：Java + MySQL + JDBC + Maven + SSM + Spring Boot + Redis + Spring Security + Spring Cloud + 计算机网络 + 消息队列 + 设计模式 + 分布式进阶  
整理日期：2026-07-16  
定位：面试速背 + 项目表达 + 原理复盘  
说明：全文按"初级 / 中级 / 中高级"标注难度，初级面试可优先刷带"初级"标记的问题，第 17-22 章为本次新增的初级+中高级补充内容。

> 版本提示：面试常问 Java 8/17/21、Spring Boot 2/3 的知识，但当前生态已经继续演进。回答时建议先讲通用原理，再补一句“新版本差异”。

## 0. 最新生态速览

|技术|当前面试建议|重点|
|---|---|---|
|Java|至少掌握 Java 8，项目推荐 Java 17/21，关注新 LTS|JVM、并发、集合、Stream、虚拟线程|
|MySQL|重点 8.0/8.4 LTS，了解 9.x 创新版/新 LTS|索引、事务、锁、MVCC、执行计划|
|Maven|重点 3.9.x，了解 Maven 4 变化|生命周期、依赖冲突、聚合继承|
|Spring|SSM/Spring Framework 6+|IoC、AOP、事务、MVC|
|Spring Boot|面试主线 2.x/3.x，了解 4.x|自动配置、Starter、Actuator、配置绑定|
|Redis|重点 6/7/8|数据结构、缓存一致性、分布式锁、持久化、高可用|
|Spring Security|重点 5/6，了解 7.x|过滤器链、认证授权、JWT、OAuth2|
|Spring Cloud|重点 2021/2022/2023/2025 release train|注册发现、配置、网关、熔断、链路追踪|

## 1. Java 基础

### 1.1 面向对象

**Q：面向对象三大特性是什么？**  
A：封装、继承、多态。封装隐藏内部实现；继承复用父类能力；多态让父类引用指向子类对象，运行时根据实际类型调用方法。

**Q：重载和重写区别？**  
A：重载发生在同一个类中，方法名相同、参数不同，编译期确定； A: 重写发生在父子类之间，方法签名相同，运行期动态绑定。

**Q：接口和抽象类区别？**  
A：抽象类强调“是什么”，==适合抽取共同状态和模板逻辑==； A: 接口强调“能做什么”，适合定义能力。Java 8 后接口可有 default/static 方法，但不能替代抽象类保存实例状态。

**Q：final 有哪些用法？**  
A：修饰类表示不可继承； A: 修饰方法表示不可重写； A: 修饰变量表示引用不可变。注意 final 对象引用不可变，不代表对象内部状态不可变。

### 1.2 String

**Q：String 为什么不可变？**  
A：String 底层值不可变，有利于字符串常量池复用、线程安全、Hash 缓存和安全性，比如类加载路径、网络地址等场景。

**Q：String、StringBuilder、StringBuffer 区别？**  
A：String 不可变；StringBuilder 可变且非线程安全，性能更好；StringBuffer 可变且方法加 synchronized，线程安全但性能较低。

**Q：new String("abc") 创建几个对象？**  
A：通常涉及常量池中的 "abc" 和堆上的 String 对象。若常量池已有 "abc"，只创建堆对象。

### 1.3 异常

**Q：Checked Exception 和 Runtime Exception 区别？**  
A：Checked Exception 编译期必须处理，如 IOException；Runtime Exception 运行期异常，如 NPE、IllegalArgumentException，通常代表程序逻辑错误。

**Q：finally 一定执行吗？**  
A：大多数情况下会执行；但 JVM 退出、线程被强制停止、机器宕机等情况不会执行。

## 2. Java 集合

### 2.1 ArrayList 和 LinkedList

**Q：ArrayList 和 LinkedList 区别？**  
A：ArrayList 基于动态数组，随机访问快，尾部追加快，插入删除可能移动元素；LinkedList 基于双向链表，按节点插入删除快，但随机访问慢，且节点对象额外占内存。

**Q：ArrayList 扩容机制？**  
A：底层数组容量不足时扩容，常见实现是扩为原容量约 1.5 倍，然后复制旧数组。频繁扩容会带来复制开销，可用初始容量优化。

### 2.2 HashMap

**Q：HashMap 底层结构？**  
A：JDK 8 后是数组 + 链表 + 红黑树。通过 hash 定位桶，冲突时链表挂接；链表过长且数组容量达到条件后树化。

**Q：HashMap put 流程？**  
A：计算 key 的 hash；定位桶下标；桶为空直接插入；桶不为空则比较 key，相同覆盖，不同追加到链表或红黑树；达到阈值触发扩容。

**Q：为什么 HashMap 容量是 2 的幂？**  
A：可以用 `(n - 1) & hash` 替代取模，提高效率，并让扩容后元素要么留在原位置，要么移动到原位置 + oldCap，迁移更快。

**Q：HashMap 线程安全吗？**  
A：不安全。并发 put 可能导致数据覆盖、结构异常或可见性问题。并发场景用 ConcurrentHashMap。

### 2.3 ConcurrentHashMap

**Q：ConcurrentHashMap 如何保证线程安全？**  
A：JDK 8 主要使用 CAS + synchronized 锁桶头节点，读操作多为无锁；扩容时多个线程可协助迁移。

**Q：ConcurrentHashMap 为什么 key/value 不允许 null？**  
A：并发场景下无法区分“没有这个 key”和“key 对应值就是 null”，会造成语义歧义。

## 3. JVM

### 3.1 内存区域

**Q：JVM 运行时内存区域有哪些？**  
A：线程私有：程序计数器、虚拟机栈、本地方法栈。线程共享：堆、方法区/元空间。堆存对象，栈存方法调用帧，元空间存类元数据。

**Q：堆和栈区别？**  
A：栈随线程创建，保存局部变量、操作数栈、返回地址等；堆被所有线程共享，保存对象实例，是 GC 主要区域。

### 3.2 类加载

**Q：类加载过程？**  
A：加载、验证、准备、解析、初始化。准备阶段给静态变量分配内存并赋默认值；初始化阶段执行静态变量赋值和 static 代码块。

**Q：双亲委派模型是什么？**  
A：类加载器先委托父加载器加载，父加载器加载不了才自己加载。好处是避免类重复加载，保护 Java 核心类库安全。

### 3.3 GC

**Q：如何判断对象可回收？**  
A：主流 JVM 使用可达性分析，从 GC Roots 出发，无法到达的对象可被回收。

**Q：常见 GC Roots 有哪些？**  
A：虚拟机栈引用的对象、静态变量引用的对象、常量引用的对象、JNI 引用、活跃线程等。

**Q：常见垃圾收集器？**  
A：Serial、Parallel、CMS、G1、ZGC、Shenandoah。现在服务端常见 G1，低延迟场景可关注 ZGC。

**Q：Minor GC、Major GC、Full GC 区别？**  
A：Minor GC 回收新生代；Major GC 通常指老年代回收；Full GC 回收整个堆和方法区相关资源，停顿影响最大。

### 3.4 调优

**Q：线上 CPU 飙高怎么排查？**  
A：先定位进程，再看线程 CPU，占用高的线程转十六进制后对照线程栈，找出热点方法；结合日志、监控、火焰图分析。

**Q：内存泄漏怎么排查？**  
A：观察堆增长和 GC 情况，导出 heap dump，用 MAT/JProfiler 分析大对象、引用链、无法释放的集合或缓存。

## 4. Java 并发

### 4.1 线程基础

**Q：线程创建方式？**  
A：继承 Thread、实现 Runnable、实现 Callable 配合 FutureTask、使用线程池。实际项目推荐线程池。

**Q：start 和 run 区别？**  
A：start 创建新线程并由 JVM 调用 run；直接调用 run 只是普通方法调用。

**Q：sleep 和 wait 区别？**  
A：sleep 属于 Thread，不释放锁；wait 属于 Object，必须在同步块中调用，会释放锁，需要 notify/notifyAll 唤醒或超时。

### 4.2 volatile

**Q：volatile 作用？**  
A：保证可见性和禁止指令重排，但不保证复合操作原子性。

**Q：i++ 用 volatile 能保证线程安全吗？**  
A：不能。i++ 包含读取、加一、写回多个步骤，需要 synchronized、Lock 或 AtomicInteger。

### 4.3 synchronized 和 Lock

**Q：synchronized 锁升级过程？**  
A：JDK 8 常见说法是无锁、偏向锁、轻量级锁、重量级锁。新版本中偏向锁已被移除或不再作为主线，需要按版本说明。

**Q：synchronized 和 ReentrantLock 区别？**  
A：synchronized 是 JVM 级关键字，自动释放锁；ReentrantLock 是 API，可中断、可超时、支持公平锁和多个 Condition，需要手动释放。

### 4.4 线程池

**Q：ThreadPoolExecutor 核心参数？**  
A：核心线程数、最大线程数、空闲存活时间、时间单位、阻塞队列、线程工厂、拒绝策略。

**Q：线程池执行流程？**  
A：线程数小于 corePoolSize 直接创建核心线程；否则入队；队列满且线程数小于 maximumPoolSize 创建非核心线程；再满则执行拒绝策略。

**Q：常见拒绝策略？**  
A：AbortPolicy 抛异常；CallerRunsPolicy 调用者执行；DiscardPolicy 静默丢弃；DiscardOldestPolicy 丢弃队列最旧任务。

**Q：为什么不推荐 Executors 创建线程池？**  
A：部分工厂方法使用无界队列或无限最大线程数，可能导致 OOM。项目中应显式配置 ThreadPoolExecutor。

## 5. MySQL

### 5.1 索引

**Q：B+ 树为什么适合数据库索引？**  
A：B+ 树层高低、磁盘 IO 少；非叶子节点只存 key，叶子节点有序且通过链表相连，适合范围查询。

**Q：聚簇索引和非聚簇索引？**  
A：InnoDB 主键索引是聚簇索引，叶子节点存整行数据；普通二级索引叶子节点存主键值，需要回表查询完整行。

**Q：什么是回表？**  
A：通过二级索引查到主键后，再去主键索引查完整数据。可通过覆盖索引减少回表。

**Q：什么是覆盖索引？**  
A：查询所需字段都在索引中，直接从索引返回结果，不必回表。

**Q：最左前缀原则？**  
A：联合索引按从左到右顺序使用，查询条件必须从最左列开始连续匹配。范围查询后的列通常难以继续用于索引定位。

**Q：索引失效场景？**  
A：对索引列使用函数或计算、隐式类型转换、like 左模糊、or 两边没有都命中索引、违反最左前缀、低选择性字段滥用索引等。

### 5.2 事务

**Q：ACID 是什么？**  
A：原子性、一致性、隔离性、持久性。

**Q：事务隔离级别？**  
A：读未提交、读已提交、可重复读、串行化。MySQL InnoDB 默认可重复读。

**Q：脏读、不可重复读、幻读？**  
A：脏读是读到未提交数据；不可重复读是同一行两次读取结果不同；幻读是同一范围两次查询出现新增或删除的行。

**Q：MVCC 是什么？**  
A：多版本并发控制。通过隐藏字段、undo log 和 ReadView 实现快照读，让读写不互相阻塞。

**Q：当前读和快照读？**  
A：普通 select 通常是快照读；select for update、update、delete 是当前读，会读取最新版本并加锁。

### 5.3 锁

**Q：InnoDB 有哪些锁？**  
A：行锁、表锁、间隙锁、临键锁、意向锁、共享锁、排他锁。

**Q：什么是间隙锁？**  
A：锁定索引记录之间的间隙，防止其他事务插入新记录，主要用于解决幻读。

**Q：如何避免死锁？**  
A：固定访问顺序、减少事务范围、合理建索引、避免大事务、降低锁持有时间、捕获死锁异常并重试。

### 5.4 SQL 优化

**Q：SQL 优化思路？**  
A：先用 explain 看执行计划；确认索引命中、扫描行数、回表、排序和临时表；再优化索引、改写 SQL、分页方式、字段选择和表结构。

**Q：深分页怎么优化？**  
A：避免 `limit offset, size` 大 offset 扫描，可用基于上一页最大 id 的延迟关联或游标分页。

**Q：explain 重点看什么？**  
A：type、key、rows、Extra。type 从好到差常见：system、const、eq_ref、ref、range、index、ALL。

## 6. JDBC

**Q：JDBC 基本流程？**  
A：加载驱动、获取 Connection、创建 Statement/PreparedStatement、执行 SQL、处理 ResultSet、关闭资源。

**Q：Statement 和 PreparedStatement 区别？**  
A：PreparedStatement 预编译、支持参数绑定、防 SQL 注入、批量执行更方便。项目中优先使用 PreparedStatement。

**Q：什么是 SQL 注入？如何防止？**  
A：攻击者把恶意 SQL 拼接进查询。防止方式是参数化查询、白名单校验、最小权限、避免字符串拼 SQL。

**Q：数据库连接池作用？**  
A：复用连接，减少频繁创建销毁连接的开销，控制连接数量，提高稳定性。常见连接池有 HikariCP、Druid。

## 7. Maven

**Q：Maven 生命周期？**  
A：clean、default、site。default 常见阶段：validate、compile、test、package、verify、install、deploy。

**Q：Maven 坐标是什么？**  
A：groupId、artifactId、version，用于唯一定位一个构件。

**Q：依赖范围有哪些？**  
A：compile 默认范围；provided 编译需要运行环境提供；runtime 运行需要；test 测试需要；system 不推荐；import 常用于 BOM。

**Q：依赖冲突如何解决？**  
A：遵循最近优先、声明优先原则。可通过 dependency:tree 查看冲突，用 dependencyManagement 锁版本，必要时 exclusions 排除传递依赖。

**Q：父子工程和聚合工程区别？**  
A：父子工程通过 parent 继承配置；聚合工程通过 modules 一次构建多个模块。两者常结合使用。

## 8. SSM：Spring + Spring MVC + MyBatis

### 8.1 Spring IoC

**Q：IoC 是什么？**  
A：控制反转，把对象创建和依赖管理交给 Spring 容器，而不是业务代码自己 new。

**Q：DI 是什么？**  
A：依赖注入，Spring 在创建 Bean 时把依赖对象注入进来，常见方式有构造器注入、setter 注入、字段注入。

**Q：Bean 生命周期？**  
A：实例化、属性填充、Aware 回调、BeanPostProcessor 前置处理、初始化、BeanPostProcessor 后置处理、使用、销毁。

**Q：Bean 作用域？**  
A：singleton、prototype、request、session、application、websocket。默认 singleton。

**Q：循环依赖怎么解决？**  
A：Spring 通过三级缓存解决单例 Bean 的 setter/字段注入循环依赖；构造器循环依赖无法解决。Spring Boot 2.6 后默认更严格，建议通过设计拆分避免循环依赖。

### 8.2 Spring AOP

**Q：AOP 是什么？**  
A：面向切面编程，把日志、事务、权限等横切逻辑从业务中抽离，通过代理织入。

**Q：JDK 动态代理和 CGLIB 区别？**  
A：JDK 动态代理基于接口；CGLIB 基于继承生成子类。final 类或 final 方法不能被 CGLIB 正常代理。

**Q：Spring AOP 失效场景？**  
A：同类内部方法调用、方法非 public、对象不是 Spring 容器管理、final 方法、切点表达式错误等。

### 8.3 Spring 事务

**Q：Spring 事务传播行为？**  
A：常用 REQUIRED、REQUIRES_NEW、NESTED、SUPPORTS、NOT_SUPPORTED、MANDATORY、NEVER。默认 REQUIRED。

**Q：@Transactional 失效场景？**  
A：同类内部调用、方法不是 public、异常被捕获未抛出、默认只回滚 RuntimeException/Error、数据库引擎不支持事务、未被 Spring 管理。

**Q：事务隔离级别如何设置？**  
A：通过 `@Transactional(isolation = Isolation.READ_COMMITTED)` 等配置，实际效果还依赖数据库支持。

### 8.4 Spring MVC

**Q：Spring MVC 请求流程？**  
A：请求进入 DispatcherServlet；HandlerMapping 找处理器；HandlerAdapter 调用 Controller；返回 ModelAndView 或响应体；ViewResolver 渲染视图或 HttpMessageConverter 写 JSON。

**Q：@Controller 和 @RestController 区别？**  
A：@RestController = @Controller + @ResponseBody，默认返回 JSON/文本响应体，不走视图解析。

**Q：拦截器和过滤器区别？**  
A：过滤器是 Servlet 规范，作用于进入 Servlet 前后；拦截器是 Spring MVC 机制，作用于 Controller 调用前后，能访问 Handler 信息。

### 8.5 MyBatis

**Q：MyBatis 一级缓存和二级缓存？**  
A：一级缓存是 SqlSession 级别，默认开启；二级缓存是 namespace 级别，需要配置开启，实际项目谨慎使用，避免脏数据。

**Q：#{} 和 ${} 区别？**  
A：#{} 使用预编译参数，防注入；${} 是字符串替换，存在 SQL 注入风险，适合白名单控制的动态表名、排序字段。

**Q：MyBatis 动态 SQL 常用标签？**  
A：if、choose、where、set、trim、foreach。

## 9. Spring Boot

**Q：Spring Boot 核心优势？**  
A：自动配置、起步依赖、内嵌容器、外部化配置、Actuator 监控，减少样板配置。

**Q：自动配置原理？**  
A：启动类开启自动配置机制，Spring Boot 根据 classpath、配置属性和条件注解加载自动配置类，创建默认 Bean。

**Q：常见条件注解？**  
A：@ConditionalOnClass、@ConditionalOnMissingBean、@ConditionalOnProperty、@ConditionalOnBean、@ConditionalOnWebApplication。

**Q：Starter 原理？**  
A：Starter 本质是一组依赖聚合，配合自动配置类，让引入依赖后自动装配相关 Bean。

**Q：Spring Boot 配置加载优先级？**  
A：常见优先级从高到低包括命令行参数、环境变量、外部配置文件、打包内配置文件、默认配置。实际以官方配置顺序为准。

**Q：Spring Boot 2 到 3 的重要变化？**  
A：最低 Java 17；Jakarta EE 包名从 `javax.*` 迁移到 `jakarta.*`；Spring Framework 6；AOT 和 Native Image 能力增强；部分旧 API 移除。

**Q：Actuator 有什么用？**  
A：暴露健康检查、指标、环境、线程、日志等运维端点。生产环境要限制暴露范围并加权限保护。

**Q：Spring Boot 启动流程概括？**  
A：创建 SpringApplication；推断应用类型；加载监听器和初始化器；准备环境；创建 ApplicationContext；加载 BeanDefinition；刷新容器；执行 Runner。

## 10. Redis

### 10.1 数据结构

**Q：Redis 常见数据类型？**  
A：String、Hash、List、Set、Sorted Set、Bitmap、HyperLogLog、Geo、Stream。

**Q：String 常见场景？**  
A：缓存对象、计数器、分布式锁、限流、Session。

**Q：Hash 常见场景？**  
A：存储对象字段，适合字段级更新，但大对象要注意 big key。

**Q：ZSet 常见场景？**  
A：排行榜、延迟队列、按权重排序。

### 10.2 缓存问题

**Q：缓存穿透是什么？如何解决？**  
A：查询不存在的数据，请求打到数据库。解决：缓存空值、布隆过滤器、参数校验。

**Q：缓存击穿是什么？如何解决？**  
A：热点 key 过期瞬间大量请求打到数据库。解决：互斥锁、逻辑过期、热点 key 不过期。

**Q：缓存雪崩是什么？如何解决？**  
A：大量 key 同时过期或 Redis 故障。解决：过期时间加随机值、多级缓存、限流降级、Redis 高可用。

**Q：缓存一致性怎么做？**  
A：常见方案是先更新数据库，再删除缓存；配合重试、消息队列、binlog 监听或延迟双删增强可靠性。强一致场景不应只依赖缓存。

### 10.3 分布式锁

**Q：Redis 分布式锁怎么实现？**  
A：使用 `SET key value NX EX seconds` 加锁，value 存唯一标识，释放时用 Lua 脚本判断 value 后删除，避免误删别人的锁。

**Q：Redisson 看门狗机制？**  
A：业务未结束时自动续期锁过期时间，防止锁提前过期；业务结束释放锁。

### 10.4 持久化和高可用

**Q：RDB 和 AOF 区别？**  
A：RDB 是快照，恢复快、文件小，但可能丢最近数据；AOF 记录写命令，数据更完整，文件更大，恢复较慢。

**Q：Redis 主从、哨兵、集群区别？**  
A：主从负责读写分离和备份；哨兵负责故障发现和主从切换；Cluster 负责分片扩容和高可用。

**Q：Redis Cluster 如何分片？**  
A：通过 16384 个哈希槽分布到不同主节点，key 根据 CRC16 计算槽位。

## 11. Spring Security

### 11.1 完整工作流程

**Q：Spring Security 请求完整流程？**  
A：

1. 请求先进入 Servlet Filter 链。
2. DelegatingFilterProxy 把请求转给 Spring 容器中的 FilterChainProxy。
3. FilterChainProxy 根据 SecurityFilterChain 匹配当前请求。
4. 多个安全过滤器按顺序执行，例如上下文加载、登出、认证、异常处理、授权等。
5. 认证过滤器从请求中提取凭证，构造 Authentication。
6. AuthenticationManager 委托 ProviderManager。
7. ProviderManager 遍历 AuthenticationProvider。
8. AuthenticationProvider 调用 UserDetailsService 或其他用户服务加载用户。
9. PasswordEncoder 校验密码或校验 token。
10. 认证成功后生成已认证 Authentication，放入 SecurityContext。
11. AuthorizationFilter/AuthorizationManager 判断当前用户是否有权限访问资源。
12. 成功则进入 Controller；失败则返回 401 或 403。

**Q：401 和 403 区别？**  
A：401 表示未认证或认证失败；403 表示已认证但权限不足。

**Q：Authentication 和 SecurityContext？**  
A：Authentication 表示当前认证主体、凭证和权限；SecurityContext 保存 Authentication，通常放在 SecurityContextHolder 中。

**Q：UserDetailsService 作用？**  
A：根据用户名加载用户信息，返回 UserDetails，包括密码、账号状态和权限集合。

**Q：PasswordEncoder 为什么必要？**  
A：密码不能明文存储，应使用 BCrypt、Argon2 等单向哈希。登录时对输入密码编码后比对。

### 11.2 JWT

**Q：JWT 登录流程？**  
A：用户登录成功后服务端签发 token；客户端后续请求携带 token；服务端校验签名、过期时间和声明，解析用户身份并放入 SecurityContext。

**Q：JWT 优缺点？**  
A：优点是无状态、适合分布式；缺点是服务端主动失效困难、token 泄露风险、权限变更不一定实时生效。

**Q：JWT 如何退出登录？**  
A：客户端删除 token；服务端可维护黑名单、版本号、短 access token + refresh token，或权限版本控制。

### 11.3 OAuth2

**Q：OAuth2 常见授权模式？**  
A：授权码模式、客户端凭证模式、设备码模式、刷新令牌。密码模式已经不推荐。

**Q：OAuth2 和 JWT 关系？**  
A：OAuth2 是授权协议，JWT 是 token 格式。OAuth2 可以使用 JWT 作为访问令牌，也可以使用不透明 token。

### 11.4 新版本注意点

**Q：Spring Security 5/6 配置变化？**  
A：逐渐弱化 WebSecurityConfigurerAdapter，推荐声明 SecurityFilterChain Bean；Spring Security 6 全面使用 Jakarta 生态并调整部分默认配置。

示例：

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .build();
}
```

## 12. Spring Cloud

### 12.1 微服务基础

**Q：微服务优缺点？**  
A：优点是独立部署、技术自治、按业务拆分、扩展灵活；缺点是分布式复杂度高，包括网络、事务、监控、部署和故障排查。

**Q：Spring Cloud 常见组件？**  
A：注册中心、配置中心、网关、负载均衡、声明式调用、熔断限流、链路追踪、消息驱动。

### 12.2 注册发现

**Q：服务注册发现流程？**  
A：服务启动后向注册中心注册实例；消费者从注册中心拉取服务列表；通过负载均衡选择实例发起调用；实例定期心跳保持存活。

**Q：Eureka 和 Nacos 区别？**  
A：Eureka 偏 AP，主要做注册发现；Nacos 同时支持注册发现和配置管理，也支持临时/持久实例、命名空间、分组等。

### 12.3 OpenFeign

**Q：OpenFeign 作用？**  
A：声明式 HTTP 客户端，用接口和注解描述远程调用，底层结合负载均衡、编码解码、拦截器等能力。

**Q：Feign 调用如何优化？**  
A：配置连接池、超时时间、日志级别、重试策略、压缩、熔断降级，避免在高频路径过度串行调用。

### 12.4 Gateway

**Q：Spring Cloud Gateway 核心概念？**  
A：Route、Predicate、Filter。Predicate 判断请求是否匹配路由，Filter 对请求和响应做处理。

**Q：网关能做什么？**  
A：统一鉴权、路由转发、限流、熔断、灰度发布、日志审计、跨域处理。

### 12.5 熔断限流

**Q：熔断、降级、限流区别？**  
A：熔断是下游故障时暂时切断调用；降级是返回兜底结果；限流是控制请求速率，保护系统容量。

**Q：Sentinel 和 Resilience4j？**  
A：Sentinel 强在流量控制、熔断降级和控制台；Resilience4j 是轻量 Java 容错库，常用于 Spring Cloud 官方生态。

### 12.6 分布式事务

**Q：分布式事务解决方案？**  
A：2PC、TCC、本地消息表、可靠消息最终一致性、Saga。互联网业务更常用最终一致性。

**Q：Seata AT 模式原理？**  
A：通过代理数据源记录 undo log，一阶段提交本地事务并上报，二阶段全局提交删除 undo log，回滚时用 undo log 反向补偿。

### 12.7 配置中心

**Q：配置中心作用？**  
A：集中管理配置，支持环境隔离、动态刷新、权限审计和版本管理。

**Q：配置刷新要注意什么？**  
A：不是所有 Bean 都能安全热更新；连接池、线程池、核心业务参数刷新要谨慎验证。

## 13. 项目回答模板

### 13.1 登录鉴权

项目表达：

> 我们使用 Spring Security + JWT 做无状态认证。登录时校验账号密码，成功后签发 access token 和 refresh token；请求进入网关后先做基础校验，业务服务再通过 Security 过滤器解析 token，把用户信息放入 SecurityContext。权限使用 RBAC 模型，接口侧通过注解和 URL 规则做授权。退出登录通过 token 黑名单或用户 tokenVersion 控制失效。

可追问：

- token 泄露怎么办：HTTPS、短 token、刷新机制、黑名单、设备维度失效。
- 权限变更如何实时生效：权限版本号、缓存失效、缩短 token 有效期。
- 为什么不用 Session：无状态更适合多实例和网关横向扩展。

### 13.2 缓存设计

项目表达：

> 热点查询使用 Redis 缓存，采用 cache-aside 模式。读请求先查缓存，未命中查数据库并写回缓存；写请求先更新数据库，再删除缓存。为降低穿透使用空值缓存和参数校验；热点 key 使用逻辑过期或互斥锁防击穿；过期时间加入随机值防雪崩。

### 13.3 秒杀/高并发扣库存

项目表达：

> 秒杀场景先在 Redis 预热库存，通过 Lua 脚本保证扣减和资格校验原子性，成功后写入消息队列异步创建订单。数据库层使用唯一索引防重复下单，库存扣减使用乐观锁或条件更新。系统侧配合网关限流、验证码、黑名单和降级保护。

### 13.4 慢 SQL 优化

项目表达：

> 我会先通过监控和慢查询日志定位 SQL，再用 explain 看索引、扫描行数和 Extra。优化方式包括补联合索引、减少回表、改写分页、避免函数导致索引失效、拆分大查询，并结合业务评估是否需要冗余字段或读写分离。

### 13.5 微服务稳定性

项目表达：

> 服务调用链路上配置了超时、重试、熔断、限流和降级。网关做入口限流，服务间调用设置合理超时，核心接口提供兜底结果。监控上接入指标、日志和链路追踪，方便定位是哪一层出现延迟或错误。

## 14. 高频综合题

**Q：一个请求从浏览器到数据库经历什么？**  
A：DNS 解析、TCP/TLS、网关、负载均衡、Spring MVC、参数校验、Security 鉴权、业务 Service、事务代理、MyBatis/JDBC、连接池、MySQL 执行 SQL、返回结果、序列化响应。

**Q：接口很慢怎么排查？**  
A：先确认慢在网关、应用、缓存、数据库还是下游服务；看日志 traceId、APM、线程池、GC、慢 SQL、Redis 延迟、远程调用耗时；定位后再优化。

**Q：系统扛不住流量怎么办？**  
A：短期限流、降级、扩容、缓存热点、异步削峰；中期优化 SQL、索引、线程池、连接池、调用链；长期做架构拆分、读写分离、分库分表和容量治理。

**Q：如何保证接口幂等？**  
A：唯一索引、防重 token、业务流水号、状态机、Redis setnx、消息去重表。核心是让重复请求不会产生重复副作用。

**Q：消息队列重复消费怎么办？**  
A：消费端做幂等，用业务唯一键、去重表或状态机控制；不要依赖 MQ 只投递一次。

**Q：如何设计分页接口？**  
A：普通列表可用 page/size；大数据深分页用游标分页；排序字段要稳定，最好有唯一兜底排序，如 create_time + id。

**Q：如何做接口防刷？**  
A：网关限流、用户/IP/设备维度限流、验证码、人机校验、黑名单、动态风控、核心接口幂等。

## 15. 背诵优先级

### 初级必背（校招/1-3 年经验）

- Java 基础：面向对象、String 不可变、异常体系
- 集合：ArrayList/LinkedList、HashMap 原理、线程安全的 HashMap
- JVM：内存区域、类加载过程、GC 基本原理
- 并发：线程创建方式、volatile、synchronized、线程池核心参数
- MySQL：索引原理、事务 ACID、隔离级别、锁的基本类型
- JDBC：PreparedStatement、SQL 注入防范、连接池作用
- Spring：IoC/DI 概念、Bean 生命周期、AOP 基本原理
- Spring MVC：请求处理流程、Controller 与 RestController 区别
- Spring Boot：自动配置、Starter 概念
- Redis：常见数据类型、缓存穿透/击穿/雪崩
- Spring Security：认证授权基本流程、401/403 区别
- **计算机网络**：TCP 三次握手四次挥手、HTTP 常见状态码、HTTP 和 HTTPS 区别
- **操作系统/Linux**：进程线程区别、常用排查命令
- **消息队列**：为什么用 MQ、Kafka/RabbitMQ 基本概念

### 中级加分（3-5 年经验）

- ConcurrentHashMap 底层实现、AQS、CompletableFuture
- JVM 调优思路、GC 收集器选型、CPU/内存排查实操
- MySQL：MVCC、间隙锁、深分页优化、explain 实战
- Spring：事务传播行为、循环依赖、AOP 失效场景
- Spring Cloud：注册发现、网关、熔断限流、配置中心
- Redis：分布式锁、Redisson 看门狗、持久化与高可用
- **Kafka/RabbitMQ**：消息不丢失、顺序消费、重复消费幂等
- **设计模式**：单例、工厂、代理、观察者、策略在 Spring 中的应用
- 分布式事务、分布式 ID、CAP/BASE 理论

### 中高级加分（5 年以上）

- Java 17/21 新特性、虚拟线程
- Spring Boot 2 到 3 迁移
- OAuth2/OIDC
- 链路追踪和可观测性
- 分库分表方案设计
- 容器化部署和 Kubernetes 基础

## 16. 官方资料入口

- Java: https://www.oracle.com/java/technologies/
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Cloud: https://spring.io/projects/spring-cloud
- MySQL: https://dev.mysql.com/doc/
- Maven: https://maven.apache.org/
- Redis: https://redis.io/docs/latest/

## 17. 计算机网络（初级必背 + 中级加分）

### 17.1 HTTP 基础（初级）

**Q：HTTP 常见请求方法有哪些？** A：GET 查询、POST 新增、PUT 全量更新、PATCH 部分更新、DELETE 删除。GET 幂等且理论上无副作用，POST 通常不幂等。

**Q：常见 HTTP 状态码？** A：2xx 成功（200 OK、201 Created、204 No Content）；3xx 重定向（301 永久、302 临时、304 未修改）；4xx 客户端错误（400 参数错误、401 未认证、403 无权限、404 不存在、429 请求过多）；5xx 服务端错误（500 内部错误、502 网关错误、503 服务不可用）。

**Q：GET 和 POST 区别？** A：GET 参数在 URL 上，有长度限制，可被缓存和收藏，幂等；POST 参数在 body 里，无固定长度限制，不会被缓存，通常不幂等，安全性相对更高（不会明文出现在 URL 和日志里）。

**Q：什么是幂等？哪些方法是幂等的？** A：同一个请求执行一次和多次效果相同。GET、PUT、DELETE 理论上幂等；POST、PATCH 通常不幂等。

### 17.2 TCP/IP（初级 + 中级）

**Q：TCP 三次握手过程？** A：客户端发 SYN；服务端回 SYN+ACK；客户端再发 ACK。目的是双方确认彼此的发送和接收能力都正常，同步初始序列号。

**Q：为什么是三次握手，不是两次或四次？** A：两次无法防止历史失效的连接请求被服务端误认为新连接，浪费资源；三次握手是能确认双方收发能力的最小次数，第三次由客户端发出，也顺带带上业务数据以提高效率。

**Q：TCP 四次挥手过程？** A：主动关闭方发 FIN；被动方回 ACK（此时被动方可能还有数据没发完）；被动方发完数据后再发 FIN；主动方回 ACK，并等待 2MSL 后彻底关闭。

**Q：为什么挥手是四次，而不是三次？** A：因为服务端收到 FIN 时，可能还有数据没发送完，不能立刻关闭，所以 ACK 和 FIN 分开发送，比握手多一次。

**Q：TCP 和 UDP 区别？** A：TCP 面向连接、可靠、有序、有拥塞控制，开销较大；UDP 无连接、不保证可靠和顺序，开销小、延迟低，适合实时音视频、DNS 查询等场景。

**Q：TIME_WAIT 状态是什么？为什么需要它？** A：主动关闭方在发送最后一个 ACK 后进入 TIME_WAIT，等待 2MSL。作用是确保对方收到最终 ACK（否则重传 FIN 时还能响应），并让网络中残留的旧报文失效，避免影响新连接。（中级：服务器 TIME_WAIT 过多会耗尽端口/文件描述符，可通过 SO_REUSEADDR、调整内核参数缓解。）

### 17.3 HTTPS（初级 + 中级）

**Q：HTTP 和 HTTPS 区别？** A：HTTPS 在 HTTP 基础上加了 TLS/SSL 层，对传输内容加密，防止窃听和篡改，并通过证书校验服务端身份。

**Q：HTTPS 握手大致流程？** A：客户端发起请求并携带支持的加密套件；服务端返回证书和选定套件；客户端校验证书合法性，生成对称密钥并用服务端公钥加密发送；双方切换到对称加密通信。简单理解为“先用非对称加密协商出一个对称密钥，再用对称加密传输数据”，因为非对称加密性能开销大。

**Q：为什么不全程用非对称加密？** A：非对称加密计算开销大，性能差；实际做法是用非对称加密安全地交换对称密钥，后续用对称加密传输数据，兼顾安全和性能。

## 18. 操作系统与 Linux 基础（初级）

**Q：进程和线程区别？** A：进程是资源分配的基本单位，拥有独立内存空间；线程是 CPU 调度的基本单位，同一进程内的线程共享内存和资源，切换开销比进程小。

**Q：常用 Linux 命令有哪些？** A：`ps -ef | grep java` 查进程；`top`/`htop` 看资源占用；`jps`/`jstack`/`jmap` 排查 Java 进程；`tail -f xxx.log` 实时看日志；`netstat -anp | grep 端口` 看端口占用；`df -h`/`du -sh` 看磁盘；`grep`/`awk`/`sed` 处理文本。

**Q：如何查看某个端口被哪个进程占用？** A：`lsof -i:端口号` 或 `netstat -anp | grep 端口号`，拿到 PID 后再用 `ps -ef | grep PID` 确认进程详情。

**Q：线上服务器 CPU/内存突然升高，你的排查思路是什么？** A：先用 `top` 定位是哪个进程占用高；如果是 Java 进程，用 `top -Hp PID` 找到占用高的线程号，转成十六进制后在 `jstack` 输出里找对应线程栈，定位具体代码；内存问题类似，用 `jmap -heap`、导出 heap dump 用工具分析。

## 19. 消息队列（初级 + 中高级）

### 19.1 基础概念（初级）

**Q：为什么要用消息队列？** A：解耦生产者和消费者、异步处理提升响应速度、削峰填谷应对突发流量，同时可以做系统间的可靠通信。

**Q：消息队列可能带来什么问题？** A：系统复杂度上升，需要考虑消息丢失、重复消费、顺序性、延迟以及消息积压等问题。

**Q：常见消息队列产品有哪些，怎么选型？** A：Kafka 吞吐量高，适合日志、大数据场景；RabbitMQ 功能丰富、路由灵活，适合业务解耦和延迟队列场景；RocketMQ 兼顾高吞吐和事务消息，国内电商场景常用。

### 19.2 Kafka（初级 + 中级）

**Q：Kafka 的 Topic、Partition、Offset 是什么关系？** A：一个 Topic 可以分为多个 Partition，每个 Partition 内消息严格有序，用 Offset 标记消费到的位置。多个 Partition 让 Topic 可以水平扩展、并行消费。

**Q：Kafka 如何保证消息顺序？** A：Kafka 只保证单个 Partition 内有序，不保证 Topic 全局有序。需要顺序消费的场景（如同一订单的多条消息）应通过相同的 key 让消息落到同一个 Partition。

**Q：Kafka 消费者组是什么？** A：同一个消费者组内的多个消费者共同消费一个 Topic，每个 Partition 在同一时刻只会被组内一个消费者消费，从而实现负载均衡；不同消费者组之间互不影响，可以各自独立消费全量消息。

**Q：Kafka 如何保证消息不丢失？（中级）** A：生产端设置 `acks=all` 并配合重试，确保消息写入足够多副本才算成功；Broker 端合理设置副本数和 `min.insync.replicas`；消费端手动提交 offset，处理完业务逻辑后再提交，避免消息还没处理完就被标记消费。

**Q：Kafka 高吞吐的原因？（中级）** A：顺序写磁盘代替随机写；零拷贝技术减少数据在内核态和用户态之间的拷贝；批量发送和压缩减少网络开销；Partition 分区支持并行读写。

### 19.3 RabbitMQ（初级 + 中级）

**Q：RabbitMQ 核心概念有哪些？** A：Producer 生产者、Exchange 交换机、Queue 队列、Binding 绑定关系、Consumer 消费者。消息先发到 Exchange，再根据 Binding 规则路由到对应 Queue。

**Q：RabbitMQ 常见交换机类型？** A：Direct 精确匹配路由 key；Fanout 广播到所有绑定队列；Topic 按通配符模式匹配路由 key；Headers 按消息头匹配。

**Q：RabbitMQ 如何保证消息不丢失？（中级）** A：生产端开启 Confirm 机制确认消息到达 Broker；队列和消息都设置持久化；消费端使用手动 ACK，业务处理成功后再确认，失败可以 nack 后重试或进死信队列。

### 19.4 可靠性与幂等（中高级）

**Q：如何处理消息重复消费？** A：消息队列一般只能保证“至少一次”投递，重复是正常现象，因此消费端要做幂等：可以用业务唯一键 + 数据库唯一索引、状态机、Redis setnx 或者消费记录表来判断是否已处理过。

**Q：消息积压了怎么办？** A：先确认瓶颈在生产还是消费；短期可以扩容消费者、提高单条处理效率或临时降级非核心逻辑；长期要评估分区/队列数是否足够、消费逻辑是否有慢操作（比如同步调用外部接口）。

**Q：如何设计一个延迟队列？** A：RabbitMQ 可以用死信交换机 + 消息 TTL 实现，或使用延迟插件；Kafka 本身不直接支持延迟消息，通常借助定时轮询、时间轮或专门的延迟消息中间件实现。

## 20. 设计模式（初级 + 中级）

**Q：单例模式怎么实现？Spring 里用到了吗？** A：常见实现有饿汉式、懒汉式（双重检查锁 + volatile）、静态内部类、枚举单例。Spring 容器中的 singleton Bean 本质上就是单例模式的应用，由容器而不是代码自己控制实例创建。

**Q：工厂模式和 Spring 有什么关系？** A：简单工厂/工厂方法用于封装对象创建逻辑，调用方不需要关心具体实现类；Spring 的 BeanFactory/ApplicationContext 本质上就是一个大工厂，根据配置和注解创建并管理 Bean。

**Q：代理模式在 Spring 中的应用？** A：Spring AOP 底层就是代理模式，JDK 动态代理基于接口、CGLIB 基于继承生成子类，在不修改原有代码的情况下增强方法（如加事务、加日志）。

**Q：观察者模式在 Spring 中的应用？** A：Spring 的事件机制（ApplicationEvent、ApplicationListener、`@EventListener`）就是观察者模式，用于业务模块间解耦，比如注册成功后发布事件，多个监听器分别处理发邮件、加积分等逻辑。

**Q：模板方法模式在项目中怎么用？** A：把固定的算法骨架放在父类，具体步骤延迟到子类实现。比如 Spring 的 JdbcTemplate、RestTemplate，都是把连接、异常处理等通用流程封装好，把可变部分交给回调实现。

**Q：策略模式常见使用场景？** A：把一系列可互换的算法各自封装成类，通过统一接口调用。比如根据支付方式（支付宝/微信/银行卡）选择不同的支付策略，可以避免大量 if-else，也便于扩展新策略。

**Q：装饰器模式和代理模式区别？（中级）** A：两者结构类似，都是包一层增强原对象；装饰器强调动态、按需叠加功能（如 Java IO 的 BufferedInputStream 包装 InputStream），代理更强调控制访问、隐藏真实对象（如 AOP 增强、远程代理）。

## 21. 分布式进阶（中高级）

### 21.1 分库分表

**Q：什么时候需要分库分表？** A：单表数据量过大（一般经验值几千万行以上）导致索引效率下降、查询变慢，或者单库连接数/IO 成为瓶颈时，考虑分库分表。分表之前通常先做慢 SQL 优化、加缓存、读写分离等手段。

**Q：垂直拆分和水平拆分区别？** A：垂直拆分按业务/字段拆，比如把用户表和订单表分到不同库，或把大字段单独拆表；水平拆分按数据行拆，把同一张表的数据按规则（如用户 ID 取模、范围）分散到多张表/多个库。

**Q：常见分片键选择策略？** A：按主键或业务 ID 取模/哈希，分布均匀但扩容需要迁移数据；按范围分片，方便扩容但可能出现热点；一致性哈希可以减少扩容时的数据迁移量。

**Q：分库分表后会带来什么新问题？** A：跨库 join、跨库分页、跨库事务变复杂；需要全局唯一 ID；部分聚合查询（如 count、sum）需要在应用层合并结果；常借助 ShardingSphere、MyCat 等中间件降低复杂度。

### 21.2 分布式 ID

**Q：为什么分布式场景不能直接用数据库自增 ID？** A：分库分表后各个库的自增 ID 会冲突，无法保证全局唯一，因此需要专门的分布式 ID 生成方案。

**Q：常见分布式 ID 生成方案？** A：数据库号段模式（批量取一段 ID 缓存到内存里发号）；雪花算法（Snowflake，由时间戳 + 机器 ID + 序列号拼接而成，趋势递增、性能高，但依赖机器时钟）；UUID（简单但无序、存储和索引效率较差，一般不直接做主键）；Redis incr 自增。

**Q：雪花算法有什么问题？** A：依赖系统时钟，如果时钟回拨可能生成重复 ID，需要做时钟回拨检测；机器 ID 需要保证在集群内唯一分配。

### 21.3 CAP 与 BASE（中高级）

**Q：CAP 理论是什么？** A：一致性（Consistency）、可用性（Availability）、分区容错性（Partition tolerance），在发生网络分区时最多只能同时满足其中两个。分布式系统中网络分区难以避免，通常在 C 和 A 之间做取舍。

**Q：BASE 理论是什么？** A：基本可用（Basically Available）、软状态（Soft State）、最终一致性（Eventually Consistent），是对 CAP 中 C 和 A 无法兼得的一种权衡思路，牺牲强一致性换取可用性，多数互联网系统采用这种最终一致性方案。

## 22. 容器化基础（初级）

**Q：Docker 是什么，解决了什么问题？** A：Docker 是容器化技术，把应用及其依赖打包成镜像，保证“开发、测试、生产环境一致”，避免“我本地能跑”的问题，部署和迁移也更方便。

**Q：镜像和容器的关系？** A：镜像是静态的模板（相当于类），容器是镜像运行起来的实例（相当于对象），一个镜像可以启动多个容器。

**Q：Dockerfile 常见指令？** A：`FROM` 指定基础镜像；`WORKDIR` 设置工作目录；`COPY`/`ADD` 拷贝文件；`RUN` 执行构建期命令；`EXPOSE` 声明端口；`ENTRYPOINT`/`CMD` 指定容器启动命令。

**Q：Kubernetes（K8s）大概是做什么的？** A：容器编排平台，负责容器的自动部署、弹性伸缩、故障自愈、服务发现和负载均衡，常见核心概念有 Pod（最小调度单位）、Deployment（管理 Pod 副本和滚动更新）、Service（提供稳定访问入口）。初级面试了解概念即可，不要求深入运维细节。