#!/bin/bash

optionsArg=$@

OIVA_BACKEND_OPTS="-c"
if [[ ! -z $optionsArg ]]; then
    OIVA_BACKEND_OPTS=$optionsArg
fi

../oiva-backend.sh yva $OIVA_BACKEND_OPTS