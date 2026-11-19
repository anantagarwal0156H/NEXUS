#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
build=$(mktemp -d); trap 'rm -rf "$build"' EXIT
javac -d "$build" backend/src/main/java/com/nexus/domain/Rules.java backend/src/main/java/com/nexus/strategy/*.java tests/CoreChecks.java
java -cp "$build" CoreChecks
