# build the project via gradle
FROM gradle:5.6.2-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# use the built project jar in jre container
FROM openjdk:11-jre-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/esd.jar
ENTRYPOINT ["java", "-jar", "/app/esd.jar"]
