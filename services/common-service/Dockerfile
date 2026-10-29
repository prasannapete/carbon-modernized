FROM --platform=linux/amd64 eclipse-temurin:25-jre
LABEL maintainer="sharanya@prapticonsulting.com"
VOLUME /tmp
EXPOSE 9202

# Upload directory (FILE_UPLOAD_PATH=/res/uploads/ in srl-config-map)
RUN mkdir -p /res/uploads/

ARG JAR_FILE=target/common-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/common-service.jar

# JVM flags come from JAVA_TOOL_OPTIONS on the k8s Deployment; the JDK's bundled
# truststore is used (the old /etc/ssl/certs/java/cacerts override does not exist here).
ENTRYPOINT ["java", "-jar", "/app/common-service.jar"]
