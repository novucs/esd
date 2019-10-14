#!/bin/bash
cd "$(dirname "$0")" || exit 1
WARFILE=$(docker-compose exec jenkins find "$1"/build/libs | grep war | head -n 1)
docker cp "$(docker-compose ps -q jenkins)":"$WARFILE" /tmp/"$2".war
docker cp /tmp/"$2".war "$(docker-compose ps -q app)":/app/glassfish/domains/domain1/autodeploy/"$2".war
