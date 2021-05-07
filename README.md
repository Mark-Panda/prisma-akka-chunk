# Prisma Server
Prisma 1 Scala code base.

## 修改
> 修改了 `servers/api/src/main/resources/application.conf` 在文件中增加了 `parsing.max-chunk-size = 10m` 
> 基于报错  `HTTP chunk size exceeds the configured limit of 1048576 bytes` 查询原因是因为Scala的 akka请求中默认请求块大小为1024KB


## 打包问题
> 打包docker时报错 `Could not parse image id  sbt` 进行了`sbt`和`sbt-docker`的版本更新，具体如下：
> project > build.properties中的 sbt.version=1.2.3 修改为 sbt.version=1.4.9
> project > project > build.properties中的 sbt.version=1.2.6 修改为 sbt.version=1.4.9
> project > plugins.sbt中 `addSbtPlugin("se.marcuslonnberg" % "sbt-docker"           % "1.5.0")`修改为`addSbtPlugin("se.marcuslonnberg" % "sbt-docker"           % "1.8.2")`

## 如何重新打包docker
- 在IDEA中打开`sbt shell`, 执行 `clean`清除编译文件 `compile`重新编译 再执行 `docker` 执行完毕后将出现一个docker镜像。
```text
REPOSITORY                  TAG              IMAGE ID       CREATED          SIZE
prismagraphql/prisma-prod   latest           6b6cf8680825   12 minutes ago   216MB
prismagraphql/prisma        latest           004cf07db4a1   12 minutes ago   216MB
```
- 修改镜像标签

```bash
docker tag prismagraphql/prisma-prod:latest prismagraphql/prisma:1.34
```