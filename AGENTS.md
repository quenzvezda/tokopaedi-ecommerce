# Agent.md - Tokopaedi Microservice Ruleset

Berikut **ringkasan *agent.md*** (ruleset singkat + contoh) buat semua service di proyek **tokopaedi-microservice**.

## Prinsip Utama

* **Clean Architecture + Vertical Slicing**
    * Struktur dipotong per fitur (slice) → tiap slice punya `domain`, `application`, `infrastructure`, `web`.
    * **Tidak** ada domain bergantung ke framework (Spring, JPA, dsb).

* **CQRS di Application Layer**
    * Pisahkan **Command** (tulis/mutasi) dan **Query** (baca).
    * Interaksi masuk via kontrak `*Commands` & `*Queries`.

* **Factory-Only Pattern (Kontrak antar layer)**
    * Wiring DI **selalu** via `@Bean` di `*BeanConfig` (Factory).
    * Controller memakai **kontrak interface**, bukan implementasi konkret.
    * Implementasi konkret disediakan adapter (JPA, HTTP, Kafka, dll) di `infrastructure`.

* **Standar Teknis Lintas Service**
    * Spring MVC, Resource Server JWT, Eureka Client, akses via Gateway.
    * Error JSON seragam dari `common-web` (RequestId, handlers, GlobalExceptionHandler).
    * Validasi: Jakarta Validation di DTO.
    * ID publik pakai **UUID**.
    * Migrasi **Flyway** (nonaktifkan `ddl-auto`).

* **Contract-First + OpenAPI (IAM & Catalog)**
    * Kontrak berada di `docs/openapi/*.yaml` dan menjadi sumber kebenaran.
    * `openapi-generator-maven-plugin` menghasilkan interface API + model (Spring, `interfaceOnly=true`).
    * Controller harus `implements` interface hasil generate (per vertical slice/tag di YAML).
    * Perubahan API dilakukan dengan mengubah YAML terlebih dahulu, lalu regen, baru implementasi + tes.
    * Gunakan `servers: [{ url: "/" }]` di YAML (relatif root; jangan hardcode host). Swagger UI agregat tetap benar via springdoc runtime.

## Pola Paket (Vertical Slice)

```
com.example.<service>
 ├─ domain/                // murni model + kontrak repo
 ├─ application/           // use-case (CQRS)
 ├─ infrastructure/        // adapter (JPA, HTTP, Cache, Message, dst.)
 ├─ web/                   // controller + dto + mapper
 └─ config/                // Bean factory (Factory-Only DI), security, common-web
```

Contoh slice **Catalog/Product**:

```
domain/product: Product, ProductRepository, ProductSearchCriteria
application/product: ProductCommands, ProductQueries (+ *Service impl)
infrastructure/jpa: JpaProduct, JpaProductRepository, ProductRepositoryImpl
web: Product DTO/mapper, Public/Admin controller
config: CatalogBeanConfig (beans untuk repo & use case)
```

## Kontrak & Factory (Contoh)

### 1) Kontrak Domain & Application

```java
// domain/product
public interface ProductRepository {
  Product save(Product p);
  Optional<Product> findById(UUID id);
  PageResult<Product> search(ProductSearchCriteria c);
  void deleteById(UUID id);
}

// application/product
public interface ProductCommands {
  Product create(String name, String desc, UUID brandId, UUID catId, Boolean published);
  Product update(UUID id, String name, String desc, UUID brandId, UUID catId, Boolean published);
  void delete(UUID id);
}

public interface ProductQueries {
  PageResult<Product> search(String q, UUID brandId, UUID catId, int page, int size);
  Product getById(UUID id);
}
```

### 2) Implementasi Use-Case (CQRS)

```java
// application/product
@RequiredArgsConstructor
class ProductCommandService implements ProductCommands {
  private final ProductRepository repo;
  public Product create(/*...*/) { /* validasi domain + repo.save */ }
  public Product update(/*...*/) { /* load -> mutasi -> save */ }
  public void delete(UUID id) { repo.deleteById(id); }
}

@RequiredArgsConstructor
class ProductQueryService implements ProductQueries {
  private final ProductRepository repo;
  public PageResult<Product> search(/*...*/){ return repo.search(new ProductSearchCriteria(/*...*/)); }
  public Product getById(UUID id){ return repo.findById(id).orElseThrow(); }
}
```

