#!/bin/bash

# Requires you to have access to oiva-deployment repository

# environment argument
envArg=$1
shift

# command argument
cmdArg=$1
shift

# user arguments
optionsArg=$@

function showHelp() {
    echo "Usage: ./docker-db.sh amos|yva [CMD] [ARGS]"
    echo -e "amos      Apply to amos database"
    echo -e "yva       Apply to yva database"
    echo -e ""
    echo -e "CMD options:"
    echo -e "init      Initialize database and Jooq-entities"
    echo -e "drop      Remove database schema"
    echo -e "            --clean"
    echo -e "generate  Re-create database schema and Jooq-entities"
    echo -e "            --clean"
    echo -e "            --populate"
    echo -e "populate  Populate database with test data"
    echo -e "connect   Connect to database"
    echo -e ""
    echo -e "ARGS options:"
    echo -e "-o        Use maven offline mode"
    echo -e ""
}

function abort() {
    showHelp
    exit 1
}

POSTGRES_IP=0.0.0.0

if [[ $envArg == "amos" ]]; then
    POSTGRES_PORT=6432
    DBNAME=oiva
    DBUSER=oiva
    DBPASSWORD=oiva
    DBSCHEMA=oiva
    cd oiva-core-model
elif [[ $envArg == "yva" ]]; then
    POSTGRES_PORT=7432
    DBNAME=kuja
    DBUSER=kuja
    DBPASSWORD=kuja
    DBSCHEMA=kuja
    cd oiva-core-model
else
    abort
fi

case $cmdArg in
    init|drop|generate|populate|connect) ;;
    *) abort ;;
esac

# Developer specific setup
sUserArgsFile=./vars-`whoami`.sh
echo "$sUserArgsFile"
if [ -f $sUserArgsFile ]; then
    source $sUserArgsFile
fi

OIVA_MVN_OPTS="-Doiva.dbhost=${POSTGRES_IP} -Doiva.dbport=${POSTGRES_PORT} -Doiva.dbname=${DBNAME} -Doiva.dbusername=${DBUSER} -Doiva.dbpassword=${DBPASSWORD} -Doiva.schema=${DBSCHEMA}"

function initDatabase() {
    cd ..
    mvn $OIVA_MVN_OPTS install -Pinit-${envArg},generate-db
}

function dropDatabase() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@clean-db
    if [[ $optionsArg == *"--clean"* ]]; then
        rm -rf src/main/generated
    fi
}

function populateDatabase() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db
}

function generateDatabase() {
    mvn $OIVA_MVN_OPTS compile -Pgenerate-db
    if [[ $optionsArg == *"--populate"* ]]; then
        populateDatabase
    fi
}

function connectDatabase() {
    PGPASSWORD=$DBPASSWORD psql -U $DBUSER -h $POSTGRES_IP -p $POSTGRES_PORT $DBNAME
}

if [[ $optionsArg == *"-o"* ]]; then
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

if [[ $cmdArg == "init" ]]; then
    initDatabase
elif [[ $cmdArg == "drop" ]]; then
    dropDatabase
elif [[ $cmdArg == "generate" ]]; then
    dropDatabase
    generateDatabase
elif [[ $cmdArg == "populate" ]]; then
    populateDatabase
elif [[ $cmdArg == "connect" ]]; then
    connectDatabase
fi
