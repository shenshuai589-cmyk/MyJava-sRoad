
```java
1. 默认容量
    private static final int DEFAULT_CAPACITY = 10;
```

```java
2. 用户明确指定容量为0时使用的空数组。
private static final Object[] EMPTY_ELEMENTDATA = {};
```


```java

private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}; 
```

```java

动态扩容的 Object 数组。
transient Object[] elementData;
```

```java
private int size;
```