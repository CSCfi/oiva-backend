#!/bin/bash

# Requires you to have access to oiva-deployment repository

# command argument
cmdArg=$1
shift

# user arguments
optionsArg=$@

function showHelp() {
    echo "Usage: ./docker-db.sh [CMD] [ARGS]"
    echo -e "CMD options:"
    echo -e "create      Use to (re-)create local database from scratch. Runs drop and populate in sequence"
    echo -e "drop        Remove database data and schema"
    echo -e "generate    Regenerate JOOQ sources and create database schema with major migrations"
    echo -e "populate    Populate database with seed data"
    echo -e "connect     Connect to database in docker"
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

POSTGRES_PORT=6432
DBNAME=oiva
DBUSER=oiva
DBPASSWORD=oiva
DBSCHEMA=oiva
cd oiva-core-model || exit

# Developer specific setup
sUserArgsFile=./vars-$(whoami).sh
echo "Reading $sUserArgsFile"
if [ -f $sUserArgsFile ]; then
    source $sUserArgsFile
fi

OIVA_MVN_OPTS="-DskipTests -Doiva.dbhost=${POSTGRES_IP} -Doiva.dbport=${POSTGRES_PORT} -Doiva.dbname=${DBNAME} -Doiva.dbusername=${DBUSER} -Doiva.dbpassword=${DBPASSWORD} -Doiva.schema=${DBSCHEMA}"
if [[ $optionsArg == *"-o"* ]]; then
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

function dropDatabase() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@clean-db
}

function populate() {
    mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db
}

function generate() {
    rm -rf src/main/generated
    mvn $OIVA_MVN_OPTS compile -Pgenerate-db
}

if [[ $cmdArg == "drop" ]]; then
    echo "Dropping existing database..."
    dropDatabase

elif [[ $cmdArg == "generate" ]]; then
    echo "Regenerating JOOQ sources and generating database schema..."
    generate

elif [[ $cmdArg == "populate" ]]; then
    echo "Seeding database..."
    populate

elif [[ $cmdArg == "connect" ]]; then
    docker exec -it oiva-backend_amos-postgres_1 bash -c "psql -U oiva"

elif [[ $cmdArg == "initialize" ]]; then
    echo "Regenerating JOOQ sources and generating database schema..."
    generate
    echo "Seeding database..."
    populate

elif [[ $cmdArg == "create" ]]; then
    echo "Dropping existing database..."
    dropDatabase
    echo "Seeding database..."
    populate

else
  abort

fi