### 3) Adapter Infrastruktur (JPA) — memetakan ke Domain

```java
// infrastructure/jpa
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
  private final JpaProductRepository jpa;
  public Product save(Product p){ return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(p))); }
  public Optional<Product> findById(UUID id){ return jpa.findById(id).map(JpaMapper::toDomain); }
  public void deleteById(UUID id){ jpa.deleteById(id); }
  public PageResult<Product> search(ProductSearchCriteria c){ /* JPA Spec + map */ }
}
```

### 4) Factory-Only DI (BeanConfig)

```java
@Configuration
public class CatalogBeanConfig {
  // Repos (domain -> adapter)
  @Bean ProductRepository productRepository(JpaProductRepository jpa){ return new ProductRepositoryImpl(jpa); }

  // Use cases (kontrak)
  @Bean ProductCommands productCommands(ProductRepository repo){ return new ProductCommandService(repo); }
  @Bean ProductQueries productQueries(ProductRepository repo){ return new ProductQueryService(repo); }
}
```

### 5) Controller Hanya Tahu Kontrak

```java
@RestController
@RequestMapping("/api/v1/catalog")
public class PublicCatalogController {
  private final ProductQueries productQueries;
  public PublicCatalogController(ProductQueries productQueries){ this.productQueries = productQueries; }

  @GetMapping("/products")
  public PageResult<ProductListItemResponse> products(@RequestParam(required=false) String q, /*...*/) {
    var pr = productQueries.search(q, /*...*/);
    return PageResult.of(pr.content().stream().map(DtoMapper::toListDto).toList(),
                         pr.page(), pr.size(), pr.totalElements());
  }
}
```

## Konvensi & Aturan

* **No Framework di Domain**: Domain tidak boleh import Spring/JPA/HTTP/Serialization.
* **DTO ≠ Domain**: Controller menerima/return **DTO**, mapping via `web.mapper`.
* **Validasi**
    * DTO: Jakarta Validation (`@NotBlank`, `@NotNull`, dll).
    * Domain rule (unik, state) → di `CommandService` + repository check.
* **Security**
    * Public GET tertentu → `permitAll`.
    * Admin path → `hasAnyRole('ADMIN','<ROLE_SPECIFIC>')` + `@PreAuthorize` di controller.
    * Resource Server JWT, handler JSON dari `common-web`.
* **Error & Observability**
    * Gunakan `GlobalExceptionHandler` dari `common-web` (JSON + `requestId`).
    * Actuator `health`/`info` terbuka.
* **Persistence**
    * Flyway mengelola schema (`ddl-auto: none`).
    * Mapping domain↔JPA via mapper terpisah (hindari kebocoran anotasi JPA ke domain).
    * Flyway rules:
        - Cek versi terakhir di `src/main/resources/db/migration` sebelum menambah migrasi.
        - Ikuti format: `V<urut>__<deskripsi>.sql` (contoh: `V4__add_outbox_events.sql`).
        - Jangan mengubah migrasi yang sudah diterapkan; buat migrasi baru untuk perubahan.
        - Jika konflik penomoran, renumber dengan aman dan koordinasi.
* **ID & Waktu**
    * ID: UUID dari application layer saat create.
    * Timestamp: `Instant.now()` di commands; DB kolom `timestamptz`.
* **Gateway & Discovery**
    * `spring.application.name` sesuai service.
    * Routing Option B (disepakati): setiap service memiliki `server.servlet.context-path` (IAM → `/iam`, Catalog → `/catalog`). Gateway tidak memakai StripPrefix untuk dua service ini.
    * Endpoint eksternal tersusun sebagai `<origin>/<context-path>/api/...` (mis. `/iam/api/v1/...`).
    * springdoc runtime otomatis menggunakan context path, sehingga Swagger UI aggregator di gateway tetap “Try it out”.
    * Komunikasi S2S gunakan service discovery (Eureka) dengan base URL `http://<service-id>` dan path yang mencakup context-path (contoh auth-service → IAM: `/iam/internal/v1/...`). Jangan mengandalkan field `servers` di YAML untuk S2S.

