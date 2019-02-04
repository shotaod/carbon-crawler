#!/usr/bin/env bash

set -xe

# authentication
gcloud auth activate-service-account --key-file=${KEY_FILE}

# run emulator
gcloud beta emulators pubsub start \
    --data-dir=/var/pubsub \
    --host-port=${PUBSUB_HOST}:${PUBSUB_PORT} \
    --project=${PROJECT_ID} \
    --user-output-enabled=true \
    --verbosity=debug
