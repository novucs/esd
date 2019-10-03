FROM openjdk:11-jre-slim

ENV DERBY_VERSION=10.14.2.0
ENV DERBY_HOME=/derby
ENV DERBY_LIB=${DERBY_HOME}/lib
ENV CLASSPATH=${DERBY_LIB}/derby.jar:${DERBY_LIB}/derbynet.jar:${DERBY_LIB}/derbytools.jar:${DERBY_LIB}/derbyoptionaltools.jar:${DERBY_LIB}/derbyclient.jar

RUN apt-get update && apt-get install -y wget && \
    wget https://dist.apache.org/repos/dist/release/db/derby/db-derby-${DERBY_VERSION}/db-derby-${DERBY_VERSION}-bin.tar.gz && \
    tar xzf /db-derby-${DERBY_VERSION}-bin.tar.gz && \
    mv /db-derby-${DERBY_VERSION}-bin /derby && \
    rm -Rf /*.tar.gz ${DERBY_HOME}/demo ${DERBY_HOME}/javadoc ${DERBY_HOME}/docs ${DERBY_HOME}/test ${DERBY_HOME}/*.html ${DERBY_HOME}/KEYS \
    apt-get purge wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /dbs
VOLUME ["/dbs"]
EXPOSE 1527

COPY runtime.policy /derby/runtime.policy

HEALTHCHECK CMD nc -z localhost 1527 || exit 1

CMD ["java", "-Djava.security.manager", "-Djava.security.policy=/derby/runtime.policy", "-Dderby.stream.error.field=System.out", "org.apache.derby.drda.NetworkServerControl", "start", "-h", "0.0.0.0"]
