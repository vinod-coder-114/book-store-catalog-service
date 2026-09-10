## Plan: Catalog Service with JWT-RSA

Build the catalog service as a Spring Boot resource server backed by MongoDB, with local JWT validation using the user-service public key/JWKS so runtime requests do not call user-service for every API hit. The plan sets up domain and API layers (`/catalog/**`), role-based authorization for admin/customer flows, Mongo indexes for fast search, and a token strategy that supports key rotation safely.

### Steps
1. Finalize platform dependencies in [`build.gradle`](build.gradle) for MongoDB, OAuth2 resource server, validation, and testing around `CatalogApplication`.
2. Define Mongo documents and indexes in [`src/main/java/com/book_store/catalog/domain/Book.java`](src/main/java/com/book_store/catalog/domain/Book.java) and [`src/main/java/com/book_store/catalog/domain/Category.java`](src/main/java/com/book_store/catalog/domain/Category.java) using `@Document`.
3. Implement repositories and core business logic in [`src/main/java/com/book_store/catalog/repository/`](src/main/java/com/book_store/catalog/repository/) and [`src/main/java/com/book_store/catalog/service/CatalogService.java`](src/main/java/com/book_store/catalog/service/CatalogService.java) for browse, search, and CRUD.
4. Expose `/catalog` REST endpoints in [`src/main/java/com/book_store/catalog/controller/BookController.java`](src/main/java/com/book_store/catalog/controller/BookController.java) and [`src/main/java/com/book_store/catalog/controller/CategoryController.java`](src/main/java/com/book_store/catalog/controller/CategoryController.java) mapped to `GET/POST/PUT/DELETE`.
5. Configure JWT-RSA authorization in [`src/main/java/com/book_store/catalog/config/SecurityConfig.java`](src/main/java/com/book_store/catalog/config/SecurityConfig.java) and [`src/main/resources/application.yaml`](src/main/resources/application.yaml) with `SecurityFilterChain`, `JwtDecoder`, and role mapping.
6. Add centralized errors and tests in [`src/main/java/com/book_store/catalog/exception/GlobalExceptionHandler.java`](src/main/java/com/book_store/catalog/exception/GlobalExceptionHandler.java) plus [`src/test/java/com/book_store/catalog/`](src/test/java/com/book_store/catalog/) for auth rules and API behavior.

### Further Considerations
1. Token key source choice: static PEM public key in config, or JWKS endpoint with cache and rotation support?
2. Authorization model choice: enforce `ROLE_ADMIN` on writes only, or also scope-check claims like `catalog.write`?
3. Search strategy choice: simple indexed regex/contains in Mongo, or text index with weighted fields for title/author/ISBN?

