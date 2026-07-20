 ### 1.mybatis的缓存

- 一级缓存：将查询的数据存储到SqlSession中
- 二级缓存：将查询到的数据 存储到SqlSessionFactory中
- 第三方缓存：EhCache、MemeCache
==缓存只针对DQL语句，也就是select语句。==

### 2.一级缓存

一级缓存是默认开启的

==一级缓存失效：只要两次查询之间出现了增删改操作，一级缓存失效==

### 3.二级缓存
二级缓存的范围是SqlSessionFactory
使用二级缓存具备的条件：
 - \<setting name="cacheEnable" value="true">全局性开启或关闭所有映射配置文件中已配置的任何缓存，默认就是true
 - 需要使用二级缓存的SqlMapper.xml中添加:\<cache/>
 - 使用二级缓存的实体类对象必须是可序列化的，也就是实体类得实现java.io.Serializable接口
 - SqlSession对象关闭或提交之后，一级缓存中的数据才会写到二级缓存中，此时二级缓存才可用
==一级缓存失效：只要两次查询之间出现了增删改操作，二级缓存失效==

### 3.引入外部缓存(Ehcache)

1.引入依赖
```
<!-- Source: https://mvnrepository.com/artifact/org.ehcache/ehcache -->
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>3.10.8</version>
    <scope>compile</scope>
</dependency>
```

2.新建echcache.xml文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <!--磁盘存储:将缓存中暂时不使用的对象,转移到硬盘,类似于Windows系统的虚拟内存-->
    <diskStore path="e:/ehcache"/>
  
    <!--defaultCache：默认的管理策略-->
    <!--eternal：设定缓存的elements是否永远不过期。如果为true，则缓存的数据始终有效，如果为false那么还要根据timeToIdleSeconds，timeToLiveSeconds判断-->
    <!--maxElementsInMemory：在内存中缓存的element的最大数目-->
    <!--overflowToDisk：如果内存中数据超过内存限制，是否要缓存到磁盘上-->
    <!--diskPersistent：是否在磁盘上持久化。指重启jvm后，数据是否有效。默认为false-->
    <!--timeToIdleSeconds：对象空闲时间(单位：秒)，指对象在多长时间没有被访问就会失效。只对eternal为false的有效。默认值0，表示一直可以访问-->
    <!--timeToLiveSeconds：对象存活时间(单位：秒)，指对象从创建到失效所需要的时间。只对eternal为false的有效。默认值0，表示一直可以访问-->
    <!--memoryStoreEvictionPolicy：缓存的3 种清空策略-->
    <!--FIFO：first in first out (先进先出)-->
    <!--LFU：Less Frequently Used (最少使用).意思是一直以来最少被使用的。缓存的元素有一个hit 属性，hit 值最小的将会被清出缓存-->
    <!--LRU：Least Recently Used(最近最少使用). (ehcache 默认值).缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存-->
    <defaultCache eternal="false" maxElementsInMemory="1000" overflowToDisk="false" diskPersistent="false"
                  timeToIdleSeconds="0" timeToLiveSeconds="600" memoryStoreEvictionPolicy="LRU"/>

</ehcache>

```

3.指定\<cache/>标签类型
```
<cache type="org.mybatis.caches.ehcache.EhcacheCache"
```

