# build the project via gradle
FROM gradle:5.6.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle war --no-daemon

# copy built app to glassfish
FROM novucs/glassfish:4.1.1-jdk8-slim.build.1
COPY --from=build /home/gradle/src/build/libs/*.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/app.war
