#!/bin/bash

# host machine ip
HOSTMACHINE_IP=$(ipconfig getifaddr en0)
if [ -z $HOSTMACHINE_IP ]; then HOSTMACHINE_IP=$(ipconfig getifaddr en5); fi

sed -e "s/HOSTIP/${HOSTMACHINE_IP}/g" docker-compose.yml | docker-compose --file - up postgres redis nginx &