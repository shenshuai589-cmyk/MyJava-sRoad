## **1. 什么是内容协商？**

内容协商是 **客户端与服务端就响应数据格式达成一致的机制**。

- 目的是让同一个接口可以返回 **JSON / XML / HTML / PDF / 图片等多种格式**
- 根据 **客户端请求** 中的信息，服务端选择合适的响应类型

### 常用场景

- REST API 根据 `Accept` 头返回 JSON 或 XML
- 浏览器访问返回 HTML，API 客户端访问返回 JSON

## **2. 内容协商原理**

Spring MVC 提供内容协商机制，核心组件是 **`ContentNegotiationManager`**。

Spring 会根据以下顺序判断返回类型：

1. **请求的 `Accept` Header**
    - 例如：`Accept: application/json` → 返回 JSON
    - `Accept: application/xml` → 返回 XML
2. **URL 后缀（可选）**
    - 例如：`/users.json` 或 `/users.xml`
3. **请求参数（可选）**
    - 例如：`/users?format=json`
4. **默认类型**
    - 如果客户端没指定，使用 `spring.mvc.contentnegotiation.favor-path-extension` 配置的默认格式（一般是 JSON）

## **3. Spring Boot 默认行为**

Spring Boot 自动配置了 **`MappingJackson2HttpMessageConverter`** 和 **`Jaxb2RootElementHttpMessageConverter`**：

![Spring Boot 默认行为](SpringBoot/images/我的第一个项目/028.png)
默认情况下：

- **JSON 优先**，XML 需加依赖 `spring-boot-starter-xml`
- 浏览器直接访问 API 返回 JSON

## **4. 配置内容协商**

**方法 1：使用 `application.properties` / `application.yml`**
```yml
spring:
  mvc:
    contentnegotiation:
      favor-path-extension: true  # 是否支持URL后缀
      favor-parameter: true       # 是否支持请求参数
      parameter-name: format      # 请求参数名
      ignore-accept-header: false # 是否忽略Accept头
      default-content-type: application/json
```

 **方法 2：Java 配置**
 ```java
 @Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorPathExtension(true)
            .favorParameter(true)
            .parameterName("format")
            .ignoreAcceptHeader(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
 ```

注意：
springboot默认转换的是json格式，如果想转换成xml需要引入以下依赖
```xml
<dependency>  
    <groupId>com.fasterxml.jackson.dataformat</groupId>  
    <artifactId>jackson-dataformat-xml</artifactId>  
</dependency>
```

除此自外，还要在pojo对象上加上@JacksonXmlRootElement注解
![@JacksonXmlRootElement|678](SpringBoot/images/我的第一个项目/029.png)
运行结果
![curl](SpringBoot/images/我的第一个项目/030.png)
