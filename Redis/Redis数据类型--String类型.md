

>  SET 添加或修改已经存在的一个String类型的键值对

```sql
SET key value
```


>  GET 根据key获取String类型的value

```sql
GET key
```


>  MSET 批量添加多个String类型的键值对

```sql
MSET k1 v1 k2 v2 k3 v3 ....
```


>  MGET  根据多个key获取多个String类型的value

```sql
MGET k1 k2 k3...
```

>  INCR  让一个整型的key自增1

```sql
INCR key
```

> INCRBY  让一个整型的key自增并指定步长

```sql
INCRBY key increment
```


 > INCRBYFLOAT  让一个浮点类型的数字自增并指定步长
 
```sql

INCRBYFLOAT key increment
```


>  SETNX   Set the value of a key ,only if the key does not exist

```sql
SETNX key value
```


>  SETEX  Sets the string value and expiration time of a key

```sql
SETEX key seconds value
```