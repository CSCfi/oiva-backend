#!/bin/sh
mvn install:install-file -Dfile=lib/prince.jar -DgroupId=com.princexml -DartifactId=prince-java -Dversion=9 -Dpackaging=jar
