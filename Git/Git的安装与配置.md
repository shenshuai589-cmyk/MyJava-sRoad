
### 1.在官网下载
[http://git-scm.com/downloads](http://git-scm.com/downloads)

### 2.配置相关信息

#### 2.1基础配置

**1.设置用户名**
```bash
git config --global user.name "shenshuai589-cmy"
```

**2.设置邮箱**
```bash
git config --global user.email "shenshuai589@email.com"
```
**3.查看是否配置成功**
```bash
git config --global init.defaultBranch main
```

#### 2.2推荐配置

**1.默认分支改为main**
```bash
git config --global init.defaultBranch main
```

**2.Windows/Linux换行问题**
```bash
git config --global core.autocrlf true
```

**3.彩色输出**
```bash
git config --global color.ui true
```

### 3.SSH

#### 3.1生成SSH key

```bash
ssh-keygen -t rsa -b 4096 -C "shenshuai589@email.com"
```

### 3.2启动ssh agent
```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
```

#### 3.3查看公钥
```bash
cat ~/.ssh/id_rsa.pub
```

#### 3.4测试连接
```bash
ssh -T git@github.com
```



### 4.1初始化本地仓库

新建一个文件夹，右击打开文件夹并在该文件夹中右键点击`Open Git Bash here`

输入：
```bash
git init
```

![初始化本地仓库](Git/image/001.png)
