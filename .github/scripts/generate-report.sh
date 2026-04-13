#!/usr/bin/env bash
#
# generate-report.sh - Build a Markdown compatibility-matrix report from JUnit XML results.
#
# Usage:
#   ./generate-report.sh <artifacts-root-dir>
#
# The script expects the following directory layout produced by actions/download-artifact:
#   <artifacts-root-dir>/
#     test-results-<mode>-java-<version>/
#       tests/<category>/<module>/build/test-results/test/TEST-*.xml
#
# Output: Markdown written to stdout (pipe to $GITHUB_STEP_SUMMARY or a file).

set -euo pipefail

ARTIFACTS_DIR="${1:?Usage: $0 <artifacts-root-dir>}"
JAVA_VERSIONS=(8 11 17 21 25)
PIPE="|"

# Ordered display names for modes
declare -a MODE_ORDER=("ubuntu-latest" "windows-latest" "macos-14" "java-mode")
declare -A MODE_LABELS=(
  ["ubuntu-latest"]="Linux"
  ["windows-latest"]="Windows"
  ["macos-14"]="MacOS"
  ["java-mode"]="Java Mode"
)

# Category display order and labels
declare -a CATEGORY_ORDER=(
  "web-servers"
  "web-frameworks"
  "http-clients"
  "async"
  "messaging"
  "test-frameworks"
  "websocket-servers"
  "websocket-servers-frameworks"
  "websocket-clients"
  "websocket-clients-frameworks"
  "websocket-messages"
  "websocket-messages-frameworks"
)
declare -A CATEGORY_LABELS=(
  ["web-servers"]="Web Servers"
  ["web-frameworks"]="Web Frameworks"
  ["http-clients"]="HTTP Clients"
  ["async"]="Async"
  ["messaging"]="Messaging"
  ["test-frameworks"]="Test Frameworks"
  ["websocket-servers"]="WebSocket Servers"
  ["websocket-servers-frameworks"]="WebSocket Server Frameworks"
  ["websocket-clients"]="WebSocket Clients"
  ["websocket-clients-frameworks"]="WebSocket Client Frameworks"
  ["websocket-messages"]="WebSocket Messages"
  ["websocket-messages-frameworks"]="WebSocket Message Frameworks"
)

# -- Helpers -------------------------------------------------------------------

# Parse a single XML file and return "pass" or "fail"
# We use grep to avoid requiring xmllint.
parse_xml_result() {
  local xml_file="$1"
  local header
  header=$(head -5 "$xml_file" | tr '\n' ' ')

  local failures errors
  failures=$(echo "$header" | grep -oP 'failures="\K[0-9]+' || echo "0")
  errors=$(echo "$header"   | grep -oP 'errors="\K[0-9]+'   || echo "0")

  if [[ "$failures" == "0" && "$errors" == "0" ]]; then
    echo "pass"
  else
    echo "fail"
  fi
}

# Determine result for a (mode, java-version, category, module) tuple.
# May have multiple XML files per module - any failure => fail.
get_result() {
  local mode="$1" java_ver="$2" category="$3" module="$4"
  local artifact_dir="${ARTIFACTS_DIR}/test-results-${mode}-java-${java_ver}/tests/${category}/${module}/build/test-results/test"

  if [[ ! -d "$artifact_dir" ]]; then
    echo "n/a"
    return
  fi

  local xml_files
  xml_files=$(find "$artifact_dir" -maxdepth 1 -name 'TEST-*.xml' 2>/dev/null)
  if [[ -z "$xml_files" ]]; then
    echo "n/a"
    return
  fi

  local overall="pass"
  while IFS= read -r f; do
    local r
    r=$(parse_xml_result "$f")
    if [[ "$r" == "fail" ]]; then
      overall="fail"
      break
    fi
  done <<< "$xml_files"

  echo "$overall"
}

# Map result to emoji
result_icon() {
  case "$1" in
    pass) printf "\xe2\x9c\x85" ;;
    fail) printf "\xe2\x9d\x8c" ;;
    *)    printf "\xe2\x80\x94" ;;
  esac
}

# -- Discover all (category, module) pairs across every artifact ---------------

declare -A SEEN_MODULES

for artifact in "${ARTIFACTS_DIR}"/test-results-*/; do
  [[ -d "$artifact/tests" ]] || continue
  for cat_dir in "$artifact"/tests/*/; do
    [[ -d "$cat_dir" ]] || continue
    local_category=$(basename "$cat_dir")
    for mod_dir in "$cat_dir"/*/; do
      [[ -d "$mod_dir" ]] || continue
      local_module=$(basename "$mod_dir")
      SEEN_MODULES["${local_category}/${local_module}"]=1
    done
  done
done

# -- Build per-category sorted module lists ------------------------------------

declare -A CATEGORY_MODULES

for key in "${!SEEN_MODULES[@]}"; do
  cat="${key%%/*}"
  mod="${key#*/}"
  if [[ -n "${CATEGORY_MODULES[$cat]+x}" ]]; then
    CATEGORY_MODULES["$cat"]="${CATEGORY_MODULES[$cat]}"$'\n'"$mod"
  else
    CATEGORY_MODULES["$cat"]="$mod"
  fi
done

for cat in "${!CATEGORY_MODULES[@]}"; do
  CATEGORY_MODULES["$cat"]=$(echo "${CATEGORY_MODULES[$cat]}" | sort)
done

# -- Render Markdown -----------------------------------------------------------

echo "# Drill4J Compatibility Matrix"
echo ""

for mode in "${MODE_ORDER[@]}"; do
  label="${MODE_LABELS[$mode]}"
  echo "## ${label}"
  echo ""

  has_data=false
  for java_ver in "${JAVA_VERSIONS[@]}"; do
    if [[ -d "${ARTIFACTS_DIR}/test-results-${mode}-java-${java_ver}" ]]; then
      has_data=true
      break
    fi
  done
  if [[ "$has_data" == "false" ]]; then
    echo "_No test results available._"
    echo ""
    continue
  fi

  # Table header
  line="${PIPE} Test ${PIPE}"
  for jv in "${JAVA_VERSIONS[@]}"; do line+=" Java ${jv} ${PIPE}"; done
  echo "$line"
  line="${PIPE}---${PIPE}"
  for _ in "${JAVA_VERSIONS[@]}"; do line+=":---:${PIPE}"; done
  echo "$line"

  for category in "${CATEGORY_ORDER[@]}"; do
    [[ -n "${CATEGORY_MODULES[$category]+x}" ]] || continue
    cat_label="${CATEGORY_LABELS[$category]}"

    line="${PIPE} **${cat_label}** ${PIPE}"
    for _ in "${JAVA_VERSIONS[@]}"; do line+=" ${PIPE}"; done
    echo "$line"

    while IFS= read -r module; do
      [[ -n "$module" ]] || continue
      line="${PIPE} ${module} ${PIPE}"
      for jv in "${JAVA_VERSIONS[@]}"; do
        r=$(get_result "$mode" "$jv" "$category" "$module")
        icon=$(result_icon "$r")
        line+=" ${icon} ${PIPE}"
      done
      echo "$line"
    done <<< "${CATEGORY_MODULES[$category]}"
  done
  echo ""
done
