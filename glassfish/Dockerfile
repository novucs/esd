# glassfish requires old jdk+maven to compile :(
FROM maven:3.2-jdk-8 AS build
ENV GLASSFISH_VERSION=5.1.0
# Download and prepare sources
RUN apt-get update && apt-get install -y --force-yes wget && \
	wget https://github.com/eclipse-ee4j/glassfish/archive/${GLASSFISH_VERSION}.tar.gz && \
	mkdir /glassfish && \
	tar xzf ${GLASSFISH_VERSION}.tar.gz -C glassfish --strip-components=1
# Compile the project
RUN mvn -f /glassfish -Dmaven.artifact.threads=30 -Pstaging install

FROM openjdk:11-jre-slim AS glassfish
ENV GLASSFISH_HOME=/app
COPY --from=build /glassfish/appserver/distributions/glassfish/target/stage/glassfish5/glassfish /app
