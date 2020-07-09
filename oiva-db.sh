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
    echo -e "drop      Remove database schema"
    echo -e "generate  Run major migrations if necessary, recreate Jooq-entities"
    echo -e "connect   Connect to database in docker"
    echo -e "restore   Drop db, restore schema and data from sql dump"
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

# Developer specific setup
sUserArgsFile=./vars-`whoami`.sh
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

if [[ $cmdArg == "drop" ]]; then
    dropDatabase

elif [[ $cmdArg == "generate" ]]; then
    echo "Deleting generated sources"
    rm -rf src/main/generated
    echo "Regenerating"
    mvn $OIVA_MVN_OPTS compile -Pgenerate-db

elif [[ $cmdArg == "connect" ]]; then
  if [[ $envArg == "amos" ]]; then
    docker exec -it oiva-backend_amos-postgres_1 bash -c "psql -U oiva"
  elif [[ $envArg == "yva" ]]; then
    docker exec -it oiva-backend_yva-postgres_1 bash -c "psql -U kuja"
  fi

elif [[ $cmdArg == "restore" ]]; then
    echo "Dropping existing db and restoring from sql dump"
    dropDatabase
    mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db

else
  abort

fi
