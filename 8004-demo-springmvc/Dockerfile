# 基于官方的 OpenJDK 17 镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中
COPY target/demo-springmvc-1.0-SNAPSHOT.jar /app/demo-springmvc-1.0-SNAPSHOT.jar

# 指定容器启动时运行的命令
ENTRYPOINT ["java", "-jar", "demo-springmvc-1.0-SNAPSHOT.jar"]