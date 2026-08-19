
## Q1：ConcurrentHashMap为什么支持多线程扩容？

答案：

> 因为ConcurrentHashMap设计了协助扩容机制，通过transferIndex分配迁移任务，多个线程可以同时迁移不同桶，提高扩容效率。

---

## Q2：为什么扩容完成才替换table？

答案：

> 因为扩容过程中newTable数据是不完整的，如果提前替换table，会导致查询不到未迁移的数据，所以必须等待所有桶迁移完成。

---

## Q3：为什么使用ForwardingNode？

答案：

> ForwardingNode作为迁移标记，让其他线程知道当前桶已经迁移完成，并能够跳转到新的table继续访问。