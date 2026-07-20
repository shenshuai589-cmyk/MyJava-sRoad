## 1. String

### 1.1 String类型的定义

```
string 是 redis 最基本的类型，你可以理解成与 Memcached 一模一样的类型，一个 key 对应一个 value。
string 类型是二进制安全的。意思是 redis 的 string 可以包含任何数据，比如jpg图片或者序列化的对象。

string 类型是 Redis 最基本的数据类型，string 类型的值最大能存储 512MB。
```

### 1.2 String类型的常见命令

```
1. set（新建键值对）: 
   set key1 value1
   
2. mset（新建多对键值对）：
   mset key1 value1 key2 value2
   
3. get（获取key对应的value）:
   get chan 
   
4. mget（获取多个key对应的value）：
   mget key1 key2 key3

5. incr（让整型的key对应的value值自增1）
   incr age
   
6. incrby（让一个整型的key对应的value自增指定步长  可以为正也可以为负）:
   incrby age 2   ----> 自增2
   
7. decr（将key对应的value的值自减1）：
   decr age
   
8. decrby（将key对应的value的值自减指定步长 可以为正也可以为负）：
   decrby age 2    ----> 自减2
   
9. incrbyfloat（让一个浮点型的数字对应的value自增指定的步长）：
   incrbyfloat score 1.5
 
10.  setnx（新增一对键值对，如果该key存在则不新增，不会改变已存在的值，key不存在则会新增）：
    setnx name lan
    
11 .setex（新增一对键值对并为其设置有效期）:
    setex name lan 100
```


## 2.hash

### 2.1. Hash类型的定义
```
Redis hash 是一个键值(key=>value)对集合，类似于一个小型的 NoSQL 数据库。

Redis hash 是一个 string 类型的 field 和 value 的映射表，hash 特别适合用于存储对象。

每个哈希最多可以存储 2^32 - 1 个键值对。
```

### 2.2 hash类型的常见命令

```
1. hset key field value :添加或修改hash类型key的field的值
   hset heima:user:1 name chan
   hset heima:user:2 name lan
   
2. hget key field:获取一个hash类型key的field的值
   hget heima:user:1 age
   
3. hmset key field1 value1 field2 value2:批量添加或修改hash类型key的field的值
   hmset heima:user:1 name chan age 24 sex 1
   
4. hmget key field1 field2 field3:批量获取hash类型key的field的值
   hmget heima:user:1 name age sex
   
5. hgetall:获取一个hash类型的key中所有的field和value
   hgetall heima:user:1

6. hkeys: 获取一个hash类型的key中所有的field
   hkeys heima:user:1

7. hvals:获取一个hash类型的key中所有的value
   hvals heima:user:1

8. hincrby: 让一个hash类型key的字段value自增并指定步长
   hincrby heima:user:1 age 2
   
9. hsetnx:添加一个hash类型的key的field值，前提是这个field不存在，否则不执行
   hsetnx: heima:user:1 addr nanjing

  
```


## 3.List

### 3.1 List类型的定义

```txt
Redis列表是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）
一个列表最多可以包含 232 - 1 个元素 (4294967295, 每个列表超过40亿个元素)。
```

### 3.2 List的核心特点

```
双端操作：你可以非常高效地在列表的 **左侧（Left）** 或 **右侧（Right）** 插入或弹出元素。  

有序性：元素进入的先后顺序会被严格记录，先入后出或先入先出完全由你控制。
  
元素可重复：同一个 List 里允许放入一模一样的数据。
  
极佳的两头性能：因为是链表，所以在头部或尾部插入/删除元素的速度极快（时间复杂度是 $O(1)$）；但如果要通过下标去中间查找或修改元素，速度就会变慢（时间复杂度是 $O(N)$）。
```
### 3.2 List类型的常见命令

```
1. lpush key value1 value2（从左侧（头部）塞入数据）: 在key的左边添加value1，再在value1左边添加value2
   
2. rpush key value1 value2：（从右侧（尾部）塞入数据）：在key的右边添加value1，再在value1右边添加value2
   
3. lpop key value（从左侧（头部）弹出一个元素，并删除） 

4. rpop key：从右侧（尾部）弹出一个元素,并删除
   
5.查看元素：lrange key start stop：查看指定范围内的元素（最常用连招：lrange key 0 -1 代表查看所有人）

```

## 4. Set类型

### 4.1 Set集合常用的命令

```redis
1. SADD key member ...:向set中添加一个或多个元素
2. SREM key member ...:移除set中指定的元素
3. SCARD key :返回set中元素的个数
4. SISMEMBER key member:判断一个元素是否存在于set集合中
5.SMEMBERS：获取set中所有的元素

```

```
# 交集、并集、差集
SINTER key1 key2...:求key1与key2的交集
SDIFF key1 key2...:求key1与key2的差集
SUNION key1 key2...:求key1与key2的并集
```

## 5. SortedSet类型

### 5.1 SortedSet具备的特性

```
可排序
元素不重复
查询速度快
```

### 5.2 SortedSet常用命令
```Redis
1.zadd key score member:添加一个或多个元素到sorted set,如果已经存在则更新其score值
2.zrem key member: 删除sorted set中的一个指定元素
3.zscore key member:获取sorted srt中指定元素的score值
4.zrank key member:获取sorted set中指定元素的排名
5.zcard key:获取sorted set中的元素个数
6.zcount key min max:统计score值在给定范围内的所有元素的个数
7.zincrby key increment member:让sorted set中的指定元素自增，步长为指定的increment值
8.zrange key min max:按照score排序后，获取指定排名范围内的元素
9.ZRANGEBYSCORE key min max 按给定的分数区间从小到大取出元素
所有排名默认都是升序，如果要降序则在命令是z后面添加rev即可
```