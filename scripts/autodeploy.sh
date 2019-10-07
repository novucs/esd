#!/usr/bin/env bash
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)/.."
cd "${PROJECT_DIR}" || exit
docker-compose up -d
trap 'trap - SIGTERM && kill -- -$$' SIGINT SIGTERM EXIT
./gradlew build
./gradlew war -t &

WARFILE="./build/libs/*.war"

function date_of_warfile() {
    return "$(stat -c %y "${WARFILE}")"
}

last_modified=date_of_warfile
while sleep 1; do
  if [[ $last_modified != date_of_warfile ]]; then
    last_modified=date_of_warfile
    docker cp ./build/libs/*.war "$(docker-compose ps -q app)":/app/glassfish/domains/domain1/autodeploy/app.war
  fi
done
