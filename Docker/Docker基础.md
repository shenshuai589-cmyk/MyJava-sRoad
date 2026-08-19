
## 1.什么是Docker

Docker 是一个**容器化技术**。

简单理解：

> Docker 可以把你的应用程序和运行环境一起打包，保证在任何机器上运行效果一致。

比如：

你的 Spring Boot 项目：

```
Spring Boot项目
    |
    |-- JDK 17
    |-- Maven依赖
    |-- MySQL
    |-- Redis
    |-- Linux环境配置
```

以前部署：

```
服务器A:
安装JDK
安装MySQL
配置环境变量
上传jar

服务器B:
重新安装一遍
可能版本不同
可能启动失败
```

Docker：

```
Docker镜像

包含:
    JDK
    Spring Boot jar
    配置文件

直接运行
```


## 2. Docker核心概念

Docker主要有：

```
镜像 Image
容器 Container
仓库 Repository
```

# 2.1 镜像 Image

镜像：

> 一个只读的应用模板

例如：

```
mysql:8.0

redis:7

nginx:latest

jdk17
```

类似：

Windows系统安装包：

```
.exe文件
```

Docker：

```
image镜像
```

查看镜像：

```
docker images
```

例如：

```
REPOSITORY    TAG
mysql         8.0
redis         7
```

---

# 2.2 容器 Container

容器：

> 镜像运行之后的实例

关系：

```
镜像
 |
 | docker run
 ↓
容器
```

例如：

镜像：

```
mysql:8.0
```

运行：

```
docker run mysql
```

产生：

```
mysql容器
```

查看容器：

```
docker ps
```

查看所有：

```
docker ps -a
```

---

# 2.3 Docker仓库

保存镜像的地方。

类似：

GitHub保存代码。

Docker Hub：

[Docker Hub](https://hub.docker.com?utm_source=chatgpt.com)

例如：

下载mysql：

```
docker pull mysql:8.0
```

实际就是：

```
Docker Hub
      |
      |
 下载
      ↓
本地镜像
```

---

# 3. Docker和虚拟机区别（面试高频）

## 传统虚拟机

例如：

VMware

结构：

```
电脑硬件

 ↓

宿主机操作系统

 ↓

VMware

 ↓

虚拟机OS

 ↓

应用
```

每个虚拟机都有完整系统：

```
Linux
Linux
Linux
```

资源消耗大。

---

## Docker

结构：

```
电脑硬件

 ↓

宿主机OS

 ↓

Docker

 ↓

容器

 ↓

应用
```

多个容器共享：

```
宿主机Linux内核
```

所以：

||虚拟机|Docker|
|---|---|---|
|启动速度|分钟|秒级|
|资源占用|大|小|
|隔离性|强|较强|
|部署速度|慢|快|