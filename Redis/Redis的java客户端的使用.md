### 1.引入redis依赖

```xml
<dependency>  
    <groupId>redis.clients</groupId>  
    <artifactId>jedis</artifactId>  
    <version>7.0.0</version>  
</dependency>
```

### 2. 新建连接
```java
public class JedisTest{  
    private Jedis jedis;  
  
    @BeforeEach  
    public void setUp(){  
        jedis = new Jedis("192.168.80.128",6379);  
        jedis.select(0);  
  
  
    }  
  
    @Test  
    void testJedis(){  
        String result = jedis.set("name", "chan");  
        System.out.println(result);  
  
        String name = jedis.get("name");  
        System.out.println(name);  
  
    }  
    @AfterEach  
    void tearDown(){  
        if(jedis != null){  
            jedis.close();  
        }  
    }  
  
}
```


### 3.使用Jedi连接池

```java
public class JedisConnectionFactory {  
  
    private static final JedisPool jedisPool;  
  
    static{  
        JedisPoolConfig config = new JedisPoolConfig();  
  
        config.setMaxTotal(10);  
  
        jedisPool = new JedisPool(config,"192.168.80.128",6379,1000,"123456");  
    }  
  
    public static Jedis getJedis(){  
        return jedisPool.getResource();  
    }  
}
```

测试代码
```java
@BeforeEach  
public void setUp(){  
    JedisConnectionFactory.getJedis();  
    jedis.select(0);  
  
  
}  
  
@Test  
void testJedis(){  
    String result = jedis.set("name", "chan");  
    System.out.println(result);  
  
    String name = jedis.get("name");  
    System.out.println(name);  
  
}
@AfterEach  
void tearDown(){  
    if(jedis != null){  
        jedis.close();  
    }  
}
```
