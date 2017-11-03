#!/bin/bash

POSTGRES_IP=0.0.0.0
POSTGRES_PORT=6432
DBNAME=oiva
DBUSER=oiva
DBPASSWORD=oiva

OIVA_MVN_OPTS="-Doiva.dbhost=${POSTGRES_IP} -Doiva.dbport=${POSTGRES_PORT} -Doiva.dbname=${DBNAME} -Doiva.dbusername=${DBUSER} -Doiva.dbpassword=${DBPASSWORD}"

# Requires you to have access to oiva-deployment repository
mvn $OIVA_MVN_OPTS initialize sql:execute@populate-db