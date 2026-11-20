#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if command -v brew >/dev/null 2>&1 && [[ -d "$(brew --prefix openjdk@21 2>/dev/null)/libexec/openjdk.jdk/Contents/Home" ]]; then export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"; export PATH="$JAVA_HOME/bin:$PATH"; fi
[[ -f .env ]] || { echo 'First run bash scripts/configure.sh'; exit 1; }
source .env
if [[ "${NEXUS_LOCAL_NO_EMAIL:-false}" == true ]]; then export EMAIL_ENABLED=false; fi
[[ -f backend/target/nexus-0.1.0.jar ]] || { echo 'First run bash scripts/build.sh'; exit 1; }
exec java -jar backend/target/nexus-0.1.0.jar
