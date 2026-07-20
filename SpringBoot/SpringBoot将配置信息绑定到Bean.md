# 1.绑定到简单Bean

**1.创建配置文件**

![配置文件](SpringBoot/images/我的第一个项目/012.png)

**2.绑定到bean**

```java
package com.powernode.springboot.config;  
  
import org.springframework.boot.context.properties.ConfigurationProperties;  
import org.springframework.stereotype.Component;  
  
//1.纳入ioc管理  
@Component  
//2.将配置文件中的属性值一次性绑定到bean对象的属性上  
@ConfigurationProperties(prefix = "myapp") //指定要绑定的属性的前缀  
public class AppConfig {  
    // 注意：配置文件中的属性名要和bean对象的属性名要保持一致  
    private String username;  
    private String password;  
    private Integer age;  
    private Boolean gender;  
  
// 3.底层实现给对象属性赋值的时候，调用了setter方法，因此必须保证每个属性提供setter方法
    public void setUsername(String username) {  
        this.username = username;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }  
  
    public void setAge(Integer age) {  
        this.age = age;  
    }  
  
    public void setGender(Boolean gender) {  
        this.gender = gender;  
    }  
}
```

---

# 2.绑定到嵌套bean

**1.编写pojo**
1. Address.java
![Address|503](SpringBoot/images/我的第一个项目/013.png)
2. User.java
![User|520](SpringBoot/images/我的第一个项目/014.png)

**在application.properties文件中编写配置**
```xml
spring.application.name=springboot3-10-config-bind-to-bean  
  
app.xyz.name=lucy  
app.xyz.addr.city=beijing  
app.xyz.addr.street=chaoyang
```
> 注意因为addr属性的返回值是一个Address的pojo对象，所以要将Address中的属性写完整

---
# 3.将配置信息绑定到bean的第三种方式

在绑定bean的前提是我们使用@Component或@Configuration注解将pojo对象放入IoC容器当中
那么在不使用这些注解的时候，我们怎样才可以将配置信息绑定到bean呢：
 - 在主入口程序种使用@EnableConfigurationProperties注解
```java

@EnableConfigurationProperties(User.class)  
@SpringBootApplication  
public class Springboot310ConfigBindToBeanApplication {  
  
    public static void main(String[] args) {  
        SpringApplication.run(Springboot310ConfigBindToBeanApplication.class, args);  
    }   
}
```
![使用EnableConfigurationProperties注解](SpringBoot/images/我的第一个项目/015.png)

---
# 4.将配置信息绑定到bean的第四种方式

> 在主入口程序中添加@ConfigurationPropertiesScan注解
```java
package com.powernode.springboot;  
  
import com.powernode.springboot.bean.User;  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;  
import org.springframework.boot.context.properties.EnableConfigurationProperties;  
  
@ConfigurationPropertiesScan(basePackages = "com.powernode.springboot.bean")  
@SpringBootApplication  
  
public class Springboot310ConfigBindToBeanApplication {  
  
    public static void main(String[] args) {  
        SpringApplication.run(Springboot310ConfigBindToBeanApplication.class, args);  
    }  
  
}
```
运行结果：
![运行结果](SpringBoot/images/我的第一个项目/016.png)

---
# 5.配置List、ArrayList、Map绑定到bean

## 5.1 绑定到properties文件中

**1.编写pojo对象**
```java
package com.powernode.springboot.bean;  
  
import org.springframework.boot.context.properties.ConfigurationProperties;  
  
import java.util.Arrays;  
import java.util.List;  
import java.util.Map;  
  
@ConfigurationProperties(prefix = "app2")  
public class AppBean {  
//    数组-简单类型  
    private String[] names;  
//    数组-非简单类型  
    private Address[] addrArray;  
//    集合-非简单类型  
    private List<Address>  addrList;  
//    Map-非简单类型  
    private Map<String, Address> addrMap;  
  
    public void setNames(String[] names) {  
        this.names = names;  
    }  
  
    public void setAddrArray(Address[] addrArray) {  
        this.addrArray = addrArray;  
    }  
  
    public void setAddrList(List<Address> addrList) {  
        this.addrList = addrList;  
    }  
  
    public void setAddrMap(Map<String, Address> addrMap) {  
        this.addrMap = addrMap;  
    }  
  
    @Override  
    public String toString() {  
        return "AppBean{" +  
                "names=" + Arrays.toString(names) +  
                ", addrArray=" + Arrays.toString(addrArray) +  
                ", addrList=" + addrList +  
                ", addrMap=" + addrMap +  
                '}';  
    }  
}
```

