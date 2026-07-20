
#### 1.编写多个配置文件
![配置文件1](SpringBoot/images/我的第一个项目/009.png)
![配置文件1](SpringBoot/images/我的第一个项目/010.png)
![配置文件1](SpringBoot/images/我的第一个项目/011.png)
#### 2.配置多环境切换的配置信息
application.properties:
```xml
spring.profiles.active=dev

注意：等于号后面的信息是根据文件名而甜填写的，例如
文件名：application-dev.properties，填写的则是dev
文件名：application-test.properties，填写的则是test
文件名：application-prod.properties，填写的则是prod

```