FROM --platform=linux/amd64 openjdk:25-jdk
LABEL maintainer="sharanya@prapticonsulting.com"
VOLUME /tmp
EXPOSE 9202
ARG JAR_FILE=target/common-service-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} common-service.jar

ENTRYPOINT ["java","-jar", "common-service.jar"]

# prasannapete/srl-common-service