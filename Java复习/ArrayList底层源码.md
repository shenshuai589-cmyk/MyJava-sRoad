
```java
public boolean add(E e) {
    // 1. 传入当前所需的最小容量：当前已有数量 size + 1
    ensureCapacityInternal(size + 1);  
    elementData[size++] = e;
    return true;
}

// 2. 容量检查的中转入口
private void ensureCapacityInternal(int minCapacity) {
    // 算出一个真正需要的最小容量（主要针对懒加载初始化的特殊处理）
    ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
}

// 3. 计算实际所需最小容量
private static int calculateCapacity(Object[] elementData, int minCapacity) {
    // 如果当前数组是无参构造函数创建出来的默认空数组 DEFAULTCAPACITY_EMPTY_ELEMENTDATA
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        // 第一次 add 时，取 默认初始容量(10) 和 minCapacity 中的较大值
        return Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    return minCapacity;
}

// 4. 显式校验容量，决定是否真正发起 grow()
private void ensureExplicitCapacity(int minCapacity) {
    modCount++; // 增加修改次数（用于 fail-fast 机制）

    // 判断逻辑：如果需要的最小容量 > 当前底层数组的实际长度
    if (minCapacity - elementData.length > 0)
        grow(minCapacity); // 触发真正的数据复制与扩容
}



// 2. 计算并增长容量
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    // 关键计算：新容量 = 旧容量 + 旧容量 / 2 （即 1.5 倍）
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    
    // 如果扩容 1.5 倍后还是不够（例如 addAll 批量添加了大数组），就直接用需要的最小容量
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
        
    // 应对超大容量溢出保护
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
        
    // 复制数据到新数组
    elementData = Arrays.copyOf(elementData, newCapacity);
}


```