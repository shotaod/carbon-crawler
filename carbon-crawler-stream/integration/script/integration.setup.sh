#!/usr/bin/env bash

COMPOSE_PATH='../../carbon-crawler-docker/docker-compose.yml'
GRADLE_WRAPPER='../../gradlew'

function pushd () {
    command pushd "$@" > /dev/null
}

function popd () {
    command popd "$@" > /dev/null
}

function stage() {
    echo --------------------------------------------------
    echo $1
    echo --------------------------------------------------
}

# --------------------------------------------------
# Steps
# --------------------------------------------------
function tear_down() {
    stage "Tear Down"

    docker-compose \
        --no-ansi \
        -f ${COMPOSE_PATH} \
        down
}

function set_up_docker() {
    stage "Set Up"

    docker-compose \
        --no-ansi \
        -f ${COMPOSE_PATH} \
        up -d
}

function check_connection() {
    stage "Wait For Connection"
    echo checking spring cloud data flow server is available
    until curl -sSf localhost:9393/about >/dev/null 2>/dev/null;
    do
        printf '.'
        sleep 1
    done
    echo done!
    echo
}

# --------------------------------------------------
# main routine
# --------------------------------------------------
set -e
tear_down
set_up_docker
check_connection