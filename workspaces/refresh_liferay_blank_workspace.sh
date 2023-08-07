#!/bin/bash

cd $(dirname "${0}")

function check_blade {
	if [ -e ~/jpm/bin/blade ]
	then
		BLADE_PATH=~/jpm/bin/blade
	fi

	if [ -e ~/Library/PackageManager/bin/blade ]
	then
		BLADE_PATH=~/Library/PackageManager/bin/blade
	fi

	if [ -z "${BLADE_PATH}" ]
	then
		echo "Blade CLI is not available. To install Blade CLI, execute the following command:"
		echo ""

		echo "curl -L https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/cli/installers/local | sh"

		exit 1
	fi

	#
	# Update Blade with Blade.
	#

	#${BLADE_PATH} update -s > /dev/null

	#
	# Update Blade directly with JPM.
	#

	#jpm install -f https://repository-cdn.liferay.com/nexus/service/local/repositories/liferay-public-releases/content/com/liferay/blade/com.liferay.blade.cli/4.1.1/com.liferay.blade.cli-4.1.1.jar
}

function refresh_liferay_blank_workspace_product_version {
	local latest_dxp_version="$(${BLADE_PATH} init --all --list | grep dxp-7.4 | head -1)"

	local sed_in_place_command=(sed -i)

	# https://unix.stackexchange.com/questions/208548/how-can-i-tell-whether-my-system-is-unix-or-linux

	if [ "$(uname)" = "Darwin" ]
	then

		# https://stackoverflow.com/questions/12696125/sed-edit-file-in-place

		sed_in_place_command+=('')
	fi

	"${sed_in_place_command[@]}" "s/liferay.workspace.product=.*/liferay.workspace.product=${latest_dxp_version}/" liferay-blank-workspace/gradle.properties
}

function main {
	check_blade

	refresh_liferay_blank_workspace_product_version
}

main "${@}"