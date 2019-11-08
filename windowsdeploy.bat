docker-compose exec app bash /app/glassfish/bin/asadmin undeploy esd
docker-compose exec app rm -rf /app/glassfish/domains/domain1/autodeploy/.autodeploystatus
FOR /F "tokens=* USEBACKQ" %%F IN (`docker-compose ps -q app`) DO (
SET containerId=%%F
)
docker cp build/app.war "%containerId%:/app/glassfish/domains/domain1/autodeploy/esd.war"
