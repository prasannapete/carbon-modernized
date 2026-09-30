FROM --platform=linux/amd64 eclipse-temurin:25-jdk
LABEL maintainer="sharanya@prapticonsulting.com"
VOLUME /tmp
EXPOSE 9204

# Create upload directory
RUN mkdir -p /res/uploads/

# Copy the application JAR
ARG JAR_FILE=target/kitkat-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} kitkat-service.jar

# Optional: explicitly define trustStore (avoids trustAnchors issues)
ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts \
               -Djavax.net.ssl.trustStorePassword=changeit"

# Run Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar kitkat-service.jar"]
