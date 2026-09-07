
> HSET  添加或修改hash类型key的field的值

```sql
HSET key field value
```



> HGET  获取一个hash类型key的field的值

```sql
HGET key field
```


> HMSET  批量添加多个hash类型key的field的值

```sql
HMSET key f1 v1 f2 v2 f3 v3... 
```


> HMGET  批量获取多个hash类型key的field的值

```sql
HMGET key f1 f2 f3...
```


> HGETALL  获取一个hash类型的key中的所有field和value

```sql
HGETALL key
```


> HKEYS  获取一个hash类型的key中的所有field

```sql
HKEYS key
```


> HVALS  获取一个hash类型的key中的所有value

```sql
HVALS key
```


> HINCRBY  让一个hash类型key的字段值自增并指定步长

```sql
HINCRBY key field increment
```


> HSETNX  添加一个hash类型的key的field值，前提是这个field不存在

```sql
HSETNX key field value
```
