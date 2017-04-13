FROM java:openjdk-8-jre
MAINTAINER Oiva dev team <foo@example.com>

EXPOSE 8099

COPY target/oiva-backend.jar /data/oiva-backend.jar
WORKDIR /data
CMD java -jar oiva-backend.jar
