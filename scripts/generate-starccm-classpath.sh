#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
STARCCM_HOME="${1:-${STARCCM_HOME:-}}"
OUTPUT_FILE="${2:-$ROOT_DIR/config/starccm-classpath.txt}"

usage() {
    cat <<EOF
Usage: $0 /path/to/STAR-CCM+ [output-file]

Creates a local STAR-CCM+ classpath file for scripts/build.sh.

Example:
  $0 "/Applications/Siemens/16.04.012/STAR-CCM+16.04.012"
EOF
}

fail() {
    echo "error: $*" >&2
    exit 1
}

if [[ -z "$STARCCM_HOME" || "$STARCCM_HOME" == "-h" || "$STARCCM_HOME" == "--help" ]]; then
    usage
    [[ "${STARCCM_HOME:-}" == "-h" || "${STARCCM_HOME:-}" == "--help" ]] && exit 0
    exit 1
fi

[[ -d "$STARCCM_HOME" ]] || fail "STAR-CCM+ directory does not exist: $STARCCM_HOME"

SEARCH_ROOTS=()
if [[ -d "$STARCCM_HOME/star/lib/java" ]]; then
    SEARCH_ROOTS+=("$STARCCM_HOME/star/lib/java")
elif [[ -d "$STARCCM_HOME/lib/java" ]]; then
    SEARCH_ROOTS+=("$STARCCM_HOME/lib/java")
else
    SEARCH_ROOTS+=("$STARCCM_HOME")
fi

((${#SEARCH_ROOTS[@]})) || fail "no Java library directories found under: $STARCCM_HOME"

TEMP_FILE="$(mktemp)"
trap 'rm -f "$TEMP_FILE"' EXIT

find "${SEARCH_ROOTS[@]}" -type f -name '*.jar' -print | sort -u > "$TEMP_FILE"
JAR_COUNT="$(wc -l < "$TEMP_FILE" | tr -d ' ')"

(( JAR_COUNT > 0 )) || fail "no .jar files found under: $STARCCM_HOME"

mkdir -p "$(dirname "$OUTPUT_FILE")"
{
    echo "# Local STAR-CCM+ classpath. This file is generated and ignored by git."
    echo "# Generated from: $STARCCM_HOME"
    cat "$TEMP_FILE"
} > "$OUTPUT_FILE"

echo "Wrote $JAR_COUNT jar paths to $OUTPUT_FILE"
