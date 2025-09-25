# Local Infrastructure

This folder contains the docker compose stack for databases, caches, message brokers, and supporting services used during local development.

## MinIO Object Storage

Profile and catalog media are stored in MinIO when running the platform locally.

1. Copy `.env.example` to `.env` and adjust the credentials if needed:
   ```bash
   cd infra
   cp .env.example .env
   # edit MINIO_ROOT_USER / MINIO_ROOT_PASSWORD as desired
   ```
   The same credentials are injected into profile-service through the `MINIO_ACCESS_KEY` and `MINIO_SECRET_KEY` variables so the backend can sign pre-signed URLs.

2. Start the infrastructure stack (MinIO, databases, Kafka, etc.):
   ```bash
   docker compose up -d
   ```

3. Verify MinIO is running:
   - API endpoint: http://localhost:9000
   - Console UI: http://localhost:9001 (login using the credentials from `.env`).

4. Buckets and prefixes:
   - The compose stack bootstraps the `user-avatars` bucket automatically via the one-off `minio-setup` container.
   - Store logos should be uploaded under the `store-logos/` prefix inside the same bucket. MinIO creates the prefix automatically when the first object is uploaded (no manual folder creation required).

If you need to reset MinIO data, stop the stack and remove the `miniodata` Docker volume:
```bash
docker compose down
docker volume rm tokopaedi-microservice_miniodata
```
