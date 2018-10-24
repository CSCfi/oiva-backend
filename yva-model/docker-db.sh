#!/bin/bash

# Requires you to have access to oiva-deployment repository
POSTGRES_IP=0.0.0.0
POSTGRES_PORT=6432
DBNAME=kuja
DBUSER=kuja
DBPASSWORD=kuja

# Developer specific setup
sUserArgsFile=./vars-`whoami`.sh
echo "$sUserArgsFile"
if [ -f $sUserArgsFile ]; then
    source $sUserArgsFile
fi

OIVA_MVN_OPTS="-Doiva.dbhost=${POSTGRES_IP} -Doiva.dbport=${POSTGRES_PORT} -Doiva.dbname=${DBNAME} -Doiva.dbusername=${DBUSER} -Doiva.dbpassword=${DBPASSWORD}"

actionArg=$1
shift
optionsArg=$@

function cleanDatabase() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@clean-db
}

function createDatabase() {
    mvn $OIVA_MVN_OPTS compile -Pgenerate-db
}

function populateDatabase() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db
}

if [[ $optionsArg == *"o"* ]]; then
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

if [[ $actionArg == "clean" ]]; then
    cleanDatabase
elif [[ $actionArg == "create" ]]; then
    cleanDatabase
    createDatabase
elif [[ $actionArg == "populate" ]]; then
    populateDatabase
else
    echo "Usage: ./docker-db.sh [ACTION] [OPTIONS]"
    echo -e "Actions:\n\tclean\t\tRemove database schema\n\tcreate\t\tRe-create database schema\n\tpopulate\tPopulate database with test data\n"
    echo -e "Options:\n\to\tUse maven offline mode\n"
    exit 1
fi