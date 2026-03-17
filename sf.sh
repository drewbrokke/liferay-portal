#!/bin/bash

# sf.sh - Liferay Source Formatter Wrapper
# This script wraps Ant targets in portal-impl/build.xml for easier access.

set -e

# Path to portal-impl build file
ANT_FILE="portal-impl/build.xml"

usage() {
	echo "Usage: ./sf.sh [options] [target]"
	echo ""
	echo "Targets:"
	echo "  (default)           Runs format-source (modified files in current branch + commit checks)"
	echo "  all                 Runs format-source-all (all files in the repository)"
	echo "  author              Runs format-source-latest-author"
	echo "  bnd                 Runs format-source-bnd (.bnd, .gradle, .json)"
	echo "  current             Runs format-source-current-branch"
	echo "  deprecated          Runs format-source-deprecated-api"
	echo "  list-checks         List all available check names"
	echo "  local               Runs format-source-local-changes"
	echo "  missing-override    Runs format-source-missing-override"
	echo ""
	echo "Options:"
	echo "  -d, --directory [dir]    A directory to format"
	echo "  -f, --files [list]       Comma-separated list of files to format"
	echo "  -e, --extensions [list]  Comma-separated list of extensions (java,jsp,xml,etc.)"
	echo "  -n, --checks [list]      Comma-separated list of specific check names to run"
	echo "  -s, --skip [list]        Comma-separated list of checks to skip"
	echo "  --no-fix                 Set source.auto.fix=false (Dry run)"
	echo "  --debug              Run with debug information (format-source-debug)"
	echo "  -h, --help               Show this help message"
	echo ""
	echo "Examples:"
	echo "  ./sf.sh                  # Format changes in current branch"
	echo "  ./sf.sh all              # Format everything"
	echo "  ./sf.sh -f file1,file2   # Format specific files"
	echo "  ./sf.sh --no-fix         # Check for violations without fixing"
	exit 0
}

list_checks() {
	git ls-files -- 'modules/util/source-formatter/**/*Check.java' | sed -e "s,^.*/,,g" -e "s,.java$,,g" | sort -u
}

check_check() {
	local check=$1
	list_checks | grep -q "^${check}$"
}

check_arg() {
	if [[ -z "$2" || "$2" == -* ]]; then
		echo "Error: Option $1 requires a non-empty argument."
		exit 1
	fi
}

check_checks() {
	local arg_name=$1
	local checks=$2
	IFS=',' read -ra ADDR <<< "$checks"
	for check in "${ADDR[@]}"; do
		if ! check_check "$check"; then
			echo "Error: Check name '$check' passed to $arg_name does not exist."
			echo "Run ./sf.sh list-checks to see possible check names."
			# shellcheck disable=SC2046
			# printf "%s, " $(list_checks)
			exit 1
		fi
	done
}

check_files() {
	local arg_name=$1
	local files=$2
	IFS=',' read -ra ADDR <<< "$files"
	for file in "${ADDR[@]}"; do
		if [[ ! -e "$file" ]]; then
			echo "Error: File or directory '$file' passed to $arg_name does not exist."
			exit 1
		fi
	done
}

TARGET="format-source"
EXTRA_ARGS=""

while [[ $# -gt 0 ]]; do
	case $1 in
		all)
			TARGET="format-source-all"
			shift
			;;
		current)
			TARGET="format-source-current-branch"
			shift
			;;
		local)
			TARGET="format-source-local-changes"
			shift
			;;
		author)
			TARGET="format-source-latest-author"
			shift
			;;
		bnd)
			TARGET="format-source-bnd"
			shift
			;;
		deprecated)
			TARGET="format-source-deprecated-api"
			shift
			;;
		list-checks)
			list_checks
			exit 0
			;;
		missing-override)
			TARGET="format-source-missing-override"
			shift
			;;
		-d|--directory)
			check_arg "$1" "$2"
			check_files "$1" "$2"
			EXTRA_ARGS="$EXTRA_ARGS -Dsource.base.dir=$2"
			shift 2
			;;
		-f|--files)
			check_arg "$1" "$2"
			check_files "$1" "$2"
			EXTRA_ARGS="$EXTRA_ARGS -Dsource.files=$2"
			shift 2
			;;
		-e|--extensions)
			check_arg "$1" "$2"
			EXTRA_ARGS="$EXTRA_ARGS -Dsource.file.extensions=$2"
			shift 2
			;;
		-n|--checks)
			check_arg "$1" "$2"
			check_checks "$1" "$2"
			EXTRA_ARGS="$EXTRA_ARGS -Dsource.check.names=$2"
			shift 2
			;;
		-s|--skip)
			check_arg "$1" "$2"
			check_checks "$1" "$2"
			EXTRA_ARGS="$EXTRA_ARGS -Dskip.check.names=$2"
			shift 2
			;;
		--no-fix)
			EXTRA_ARGS="$EXTRA_ARGS -Dsource.auto.fix=false"
			shift
			;;
		--debug)
			if [ "$TARGET" == "format-source" ]; then
				TARGET="format-source-debug"
			else
				EXTRA_ARGS="$EXTRA_ARGS -Dsource.formatter.show.debug.information=true"
			fi
			shift
			;;
		-h|--help)
			usage
			;;
		*)
			echo "Unknown option: $1"
			usage
			;;
	esac
done

echo "Running: ant -f $ANT_FILE $TARGET $EXTRA_ARGS"
ant -f "$ANT_FILE" "$TARGET" $EXTRA_ARGS