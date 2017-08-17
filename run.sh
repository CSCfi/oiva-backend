#!/bin/bash

oivaOptionsArg=$@

OIVA_MEMORY="4096m"

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva-backend -server"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xms${OIVA_MEMORY} -Xmx${OIVA_MEMORY}"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=5 -XX:InitiatingHeapOccupancyPercent=70"


if [[ $oivaOptionsArg == *"r"* ]]; then
    echo -e "Using jmxremote arguments\n"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.port=8189"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.authenticate=false"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.ssl=false"
fi

sudo -u oiva java $OIVA_JAVA_OPTS -jar oiva-backend.jar
