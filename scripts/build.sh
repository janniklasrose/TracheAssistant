#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_DIR="$ROOT_DIR/src"
BUILD_DIR="${BUILD_DIR:-$ROOT_DIR/build}"
DIST_DIR="${DIST_DIR:-$ROOT_DIR/dist}"
CLASSES_DIR="$BUILD_DIR/classes"
JAR_FILE="${JAR_FILE:-$DIST_DIR/TracheAssistant.jar}"
CLASSPATH_FILE="${STARCCM_CLASSPATH_FILE:-$ROOT_DIR/config/starccm-classpath.txt}"
CHECK_ONLY=false

usage() {
    cat <<EOF
Usage: $0 [--check]

Builds dist/TracheAssistant.jar without NetBeans or IntelliJ.

STAR-CCM+ dependencies can be provided in either form:
  STARCCM_CLASSPATH=/path/a.jar:/path/b.jar $0
  $0  # after creating config/starccm-classpath.txt

Use scripts/generate-starccm-classpath.sh to create config/starccm-classpath.txt
from a local STAR-CCM+ installation.
EOF
}

fail() {
    echo "error: $*" >&2
    exit 1
}

while (($#)); do
    case "$1" in
        --check)
            CHECK_ONLY=true
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            usage >&2
            fail "unknown argument: $1"
            ;;
    esac
done

require_command() {
    command -v "$1" >/dev/null 2>&1 || fail "$1 is required but was not found on PATH"
}

java_major_version() {
    local version_output version major rest
    if ! version_output="$(javac -version 2>&1)"; then
        fail "JDK 11 or newer is required, but javac is not usable. Install a JDK and ensure JAVA_HOME/PATH point to it. javac output: $version_output"
    fi
    version="${version_output#javac }"
    major="${version%%.*}"
    if [[ "$major" == "1" ]]; then
        rest="${version#1.}"
        major="${rest%%.*}"
    fi
    [[ "$major" =~ ^[0-9]+$ ]] || fail "could not parse javac version from: $version_output"
    echo "$major"
}

resolve_starccm_classpath() {
    if [[ -n "${STARCCM_CLASSPATH:-}" ]]; then
        echo "$STARCCM_CLASSPATH"
        return
    fi

    if [[ -f "$CLASSPATH_FILE" ]]; then
        awk '!/^[[:space:]]*(#|$)/ { print }' "$CLASSPATH_FILE" | paste -sd: -
        return
    fi

    fail "STAR-CCM+ classpath not configured. Set STARCCM_CLASSPATH or create $CLASSPATH_FILE"
}

validate_classpath() {
    local cp="$1"
    local missing=0
    local entry

    [[ -n "$cp" ]] || fail "STAR-CCM+ classpath is empty"

    IFS=: read -r -a entries <<< "$cp"
    for entry in "${entries[@]}"; do
        [[ -n "$entry" ]] || continue
        if [[ ! -e "$entry" ]]; then
            echo "missing classpath entry: $entry" >&2
            missing=1
        fi
    done

    [[ "$missing" == 0 ]] || fail "one or more STAR-CCM+ classpath entries do not exist"
}

require_command javac
require_command jar

JAVA_MAJOR="$(java_major_version)"
if (( JAVA_MAJOR < 11 )); then
    fail "JDK 11 or newer is required; found javac $(javac -version 2>&1)"
fi

STARCCM_CP="$(resolve_starccm_classpath)"
validate_classpath "$STARCCM_CP"

if [[ "$CHECK_ONLY" == true ]]; then
    echo "Build environment OK"
    echo "javac: $(javac -version 2>&1)"
    echo "classpath entries: $(awk -v RS=: 'NF { count++ } END { print count + 0 }' <<< "$STARCCM_CP")"
    exit 0
fi

SOURCES_FILE="$(mktemp)"
trap 'rm -f "$SOURCES_FILE"' EXIT

find "$SRC_DIR" -name '*.java' -print | sort > "$SOURCES_FILE"
[[ -s "$SOURCES_FILE" ]] || fail "no Java sources found under $SRC_DIR"

rm -rf "$CLASSES_DIR" "$JAR_FILE"
mkdir -p "$CLASSES_DIR" "$DIST_DIR"

javac --release 11 \
    -encoding UTF-8 \
    -classpath "$STARCCM_CP" \
    -d "$CLASSES_DIR" \
    @"$SOURCES_FILE"

while IFS= read -r resource; do
    relative_path="${resource#"$SRC_DIR"/}"
    mkdir -p "$CLASSES_DIR/$(dirname "$relative_path")"
    cp "$resource" "$CLASSES_DIR/$relative_path"
done < <(find "$SRC_DIR" -type f ! -name '*.java' -print | sort)

jar --create --file "$JAR_FILE" -C "$CLASSES_DIR" .

echo "Built $JAR_FILE"
