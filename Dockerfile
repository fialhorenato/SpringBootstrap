FROM openjdk:17-ea-3-jdk-oraclelinux8
VOLUME /tmp
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]