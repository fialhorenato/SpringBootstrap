FROM openjdk:16-ea-20-jdk-oraclelinux8
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]