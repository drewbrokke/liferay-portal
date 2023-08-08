#!/bin/bash

cd $(dirname "${0}")

function main {
	for dir in ./liferay-*-workspace
	do
		if [ ${dir} = "./liferay-blank-workspace" ]
		then
			continue
		fi

		rsync \
			-a --delete \
			--exclude "README.markdown" \
			--exclude "client-extensions" \
			--exclude "modules" \
			--exclude "node_modules" \
			--exclude "node_modules_cache" \
			--exclude "poshi/build.gradle" \
			--exclude "poshi/poshi-ext.properties" \
			--exclude "poshi/src" \
			--exclude "poshi" \
			--exclude "build.gradle" \
			--exclude "test.properties" \
			--exclude "themes" \
			liferay-blank-workspace/ ${dir}
	done
}

main "${@}"