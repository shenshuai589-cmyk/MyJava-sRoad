
> 类似于java的HashSet


> SADD 向set中添加一个或多个元素

```sql
SADD key member ...
```


> SREM  移除set中指定的元素

```sql
SREM key member ...
```


> SCARD 返回set中元素的个数

```sql
SCARD key
```


> SISMEMBER  判断一个元素是否存在于set中

```sql
SISMEMBER key member
```


> SMEMBERS 获取set中的所有元素

```sql
SMEMBERS key
```


> SINTER  求不同set之间的交集

```sql
SINTER key1 key2...
```

>SDIFF  求不同set之间的差集

```sql
SDIFF key1 key2...
```


> SUNION  求不同set之间的合集

```sql
SUNION key1 key2 ...
```