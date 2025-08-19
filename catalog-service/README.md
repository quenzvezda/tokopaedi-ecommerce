# sample-service (MVC Skeleton)

Spring Boot MVC microservice skeleton wired to **common-web** for:
- Standardized JSON error responses (400/401/403/404/409/500)
- Security JSON handlers for 401/403
- `X-Request-Id` propagation via filter
- Clean-architecture friendly layout

## Quick start

1. Ensure parent aggregator `ecommerce` and module `common-web` are built/installed.
2. `mvn spring-boot:run` (or run from your IDE).
3. Try:
   - `GET /api/v1/ping` → 200 OK
   - `GET /api/v1/secure` without token → 401 JSON error

## Notes
- Set `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` or a custom `JwtDecoder` for real JWT checks.
- Turn off `app.errors.verbose` in production.
