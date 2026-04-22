#!/bin/bash

set -euo pipefail

##
## Default ports (reserved for main worktree — never used in a secondary worktree)
##

DEFAULT_TOMCAT_SHUTDOWN=8005
DEFAULT_TOMCAT_HTTP=8080
DEFAULT_TOMCAT_HTTPS_REDIRECT=8443
DEFAULT_TOMCAT_AJP=8009
DEFAULT_OSGI_CONSOLE=11311
DEFAULT_ES_SIDECAR_HTTP=9201
DEFAULT_ES_TRANSPORT=9301
DEFAULT_ARQUILLIAN=32763
DEFAULT_DATAGUARD=42763
DEFAULT_GLOWROOT=4000
DEFAULT_DB_NAME="lportal"

##
## Ports to check for availability (those that actually bind at runtime)
##

CHECKED_DEFAULTS=(
	"${DEFAULT_TOMCAT_HTTP}"
	"${DEFAULT_TOMCAT_SHUTDOWN}"
	"${DEFAULT_OSGI_CONSOLE}"
	"${DEFAULT_ES_SIDECAR_HTTP}"
	"${DEFAULT_ES_TRANSPORT}"
	"${DEFAULT_GLOWROOT}"
)

##
## Script state
##

ARG_BUNDLE_DIR=""
ARG_DRY_RUN=false
ARG_FORCE_RESCAN=false
ARG_OFFSET=""
ARG_STATUS=false

BUNDLE_DIR=""
REPO_ROOT=""
TOMCAT_DIR=""

##
## Helpers
##

function sed_inplace() {
	if command -v gsed &>/dev/null; then
		gsed -i "$@"
	elif [[ "$(uname)" == "Darwin" ]]; then
		sed -i '' "$@"
	else
		sed -i "$@"
	fi
}

function derive_db_name() {
	local worktree_dir

	worktree_dir=$(basename "${REPO_ROOT}")

	# Strip common "liferay-portal-" prefix if present

	local suffix="${worktree_dir#liferay-portal-}"

	# If stripping removed everything (dir was exactly "liferay-portal"), this is the main worktree

	if [[ -z "${suffix}" || "${suffix}" == "liferay-portal" ]]; then
		echo "${DEFAULT_DB_NAME}"

		return
	fi

	# Sanitize: lowercase, replace non-alphanumeric with underscore, collapse multiple underscores, trim

	local sanitized

	sanitized=$(echo "${suffix}" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9]/_/g' | sed 's/__*/_/g' | sed 's/^_//;s/_$//')

	# Truncate to fit MySQL's 64-char limit (lportal_ = 8 chars, leaves 56)

	sanitized="${sanitized:0:56}"

	echo "${DEFAULT_DB_NAME}_${sanitized}"
}

function is_port_available() {
	local port=$1

	if nc -z localhost "${port}" 2>/dev/null; then
		return 1
	else
		return 0
	fi
}

function find_available_offset() {
	local offset=1

	while [[ ${offset} -le 100 ]]; do
		local all_available=true

		for default_port in "${CHECKED_DEFAULTS[@]}"; do
			local candidate=$(( default_port + offset ))

			if ! is_port_available "${candidate}"; then
				all_available=false

				break
			fi
		done

		if [[ "${all_available}" == "true" ]]; then
			echo "${offset}"

			return 0
		fi

		offset=$(( offset + 1 ))
	done

	echo "ERROR: Could not find an available port offset after 100 attempts." >&2

	return 1
}

##
## Bundle directory resolution
##