**2.在主入口程序中添加该类的@EnableConfigurationProperties**
```java
  
@EnableConfigurationProperties({User.class, AppBean.class})  
//@ConfigurationPropertiesScan(basePackages = "com.powernode.springboot.bean")  
@SpringBootApplication  
  
public class Springboot310ConfigBindToBeanApplication {  
  
    public static void main(String[] args) {  
        SpringApplication.run(Springboot310ConfigBindToBeanApplication.class, args);  
    }  
}

```

**3.编写相应的properties配置信息**
```xml
app2.names[0]=jack  
app2.names[1]=rose  
  
app2.addrArray[0].city=beijing  
app2.addrArray[0].street=chaoyang  
app2.addrArray[1].city=shanghai  
app2.addrArray[1].street=pudong  
  
app2.addrList[0].city=beijing_list  
app2.addrList[0].street=chaoyang_list  
app2.addrList[1].city=shanghai_list  
app2.addrList[1].street=pudong_list  
  
app2.addrMap.key1.city=beijing_map  
app2.addrMap.key1.street=chaoyang_map  
app2.addrMap.key2.city=shanghai_map  
app2.addrMap.key2.street=pudong_map
```

注意：
```txt
当返回值类型是Map是，app2.addrMap.key1.city=beijing_map，key1表示的是Map的键,那么每个 key 对应一个对象，包含 `city` 和 `street` 两个属性
```

## 5.2 绑定到yml文件中

![properties中的配置|388](SpringBoot/images/我的第一个项目/017.png)
将上图中application.properties中的配置转成application.yml形式：
```yml
app2:  
  names:  
    - jack  
    - rose  
  addrArray:  
    - city: beijing  
      street: chaoyang  
    - city: shanghai  
      street: pudong  
  addrList:  
    - city: beijing_list2  
      street: chaoyang_list2  
    - city: shanghai_list2  
      street: pudong_list2  
  addrMap:  
    key1:  
      city: beijing_map2  
      street: chaoyang_map2  
    key2:  
      city: shanghai_map2  
      street: pudong_map2
```

# 6.将配置绑定到第三方库

如果现在我们使用的是别人给我们的代码，那么我们不可以修改里面的代码，也就意味着不可以在里面添加@ConfigurationProperties注解，那么此时我们该怎么作呢？

**1.新定义一个config类**
```java
package com.powernode.springboot.config;  
  
import com.powernode.springboot.bean.Address;  
import org.springframework.boot.context.properties.ConfigurationProperties;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
  
@Configuration  
public class AddressConfig {  
  
    @Bean  
    @ConfigurationProperties(prefix = "other.configs")  
    public Address address(){  
        return new Address();  
    }  
}
```
在里面添加@Configuration注解、@Bean注解以及@ConfigurationProperties

**2.编写yml配置**

```yml
other:  
  configs:  
    city: beijing_other  
    street: chaoyang_other
```

**编写测试**
```java
@Autowired  
private Address  address;  
@Test  
void test04(){  
    System.out.println(address);  
}
```
![test运行结果](SpringBoot/images/我的第一个项目/018.png)

# 7.指定数据来源

> 当我们要引入外部配置文件时，那我们就需要@PropertySource注解

**1.编写外部配置文件**
![路径](SpringBoot/images/我的第一个项目/019.png)
该配置文件的路径
```yml
group.name=IT  
group.leader=LaoDu  
group.count=20
```

**2.编写要传输的bean**

> 指定路径： @PropertySource("classpath:/config/a/b/group-info.properties") 
```java
package com.powernode.springboot.bean;  
  
import org.springframework.boot.context.properties.ConfigurationProperties;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.context.annotation.PropertySource;  
  
@Configuration  
@ConfigurationProperties(prefix = "group")  
@PropertySource("classpath:/config/a/b/group-info.properties")  
public class Group {  
  
    private String name;  
    private String leader;  
    private Integer count;  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public void setLeader(String leader) {  
        this.leader = leader;  
    }  
  
    public void setCount(Integer count) {  
        this.count = count;  
    }  
  
    @Override  
    public String toString() {  
        return "Group{" +  
                "name='" + name + '\'' +  
                ", leader='" + leader + '\'' +  
                ", count=" + count +  
                '}';  
    }  
}
```