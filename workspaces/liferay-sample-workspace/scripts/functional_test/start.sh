#!/usr/bin/env bash

#
# Stands up the functional test stack: PostgreSQL, Liferay, and the
# microservice client extensions. Safe to run repeatedly. A running stack is
# reused, and only client extensions whose zip changed are redeployed.
#
# Expects the Liferay image and the client extension zips to be built, which
# the startTestEnvironment Gradle task does before calling this script.
#

cd "$(dirname "${BASH_SOURCE[0]}")"

source _functional_test_common.sh

CLIENT_EXTENSIONS_DIR=/opt/liferay/osgi/client-extensions

CLIENT_EXTENSIONS_REDEPLOYED=false

function check_image {
	if ! docker image inspect "${LIFERAY_DOCKER_IMAGE}" > /dev/null 2>&1
	then
		_die "The image ${LIFERAY_DOCKER_IMAGE} does not exist. Run \"./gradlew buildDockerImage\" first."
	fi
}

function deploy_client_extensions {
	declare -A deployed_checksums

	local checksum
	local file_path

	while read -r checksum file_path
	do
		deployed_checksums[$(basename "${file_path}")]=${checksum}
	done < <(docker_compose exec -T liferay sh -c "sha256sum ${CLIENT_EXTENSIONS_DIR}/*.zip 2> /dev/null" || true)

	local deployed_count=0
	local zip_file

	for zip_file in ../../client-extensions/*/dist/*.zip
	do
		local zip_file_name

		zip_file_name=$(basename "${zip_file}")

		checksum=$(sha256sum "${zip_file}" | cut --delimiter " " --fields 1)

		if [[ ${deployed_checksums[${zip_file_name}]:-} == "${checksum}" ]]
		then
			continue
		fi

		echo "Deploying ${zip_file_name}."

		docker_compose cp "${zip_file}" "liferay:${CLIENT_EXTENSIONS_DIR}/${zip_file_name}"

		deployed_count=$((deployed_count + 1))
	done

	if [[ ${deployed_count} -eq 0 ]]
	then
		echo "Every client extension is already deployed."

		return
	fi

	CLIENT_EXTENSIONS_REDEPLOYED=true

	wait_for_portal_log_quiet
}

function reset_if_product_changed {
	local container_id

	container_id=$(docker_compose ps --all --quiet liferay)

	if [[ -z ${container_id} ]]
	then
		return
	fi

	local container_image

	container_image=$(docker inspect --format "{{.Config.Image}}" "${container_id}")

	if [[ ${container_image} == "${LIFERAY_DOCKER_IMAGE}" ]]
	then
		return
	fi

	echo "The stack runs ${container_image}, not ${LIFERAY_DOCKER_IMAGE}. Removing it so the new version starts from an empty database."

	docker_compose down --volumes
}

function start_liferay {
	echo "Starting PostgreSQL and Liferay. A fresh database takes several minutes."

	docker_compose up --detach --wait --wait-timeout 900 database liferay
}

function start_microservices {
	local microservice_names

	mapfile -t microservice_names < <(get_microservice_names)

	if [[ ${#microservice_names[@]} -eq 0 ]]
	then
		return
	fi

	local up_args=(--build --detach --wait --wait-timeout 300)

	#
	# Redeploying a client extension gives its OAuth application a new client
	# ID, and a running microservice only reads the routes at startup, so
	# recreate the microservices after any redeploy.
	#

	if [[ ${CLIENT_EXTENSIONS_REDEPLOYED} == true ]]
	then
		up_args+=(--force-recreate)
	fi

	echo "Starting ${microservice_names[*]}."

	docker_compose up "${up_args[@]}" "${microservice_names[@]}"
}

#
# A deployed client extension logs nothing reliable across versions, so wait
# until the portal log stops growing for a while instead.
#

function wait_for_portal_log_quiet {
	local quiet_seconds=20
	local timeout_seconds=600

	local elapsed_seconds=0
	local last_line_count=-1
	local quiet_elapsed_seconds=0

	echo "Waiting for the portal log to stay quiet for ${quiet_seconds} seconds."

	while [[ ${quiet_elapsed_seconds} -lt ${quiet_seconds} ]]
	do
		if [[ ${elapsed_seconds} -ge ${timeout_seconds} ]]
		then
			_die "The portal log was still active after ${timeout_seconds} seconds."
		fi

		sleep 5

		elapsed_seconds=$((elapsed_seconds + 5))

		local line_count

		line_count=$(docker_compose logs --no-log-prefix liferay 2> /dev/null | wc --lines)

		if [[ ${line_count} -eq ${last_line_count} ]]
		then
			quiet_elapsed_seconds=$((quiet_elapsed_seconds + 5))
		else
			quiet_elapsed_seconds=0
		fi

		last_line_count=${line_count}
	done
}

function main {
	check_image

	reset_if_product_changed

	start_liferay

	deploy_client_extensions

	start_microservices

	echo "The functional test stack is running at http://localhost:${LIFERAY_FUNCTIONAL_TEST_PORT:-18080}."
}

main "${@}"