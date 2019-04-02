#!/usr/bin/env bash
mvn clean docker:build -Dintegration-test && export MACHINE_IP=$(docker-machine ip test-db-machine) && mvn verify -Dintegration-test