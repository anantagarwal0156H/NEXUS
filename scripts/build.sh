#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if command -v brew >/dev/null 2>&1 && [[ -d "$(brew --prefix openjdk@21 2>/dev/null)/libexec/openjdk.jdk/Contents/Home" ]]; then export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"; export PATH="$JAVA_HOME/bin:$PATH"; fi
java -version; mvn -version; node -v
(cd frontend; npm install --no-audit --no-fund; npm run build)
mkdir -p backend/src/main/resources/static
cp -R frontend/dist/. backend/src/main/resources/static/
(cd backend; mvn clean verify)
