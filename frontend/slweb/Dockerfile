FROM --platform=linux/amd64 openjdk:25-jdk
LABEL maintainer="sharanya@prapticonsulting.com"
VOLUME /tmp
EXPOSE 9205
COPY res /res

RUN mkdir -p /res/uploads/

ARG JAR_FILE=target/slweb-0.0.1-SNAPSHOT.jar
ARG CONFIG_FILE=slweb-application.yml

ADD ${JAR_FILE} slweb.jar
ADD ${CONFIG_FILE} config.yml

ENTRYPOINT ["java","-jar","-Dspring.config.location=/config.yml","slweb.jar"]

# prasannapete/shell-web-application
