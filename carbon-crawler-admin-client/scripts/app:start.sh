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
CMD="${CMD} --host ${CARBON_CRAWLER_ADMIN_CLIENT_HOST}"
CMD="${CMD} --port ${CARBON_CRAWLER_ADMIN_CLIENT_PORT}"
CMD="${CMD} --https"
CMD="${CMD} --cert ~/Documents/secret/local_ssl/fr-admin.carbon-crawler.local.crt"
CMD="${CMD} --key ~/Documents/secret/local_ssl/fr-admin.carbon-crawler.local.key"


echo '--------------------------------------------------'
echo 'Run Command'
echo '--------------------------------------------------'
echo $CMD
eval ${ENV_KVS} ${CMD}
