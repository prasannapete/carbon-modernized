FROM --platform=linux/amd64 ubuntu
RUN apt-get update && \
    apt-get install -y openjdk-25-jre-headless && \
    rm -rf /var/lib/apt/lists/
LABEL maintainer="sharanya@prapticonsulting.com"
VOLUME /tmp
EXPOSE 9201
COPY res /res
ARG JAR_FILE=target/authserver-0.0.1-SNAPSHOT.jar
ARG CONFIG_FILE=authserver.yml
ADD ${JAR_FILE} auth-service.jar
ADD ${CONFIG_FILE} config.yml

ENTRYPOINT ["java","-jar",  "-Dspring.config.location=/config.yml","auth-service.jar"]

# prasannapete/srl-auth-server