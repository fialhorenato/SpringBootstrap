FROM openjdk:17 as builder

USER root

RUN apk --update add --no-cache --virtual .jlink-build-deps binutils=~2.34-r2 && \
    jlink \
    --module-path "$JAVA_HOME/jmods" \
    --add-modules java.compiler,java.sql,java.naming,java.management,java.instrument,java.rmi,java.desktop,jdk.internal.vm.compiler.management,java.xml.crypto,java.scripting,java.security.jgss,jdk.httpserver,java.net.http,jdk.naming.dns,jdk.crypto.cryptoki,jdk.unsupported \
    --verbose \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/jre-minimal

USER app

# Now it is time for us to build our real image on top of an alpine version of it

FROM openjdk:17-alpine

WORKDIR /app

# Copy the JRE created in the last step into our $JAVA_HOME

COPY --from=builder /opt/jre-minimal $JAVA_HOME

COPY build/libs/app.jar build/app.jar
