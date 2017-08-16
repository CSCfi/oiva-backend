#!/bin/bash

oivaOptionsArg=$@

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xms4096m -Xmx4096m"

if [[ $oivaOptionsArg == *"r"* ]]; then
    echo -e "Using jmxremote arguments\n"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.port=8189"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.authenticate=false"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dcom.sun.management.jmxremote.ssl=false"
fi

sudo -u oiva java $OIVA_JAVA_OPTS -jar oiva-backend.jar
