FROM google/cloud-sdk:231.0.0-alpine

ENV PROJECT_ID 'dummy.app'
ENV KEY_FILE '/certs/svc_accounts.json'
ENV PUBSUB_HOST localhost
ENV PUBSUB_PORT 8085

RUN set -x && \
    apk --update add openjdk7-jre && \
    gcloud components install --quiet beta pubsub-emulator && \
    mkdir -p /var/pubsub

VOLUME /var/pubsub

ADD ./entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
