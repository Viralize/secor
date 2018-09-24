FROM maven:3.5-jdk-8 as BUILD

# set up certificates for twitter
RUN echo -n | openssl s_client -connect maven.twttr.com:443 | \
    sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > /tmp/twttrcert.crt
RUN keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts \
    -storepass changeit -noprompt -alias twttrcert -file /tmp/twttrcert.crt

# actualy do the compilation
COPY src /usr/src/secor/src
COPY pom.xml /usr/src/secor
RUN mvn -f /usr/src/secor/pom.xml package -DskipTests=true

# due to problems with hadoop libs, do not use jre > 8
FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/secor
COPY --from=BUILD /usr/src/secor/target/secor-*-bin.tar.gz /opt/secor/

# for local testing, need the `config` directory to be mounted as
#   ~/dev/secor$ ls -l config
#   total 72
#   -rw-r--r--  1 nids  staff    766 Sep 19 12:04 log4j.prod.properties
#   -rw-r--r--  1 nids  staff    783 Sep 19 12:04 log4j.progress-monitor.prod.properties
#   -rw-r--r--  1 nids  staff  15203 Sep 21 11:47 secor.common.properties
#   -rw-r--r--  1 nids  staff   1336 Sep 19 11:43 secor.prod.partition.properties
#   -rw-r--r--  1 nids  staff   1652 Sep 21 11:47 secor.prod.properties
#   -rw-r--r--  1 nids  staff   2341 Sep 19 11:24 test-key.json
#   ~/dev/secor$ docker run -it -v `pwd`/config:/home/secor/config 751c8fd4f3c7
#
RUN mkdir /home/secor
RUN tar -zxvf /opt/secor/secor-*-bin.tar.gz -C /home/secor

WORKDIR /home/secor
RUN rm -rf *.properties
RUN mkdir /home/secor/config && chmod 775 /home/secor/config
CMD exec java -ea -Dsecor_group=secor_partition \
    -Dlog4j.configuration="file:///home/secor/config/log4j.prod.properties" \
    -Dconfig="file:///home/secor/config/secor.prod.partition.properties" \
    -cp "config/*:secor-0.26.jar:lib/*" \
    com.pinterest.secor.main.ConsumerMain
