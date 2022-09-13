#!/usr/bin/env bash

PROJECT_KEY=draysontechnologies_${CIRCLE_PROJECT_REPONAME}
GIT_HASH=`git log --pretty=format:'%h' -n 1`



mvn clean package sonar:sonar \
    -Dsonar.host.url=${SONARQUBE_URL} \
    -Dsonar.login=${SONARQUBE_LOGIN_KEY} \
    -Dsonar.organization=sensynehealth \
    -Dsonar.projectKey=${PROJECT_KEY} \
    -Dsonar.${COVERAGE_TECHNOLOGY}.reportPaths="${COVERAGE_REPORT_DIR}/${COVERAGE_REPORT_FILE}" \
    -Dsonar.analysis.scmRevision=${GIT_HASH} \
    -X