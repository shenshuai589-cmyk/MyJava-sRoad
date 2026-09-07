
> 类似于LinkedList


> LPUSH 向列表左侧插入一个或多个元素

```sql
LPUSH key element [element1, element2,...]
```

> LPOP  移除并返回列表左侧的第一个元素，每页则返回nil

```sql
LPOP key [count]
```


> RPUSH 向列表右侧插入一个或多个元素

```sql
RPUSH key element [element1, element2,...]
```

> RPOP  移除并返回列表右侧的第一个元素，每页则返回nil

```sql
RPOP key [count]
```


> LRANGE  返回一段角标范围内的所有元素 第一个从0开始

```sql
LRANGE key star end
```


> BLPOP 和 BRPOP  没有元素时等待指定时间

```sql
BLPOP key [key...] timeout
```


### 用list构建不同

#### 1. 栈

> 入口和出口在同一边


#### 2.队列

> 入口和出口在不同边


#### 3.阻塞队列

> 入口和出口在不同边
> 出队时采用BLPOP或BRPOP

