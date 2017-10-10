FROM java:openjdk-8-jre
MAINTAINER Oiva dev team <foobar@csc.fi>

EXPOSE 8099

COPY target/oiva-backend.jar /opt/oiva/backend/oiva-backend.jar
WORKDIR /opt/oiva/backend
CMD java -jar oiva-backend.jar
