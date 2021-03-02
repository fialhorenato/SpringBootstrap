FROM openjdk:16-ea-2-jdk-oraclelinux7
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]