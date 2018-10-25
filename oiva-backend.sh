#!/bin/bash

# environment argument
envArg=$1
shift

# Options:
#  c    use docker-compose database and nginx
#  l    use local database
#  d    debug mode
#  r    jrebel mode
#  o    mvn offline mode

optionsArg=$@

function showHelp() {
    echo "Usage: ./oiva-backend.sh amos|yva [MODE] [ARGS]"
    echo -e "amos      Run amos-backend"
    echo -e "yva       Run yva-backend"
    echo -e ""
    echo -e "MODE (required to use one of following):"
    echo -e "-c        Use docker-compose database and nginx (Recommended)"
    echo -e "-l        Use local database"
    echo -e ""
    echo -e "ARGS options:"
    echo -e "-d        Use Java remote debug"
    echo -e "-r        Use JRebel"
    echo -e "-o        Use maven offline mode"
    echo -e ""
}

function abort() {
    showHelp
    exit 1
}

OIVA_MEMORY=512m
OIVA_JAVA_JREBEL=""
OIVA_POSTGRES_HOST="0.0.0.0"
OIVA_REDIS_HOST="0.0.0.0"

if [[ $envArg == "amos" ]]; then
    OIVA_BACKEND_NAME="oiva-backend"
    POSTGRES_PORT=6432
    REDIS_PORT=7379
    cd amos-backend
elif [[ $envArg == "yva" ]]; then
    OIVA_BACKEND_NAME="kuja-backend"
    POSTGRES_PORT=7432
    REDIS_PORT=8379
    cd yva-backend
else
    abort
fi

if [[ ! $optionsArg == *"-c"* && ! $optionsArg == *"-l"* ]]; then
    abort
fi

# Developer specific setup
sUserArgsFile=./vars-`whoami`.sh
echo "$sUserArgsFile"
if [ -f $sUserArgsFile ]; then
    source $sUserArgsFile
fi

PATH_TO_BACKEND="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -Pdev -Dspring.profiles.active=dev"

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -D${OIVA_BACKEND_NAME} -Djava.rmi.server.hostname=localhost"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dproject.path=${PATH_TO_BACKEND} -noverify"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xms${OIVA_MEMORY} -Xmx${OIVA_MEMORY}"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70"

echo -e "\nLaunching oiva-backend using options:"

if [[ $optionsArg == *"-c"* ]]; then
    echo "docker-compose setup"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva.dbhost=${OIVA_POSTGRES_HOST} -Doiva.dbport=${POSTGRES_PORT} -Dredis.host=${OIVA_REDIS_HOST} -Dredis.port=${REDIS_PORT}"
fi

if [[ $optionsArg == *"-l"* ]]; then
    echo "local setup"
fi

if [[ $optionsArg == *"-d"* ]]; then
    echo "java debug"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
fi

if [[ $optionsArg == *"-r"* ]]; then
    echo "jrebel"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -agentpath:${OIVA_JREBEL_AGENT}"
fi

if [[ $optionsArg == *"-o"* ]]; then
    echo "maven offline mode"
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

echo -e "\nMaven options:${OIVA_MVN_OPTS}\nJava options:${OIVA_JAVA_OPTS}\n"

mvn $OIVA_MVN_OPTS spring-boot:run -Djvm.fork.arguments="${OIVA_JAVA_OPTS}"