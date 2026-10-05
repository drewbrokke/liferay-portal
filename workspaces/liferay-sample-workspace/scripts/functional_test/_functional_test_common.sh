#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/../_common.sh"

export LIFERAY_COMPOSE_OVERRIDES=microservices,functional-test

if [[ -z ${LIFERAY_DOCKER_IMAGE:-} ]]
then
	LIFERAY_DOCKER_IMAGE="$(basename "$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)")-liferay:$(get_gradle_property liferay.workspace.product | sed "s/^dxp-//")"
fi

export LIFERAY_DOCKER_IMAGE

MICROSERVICE_NAMES=(
	liferay-sample-etc-node
	liferay-sample-etc-spring-boot
)