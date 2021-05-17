# Prisma Server
Prisma 1 的Docker服务端的基础代码。使用Scala编写。sbt来打包程序。

[Prisma1地址](https://github.com/prisma/prisma1)

[本服务原地址](https://github.com/prisma/prisma1/tree/master/server)

## 修改
> 修改了 `servers/api/src/main/resources/application.conf` 在文件中增加了 `parsing.max-chunk-size = 10m` `parsing.max-chunk-size = ${?AKKA_MAX_CHUNK_SIZE}` 默认传输块的大小为10m,可以在构造Docker镜像时定义环境变量 `AKKA_MAX_CHUNK_SIZE` 例如`AKKA_MAX_CHUNK_SIZE: 20m`来重新规范传输块的大小

> 基于报错  `HTTP chunk size exceeds the configured limit of 1048576 bytes` 查询原因是因为Scala的 akka请求中默认请求块大小为1024KB

> 修改了 `.envrc`里面的 `export CLUSTER_VERSION=local` 为 `export CLUSTER_VERSION=1.34.0`, 这里是环境变量，如何生效按各自系统的方法实现。这样修改是针对 `prisma deploy`时，报错
```text
▸    Invalid Version: local
```
## 打包问题
> 打包之前一定要让 `.envrc`文件中的环境变量生效，我使用的是Mac，用的`.bash_profile`或者`.zshRC`添加进文件后`source`生效即可，但是IDEA需要重新启动才会识别到环境变量。

> 打包docker时报错 `Could not parse image id  sbt` 进行了`sbt`和`sbt-docker`的版本更新，具体如下：
 - project > build.properties 中的 sbt.version=1.2.3 修改为 sbt.version=1.4.9
 - project > project > build.properties中的 sbt.version=1.2.6 修改为 sbt.version=1.4.9
 - project > plugins.sbt 中 `addSbtPlugin("se.marcuslonnberg" % "sbt-docker"           % "1.5.0")`修改为`addSbtPlugin("se.marcuslonnberg" % "sbt-docker"           % "1.8.2")`

## 如何重新打包docker
- 本服务是在 `IntelliJ IDEA`下编译成功的，首先需要在 `IntelliJ IDEA`的 `plugins`中下载 `Scala`插件。使用 `IntelliJ IDEA` 不需要全局下载 `sbt`了。
- 将服务文件拉取，直接导入IDEA中等待服务初始化完毕。
- 在 `IntelliJ IDEA` 中打开`sbt shell`, 执行 `clean`清除编译文件 `compile`重新编译 再执行 `docker` 执行完毕后将出现两个docker镜像。我们用到的是 `prismagraphql/prisma:latest`
```text
REPOSITORY                  TAG              IMAGE ID       CREATED          SIZE
prismagraphql/prisma-prod   latest           6b6cf8680825   12 minutes ago   216MB
prismagraphql/prisma        latest           004cf07db4a1   12 minutes ago   216MB
```
- 修改镜像标签
> 这里不能使用 `prismagraphql/prisma-prod:latest`来重命名标签并且依赖此镜像打包新的`Docker`，否则会报关于 `RabbitMQ` 的错误。
```bash
错误的
docker tag prismagraphql/prisma-prod:latest prismagraphql/prisma:1.34
正确的
docker tag prismagraphql/prisma:latest prismagraphql/prisma:1.34
```

## 淌坑问题

> 寻找问题 `▸    Invalid Version: local` 时 由于各种原因修改过 `project/Dependencies.scala` 中 `val mariaDbClient  = "org.mariadb.jdbc"  % "mariadb-java-client" % "2.1.2"`  修改为 `val mariaDbClient  = "org.mariadb.jdbc"  % "mariadb-java-client" % "2.7.3"` 从而引发下面的问题
```text
针对报错java.sql.SQLFeatureNotSupportedException: (conn=6) ALGORITHM=INPLACE
```
> 从而修改了 `connectors/deploy-connector-mysql/src/main/scala/com/prisma/deploy/connector/mysql/database/MysqlJdbcDeployDatabaseMutationBuilder.scala` 内的 `ALGORITHM = INPLACE` 改为`ALGORITHM = COPY`

> 该问题目前不存在，已恢复至初始状态