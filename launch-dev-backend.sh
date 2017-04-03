#!/bin/sh
PATH_TO_BACKEND="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
mvn -Pdev -Dspring.profiles.active=dev spring-boot:run -Djvm.fork.arguments="-Djava.rmi.server.hostname=localhost -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Dproject.path=$PATH_TO_BACKEND -noverify"