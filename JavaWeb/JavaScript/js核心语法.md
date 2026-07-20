
# 1.js引入方式

### 1.1.内部脚本

JavaScript代码必须位于 ==script== 标签之间，一般把脚本置于==body== 元素的底部，可以改善显示速度

### 1.2.内部脚本

将js代码定义在外部js文件中，然后引入到html页面中

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>
  <!-- <script>
    alert("Hello world");
  </script> -->
  <script src="js-外部引入.js"></script>
</body>
</html>
```

# 2.变量和常量

### 2.1.定义变量(let)

```html
<body>
  <script>
    <!-- js中用let声明变量，用const声明常量 -->
   let a = 10;
   alert(a);
  </script>
</body>
```

### 2.2定义常量(const)

```html
<body>
  <script>
    <!-- js中用let声明变量，用const声明常量 -->
   const pi = 3.14;
   console.log(pi);
  </script>
</body>
```

# 3.数据类型 

### 3.1基本数据类型

- number : 数字(整数、小数、NaN)
- boolean ： 布尔 --> true | false
- null : 对象为空。
- undefined : 当声明的变量未初始化时，该变量的默认值是undefined
- string : 字符串，单引号、双引号、反引号都可以，推荐使用单引号

```html
<body>
  <script>
    alert(typeof 10);
    alert(typeof 1.5);
    alert(typeof true);
    alert(typeof false);
    alert(typeof "hello");
    alert(typeof 'hi');
    alert(typeof 'hi');
    alert(typeof `javaScript`);
    alert(typeof null); // object
    alert(typeof undefined);
  </script>
</body>
```

### 3.2模板字符串

反引号``在内容拼接变量时，使用${}包住变量

```html
<body>
  <script>
    let name = 'Tom';
    let age = 18;
    console.log('大家好，我是新入职的'+name+',今年'+age+'岁了。');
    console.log(`大家好，我是新入职的${name},今年${age}岁了。`);
  </script>
</body>
```

# 4.函数

```html
定义：javaScript中的函数调用通过 function 关键字进行定义-->基本语法

function functionName(参数1，参数2...){
	要执行的代码
}
  <script>
    function sayHello(a,b){
      return a+b;
    }
    let result = sayHello(10,20);
    alert(result);
  </script>
```

# 5.自定义对象

### 5.1对象的格式

```html
    <!-- 格式：
     let 对象名 = {
        属性名1：属性值1,
        属性名2：属性值2,
    方法名1 ：function(形参列表){
          //方法体
        }
  }
  -->
<script>
    let user = {
      name:'Tom',
      age:20,
      gender:'男',
      sing:function(){
        alert(this.name+'唱着最炫民族风');
      }
    }
    // 调用对象属性和方法
    alert(user.age)
    user.sing()
  </script> 
```

### 5.2.JSON对象

#### 5.2.1什么是JSON

JSON是一种轻量级的数据交换格式，用于前后端交互、接口传参、配置文件、存储数据等.

#### 5.2.2.JSON的基本格式

```
{  
"name": "张三",  
"age": 18,  
"city": "南京"  
}
特点：
- 键必须使用双引号
- 值可以是字符串、数字、布尔、数组、对象等
```

#### 5.2.3.js对象和json对象之间的转换

1. js转json : JSON.stringify(js对象名);

```html
<scrpit>
    // js和转json
    let person = {
      name:'kris',
      age:22,
      gender:"男",
      heigth:177,
      weight:130,
      sing:function(){
        alert(this.name+"在唱歌");
      }
    }
    alert(JSON.stringify(person));
    <!--打印结果为: 
    {"name":"kris","age":22,"gender":"男","heigth":177,"weight":130}
    -->
 </script>
```

2. json 转为 js ： ==JSON.parse(json对象)==

```html
<script>
    let personJson = `{
      "name":"kris",
      "age":20,
      "gender":"男"
    }`;
    alert(personJson);
    alert(JSON.parse(personJson).name);
  </script>

```