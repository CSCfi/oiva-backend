#!/bin/sh

# Options:
#  n    do not use docker-compose setup
#  d    debug mode
#  r    jrebel mode
#  o    mvn offline mode

optionsArg=$@

if [[ ! $optionsArg == *"c"* && ! $optionsArg == *"l"* ]]; then
    echo "Usage: ./launch-dev-backend.sh [MODE] [OPTIONS]"
    echo "MODE (required to use one of following):\n\tc\tUse docker-compose database and nginx (Recommended)\n\tl\tUse local database"
    echo "OPTIONS (optional):\n\td\tUse Java remote debug\n\tr\tUse JRebel\n\to\tUse maven offline mode\n"
    exit 1
fi

PATH_TO_BACKEND="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

OIVA_MEMORY=512m
OIVA_JAVA_JREBEL=""
OIVA_DOCKER_HOST="0.0.0.0"

# Developer specific setup
sUserArgsFile=./vars-`whoami`.sh
echo "$sUserArgsFile"
if [ -f $sUserArgsFile ]; then
    source $sUserArgsFile
fi

OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -Pdev -Dspring.profiles.active=dev"

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva-backend -Djava.rmi.server.hostname=localhost"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dproject.path=$PATH_TO_BACKEND -noverify"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xms${OIVA_MEMORY} -Xmx${OIVA_MEMORY}"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70"

echo "\nLaunching oiva-backend using options:"

if [[ $optionsArg == *"c"* ]]; then
    echo "docker-compose setup"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva.dbhost=$OIVA_DOCKER_HOST -Doiva.dbport=6432 -Dredis.host=$OIVA_DOCKER_HOST -Dredis.port=7379"
fi

if [[ $optionsArg == *"l"* ]]; then
    echo "local setup"
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

echo "\nMaven options:${OIVA_MVN_OPTS}\nJava options:${OIVA_JAVA_OPTS}\n"

mvn $OIVA_MVN_OPTS spring-boot:run -Djvm.fork.arguments="${OIVA_JAVA_OPTS}"