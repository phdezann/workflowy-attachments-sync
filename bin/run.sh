#!/usr/bin/env bash

set -e -u -x

current_directory="$(
  cd "$(dirname "$0")" >/dev/null 2>&1
  pwd -P
)/.."

find_jar() {
  find "${current_directory}/target" -type f -name "*.jar" ! -name "original*" -maxdepth 1 -print0 | while read -r -d $'\0' filename; do
    echo "${filename}"
    break
  done
}

jar=$(find_jar)
if [[ -z ${jar} || $(find "${jar}" -mmin +1 -print) ]]; then
  mvn -f "${current_directory}/pom.xml" clean install
fi

jar=$(find_jar)

java -jar "${jar}" \
  --config-file "${CONFIG_FILE}" \
  --attachments-folder "${ATTACHMENTS_FOLDER}" \
  "$@"
