#!/usr/bin/env bash
set -euo pipefail

ORIGIN=${ORIGIN:-http://localhost:5173}
BASE=${BASE:-http://localhost:8080}

echo "==> Preflight OPTIONS to ${BASE}/auth/api/v1/auth/login with Origin ${ORIGIN}" >&2
curl -i -s -X OPTIONS \
  -H "Origin: ${ORIGIN}" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type, Authorization" \
  "${BASE}/auth/api/v1/auth/login" | sed -n '1,30p'

echo "\n==> Actual POST (expect CORS headers + Allow-Credentials present)" >&2
curl -i -s -X POST \
  -H "Origin: ${ORIGIN}" \
  -H "Content-Type: application/json" \
  --data '{"username":"demo","password":"demo"}' \
  "${BASE}/auth/api/v1/auth/login" | sed -n '1,40p'

echo "\nNote: POST body and status depend on backend auth-service." >&2