function resolve_bundle_dir() {
	REPO_ROOT=$(git -C "$(dirname "${BASH_SOURCE[0]}")" rev-parse --show-toplevel)

	if [[ -n "${ARG_BUNDLE_DIR}" ]]; then
		BUNDLE_DIR="${ARG_BUNDLE_DIR}"
	else
		local user_props="${REPO_ROOT}/app.server.$(whoami).properties"

		if [[ -f "${user_props}" ]]; then
			local parent_dir

			parent_dir=$(grep '^[[:space:]]*app\.server\.parent\.dir=' "${user_props}" | tail -1 | sed 's/^[[:space:]]*app\.server\.parent\.dir=//')

			parent_dir="${parent_dir//\$\{project\.dir\}/${REPO_ROOT}}"

			if [[ -d "${parent_dir}" ]]; then
				BUNDLE_DIR="${parent_dir}"
			fi
		fi

		if [[ -z "${BUNDLE_DIR}" ]]; then
			BUNDLE_DIR="${REPO_ROOT}/bundles"
		fi
	fi

	if [[ ! -d "${BUNDLE_DIR}" ]]; then
		echo "ERROR: Bundle directory does not exist: ${BUNDLE_DIR}" >&2

		exit 1
	fi

	BUNDLE_DIR=$(cd "${BUNDLE_DIR}" && pwd)

	TOMCAT_DIR=$(find "${BUNDLE_DIR}" -maxdepth 1 -type d -name "tomcat-*" | sort -V | tail -1)

	if [[ -z "${TOMCAT_DIR}" ]]; then
		echo "ERROR: No tomcat-* directory found in ${BUNDLE_DIR}" >&2

		exit 1
	fi
}

##
## Offset persistence
##

function get_or_find_offset() {
	local offset_file="${BUNDLE_DIR}/.worktree-port-offset"

	if [[ -n "${ARG_OFFSET}" ]]; then
		if ! [[ "${ARG_OFFSET}" =~ ^[0-9]+$ ]] || [[ "${ARG_OFFSET}" -eq 0 ]]; then
			echo "ERROR: --offset requires a positive integer (offset 0 is reserved for the main worktree)." >&2

			exit 1
		fi

		echo "${ARG_OFFSET}" > "${offset_file}"
		echo "${ARG_OFFSET}"

		return
	fi

	if [[ "${ARG_FORCE_RESCAN}" == "false" && -f "${offset_file}" ]]; then
		local saved_offset

		saved_offset=$(cat "${offset_file}")

		if [[ "${saved_offset}" =~ ^[0-9]+$ && "${saved_offset}" -gt 0 ]]; then
			echo "${saved_offset}"

			return
		fi
	fi

	local offset

	offset=$(find_available_offset)

	echo "${offset}" > "${offset_file}"
	echo "${offset}"
}

##
## Patching functions
##

function patch_server_xml() {
	local offset=$1
	local server_xml="${TOMCAT_DIR}/conf/server.xml"

	if [[ ! -f "${server_xml}" ]]; then
		echo "  WARNING: server.xml not found at ${server_xml}"

		return
	fi

	local target_http=$(( DEFAULT_TOMCAT_HTTP + offset ))
	local target_shutdown=$(( DEFAULT_TOMCAT_SHUTDOWN + offset ))
	local target_https=$(( DEFAULT_TOMCAT_HTTPS_REDIRECT + offset ))
	local target_ajp=$(( DEFAULT_TOMCAT_AJP + offset ))

	if grep -q "port=\"${target_http}\"" "${server_xml}"; then
		echo "  [SKIP] server.xml already patched"

		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would patch server.xml"

		return
	fi

	# Determine current offset by reading the HTTP connector port from server.xml

	local current_http

	current_http=$(grep 'protocol="HTTP/1.1"' "${server_xml}" | grep -o 'port="[0-9]*"' | head -1 | grep -o '[0-9]*')

	if [[ -z "${current_http}" ]]; then
		current_http=$(grep -o 'Connector[^>]*port="[0-9]*"' "${server_xml}" | head -1 | grep -o 'port="[0-9]*"' | grep -o '[0-9]*')
	fi

	if [[ -z "${current_http}" ]]; then
		echo "  WARNING: Could not parse HTTP port from server.xml"

		return
	fi

	local current_offset=$(( current_http - DEFAULT_TOMCAT_HTTP ))

	local current_shutdown=$(( DEFAULT_TOMCAT_SHUTDOWN + current_offset ))
	local current_https=$(( DEFAULT_TOMCAT_HTTPS_REDIRECT + current_offset ))
	local current_ajp=$(( DEFAULT_TOMCAT_AJP + current_offset ))

	sed_inplace \
		-e "s/port=\"${current_shutdown}\"/port=\"${target_shutdown}\"/g" \
		-e "s/port=\"${current_http}\"/port=\"${target_http}\"/g" \
		-e "s/port=\"${current_ajp}\"/port=\"${target_ajp}\"/g" \
		-e "s/port=\"${current_https}\"/port=\"${target_https}\"/g" \
		-e "s/redirectPort=\"${current_https}\"/redirectPort=\"${target_https}\"/g" \
		"${server_xml}"

	echo "  [OK] server.xml: shutdown=${target_shutdown} http=${target_http} https-redirect=${target_https} ajp=${target_ajp}"
}

