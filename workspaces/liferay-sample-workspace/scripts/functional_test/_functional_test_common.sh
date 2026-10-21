#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/../_common.sh"

WORKSPACE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

#
# docker-compose.functional-test.yaml is shared and syncs from
# liferay-sample-workspace. docker-compose-functional-test.yaml, when it
# exists, belongs to this workspace, and the docker_compose function includes
# both for the same override name. The microservices override is included only
# when the workspace has one.
#

LIFERAY_COMPOSE_OVERRIDES=functional-test

if [[ -f "${WORKSPACE_DIR}/docker-compose.microservices.yaml" ]] ||
[[ -f "${WORKSPACE_DIR}/docker-compose-microservices.yaml" ]]
then
	LIFERAY_COMPOSE_OVERRIDES=microservices,${LIFERAY_COMPOSE_OVERRIDES}
fi

export LIFERAY_COMPOSE_OVERRIDES

if [[ -z ${LIFERAY_DOCKER_IMAGE:-} ]]
then
	LIFERAY_DOCKER_IMAGE="$(basename "${WORKSPACE_DIR}")-liferay:$(get_gradle_property liferay.workspace.product | sed "s/^dxp-//")"
fi

export LIFERAY_DOCKER_IMAGE

#
# The microservices are every service the overrides add on top of the base
# docker-compose.yaml.
#

function get_microservice_names {
	local base_services

	base_services=$(docker compose --file "${WORKSPACE_DIR}/docker-compose.yaml" config --services)

	docker_compose config --services | grep --invert-match --line-regexp --fixed-strings "${base_services}" || true
}