## Do & Don't

### Do
* Tulis use-case granular (satu tanggung jawab).
* Tutup semua dependency via **interface**; implementasi dipasang di `*BeanConfig`.
* Pastikan query berat disajikan oleh read-model (nanti) → **CQRS ringan**.

### Don't
* Jangan inject `JpaRepository` langsung ke controller/use case.
* Jangan bawa anotasi JPA/`@Entity` ke package `domain`.
* Jangan campur Command & Query dalam satu service kelas.
* Jangan mengubah implementasi API tanpa memperbarui YAML kontrak terlebih dahulu.

## Commit Message Format

Gunakan pola Conventional Commits dengan scope nama service/module:

- Bentuk: `type(scope): short summary`
- `type`: feat, fix, docs, test, chore, refactor, perf, build, ci, style, revert
- `scope`: nama service/module (mis. inventory, catalog, auth, gateway, iam);
  untuk dokumentasi internal, bisa pakai scope khusus (mis. `docs(agent)`).
- Summary singkat, gunakan kalimat perintah (imperative), huruf kecil, ≤ 72 char.
- Body opsional untuk rincian perubahan; gunakan bullet list `- ...` per poin.
- Satu commit boleh memuat beberapa header (multi-scope/type) seperti contoh di bawah.

Contoh (direkomendasikan):

```
feat(inventory): add Kafka DLQ + retry; harden consumer

- Add DefaultErrorHandler with DeadLetterPublishingRecoverer and RECORD ack
- Throw on invalid payload to route to DLT
- Configure spring.kafka consumer enable-auto-commit=false

feat(catalog): implement outbox pattern for SKU events

- Add outbox_events table (V4 migration)
- Write outbox rows BEFORE_COMMIT; schedule publisher to catalog-events
- Remove immediate publisher to avoid double-send

fix(catalog): map outbox payload as JSONB and avoid Map.of null

- Use @JdbcTypeCode(SqlTypes.JSON) on payload column
- Build LinkedHashMap for payloads to allow null productId

test: add unit tests for outbox and kafka consumer

docs(agent): add Flyway migration rules and guidance
```

Catatan tambahan:
- Untuk breaking change, sertakan `BREAKING CHANGE:` pada body dan jelaskan migrasinya.
- Jika perubahan mencakup banyak service yang berbeda dan signifikan, pertimbangkan untuk memecahnya ke beberapa commit agar histori lebih jelas.
- Jangan insiatif melakukan commit secara mandiri, lakukan commit jika user meminta secara eksplisit.

## Workflow & Commit Policy (Agent)

- Jangan melakukan commit perubahan apa pun (kode, konfigurasi, atau dokumentasi) jika pengguna tidak meminta secara eksplisit. Hanya lakukan commit saat diminta.
- Saat commit diminta, pastikan pesan commit jelas dan konsisten dengan format di atas.

## Workflow Contract-First (Wajib untuk IAM & Catalog)

1) Ubah kontrak OpenAPI
- Edit YAML di `docs/openapi/iam.yaml` atau `docs/openapi/catalog.yaml`.
- Gunakan tags per slice untuk memisahkan interface (`<Tag>Api`).
- Gunakan `servers: [{ url: "/" }]` (atau hilangkan bagian `servers`).
- Jaga kompatibilitas: field baru opsional aman; breaking change perlu versi baru endpoint.

2) Regenerasi interface & model
- Jalankan build/test (mvn) — plugin codegen berjalan di fase `generate-sources`.
- Package API: `com.example.<artifactId>.web.api`, Model: `com.example.<artifactId>.web.model`.

3) Implementasi controller (vertical slice)
- Setiap slice controller `implements <Tag>Api` dan mapping domain↔model dilakukan eksplisit.
- Hindari bentrok nama tipe generated vs domain: gunakan FQCN untuk tipe generated bila perlu.

