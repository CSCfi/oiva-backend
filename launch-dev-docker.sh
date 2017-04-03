#!/bin/sh
docker-machine start default
eval $(docker-machine env default)
docker-compose up postgres redis