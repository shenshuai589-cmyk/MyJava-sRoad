
# 1.确保依赖引入

只要pom文件中引入的是数据库相关的starter，那么Spring Boot 就会自动配置好 `PlatformTransactionManager`

# 2.在方法或类上添加 `@Transactional`

从 Spring Boot 2.x 开始，启动类上**不再需要**显式添加 `@EnableTransactionManagement` 注解，只要引入了相关依赖，默认就会开启。

```java
@Service
public class UserService {

@Autowired
private UserRepository userRepository;

  
@Transactional // 开启事务
public void registerUser(User user) {
// 1. 保存用户
userRepository.save(user);
// 2. 模拟异常：如果这里抛出 RuntimeException，上面保存的用户会回滚
	if (user.getName().equals("error")) {
	
	throw new RuntimeException("注册失败，触发回滚");
	
	}
}

}
```

# 3. `@Transactional` 的核心参数

## ① 事务传播行为（Propagation）

传播行为定义了**当一个事务方法被另一个事务方法调用时，该如何运行**。常用的有以下几种:
- required : 如果当前有事务，就加入；没有就新建一个。
- REQUIRES_NEW : 无论当前有没有事务，都新建一个事务，并将当前事务挂起。
- NESTED : 如果当前有事务，则在嵌套事务内执行（外层回滚内层必回滚，内层回滚外层可不回滚）

## ② 事务隔离级别（Isolation）

用于解决多线程并发访问数据库时带来的“脏读、不可重复读、幻读”问题。通常直接**使用数据库默认的隔离级别**即可

## ③ 回滚规则（Rollback Rules）

**默认机制**：Spring 事务**默认只在遇到 `RuntimeException`（运行时异常）和 `Error` 时才会回滚**。

显式指定 `rollbackFor = Exception.class`

```java
@Transactional(rollbackFor = Exception.class) // 任何异常都回滚
```
