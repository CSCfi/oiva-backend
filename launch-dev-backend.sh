#!/bin/sh

# Options:
#  o    mvn offline mode
#  d    debug mode
#  r    jrebel mode

optionsArg=$@

PATH_TO_BACKEND="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -Pdev -Dspring.profiles.active=dev"

OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Doiva-backend -Djava.rmi.server.hostname=localhost"
OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Dproject.path=$PATH_TO_BACKEND -noverify"

if [[ $optionsArg == *"o"* ]]; then
    echo "Using mvn offline mode"
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

if [[ $optionsArg == *"d"* ]]; then
    echo "Using debug options"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
fi

if [[ $optionsArg == *"r"* ]]; then
    echo "Using jrebel options"
    OIVA_JAVA_OPTS="${OIVA_JAVA_OPTS} -agentpath:'/Users/aheikkinen/jrebel6path/lib/libjrebel64.dylib'"
fi

mvn $OIVA_MVN_OPTS spring-boot:run -Djvm.fork.arguments="${OIVA_JAVA_OPTS}"