#!/usr/bin/env bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _functional_test_common.sh

function main {
	echo "Removing the functional test containers and volumes."

	docker_compose down --volumes
}

main "${@}"