FROM openjdk:8-jre
MAINTAINER Oiva dev <aki.heikkinen@arcusys.fi>

EXPOSE 9099

COPY target/oiva-backend.jar /opt/oiva/backend/oiva-backend.jar
COPY application-dev.yml /opt/oiva/backend/application.yml
WORKDIR /opt/oiva/backend
CMD java -jar oiva-backend.jar --oiva.dbhost=postgres --redis.host=redis --templates.base.path=/opt/oiva/template --spring.profiles.active=dev