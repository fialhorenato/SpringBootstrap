FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar

RUN jar xf app.jar
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 25 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    app.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info),jdk.crypto.ec \
    --compress=zip-9 \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /jre-custom

# Reduce size of native libs in the custom runtime.
RUN strip -p --strip-unneeded /jre-custom/lib/server/libjvm.so && \
   find /jre-custom -name '*.so' -exec strip -p --strip-unneeded {} +

FROM alpine:3.23

ENV JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=builder /jre-custom ${JAVA_HOME}
COPY --from=builder /build/app.jar /app/app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
