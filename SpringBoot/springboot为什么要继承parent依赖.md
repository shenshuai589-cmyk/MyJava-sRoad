### 1.parent依赖
```xml
<parent>  
	<groupId>org.springframework.boot</groupId>  
	<artifactId>spring-boot-starter-parent</artifactId>  
	<version>3.5.0</version>  
</parent>
```

### 2.为什么要继承parent依赖

#### 2.1 不继承会怎么样
当我们自己在引入依赖时，不光要写\<groupId>和\<artifactId>，还要写对应的\<version>
那么不同的依赖版本与版本之间存在冲突，那么我们就要花费很大部分的时间和精力在这上面。
#### 2.2 继承了parent依赖之后会怎样

1. parent依赖提前指定好了版本，那么我在引入需要的依赖的时候就不要在写\<version>了