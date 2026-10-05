#!/usr/bin/env bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source ../_common.sh

function main {
	local product

	product=$(get_gradle_property liferay.workspace.product)

	local version_tag

	version_tag=$(echo "${product}" | sed --expression "s/^dxp-//")

	if [[ -f pre-build.sh ]]
	then
		source pre-build.sh
	fi

	../../gradlew --project-dir ../.. clean

	./extract_hotfix.sh
	./extract_license.sh

	echo "Building Docker image."

	../../gradlew --project-dir ../.. buildDockerImage

	local built_image

	built_image="$(basename "$(dirname "$(dirname "${PWD}")")")-liferay:${version_tag}"

	local liferay_image

	liferay_image=$(get_liferay_image)

	echo "Tagging ${built_image} as ${liferay_image} and liferay:local."

	docker tag "${built_image}" "${liferay_image}"

	#
	# Convenience alias for running "docker compose up" without these scripts.
	# It points at whichever workspace built last; a running stack keeps the
	# workspace specific tag it was created from.
	#
	docker tag "${built_image}" "liferay:local"

	if [[ -f post-build.sh ]]
	then
		source post-build.sh
	fi
}

main "${@}"