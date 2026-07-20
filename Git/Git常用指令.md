![基础操作指令](Git/image/002.png)

```bash
git add (工作区 --> 暂存区)
git commit (暂存区 --> 仓库)
```

```bash
查看状态：git status

git log[option] 查看提交日志：
    -options
       -  --all 显示所有分支
       -  --pretty=oneline将提交信息显示为一行
       -  --abbrev-commit使得输出的commited更简短
       -  --graph 以图的形式显示
```

```bash
版本回退
git reset --hard commitID

查看已经删除的记录
git reflog 
```

### 分支
```bash
git branch 查看分支
git branch 分支名 创建分支
git checkout 分支名 切换分支
git merge 分支名 合并分支
git branch -d 分支名 删除分支

```
