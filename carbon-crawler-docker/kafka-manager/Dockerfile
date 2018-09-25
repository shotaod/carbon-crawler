FROM openjdk:8u171-jdk-alpine3.8

ENV KM_VERSION=1.3.3.18
ENV KM_REVISION=8dcdbf8fabb0001691c9b52b447b656f498b4d7b
ENV KM_CONFIGFILE="conf/application.conf"

RUN mkdir -p /root/work && \
    mkdir -p /opt/scripts
ADD ./scripts/entrypoint.sh /opt/scripts/

RUN set -x && \
    chmod +x /opt/scripts/entrypoint.sh && \
    apk --update add --no-cache bash && \
    apk --update add --no-cache --virtual build-dependencies git wget && \
    cd /root/work && \
    git clone https://github.com/yahoo/kafka-manager && \
    cd /root/work/kafka-manager && \
    git checkout ${KM_REVISION} && \
    echo 'scalacOptions ++= Seq("-Xmax-classfile-name", "72")' >> build.sbt && \
    rm -rf /tmp/* && \
    apk del build-dependencies

VOLUME /root/.sbt /root/.ivy2 /opt/kafka-manager

WORKDIR /opt/scripts

ENTRYPOINT ["./entrypoint.sh"]
