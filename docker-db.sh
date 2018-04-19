#!/bin/bash

# Requires you to have access to oiva-deployment repository
POSTGRES_IP=0.0.0.0
POSTGRES_PORT=6432
DBNAME=oiva
DBUSER=oiva
DBPASSWORD=oiva

OIVA_MVN_OPTS="-Doiva.dbhost=${POSTGRES_IP} -Doiva.dbport=${POSTGRES_PORT} -Doiva.dbname=${DBNAME} -Doiva.dbusername=${DBUSER} -Doiva.dbpassword=${DBPASSWORD}"

actionArg=$1
shift
optionsArg=$@

if [[ $optionsArg == *"o"* ]]; then
    OIVA_MVN_OPTS="${OIVA_MVN_OPTS} -o"
fi

if [[ $actionArg == "clean" ]]; then
	mvn $OIVA_MVN_OPTS initialize sql:execute@clean-db
elif [[ $actionArg == "create" ]]; then
	mvn $OIVA_MVN_OPTS initialize sql:execute@clean-db
	mvn $OIVA_MVN_OPTS compile -Pgenerate-db
elif [[ $actionArg == "populate" ]]; then
	mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db
else
	echo "Usage: ./docker-db.sh [ACTION] [OPTIONS]"
	echo -e "Actions:\n\tclean\t\tRemove database schema\n\tcreate\t\tRe-create database schema\n\tpopulate\tPopulate database with test data\n"
    echo -e "Options:\n\to\tUse maven offline mode\n"
    exit 1
fi