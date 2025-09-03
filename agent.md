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
    * Akses lewat Gateway `/prefix/** → lb://<service>`; testing via `{{gateway_base}}`.

## Do & Don't

### Do
* Tulis use-case granular (satu tanggung jawab).
* Tutup semua dependency via **interface**; implementasi dipasang di `*BeanConfig`.
* Pastikan query berat disajikan oleh read-model (nanti) → **CQRS ringan**.

### Don't
* Jangan inject `JpaRepository` langsung ke controller/use case.
* Jangan bawa anotasi JPA/`@Entity` ke package `domain`.
* Jangan campur Command & Query dalam satu service kelas.

## Commit Message Format

Format commit message yang harus digunakan:

```
[service] Message Commit Bebas
```

**Penjelasan:**
* Di dalam `[]` adalah nama service/package yang sedang diupdate/commit agar jelas tiap commitnya.
* Jika ada lebih dari 1 package, gunakan format seperti ini:

```
[iam][auth][catalog] Message Commit Bebas
```

**Contoh:**
```
[catalog] Add product search functionality
[iam][auth] Fix JWT token validation
[order][payment] Implement order processing workflow
```

## Contoh End-to-End Alur (Create Product)

1. **AdminController** menerima `ProductCreateRequest` → validasi.
2. Panggil **`ProductCommands.create(...)`**.
3. Command melakukan rule (nama wajib, referensi brand/category ada) → panggil **`ProductRepository.save`**.
4. Repository adapter (JPA) simpan entity JPA → map kembali ke **Domain Product**.
5. Controller kembalikan **DTO** hasil map domain.
