启动redis :sudo docker start my-redis
开启redis的shell：sudo docker exec -it my-redis redis-cli

```txt
1. set（新建键值对）: 
   set key1 value1
   
   mset（新建多对键值对）
   mset key1 value1 key2 value2 key3 value3
   
2. keys（查看符合模板的所以key）： 
   key * --->表示查看所有
   key a* ---> 表示查看所有以a开头的key
   key a? ---> 表示查看以a开头的并a后只有一个占位符的key
   
3. del（删除一个指定的key）:
   del age
   del age name sex（删除多个key）
   
4. exists（判断key是否存在）:
   exists age
   exists age name sex
   
5. expire（给key一个存活的期限，单位是秒）:
   expire age --->表示永久存活
   expire name 10 ---->表示存活10秒

6. ttl（查看一个key剩余的有效期）:
   ttl age 
   
7. get（获取key对应的value）:
   get chan 
   
   mget（获取多个value）:
   mget name age sex
   
```