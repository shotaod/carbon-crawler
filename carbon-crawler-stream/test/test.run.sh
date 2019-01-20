#!/usr/bin/env bash

COMPOSE_PATH='../../carbon-crawler-docker/docker-compose.yml'

function stage() {
    echo --------------------------------------------------
    echo $1
    echo --------------------------------------------------
}

function tear_down() {
    stage "Tear Down"

    docker-compose \
        -f ${COMPOSE_PATH} \
        down
}

function set_up_docker() {
    stage "Set Up"

    docker-compose \
        -f ${COMPOSE_PATH} \
        up -d \
        main_db db_migration selenium_hub chrome_node pause zookeeper kafka_root dataflow
}

function run_test() {
    stage "Register Apps"
    DIST=$(pwd)/properties
    pushd ../../
        ./gradlew \
            --console=plain \
            -x test \
            clean \
            build \
            bootInstall \
            :carbon-crawler-stream:flow:printFlowProperties -PpropertyDist=${DIST}
    popd

    stage "Wait for connection"
    echo checking spring cloud data flow server is available
    until curl -sSf localhost:9393/about >/dev/null 2>/dev/null;
    do
        echo -n .
        sleep 1
    done
    echo

    stage "Run Test"
    pushd ../../
        serverUri=http://localhost:9393 streamRegistrationResource=${DIST} \
            ./gradlew :carbon-crawler-stream:test:check --stacktrace
    popd
}

function result_success() {
    stage "Test Result"
    echo success
    exit 0
}
function result_failure() {
    stage "Test Result"
    echo failure

    RET=$?
    exit ${RET}
}

# --------------------------------------------------
# main routine
# --------------------------------------------------
tear_down
set_up_docker
run_test

## ----------
## try
## ----------
#(
#    setup
#    run_test
#)
## ----------
## handle result
## ----------
#if [[ $? -eq 0 ]]; then
#    result_success
#else
#    ( result_failure )
#fi
## ----------
## finally
## ----------
#{
#    tear_down
#}