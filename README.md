# sb-ecom: Spring Boot E-Commerce Category API

[![CI](https://github.com/shane24sweeney/sb-ecom/actions/workflows/ci.yml/badge.svg)](https://github.com/shane24sweeney/sb-ecom/actions/workflows/ci.yml)

A REST API for managing e-commerce product categories, built with **Spring Boot 3.2** and **Java 17**. It demonstrates a layered controller → service → repository design, DTOs, request validation, pagination, and centralized error handling, with MockMvc tests running in GitHub Actions.

## Tech stack

| Area | Technology |
|---|---|
| Language / runtime | Java 17 |
| Framework | Spring Boot 3.2 (Spring Web, Spring Data JPA) |
| Persistence | Hibernate / JPA, H2 in-memory database |
| Validation | Jakarta Bean Validation |
| Mapping | ModelMapper (entity ↔ DTO) |
| Boilerplate | Lombok |
| Testing | JUnit 5, Spring Boot Test, MockMvc |
| Build / CI | Maven, GitHub Actions |

## Architecture

```
HTTP request
   │
   ▼
CategoryController      @RestController: routing, @Valid request DTOs, HTTP status codes
   │
   ▼
CategoryService(Impl)   business rules: duplicate-name check, not-found handling, paging
   │
   ▼
CategoryRepository      Spring Data JPA (JpaRepository + derived query findByCategoryName)
   │
   ▼
H2 database

MyGlobalExceptionHandler (@RestControllerAdvice) maps exceptions to consistent responses:
  MethodArgumentNotValidException → 400 with field-level messages
  APIException                    → 400 (e.g. duplicate category)
  ResourceNotFoundException       → 404
```

```
src/main/java/com/ecommerce
├── controller/     CategoryController
├── service/        CategoryService, CategoryServiceImpl
├── repositories/   CategoryRepository
├── model/          Category (JPA entity)
├── payload/        CategoryDTO, CategoryResponse (paged response)
├── exceptions/     APIException, ResourceNotFoundException, MyGlobalExceptionHandler
└── config/         AppConfig (ModelMapper bean), AppConstants (paging defaults)
```

## API

| Method | Endpoint | Description | Success |
|---|---|---|---|
| GET | `/api/public/categories?pageNumber=0&pageSize=50` | List categories (paged) | 200 |
| POST | `/api/public/categories` | Create a category | 201 |
| PUT | `/api/public/categories/{categoryId}` | Update a category | 200 |
| DELETE | `/api/admin/categories/{categoryId}` | Delete a category | 200 |

**Validation and errors**

- `categoryName` is required and must be at least 5 characters → `400 {"categoryName": "Needs to be a minimum of 5 chars"}`
- Creating a category whose name already exists → `400`
- Updating or deleting an unknown id → `404 Category not found with categoryId: 99`

**Paged response**

```json
{
  "content": [{ "categoryId": 1, "categoryName": "Electronics" }],
  "pageNumber": 0,
  "pageSize": 50,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

## Run locally

Requires Java 17+.

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. The H2 console is available at `/h2-console` (JDBC URL `jdbc:h2:mem:test`).

```bash
# create
curl -X POST http://localhost:8080/api/public/categories \
  -H "Content-Type: application/json" \
  -d '{"categoryName":"Electronics"}'

# list (paged)
curl "http://localhost:8080/api/public/categories?pageNumber=0&pageSize=10"

# update
curl -X PUT http://localhost:8080/api/public/categories/1 \
  -H "Content-Type: application/json" \
  -d '{"categoryName":"Consumer Electronics"}'

# delete
curl -X DELETE http://localhost:8080/api/admin/categories/1
```

## Tests

```bash
./mvnw test
```

`CategoryControllerTests` exercises the API end to end through MockMvc: create and paged listing, field-level validation errors, duplicate-name rejection, updates, and 404 handling.
