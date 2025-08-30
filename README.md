# Tokopaedi E-commerce

Tokopaedi adalah sistem e-commerce berbasis Java yang menerapkan arsitektur microservice dan menggunakan Maven multi-module. Seluruh layanan dibangun dengan Java 21, Spring Boot 3.3, dan Spring Cloud 2023.

## Struktur Modul
- **gateway-service** – API Gateway berbasis Spring Cloud Gateway.
- **auth-service** – layanan autentikasi pengguna.
- **iam-service** – pengelolaan identitas dan akses.
- **profile-service** – profil pengguna.
- **catalog-service** – katalog produk.
- **discovery-service** – service discovery menggunakan Eureka.
- **common-web** – utilitas web bersama untuk respons JSON standar, handler keamanan, dan filter `X-Request-Id`.

Setiap service mengikuti pola *clean architecture* dengan pemisahan paket `application`, `web`, `infrastructure`, `domain`, `security`, dan `config` agar logika inti terisolasi dari antarmuka.

## Pengembangan Lokal
1. Masuk ke direktori `infra` lalu jalankan Docker Compose untuk menyiapkan Postgres, Redis, Kafka, dan Kafka UI:
   ```bash
   cd infra
   docker compose up -d
   ```
2. Bangun seluruh modul dari akar proyek:
   ```bash
   mvn clean install
   ```
3. Jalankan setiap service secara terpisah:
   ```bash
   mvn spring-boot:run -pl <nama-module>
   ```
4. Bersihkan database dengan Flyway bila diperlukan:
   ```bash
   mvn flyway:clean
   ```

## Langkah Pembelajaran
- Telusuri modul `common-web` untuk memahami pola respons, filter, dan handler keamanan bersama.
- Perhatikan pembagian paket pada tiap service untuk mempelajari penerapan *clean architecture*.
- Dalami ekosistem Spring: Spring Boot 3.x, Spring Security (OAuth2/JWT), Spring Cloud (Gateway, Eureka, LoadBalancer), JPA/Flyway, serta integrasi Redis dan Kafka.
- Eksplorasi pengujian dengan JUnit/Mockito, penggunaan Resilience4j, dan pengecekan coverage menggunakan Jacoco.

Dengan memahami struktur modul, infrastruktur pendukung, dan konvensi lintas service, kontributor baru dapat cepat beradaptasi dan menambahkan fungsionalitas baru.
