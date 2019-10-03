#!/usr/bin/env bash
if ! [ -x "$(command -v inotifywait)" ]; then
  echo 'Error: inotify tools must be installed' >&2
  exit 1
fi

PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )/.."
cd "${PROJECT_DIR}" || exit

if [ -z $(docker ps -q --no-trunc | grep $(docker-compose ps -q app)) ]; then
  docker-compose up -d
fi

trap "trap - SIGTERM && kill -- -$$" SIGINT SIGTERM EXIT
./gradlew war -t &

while (inotifywait -qq -e modify ./build/libs/esd*.war); do
  docker cp ./build/libs/*.war $(docker-compose ps -q app):/app/glassfish/domains/domain1/autodeploy/app.war
done
