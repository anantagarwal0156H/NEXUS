#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ -e .env ]]; then echo '.env already exists. Edit it locally or rename it before configuring again.'; exit 1; fi
printf 'Neon direct connection hostname (no protocol): '; read -r host
printf 'Database name: '; read -r db
printf 'Database user/role: '; read -r dbuser
printf 'Database password (hidden): '; read -rs dbpass; printf '\n'
printf 'Choose demo account password (10+ characters, hidden): '; read -rs demopass; printf '\n'
[[ "$host" =~ ^[a-zA-Z0-9.-]+$ && "$db" =~ ^[a-zA-Z0-9_-]+$ && -n "$dbuser" && -n "$dbpass" && ${#demopass} -ge 10 && ${#demopass} -le 72 ]] || { echo 'Invalid values; retry.'; exit 1; }
umask 077
{ printf 'export DATABASE_URL=%q\n' "jdbc:postgresql://${host}:5432/${db}?sslmode=verify-full"; printf 'export DATABASE_USERNAME=%q\n' "$dbuser"; printf 'export DATABASE_PASSWORD=%q\n' "$dbpass"; printf 'export DEMO_PASSWORD=%q\n' "$demopass"; printf 'export NEXUS_DEMO=true\nexport EMAIL_ENABLED=false\nexport COOKIE_SECURE=false\n'; } > .env
printf 'Local configuration saved to .env (excluded from Git).\n'
