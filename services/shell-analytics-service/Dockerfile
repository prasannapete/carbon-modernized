FROM --platform=linux/amd64 eclipse-temurin:25-jdk

LABEL maintainer="sharanya@prapticonsulting.com"

# Ensure CA certificates are present for HTTPS trust
RUN apt-get update && \
    apt-get install -y ca-certificates && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

VOLUME /tmp
EXPOSE 9203

# Create upload directory
RUN mkdir -p /res/uploads/

# Copy the application JAR
ARG JAR_FILE=target/shell-analytics-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} shell-analytics-service.jar

# Optional: explicitly define trustStore (avoids trustAnchors issues)
ENV JAVA_OPTS="-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts \
               -Djavax.net.ssl.trustStorePassword=changeit"

# Run Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar shell-analytics-service.jar"]
