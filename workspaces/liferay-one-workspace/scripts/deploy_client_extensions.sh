#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

source _common.sh

function main {
	cd ..

	./gradlew deploy \
		-Ddeploy.docker.container.id="$(docker ps --quiet --filter "name=^liferay$")"

	echo "Rebuilding Spring Boot client extension image."
	bash client-extensions/liferay-one-etc-spring-boot/scripts/build_spring_boot_image.sh

	echo "Recreating Spring Boot client extension container."
	docker compose up --detach liferay-one-etc-spring-boot
}

main "${@}"