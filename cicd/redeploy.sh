#!/bin/bash
cd "$(dirname "$0")" || exit 1
docker cp "$(docker-compose ps -q jenkins)":"$1"/build/libs/*.war /tmp/"$2".war
docker cp /tmp/"$2".war "$(docker-compose ps -q app)":/app/glassfish/domains/domain1/autodeploy/"$2".war
