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
function tear_down_compose() {
    stage "Tear Down"

    if [[ ${SKIP_COMPOSE_DOWN} == t* ]]; then
        echo "skip compose down"
    else
        echo "To suppress overhead in re-creating docker image"
        echo "you can skip this step by passing SKIP_COMPOSE_DOWN=true"
        docker-compose \
            --no-ansi \
            -f ${COMPOSE_PATH} \
            down
    fi
}

function set_up_compose() {
    stage "Set Up"

    docker-compose \
        --no-ansi \
        -f ${COMPOSE_PATH} \
        up -d
}

function check_connection() {
    stage "Wait For Connection"
    echo checking spring cloud data flow server is available
    until curl -sSf localhost:40008/about >/dev/null 2>/dev/null;
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
tear_down_compose
set_up_compose
check_connection
