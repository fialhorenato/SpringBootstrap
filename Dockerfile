FROM gradle:jdk24-alpine AS builder

WORKDIR /build
COPY /build/libs/app.jar app.jar

RUN jar xf app.jar

RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 24  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    app.jar > deps.info

RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output jre-custom

# reduce image size a little bit more (-4MB)
RUN strip -p --strip-unneeded jre-custom/lib/server/libjvm.so && \
   find jre-custom -name '*.so' | xargs -i strip -p --strip-unneeded {}

FROM alpine:latest
WORKDIR /deployment

COPY --from=builder /build/jre-custom jre-custom/
COPY --from=builder /build/app.jar build/app.jar

CMD ["jre-custom/bin/java","-jar","build/app.jar"]


EXPOSE 8080
