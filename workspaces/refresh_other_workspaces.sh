#!/bin/bash

cd $(dirname "${0}")

function main {
	for dir in ./liferay-*-workspace
	do
		if [ ${dir} = "./liferay-blank-workspace" ]
		then
			continue
		fi

		# Do not copy from source

		rsync_excludes=(
			--exclude "configs/common"
			--exclude "configs/dev"
			--exclude "configs/docker"
			--exclude "configs/prod"
			--exclude "configs/uat"
			--exclude "Dockerfile.ext"
			--exclude "GETTING_STARTED.markdown"
			--exclude "gradle-local.properties"
			--exclude "modules"
			--exclude "platform.bndrun"
			--exclude "themes"
		)

		# Do not delete from destination

		rsync_excludes+=(
			--exclude "build.gradle"
			--exclude "client-extensions"
			--exclude "node_modules"
			--exclude "node_modules_cache"
			--exclude "poshi"
			--exclude "poshi/build.gradle"
			--exclude "poshi/poshi-ext.properties"
			--exclude "poshi/poshi.properties"
			--exclude "poshi/src"
			--exclude "README.markdown"
			--exclude "test.properties"
		)

		rsync -a --delete "${rsync_excludes[@]}" liferay-blank-workspace/ ${dir}
	done
}

main "${@}"