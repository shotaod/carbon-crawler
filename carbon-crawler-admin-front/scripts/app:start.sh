#!/usr/bin/env bash

# with .env variables
## process substitution
ENV_KVS=$(cat ../.env.local | grep --invert-match \#)
echo '--------------------------------------------------'
echo 'LOAD Environments'
echo '--------------------------------------------------'
echo "${ENV_KVS}"
eval "${ENV_KVS}"

CMD="webpack-dev-server"
CMD="${CMD} --mode development"
CMD="${CMD} --hot"
CMD="${CMD} --progress"
CMD="${CMD} --color"
CMD="${CMD} --host ${CARBON_FRONT_CRAWLER_ADMIN_HOST}"
CMD="${CMD} --port ${CARBON_FRONT_CRAWLER_ADMIN_PORT}"

echo '--------------------------------------------------'
echo 'Run Command'
echo '--------------------------------------------------'
echo $CMD
eval ${ENV_KVS} ${CMD}
