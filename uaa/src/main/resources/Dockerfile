FROM registry.cn-shenzhen.aliyuncs.com/mogublog/jdk:8-alpine
VOLUME /tmp
ADD uaa-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
