#!/bin/bash
cd "$(dirname "$0")" || exit 1
# tr -d '\r' resolves installation specifc docker-compose bug with non-unix line endings
# this took too long to debug...
WARFILE="$(docker-compose ps -q jenkins):$(docker-compose exec -T jenkins find "$1"/build/libs | grep war | head -n 1 | tr -d '\r')"
docker cp "${WARFILE}" /tmp/"$2".war
docker cp /tmp/"$2".war "$(docker-compose ps -q app)":/app/glassfish/domains/domain1/autodeploy/"$2".war
