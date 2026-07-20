因为有些有重复的代码，那么我们可以通过util命名空间对其进行统一配置

第一步：修改xml文件
添加两行数据：
```
xmlns:util="http://www.springframework.org/schema/util"

http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
```

```xml
<beans xmlns="http://www.springframework.org/schema/beans"  
       xmlns:util="http://www.springframework.org/schema/util"  
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd  
                           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">
```
![xml的配置|581](SSM/Spring/image/020.png)
从上图可知，两个bean的重复配置有点多，那么我们通过util对其改善
![改善后的代码|556](SSM/Spring/image/022.png)
```
从图上可以发现，代码变少了，并且util还可以服用

```
