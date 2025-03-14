# 基于官方的 OpenJDK 17 镜像
FROM openjdk:17-jdk

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中
COPY target/8004-demo-springmvc-1.0-SNAPSHOT.jar /app/8004-demo-springmvc-1.0-SNAPSHOT.jar

# 指定容器启动时运行的命令
ENTRYPOINT ["java", "-jar", "8004-demo-springmvc-1.0-SNAPSHOT.jar"]