4) Testing
- Tambah/ubah `@WebMvcTest` untuk endpoint baru/ubah.
- Pada slice test, gunakan path `/api/...` (context path tidak terpasang di slice test).
- Gunakan `GlobalExceptionHandler` (common-web) untuk verifikasi error JSON.

5) Verifikasi & Coverage
- Jalankan unit tests dan profile coverage (`-Pcoverage`) bila diperlukan.
- Hindari menurunkan threshold tanpa alasan kuat.

## Versi & Kompatibilitas API

- Non-breaking:
  - Tambah field response opsional, tambah endpoint baru.
- Breaking:
  - Ubah tipe/required field, ubah path/method — buat versi baru (mis. `/api/v2/...`) dan deprecate yang lama.
- Rekomendasi CI: tambahkan pemeriksaan `openapi-diff` untuk mendeteksi breaking change antar versi YAML.

## Contoh Evolusi Layanan (Pricing)

- Jika kebutuhan harga menjadi kompleks (platform/region) dan dipisah ke service terpisah:
  - Buat kontrak OpenAPI baru untuk `pricing-service` terlebih dahulu.
  - Di catalog, pertimbangkan:
    - Deprecate field `price` di response produk dan sediakan ringkasan/tautan, atau
    - Tambah endpoint komposit di BFF/gateway.
  - Pastikan perubahan catalog tetap backward-compatible selama masa transisi.

## Contoh End-to-End Alur (Create Product)

1. **AdminController** menerima `ProductCreateRequest` → validasi.
2. Panggil **`ProductCommands.create(...)`**.
3. Command melakukan rule (nama wajib, referensi brand/category ada) → panggil **`ProductRepository.save`**.
4. Repository adapter (JPA) simpan entity JPA → map kembali ke **Domain Product**.
5. Controller kembalikan **DTO** hasil map domain.
**Kontrak-First: Pedoman Controller & Keamanan**
- Gunakan pendekatan kontrak-first untuk semua fitur baru. Definisikan/ubah kontrak OpenAPI di `docs/openapi/<service>.yaml` sebelum menulis kode.
- Jika suatu endpoint membutuhkan otorisasi khusus, sebutkan kebutuhannya di `description` kontrak (bukan di bagian security):
  - Roles: mis. `ROLE_ADMIN`, `ROLE_CATALOG_EDITOR`.
  - Permissions/Scopes: gunakan format `<service>:<subject>:<action>` (contoh: `catalog:product:write`).
- Saat membuat controller, implement interface yang dihasilkan dari kontrak (hasil `openapi-generator`).
  - Controller harus `implements` interface API yang ter-generate (paket `...web.api...`).
  - Jangan menambahkan anotasi mapping (`@RequestMapping/@GetMapping/@PostMapping`) di implementasi; mapping berasal dari interface kontrak.
  - Path/mapping mengikuti kontrak; jangan mengubah context-path. Set `servers: ["/"]` di YAML agar mengikuti origin/context-path dari gateway.

**Kebijakan Khusus Per Layanan**
- IAM: Otorisasi sudah diatur secara global pada `iam-service/src/main/java/com/example/iam/config/SecurityConfig.java`. Tidak perlu menambahkan `@PreAuthorize` di setiap endpoint IAM.
- Auth: Semua endpoint (`login`, `logout`, `register`, `refresh`) bersifat `permitAll`. Tidak perlu `@PreAuthorize`.
- Layanan lain (misalnya Catalog): Terapkan `@PreAuthorize` pada endpoint write sesuai kebutuhan yang dideskripsikan di kontrak.

**Praktik Tambahan**
- Di kontrak YAML, hindari mengikat implementasi pada skema oauth2/scopes. Dokumentasikan kebutuhan roles/permissions di `description` per operasi.
- Pastikan versi `openapi-generator` dikelola terpusat di parent POM; child POM tidak mendefinisikan versinya lagi.
- Setelah generate, verifikasi semua controller sudah mengimplementasikan interface kontrak tanpa method yang terlewat dan lengkapi `@PreAuthorize` bila diperlukan (kecuali IAM/Auth sesuai kebijakan di atas).
- Tambahkan/ubah WebMvcTest untuk endpoint baru, termasuk verifikasi aturan izin jika ada.
