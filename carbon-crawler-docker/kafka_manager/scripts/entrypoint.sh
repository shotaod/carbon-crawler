#!/bin/bash
VERSION='1.4'
KM_UNZIP_DIR="/opt/kafka-manager-${KM_VERSION}"
EXEC_DIR='/opt/kafka-manager'
PID_FILE="${EXEC_DIR}/RUNNING_PID"
EXEC_FILE="${EXEC_DIR}/bin/kafka-manager"

echo '--------------------------------------------------'
echo "  kafka manager executer [ver ${VERSION}]"
echo '--------------------------------------------------'

throw() {
    echo "$* ...aborting" >&2
    exit 1
}

check() {
  echo '--------------------------------------------------'
  echo '  kafka manager [check condition]'
  echo '--------------------------------------------------'
  echo '[env]'
  echo "KM_VERSION   : ${KM_VERSION}"
  echo "KM_REVISION  : ${KM_REVISION}"
  echo "KM_CONFIGFILE: ${KM_CONFIGFILE}"
  echo ''
  if [[ -z ${KM_VERSION} ]]; then
    throw 'error missing env $KM_VERSION'
  elif [[ -z ${KM_REVISION} ]]; then
    throw 'error missing env $KM_REVISION'
  elif [[ -z ${KM_CONFIGFILE} ]]; then
    throw 'error missing env $KM_CONFIGFILE'
  fi
}

deploy() {
  echo '--------------------------------------------------'
  echo '  kafka manager [deploy]'
  echo '--------------------------------------------------'
  echo '[check source mode(before)]'
  # ls -la "${KM_UNZIP_DIR}/"
  # chmod +xwr "${KM_UNZIP_DIR}/*"
  # echo '[check source mode(after)]'
  # ls -la "${KM_UNZIP_DIR}/"

  rm -rf "${EXEC_DIR}/"*

  mv "${KM_UNZIP_DIR}/conf/" ${EXEC_DIR}
  mv "${KM_UNZIP_DIR}/lib/" ${EXEC_DIR}
  mv "${KM_UNZIP_DIR}/bin/" ${EXEC_DIR}
}

setup() {
  echo '--------------------------------------------------'
  echo '  kafka manager [build]'
  echo '--------------------------------------------------'
  if [[ ! -f ${EXEC_FILE} ]]; then
    echo "not found exec file(@${EXEC_FILE}) ---> build kafka-manager"
    cd /root/work/kafka-manager
    ./sbt clean dist
    unzip -d /opt ./target/universal/kafka-manager-${KM_VERSION}.zip
    deploy
  else
    echo "found exec file(@${EXEC_FILE}) ---> skip build kafka-manager"
  fi
}

beforeRun() {
  echo '--------------------------------------------------'
  echo '  kafka manager [beforeRun]'
  echo '--------------------------------------------------'
  cd ${EXEC_DIR}
  echo '[pid] check...'
  if [[ -f ${PID_FILE} ]]; then
    echo 'exists ---> remove it'
    rm ${PID_FILE}
  else
    echo 'not found ---> going on'
  fi

  printKafka
}

printKafka() {
  echo ''
  echo ''
  echo '                \welcome/'
  echo '┏----------------------------------------------------┓'
  echo '| ██╗  ██╗ █████╗ ███████╗██╗  ██╗ █████╗            |'
  echo '| ██║ ██╔╝██╔══██╗██╔════╝██║ ██╔╝██╔══██╗           |'
  echo '| █████╔╝ ███████║█████╗  █████╔╝ ███████║           |'
  echo '| ██╔═██╗ ██╔══██║██╔══╝  ██╔═██╗ ██╔══██║           |'
  echo '| ██║  ██╗██║  ██║██║     ██║  ██╗██║  ██║           |'
  echo "| ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝  ver. ${VERSION} |"
  echo '┗----------------------------------------------------┛'
  echo ''
}

check
setup
beforeRun
exec ${EXEC_FILE} -Dconfig.file=${KM_CONFIGFILE} "${KM_ARGS}" "${@}"