function patch_portal_developer_properties() {
	local offset=$1
	local dev_props="${TOMCAT_DIR}/webapps/ROOT/WEB-INF/classes/portal-developer.properties"

	if [[ ! -f "${dev_props}" ]]; then
		echo "  [SKIP] portal-developer.properties not found (not yet deployed?)"

		return
	fi

	local target_port=$(( DEFAULT_OSGI_CONSOLE + offset ))

	if grep -q "osgi\.console=localhost:${target_port}" "${dev_props}"; then
		echo "  [SKIP] portal-developer.properties already patched"

		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would patch portal-developer.properties"

		return
	fi

	sed_inplace "s/osgi\.console=localhost:[0-9]*/osgi.console=localhost:${target_port}/" "${dev_props}"

	echo "  [OK] portal-developer.properties: osgi.console=localhost:${target_port}"
}

function create_elasticsearch_config() {
	local offset=$1
	local config_dir="${BUNDLE_DIR}/osgi/configs"
	local config_file="${config_dir}/com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration.config"

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would create ElasticsearchConfiguration.config"

		return
	fi

	mkdir -p "${config_dir}"

	local http_port=$(( DEFAULT_ES_SIDECAR_HTTP + offset ))
	local transport_port=$(( DEFAULT_ES_TRANSPORT + offset ))

	cat > "${config_file}" <<EOF
sidecarHttpPort="${http_port}"
transportTcpPort="${transport_port}"
networkBindHost="127.0.0.1"
networkPublishHost="127.0.0.1"
EOF

	echo "  [OK] ElasticsearchConfiguration.config: sidecarHttpPort=${http_port} transportTcpPort=${transport_port}"
}

function create_arquillian_config() {
	local offset=$1
	local config_dir="${BUNDLE_DIR}/osgi/configs"
	local config_file="${config_dir}/com.liferay.arquillian.extension.junit.bridge.connector.ArquillianConnector.config"

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would create ArquillianConnector.config"

		return
	fi

	mkdir -p "${config_dir}"

	local port=$(( DEFAULT_ARQUILLIAN + offset ))

	cat > "${config_file}" <<EOF
port="${port}"
EOF

	echo "  [OK] ArquillianConnector.config: port=${port}"
}

function create_dataguard_config() {
	local offset=$1
	local config_dir="${BUNDLE_DIR}/osgi/configs"
	local config_file="${config_dir}/com.liferay.data.guard.connector.DataGuardConnector.config"

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would create DataGuardConnector.config"

		return
	fi

	mkdir -p "${config_dir}"

	local port=$(( DEFAULT_DATAGUARD + offset ))

	cat > "${config_file}" <<EOF
port="${port}"
EOF

	echo "  [OK] DataGuardConnector.config: port=${port}"
}

function patch_glowroot() {
	local offset=$1
	local admin_json="${BUNDLE_DIR}/glowroot/admin.json"

	if [[ ! -f "${admin_json}" ]]; then
		echo "  [SKIP] glowroot/admin.json not found"

		return
	fi

	if ! command -v jq &>/dev/null; then
		echo "  WARNING: jq not found. Cannot patch glowroot/admin.json"
		echo "    Install jq or manually set .web.port to $(( DEFAULT_GLOWROOT + offset )) in ${admin_json}"

		return
	fi

	local target_port=$(( DEFAULT_GLOWROOT + offset ))
	local current_port

	current_port=$(jq '.web.port' "${admin_json}")

	if [[ "${current_port}" == "${target_port}" ]]; then
		echo "  [SKIP] glowroot/admin.json already patched"

		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would patch glowroot/admin.json"

		return
	fi

	local tmp_file="${admin_json}.tmp"

	jq ".web.port = ${target_port}" "${admin_json}" > "${tmp_file}" && mv "${tmp_file}" "${admin_json}"

	echo "  [OK] glowroot/admin.json: port=${target_port}"
}

