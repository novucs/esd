# build the project via gradle
FROM gradle:5.6.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle war --no-daemon

# use the built project jar in jre container
# TODO: Create and maintain an updated glassfish docker build for jdk11 (currently jdk8).
FROM glassfish
COPY --from=build /home/gradle/src/build/libs/*.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/app.war
