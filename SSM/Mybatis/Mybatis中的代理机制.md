
我们在写AccountServiceImpl的时候，会发现每次创建accountDao对象的时候，都要我们手动的去new AccountDaoImpl对象，太过繁琐，那么mybatis为我们提供了一个自动生成的代理机制（getMapper)
![getMapper()](SSM/Mybatis/图片/我的第一个Mybatis/071.png)

==使用代理机制的前提条件==：
1.XxxMapper.xml文件中的namespace必须是Dao包或者Mapper包下接口的全路径
2.id必须是接口里面对应的方法名
![CarMapper.xml](SSM/Mybatis/图片/我的第一个Mybatis/069.png)