function patch_portal_ext_properties() {
	local offset=$1
	local props_file="${BUNDLE_DIR}/portal-ext.properties"
	local http_port=$(( DEFAULT_TOMCAT_HTTP + offset ))

	local expected_lines=(
		"include-and-override=portal-developer.properties"
		"portal.instance.inet.socket.address=localhost:${http_port}"
		"browser.launcher.url="
		"setup.wizard.enabled=false"
	)

	if [[ ! -f "${props_file}" ]]; then
		if [[ "${ARG_DRY_RUN}" == "true" ]]; then
			echo "  [DRY RUN] Would create portal-ext.properties"

			return
		fi

		for line in "${expected_lines[@]}"; do
			echo "${line}" >> "${props_file}"
		done

		echo "  [OK] portal-ext.properties: created with worktree properties"

		return
	fi

	local needs_update=false

	for line in "${expected_lines[@]}"; do
		if ! grep -qF "${line}" "${props_file}"; then
			needs_update=true

			break
		fi
	done

	if [[ "${needs_update}" == "false" ]]; then
		echo "  [SKIP] portal-ext.properties: already correct"

		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would patch portal-ext.properties"

		return
	fi

	sed_inplace \
		-e '/^include-and-override=portal-developer\.properties$/d' \
		-e '/^portal\.instance\.inet\.socket\.address=/d' \
		-e '/^portal\.instance\.http\.socket\.address=/d' \
		-e '/^browser\.launcher\.url=/d' \
		-e '/^setup\.wizard\.enabled=/d' \
		"${props_file}"

	for line in "${expected_lines[@]}"; do
		echo "${line}" >> "${props_file}"
	done

	echo "  [OK] portal-ext.properties: include-and-override=portal-developer.properties"
	echo "  [OK] portal-ext.properties: portal.instance.inet.socket.address=localhost:${http_port}"
	echo "  [OK] portal-ext.properties: browser.launcher.url= (disabled)"
}

