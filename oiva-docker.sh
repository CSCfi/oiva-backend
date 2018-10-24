#!/bin/bash

# command argument
cmdArg=$1
shift

# user arguments
userArgs=$@

if [[ $cmdArg == "list" ]]; then
    docker ps --format "{{.Names}}" | grep oiva-backend
elif [[ $cmdArg == "stop" ]]; then
    echo -e "Stopping all oiva-backend containers"
    docker ps --format "{{.Names}}" | grep oiva-backend | xargs docker stop
    if [[ $1 == "--destroy" ]]; then
        echo -e "Removing all oiva-backend containers"
        docker ps -a --format "{{.Names}}" | grep oiva-backend | xargs docker rm
    fi
elif [[ $cmdArg == "start" ]]; then
    # host machine ip
    DEVICEID=$userArgs
    if [ ! -z $DEVICEID ]; then
        HOSTMACHINE_IP=$(ipconfig getifaddr $DEVICEID);
        if [ -z $HOSTMACHINE_IP ]; then
            echo -e "No such ethernetdevice: $DEVICEID"
        fi
    fi
    if [ -z $HOSTMACHINE_IP ]; then
        for DEVICEID in {0..9}; do
            if [ -z $HOSTMACHINE_IP ]; then
                HOSTMACHINE_IP=$(ipconfig getifaddr en$DEVICEID);
            else
                break
            fi
        done
    fi

    if [ -z $HOSTMACHINE_IP ]; then
        echo "No ethernetdevice found. Try adding it as argument"
    else
        echo -e "Using hostmachine ip: $HOSTMACHINE_IP"
        sed -e "s/HOSTIP/${HOSTMACHINE_IP}/g" docker-compose.yml | docker-compose --file - up amos-postgres yva-postgres amos-redis yva-redis nginx &
    fi
else
    echo "Usage: ./oiva-docker.sh CMD [args]"
    echo -e "CMD options:"
    echo -e "start     Start containers"
    echo -e "               [ip-address] = address to use"
    echo -e "stop      Stop containers"
    echo -e "               --destroy = remove images"
    echo -e "list      List containers"
    echo -e ""
    exit 1
fi