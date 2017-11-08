#!/bin/bash

# host machine ip

DEVICEID=$@
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
    sed -e "s/HOSTIP/${HOSTMACHINE_IP}/g" docker-compose.yml | docker-compose --file - up postgres redis nginx &
fi