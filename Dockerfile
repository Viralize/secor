FROM maven:3.5-jdk-8 as BUILD

# set up certificates for twitter
RUN echo -n | openssl s_client -connect maven.twttr.com:443 | \
    sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /tmp/twttrcert.crt
RUN keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts \
    -storepass changeit -noprompt -alias twttrcert -file /tmp/twttrcert.crt

# actualy do the compilation
COPY src /usr/src/secor/src
COPY pom.xml /usr/src/secor
RUN mvn -f /usr/src/secor/pom.xml package -DskipTests=false

FROM openjdk:8-jre

RUN mkdir -p /opt/secor
COPY --from=BUILD /usr/src/secor/target/secor-*-bin.tar.gz /opt/secor/
