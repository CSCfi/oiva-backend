#!/bin/sh

# Developer
userArg=$1
shift

PATH_TO_BACKEND="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

OIVA_MEMORY=512m
OIVA_JAVA_JREBEL=''

# Developer specific setup

if [[ $userArg == "aki" ]]; then
    OIVA_JREBEL_AGENT=/Users/aheikkinen/jrebel6path/lib/libjrebel64.dylib

elif [[ $userArg == "jari" ]]; then
    OIVA_JREBEL_AGENT=/Users/jarisaarela/projects/jrebel2/lib/libjrebel64.dylib

elif [[ $userArg == "samu" ]]; then
    OIVA_JREBEL_AGENT=''
else
    echo "Usage: ./launch-dev-backend.sh [DEVNAME] [OPTIONS]"
    echo "Options:\n\tc\tUse docker-compose databases\n\td\tUse Java remote debug\n\tr\tUse JRebel\n\to\tUse maven offline mode\n\tl\tUse Ldap ssh-tunnel\n"
    exit 1
fi

OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -Pdev -Dspring.profiles.active=dev"

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva-backend -Djava.rmi.server.hostname=localhost"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dproject.path=$PATH_TO_BACKEND -noverify"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xms${OIVA_MEMORY} -Xmx${OIVA_MEMORY}"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70"


echo "\nLaunching oiva-backend as $userArg using options:"

# Options:
#  o    mvn offline mode
#  d    debug mode
#  r    jrebel mode

optionsArg=$@


if [[ $optionsArg == *"c"* ]]; then
    echo "docker-compose setup"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva.dbhost=0.0.0.0 -Doiva.dbport=6432 -Dredis.host=0.0.0.0 -Dredis.port=7379"
fi

if [[ $optionsArg == *"d"* ]]; then
    echo "java debug"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
fi

if [[ $optionsArg == *"r"* ]]; then
    echo "jrebel"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -agentpath:${OIVA_JREBEL_AGENT}"
fi

if [[ $optionsArg == *"o"* ]]; then
    echo "maven offline mode"
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

if [[ $optionsArg == *"l"* ]]; then
    echo "ldap ssh-tunnel"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dldap.url=ldaps://127.0.0.1:20636"
fi

echo "\nMaven options:${OIVA_MVN_OPTS}\nJava options:${OIVA_JAVA_OPTS}\n"

mvn $OIVA_MVN_OPTS spring-boot:run -Djvm.fork.arguments="${OIVA_JAVA_OPTS}"