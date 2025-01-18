# Plantastic

## 注意事项

1、 已经下载好Spigot依赖的同学，添加了lombok的Maven依赖，在Reload Maven前，请在pom树中注释掉以下行，才能完成lombok的下载

2、 更新了fastjson、jedis的依赖 注意注释掉下述依赖后自动下载，打包后体积会变大，然后可能PlugmanX重载后报错，就需要重启服务器

```
<repository>
		<id>spigot-repo</id>
		<url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
</repository>
```

## 环境搭建

1、下载并安装IntelliJ IDEA Community Edition 2023.2.1

2、下载apache-maven-3.9.9-bin.zip，这个在群文件有，然后参考这个博客教程进行配置maven，eclipse创建项目那一步不用。

https://blog.csdn.net/weixin_43853855/article/details/117073100

3、在IDEA中配置MAVEN，文件->设置 搜索Maven 修改三个路径，参考这个博客

https://blog.csdn.net/m0_46816573/article/details/119298361

4、打开IDEA,文件->新建->来自版本控制的项目 在第一行输入本项目的地址

https://git.nwafu.edu.cn/qingshufan/plantastic-spigot.git

第二行选择你存放项目的路径，一个空目录

5、创建完项目后，有提示Maven加载就选择加载，然后等待Maven构建成功，在右侧的Maven中的生命周期选择package命令，如果提示BUILD
SUCCESS 则成功

6、完成Redis的配置，参考以下教程进行配置Redis，很容易，Redis的压缩包放到群文件了
https://www.runoob.com/redis/redis-install.html

## 相关配置参考

### 紫一号.yml

```
state: 
#顺序不可改变
- '正常生长'
- '死亡'
- '缺水'
- '营养不良'
- '倒伏'
- '患病'
- '水中毒'
- '盐碱'
name: '紫一号'
lore: '紫一号初级种子'
grow:
  info:
    种子: 
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '20-30'
      nutrition: '20-30'
    出苗:
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '30-40'
      nutrition: '20-30'
    幼株:
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '30-40'
      nutrition: '20-30'
    生长期:
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '50-80'
      nutrition: '50-80'
    开花育种期:
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '50-80'
      nutrition: '30-50'
    成熟期:
      fallChance: 0.9
      diseaseChance: 0.8
      bottle: 5
      powder: 5
      water: '50-80'
      nutrition: '20-30'
page: 
#右键作物看到的信息
 1:
 #第一页
 - '#################NWAFU#################'
 - '植物名称：%name%'
 - '成长阶段：%grow%'
 - '水分含量：%water%'
 - '倒伏概率：%fallChance%'
 - '患病概率：%diseaseChance%'
 - '养分含量：%nutrition%'
 - '植株状态：%state%'
 - '################植物信息###############'
 
```