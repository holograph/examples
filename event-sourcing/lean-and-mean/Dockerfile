FROM openjdk:alpine
MAINTAINER Tomer Gabel <tomer@tomergabel.com>

ADD docker-config.yml /opt/docker-config.yml
ADD target/lib /opt/site-service/lib
ARG JAR_FILE
ADD target/${JAR_FILE} /opt/site-service/site-service.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/site-service/site-service.jar", "server", "/opt/docker-config.yml"]

EXPOSE 8080
