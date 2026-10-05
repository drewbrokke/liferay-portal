#!/bin/bash

#
# Mounted as the runner image's set up hook. Liferay writes the DXP routes
# when it deploys the first client extension, and the samples read them once
# at startup, so hold the process until they exist.
#

set -o errexit
set -o nounset
set -o pipefail

function main {
	local main_domain_file="${LIFERAY_ROUTES_DXP}/com.liferay.lxc.dxp.mainDomain"

	until [[ -f ${main_domain_file} ]]
	do
		echo "Waiting for ${main_domain_file}."

		sleep 2
	done

	echo "Found the DXP routes."
}

main "${@}"