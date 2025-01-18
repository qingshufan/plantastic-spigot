# Plantastic

##Precautions

1. For students who have already downloaded the Spigot dependency and added the Lombok Maven dependency, please comment out the following line in the pom tree before Reload Maven to complete the Lombok download

2. After updating the dependencies of fastjson and jedis, please note that commenting out the following dependencies will automatically download them. After packaging, the volume will increase, and there may be errors when PlugmanX is overloaded, so the server needs to be restarted

```
<repository>
<id>spigot-repo</id>
<url> https://hub.spigotmc.org/nexus/content/repositories/snapshots/ </url>
</repository>
```

##Environment construction

1. Download and install IntelliJ IDEA Community Edition 2023.2.1

2. Download apache-maven-3.9-bin.zip, which can be found in the group file, and then refer to this blog tutorial to configure Maven. There is no need for the Eclipse project creation step.

https://blog.csdn.net/weixin_43853855/article/details/117073100

3. Configure MAVEN in IDEA, file ->set search Maven, modify three paths, refer to this blog

https://blog.csdn.net/m0_46816573/article/details/119298361

4. Open IDEA, go to File ->New ->Project from Version Control. On the first line, enter the address of this project

https://git.nwafu.edu.cn/qingshufan/plantastic-spigot.git

On the second line, select the path where you store the project, an empty directory

5. After creating the project, if prompted to load Maven, select 'load' and wait for Maven to build successfully. In the Maven lifecycle on the right, select the 'package' command. If prompted to 'build'
   Success means success

6. Complete the configuration of Redis, refer to the following tutorial to configure Redis. It is easy, and the Redis compressed files are placed in the group file
   https://www.runoob.com/redis/redis-install.html
