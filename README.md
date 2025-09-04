# Tokopaedi E-commerce

Tokopaedi is a Java-based e-commerce system built with a microservice architecture and a Maven multi-module setup. Services run on Java 21, Spring Boot 3.3.x, and Spring Cloud 2023.x.

## Modules
- **gateway-service**: API Gateway using Spring Cloud Gateway; aggregates service OpenAPI docs.
- **discovery-service**: Eureka server for service discovery.
- **auth-service**: Authentication and JWT issuance.
- **iam-service**: Identity, entitlements, and authorization; persists with PostgreSQL. Redis integration for entitlements caching is planned (not yet implemented).
- **profile-service**: User profile service.
- **catalog-service**: Product catalog; PostgreSQL + Flyway; publishes domain events to Kafka via Outbox pattern.
- **inventory-service**: Inventory management; PostgreSQL + Flyway; consumes catalog events from Kafka and exposes read-only stock APIs.
- **common-web**: Shared web utilities for JSON error responses, security handlers, request ID filters, etc.

All services follow a “clean architecture” layering with packages like `application`, `web`, `infrastructure`, `domain`, `security`, and `config` to keep core logic isolated from adapters.

## Architecture Highlights
- **Service Mesh**: Gateway routes to services registered in Eureka (client-side load balancing via Spring Cloud LoadBalancer).
- **Security**: Resource servers validate JWTs (JWKs served by `auth-service`). Common security helpers live in `common-web`.
- **Kafka Integration**:
  - `catalog-service` produces to topic `catalog-events` using a database Outbox + scheduled publisher (idempotent producer enabled).
  - `inventory-service` consumes `catalog-events` with group id `inventory-service`, auto-commit disabled; container acknowledges per record (ack mode `RECORD`).
  - Errors are retried (fixed back-off) and then published to dead-letter topic `<topic>.DLT` using `DeadLetterPublishingRecoverer`.
- **Observability**: Actuator health/info are exposed per service; Gateway actuator exposes `gateway` metrics.

## Local Development
Prerequisites: Java 21, Maven 3.9+, Docker.

1) Start local infrastructure (Postgres, Redis, Kafka, Kafka UI):
```bash
cd infra
docker compose up -d
```
Infra ports: Postgres `5432`, Redis `6379`, Kafka `29092` (host), Kafka UI `8082` (http://localhost:8082).

2) Build all modules from project root:
```bash
mvn clean install
```

3) Run services (recommended order):
```bash
# in separate terminals
mvn -pl discovery-service spring-boot:run
mvn -pl gateway-service spring-boot:run
mvn -pl auth-service spring-boot:run
mvn -pl iam-service spring-boot:run
mvn -pl catalog-service spring-boot:run
mvn -pl inventory-service spring-boot:run
```

4) Open docs and dashboards:
- Gateway Swagger UI: http://localhost:8080/swagger-ui.html
- Eureka dashboard: http://localhost:8761
- Kafka UI: http://localhost:8082

## Service Ports (dev defaults)
- `gateway-service`: 8080
- `discovery-service`: 8761
- `auth-service`: 9000
- `iam-service`: 9100
- `catalog-service`: 9200
- `profile-service`: 9300
- `inventory-service`: 9400

## Inventory Service (new details)
- **Responsibility**: Maintains stock for SKUs and products; exposes read-only endpoints:
  - `GET /api/v1/inventory/{skuId}` returns stock for a SKU.
  - `GET /api/v1/inventory/product/{productId}` returns stock items for a product.
- **Kafka consumer**: Listens to `catalog-events` with group id `inventory-service`. Supported event types include:
  - `catalog.sku.created` → create initial stock item for a new SKU
  - `catalog.sku.activated` / `catalog.sku.deactivated` → toggle stock availability
- **Error handling**: Retries with fixed backoff; on failure publishes to `catalog-events.DLT`.
- **Config**: `spring.kafka.bootstrap-servers=localhost:29092` is set in `application.yml`. See `KafkaConsumerConfig` for listener and error handler wiring.

## Kafka & Topics
- **Bootstrap**: `localhost:29092` (from `infra/docker-compose.yml`).
- **Topics**: Auto-created in local dev; primary topic `catalog-events`, dead-letter `<topic>.DLT`.
- **Outbox Publisher**: `catalog-service` persists events to an Outbox table and publishes them on a schedule for reliability (see `OutboxPublisher`).

## Dependency Management (Aggregator POM)
- The root `pom.xml` imports Spring Boot and Spring Cloud BOMs under `<dependencyManagement>` and centralizes common library/plugin versions.
- **Kafka versions** are managed by the Spring Boot BOM. Child modules declare `org.springframework.kafka:spring-kafka` without versions and inherit the version from the aggregator.
- To upgrade:
  - Preferred: bump `spring.boot.version` in the root POM to a compatible release.
  - Advanced: override `spring-kafka` (and optionally `kafka-clients`) in the root `<dependencyManagement>` if you need a specific version.

## Database & Migrations
- **Databases**: Each service uses its own PostgreSQL schema/db (initialized by `infra/init/*.sql`).
- **Flyway**: Enabled on application startup. Maven plugin is configured per service for ad-hoc runs.
  - Catalog: set `CATALOG_DB_URL`, `CATALOG_USER`, `CATALOG_PASSWORD` env vars to use the plugin.
  - Inventory: set `INVENTORY_DB_URL`, `INVENTORY_USER`, `INVENTORY_PASSWORD` env vars.
  - Example (bash):
    ```bash
    # Catalog migrations via Maven plugin
    export CATALOG_DB_URL=jdbc:postgresql://localhost:5432/catalog_db
    export CATALOG_USER=catalog_user
    export CATALOG_PASSWORD=catalog_pass
    mvn -pl catalog-service flyway:migrate
    ```

## Build, Test, Coverage
- Build all: `mvn clean install`
- Unit tests: `mvn test`
- Coverage gate: `mvn -Pcoverage test` (JaCoCo: 80% instructions, 60% branches). Services can override exclusions in their POM if needed.

## Security
- OAuth2 Resource Server on each service; JWKS URI defaults to `http://auth-service/.well-known/jwks.json` (via service discovery).
- Public endpoints are limited (e.g., catalog/product reads and inventory reads). Admin endpoints require JWT with proper roles/scopes.
- `common-web` provides JSON auth entry point and access denied handlers for consistent error shapes, plus `X-Request-Id` propagation.

## Contributing
- Use Java 21 and Maven 3.9+.
- Follow the package layering (`application`, `domain`, `web`, `infrastructure`, `security`, `config`).
- Keep new dependencies versionless in child modules whenever possible and manage versions in the aggregator POM.

Happy hacking!

## Roadmap
- IAM: Add Redis-based caching for entitlements/authorization lookups to reduce database load and latency.
