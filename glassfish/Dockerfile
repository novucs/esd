# Glassfish does not yet support JDK11
FROM openjdk:8-slim

ENV GLASSFISH_HOME=/app
ENV GLASSFISH_VERSION=4.1.1
COPY googleapi.crt $JAVA_HOME/jre/lib/security
RUN cd $JAVA_HOME/jre/lib/security && \
    keytool -keystore cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias googleapicert -file googleapi.crt

RUN apt-get update && apt-get install -y wget zip netcat && \
    wget "http://download.oracle.com/glassfish/${GLASSFISH_VERSION}/release/glassfish-${GLASSFISH_VERSION}.zip" -O glassfish.zip && \
    unzip glassfish.zip && \
    mv glassfish4 ${GLASSFISH_HOME} && \
    rm -Rf /*.tar.gz && \
    apt-get purge -y wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR ${GLASSFISH_HOME}
EXPOSE 4848 8080 8181 9009

HEALTHCHECK CMD nc -z localhost 4848 || exit 1

CMD ["./glassfish/bin/asadmin", "start-domain", "--verbose"]
