#!/usr/bin/env bash
tests=*IT
usage() {
    echo "Usage: $0 [[-d][-t regex] | [-h]]"
    echo -e "-d | --debug      Run integration tests with remote debug in port 5005"
    echo -e "-t | --tests      Test regex pattern for running only specific tests"
    echo -e "-h | --help       Prints this help"
}

while [[ "$1" != "" ]]; do
    case $1 in
        -t | --tests )  shift
                        tests=$1
                        ;;
        -d | --debug )  debug=-Dmaven.failsafe.debug
                        ;;
        -h | --help )   usage
                        exit
                        ;;
        * )             usage
                        exit 1
                        ;;
    esac
    shift
done

echo "Tests: $tests"
mvn clean docker:build -Dintegration-test && export MACHINE_IP=$(docker-machine ip test-db-machine) && mvn verify ${debug} -Dit.test=${tests} -Dintegration-test