## 一、YAML的语法格式

> YAML的语法规则
1. 数据结构：YAML支持多种数据类型，包括：
     - 字符串、数字、布尔
     - 数组、list集合
     - map键值对等
2. YAML使用一个空格来分隔属性名和属性值，属性名: 属性值
```yml
name: mail
```
3. YAML使用换行+空格表示层级关系，==不可以使用Tab键==
    - 在properties中，文件这样配置：myapp.name=mail
	- 在yaml中，则这样配置 
```yml
myapp:
  name: mail
```
![yml文件|466](SpringBoot/images/我的第一个项目/002.png)

