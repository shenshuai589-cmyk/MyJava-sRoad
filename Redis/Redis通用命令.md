

> 启动redis

```sql
redis-cli 
```

```sql
启动之后没加密码是不能使用命令的
auth password;
```


> KEYS 查看符合模板的所有key

```sql
keys *
```


> DEL 删除一个指定的key

```sql
del key
```


> EXISTS 判断key是否存在

```sql
exists key
```


> EXPIRE 给一个key设置有效期，有效期到期时，该key会被自动删除

```sql
EXPIRE key second
```

> TTL 查看一个key的剩余有效期

```sql
TTL key
```