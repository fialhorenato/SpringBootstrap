FROM openjdk:17-alpine
VOLUME /tmp
COPY build/libs/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]