function configure_database() {
	local offset=$1
	local props_file="${BUNDLE_DIR}/portal-ext.properties"
	local db_name

	db_name=$(derive_db_name)
	local jdbc_url="jdbc:mysql://localhost/${db_name}?characterEncoding=UTF-8&dontTrackOpenResources=true&holdResultsOpenOverStatementClose=true&serverTimezone=GMT&useFastDateParsing=false&useUnicode=true"

	if [[ ! -f "${props_file}" ]]; then
		if [[ "${ARG_DRY_RUN}" == "true" ]]; then
			echo "  [DRY RUN] Would create portal-ext.properties with MySQL config"

			return
		fi

		{
			echo "jdbc.default.driverClassName=com.mysql.cj.jdbc.Driver"
			echo "jdbc.default.url=${jdbc_url}"
			echo "jdbc.default.username=root"
			echo "jdbc.default.password="
		} >> "${props_file}"

		echo "  [OK] portal-ext.properties: MySQL database=${db_name}"

		_try_create_database "${db_name}" "root" ""

		return
	fi

	if grep -q "^jdbc\.default\.url=.*/${db_name}?" "${props_file}"; then
		echo "  [SKIP] portal-ext.properties: MySQL database already set to ${db_name}"

		local db_user="root"
		local db_pass=""

		if grep -q '^jdbc\.default\.username=' "${props_file}"; then
			db_user=$(grep '^jdbc\.default\.username=' "${props_file}" | tail -1 | sed 's/^jdbc\.default\.username=//')
		fi

		if grep -q '^jdbc\.default\.password=' "${props_file}"; then
			db_pass=$(grep '^jdbc\.default\.password=' "${props_file}" | tail -1 | sed 's/^jdbc\.default\.password=//')
		fi

		_try_create_database "${db_name}" "${db_user}" "${db_pass}"

		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would configure MySQL database=${db_name} in portal-ext.properties"

		return
	fi

	# Read existing username/password if present

	local db_user="root"
	local db_pass=""

	if grep -q '^jdbc\.default\.username=' "${props_file}"; then
		db_user=$(grep '^jdbc\.default\.username=' "${props_file}" | tail -1 | sed 's/^jdbc\.default\.username=//')
	fi

	if grep -q '^jdbc\.default\.password=' "${props_file}"; then
		db_pass=$(grep '^jdbc\.default\.password=' "${props_file}" | tail -1 | sed 's/^jdbc\.default\.password=//')
	fi

	# Remove existing JDBC properties

	sed_inplace \
		-e '/^jdbc\.default\.driverClassName=/d' \
		-e '/^jdbc\.default\.url=/d' \
		-e '/^jdbc\.default\.username=/d' \
		-e '/^jdbc\.default\.password=/d' \
		"${props_file}"

	# Append new JDBC properties

	{
		echo "jdbc.default.driverClassName=com.mysql.cj.jdbc.Driver"
		echo "jdbc.default.url=${jdbc_url}"
		echo "jdbc.default.username=${db_user}"
		echo "jdbc.default.password=${db_pass}"
	} >> "${props_file}"

	echo "  [OK] portal-ext.properties: MySQL database=${db_name} user=${db_user}"

	_try_create_database "${db_name}" "${db_user}" "${db_pass}"
}

function _try_create_database() {
	local db_name=$1
	local db_user="${2:-root}"
	local db_pass="${3:-}"

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would attempt to create database ${db_name}"

		return
	fi

	local create_sql="CREATE DATABASE IF NOT EXISTS ${db_name} CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci"

	if ! command -v mysql &>/dev/null; then
		echo ""
		echo "  WARNING: mysql CLI not found. Please create the database manually:"
		echo "    mysql -u ${db_user} -e '${create_sql};'"

		return
	fi

	local mysql_args=(-u "${db_user}")

	if [[ -n "${db_pass}" ]]; then
		mysql_args+=(-p"${db_pass}")
	fi

	local mysql_output

	if mysql_output=$(mysql "${mysql_args[@]}" -e "${create_sql};" 2>&1); then
		echo "  [OK] MySQL database '${db_name}' created (or already exists)"
	else
		echo ""
		echo "  WARNING: Could not auto-create database:"
		echo "    ${mysql_output}"
		echo ""
		echo "  Please run manually:"
		echo "    mysql -u ${db_user} -e '${create_sql};'"
	fi
}

function clear_osgi_state() {
	local state_dir="${BUNDLE_DIR}/osgi/state"

	if [[ ! -d "${state_dir}" ]]; then
		return
	fi

	if [[ "${ARG_DRY_RUN}" == "true" ]]; then
		echo "  [DRY RUN] Would clear osgi/state cache"

		return
	fi

	rm -rf "${state_dir}"

	echo "  [OK] Cleared osgi/state cache"
}

##
## Status / summary
##

