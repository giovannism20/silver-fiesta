# Mercado Libre Challenge - Giovanni Martins

## How to Run:

```bash
cp .env.example .env

docker compose up
```

The application will be available at `http://localhost:8080`

### Database

- **Database**: H2 in-memory
- **H2 Console**: http://localhost:8080/h2-console?url=jdbc:h2:mem:mercadolivre

**Note:** The project will start with some pre-registered products.

### Monitoring

To check the API status via Spring Boot Actuator:

- Health Check: http://localhost:8080/actuator/health

Example verification call:
```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

## API Documentation

After starting the application, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html

**Important:** All API endpoints are versioned at `/api/v1/*`

## Performance Tests

The project includes an automated script for load testing using `wrk`:

**Prerequisite:** Install `wrk`
```bash
# Ubuntu/Debian
sudo apt-get install wrk

# macOS
brew install wrk
```

```bash
./performance-test.sh
```

**The script executes 4 test scenarios:**
1. Product listing (default pagination)
2. Search by ID with active cache
3. Custom pagination with sorting
4. Stress test (200 simultaneous connections)

## Endpoints

### List Products
```http
GET /api/v1/products
```

**Example with cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/products
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Notebook Dell Inspiron 15",
    "description": "Notebook Dell Inspiron 15 3000...",
    "price": 3500.00
  }
]
```

### Get Product by ID
```http
GET /api/v1/products/{id}
```

**Example with cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/products/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Notebook Dell Inspiron 15",
  "description": "Notebook Dell Inspiron 15 3000...",
  "price": 3500.00
}
```

### Create Product
```http
POST /api/v1/products
Content-Type: application/json
```

**Example with cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook Dell Inspiron 15",
    "price": 3500.00
  }'
```

**Response (201 Created):**
```json
{
  "id": 6,
  "name": "Notebook Dell",
  "description": "Notebook Dell Inspiron 15",
  "price": 3500.00
}
```

### Update Product
```http
PUT /api/v1/products/{id}
Content-Type: application/json
```

**Example with cURL:**
```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Notebook Dell",
    "description": "New description",
    "price": 3800.00
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Updated Notebook Dell",
  "description": "New description",
  "price": 3800.00
}
```

### Delete Product
```http
DELETE /api/v1/products/{id}
```

**Example with cURL:**
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1
```

**Response:** 204 No Content (no response body)

## Error Examples

### Data Validation (400 Bad Request)

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -100
  }'
```

**Response:**
```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error in the provided fields",
  "path": "/api/v1/products",
  "errors": [
    {
      "field": "name",
      "message": "Name cannot be empty"
    },
    {
      "field": "price",
      "message": "Price must be greater than zero"
    }
  ]
}
```

### Product Not Found (404 Not Found)

**Request:**
```bash
curl -X GET http://localhost:8080/api/v1/products/999
```

**Response:**
```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/v1/products/999"
}
```

## Configuration

### CORS
The application only allows requests from specific origins configured via environment variable:

```bash
# In .env file
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080
```

### Environment Variables

Settings can be customized through the `.env` file:

```bash
# Application port
APP_PORT=8080

# CORS - Allowed origins (comma-separated)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080

# Logs
LOG_LEVEL=DEBUG  # DEBUG, INFO, WARN, ERROR
```

## Error Response Example

```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error in the provided fields",
  "path": "/api/v1/products",
  "errors": [
    {
      "field": "name",
      "message": "Name cannot be empty"
    },
    {
      "field": "price",
      "message": "Price must be greater than zero"
    }
  ]
}
```
