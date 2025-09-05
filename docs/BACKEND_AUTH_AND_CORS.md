Backend Auth & CORS (Phase 1)
=============================

Overview
- Gateway: Spring Cloud Gateway (WebFlux) on `http://localhost:8080`
- Frontend: Vite on `http://localhost:5173`
- Goal: Cookie httpOnly for refresh, access token in response body, and strict CORS with credentials.

CORS Policy
- Env `FRONTEND_ORIGINS` (comma-separated): explicit allowed origins.
  - Default in code: `http://localhost:5173`
  - Example: `FRONTEND_ORIGINS=http://localhost:5173,https://staging.example.com,https://app.example.com`
- Methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS`
- Headers: `Content-Type, Authorization`
- Exposed: `Authorization` (optional if tokens in header)
- Credentials: `true` (frontend uses `withCredentials=true` for refresh cookie)
- Preflight (OPTIONS) returns 200 and includes: `Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Credentials`, `Vary: Origin`.
- Actual responses include: `Access-Control-Allow-Origin`, `Access-Control-Allow-Credentials`, `Vary: Origin`.

Implementation (Gateway)
- File: `gateway-service/src/main/java/com/example/gateway/config/CorsConfig.java`
  - Splits `FRONTEND_ORIGINS` by comma, trims values, configures `CorsWebFilter` for `/**`.
  - Allows the methods/headers above, exposes `Authorization`, and sets `allowCredentials=true`.

Cookie & Token Settings (Auth Service)
- Access token: short-lived JWT, returned in login/refresh response body: `{ tokenType: "Bearer", accessToken: "...", expiresIn: <sec> }`.
- Refresh token: long-lived, rotated, stored httpOnly cookie.
- Recommended envs:
  - `ACCESS_TOKEN_TTL_SECONDS=900`
  - `REFRESH_TOKEN_TTL_SECONDS=2592000`
  - `REFRESH_COOKIE_NAME=refresh_token`
  - `REFRESH_COOKIE_DOMAIN=localhost` (set per env)
  - `REFRESH_COOKIE_PATH=/` (or `/auth`)
  - `REFRESH_COOKIE_SAMESITE=Lax` (Strict/None per need)
  - `REFRESH_COOKIE_SECURE=false` (true in HTTPS)
- In Spring: build cookies via `ResponseCookie.from(name, value) ... httpOnly(true).path(path).sameSite(sameSite).secure(secure).maxAge(ttl) ...` and add via `Set-Cookie` header.

Verification
- Script: `gateway-service/scripts/verify-cors.sh`
  - Preflight: `OPTIONS /auth/api/v1/auth/login` with `Origin: http://localhost:5173` → 200 and CORS headers present, includes `Access-Control-Allow-Credentials: true`.
  - Actual: `POST /auth/api/v1/auth/login` with `Origin: http://localhost:5173` → includes `Access-Control-Allow-Origin` and `Access-Control-Allow-Credentials`.

Common Pitfalls
- Do not use wildcard `*` when `allowCredentials=true`.
- Ensure `FRONTEND_ORIGINS` includes every dev/staging/prod origin used by the frontend.
- If refresh cookies are not set/received, check `SameSite`, `Domain`, and `Secure` attributes and that the browser treats the request as same-site vs cross-site for `localhost`.
- When moving to HTTPS origins, enable `REFRESH_COOKIE_SECURE=true` and include the HTTPS origins in `FRONTEND_ORIGINS`.

Acceptance
- Frontend can login (refresh cookie set, access token returned).
- Frontend can refresh (cookie-only) and get a new access token.
- Frontend can logout (cookie cleared) and protected routes fail as expected.
- CORS works for allowed origins only; credentials allowed.

