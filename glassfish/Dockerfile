# Glassfish does not yet support JDK11
FROM openjdk:8-slim

ENV GLASSFISH_HOME=/app
ENV GLASSFISH_VERSION=5.1.0

RUN apt-get update && apt-get install -y wget zip netcat && \
    wget "https://www.eclipse.org/downloads/download.php?file=/glassfish/glassfish-${GLASSFISH_VERSION}.zip&r=1" -O glassfish.zip && \
    unzip glassfish.zip && \
    mv glassfish5 ${GLASSFISH_HOME} && \
    rm -Rf /*.tar.gz && \
    apt-get purge -y wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR ${GLASSFISH_HOME}
EXPOSE 4848 8080 8181

HEALTHCHECK CMD nc -z localhost 4848 || exit 1

CMD ["./glassfish/bin/asadmin", "start-domain", "--verbose"]
