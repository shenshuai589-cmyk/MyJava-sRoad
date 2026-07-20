
JVM的内存结构被分成了几个部分：
 - Method Area 方法区
 - Heap 堆
 - JVM Stacks 虚拟机栈
 - PC Register 程序计数器
 - Native Method Stacks 本地方法栈
### 1. PC Register 程序计数器 

程序计数器的作用：记住下一条jvm指令的执行地址

二进制字节码通过==解释器==被解释成==机器码==，然后cpu执行机器码

