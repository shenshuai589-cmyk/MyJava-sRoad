

> 类似于TreeSet



> ZADD 添加或修改Sorted Set中的一个成员，并设置score

```
ZADD key score member
```

> ZRANGE 按照score从低到高获取Sorted Set中的成员

```
ZRANGE key start stop
```

> ZREVRANGE 按照score从高到低获取Sorted Set中的成员

```
ZREVRANGE key start stop
```

> ZSCORE 根据member获取对应的score

```
ZSCORE key member
```

> ZRANK 获取成员按照score从低到高的排名，排名从0开始

```
ZRANK key member
```

> ZREVRANK 获取成员按照score从高到低的排名，排名从0开始

```
ZREVRANK key member
```

> ZREM 删除Sorted Set中的指定成员

```
ZREM key member
```

> ZRANGEBYSCORE 按照score范围获取Sorted Set中的成员

```
ZRANGEBYSCORE key min max
```

> ZCOUNT 统计score在指定范围内的成员数量

```
ZCOUNT key min max
```

> ZCARD 获取Sorted Set中的成员总数量

```
ZCARD key
```

> WITHSCORES 在查询成员时同时显示对应的score

```
ZRANGE key start stop WITHSCORES
```