function print_port_table() {
	local offset=$1

	echo ""
	echo "=== Port Assignment (offset=${offset}) ==="
	echo ""

	printf "  %-24s %s\n" "Tomcat Shutdown:" "$(( DEFAULT_TOMCAT_SHUTDOWN + offset ))"
	printf "  %-24s %s\n" "Tomcat HTTP:" "$(( DEFAULT_TOMCAT_HTTP + offset ))"
	printf "  %-24s %s\n" "Tomcat HTTPS Redirect:" "$(( DEFAULT_TOMCAT_HTTPS_REDIRECT + offset ))"
	printf "  %-24s %s\n" "Tomcat AJP:" "$(( DEFAULT_TOMCAT_AJP + offset ))"
	printf "  %-24s %s\n" "OSGi Console:" "$(( DEFAULT_OSGI_CONSOLE + offset ))"
	printf "  %-24s %s\n" "ES Sidecar HTTP:" "$(( DEFAULT_ES_SIDECAR_HTTP + offset ))"
	printf "  %-24s %s\n" "ES Transport:" "$(( DEFAULT_ES_TRANSPORT + offset ))"
	printf "  %-24s %s\n" "Arquillian:" "$(( DEFAULT_ARQUILLIAN + offset ))"
	printf "  %-24s %s\n" "DataGuard:" "$(( DEFAULT_DATAGUARD + offset ))"
	printf "  %-24s %s\n" "Glowroot:" "$(( DEFAULT_GLOWROOT + offset ))"
	printf "  %-24s %s\n" "MySQL Database:" "$(derive_db_name)"

	echo ""
	echo "  Liferay URL: http://localhost:$(( DEFAULT_TOMCAT_HTTP + offset ))"
	echo "  Glowroot URL: http://localhost:$(( DEFAULT_GLOWROOT + offset ))/o/glowroot"
	echo ""
}

function do_status() {
	resolve_bundle_dir

	local offset_file="${BUNDLE_DIR}/.worktree-port-offset"

	echo "Bundle: ${BUNDLE_DIR}"
	echo "Tomcat: ${TOMCAT_DIR}"

	if [[ ! -f "${offset_file}" ]]; then
		echo ""
		echo "Not configured. Run without --status to set up."

		exit 0
	fi

	local offset

	offset=$(cat "${offset_file}")

	print_port_table "${offset}"
}

##
## CLI
##

function usage() {
	cat <<-'USAGE'
	Usage: setup-worktree-ports.sh [OPTIONS] [BUNDLE_DIR]

	Set up a Liferay bundle with unique ports for worktree isolation.

	Options:
	  -f, --force-rescan   Ignore saved offset and scan for new available ports
	  -n, --dry-run        Show what would be changed without modifying files
	  -o, --offset N       Use specific offset N (skip port scanning)
	  -s, --status         Print assigned ports without modifying anything
	  -h, --help           Show this help message

	Arguments:
	  BUNDLE_DIR           Path to the Liferay bundle directory.
	                       Auto-detected from app.server.${USER}.properties if omitted,
	                       falls back to ${REPO_ROOT}/bundles/.
	USAGE
}

function parse_args() {
	while [[ $# -gt 0 ]]; do
		case "$1" in
			-f|--force-rescan)
				ARG_FORCE_RESCAN=true

				shift
				;;
			-h|--help)
				usage

				exit 0
				;;
			-n|--dry-run)
				ARG_DRY_RUN=true

				shift
				;;
			-o|--offset)
				if [[ -z "${2:-}" ]]; then
					echo "ERROR: --offset requires a numeric argument" >&2

					exit 1
				fi

				ARG_OFFSET="$2"

				shift 2
				;;
			-s|--status)
				ARG_STATUS=true

				shift
				;;
			-*)
				echo "ERROR: Unknown option: $1" >&2

				usage

				exit 1
				;;
			*)
				ARG_BUNDLE_DIR="$1"

				shift
				;;
		esac
	done
}

##
## Main
##

function main() {
	parse_args "$@"

	if [[ "${ARG_STATUS}" == "true" ]]; then
		do_status

		return
	fi

	resolve_bundle_dir

	echo "Bundle: ${BUNDLE_DIR}"
	echo "Tomcat: $(basename "${TOMCAT_DIR}")"

	local offset

	offset=$(get_or_find_offset)

	echo ""
	echo "=== Applying offset: ${offset} ==="
	echo ""

	patch_server_xml "${offset}"
	patch_portal_developer_properties "${offset}"
	create_elasticsearch_config "${offset}"
	create_arquillian_config "${offset}"
	create_dataguard_config "${offset}"
	patch_glowroot "${offset}"
	patch_portal_ext_properties "${offset}"
	configure_database "${offset}"
	clear_osgi_state

	print_port_table "${offset}"

	echo "Offset saved to: ${BUNDLE_DIR}/.worktree-port-offset"
	echo "Restart Tomcat to apply changes."
}

main "$@"
