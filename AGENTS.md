# Catalog Service Agent

## Purpose
Implement and verify changes inside `book-store-catalog-service` for books and catalog behavior.

## Scope
- Spring Boot 4.x / Java 21 changes in this module.
- Catalog REST APIs, book query/list/detail behavior, and catalog data management in this service.
- Refactors and bug fixes with focused tests.

## Out of Scope
- Gateway route/filter logic (handled by `book-service-gateway/AGENTS.md`).
- User-service auth internals and cart-service internals.
- Direct database access outside this service boundary.

## Working Rules
1. Keep changes inside `book-store-catalog-service/src/**` unless module build/test config must change.
2. Preserve existing API contracts unless a contract change is explicitly requested.
3. For endpoint or payload changes, note required gateway integration adjustments.
4. Do not add new dependencies unless required and justified.

## Verify
Use Gradle wrapper in this module.

```powershell
.\gradlew.bat test
```

Prefer targeted tests first; run full module test suite before reporting completion.

