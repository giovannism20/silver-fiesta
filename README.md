# Desafio Mercado Livre - Giovanni Martins

## Como Executar:

```bash
cp .env.example .env

docker compose up
```

A aplicação estará disponível em `http://localhost:8080`

### Banco de Dados

- **Banco de Dados**: H2 in-memory
- **Console H2**: http://localhost:8080/h2-console?url=jdbc:h2:mem:mercadolivre

**Obs:** O projeto já iniciará com alguns produtos pré-cadastrados.

### Monitoramento

Para verificar o funcionamento da API via Spring Boot Actuator:

- Health Check: http://localhost:8080/actuator/health

Exemplo de chamada para verificação:
```bash
curl http://localhost:8080/actuator/health
```

**Resposta:**
```json
{
  "status": "UP"
}
```

## Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html

**Importante:** Todos os endpoints da API estão versionados em `/api/v1/*`

## Endpoints

### Listar Produtos
```http
GET /api/v1/products
```

**Exemplo com cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/products
```

**Resposta:**
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

### Buscar Produto por ID
```http
GET /api/v1/products/{id}
```

**Exemplo com cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/products/1
```

**Resposta:**
```json
{
  "id": 1,
  "name": "Notebook Dell Inspiron 15",
  "description": "Notebook Dell Inspiron 15 3000...",
  "price": 3500.00
}
```

### Criar Produto
```http
POST /api/v1/products
Content-Type: application/json
```

**Exemplo com cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook Dell Inspiron 15",
    "price": 3500.00
  }'
```

**Resposta (201 Created):**
```json
{
  "id": 6,
  "name": "Notebook Dell",
  "description": "Notebook Dell Inspiron 15",
  "price": 3500.00
}
```

### Atualizar Produto
```http
PUT /api/v1/products/{id}
Content-Type: application/json
```

**Exemplo com cURL:**
```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell Atualizado",
    "description": "Nova descrição",
    "price": 3800.00
  }'
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "name": "Notebook Dell Atualizado",
  "description": "Nova descrição",
  "price": 3800.00
}
```

### Deletar Produto
```http
DELETE /api/v1/products/{id}
```

**Exemplo com cURL:**
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1
```

**Resposta:** 204 No Content (sem corpo de resposta)

## Exemplos de Erros

### Validação de Dados (400 Bad Request)

**Requisição:**
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -100
  }'
```

**Resposta:**
```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos campos informados",
  "path": "/api/v1/products",
  "errors": [
    {
      "field": "name",
      "message": "Nome é obrigatório"
    },
    {
      "field": "price",
      "message": "Preço deve ser positivo"
    }
  ]
}
```

### Produto Não Encontrado (404 Not Found)

**Requisição:**
```bash
curl -X GET http://localhost:8080/api/v1/products/999
```

**Resposta:**
```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado com ID: 999",
  "path": "/api/v1/products/999"
}
```

## Configuração

### CORS
A aplicação permite requisições apenas de origens específicas configuradas via variável de ambiente:

```bash
# No arquivo .env
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080
```

### Variáveis de Ambiente

As configurações podem ser customizadas através do arquivo `.env`:

```bash
# Porta da aplicação
APP_PORT=8080

# CORS - Origens permitidas (separadas por vírgula)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080

# Logs
LOG_LEVEL=DEBUG  # DEBUG, INFO, WARN, ERROR
```

## Exemplo de Resposta de Erro

```json
{
  "timestamp": "2025-11-30T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação nos campos informados",
  "path": "/api/v1/products",
  "errors": [
    {
      "field": "name",
      "message": "Nome é obrigatório"
    },
    {
      "field": "price",
      "message": "Preço deve ser positivo"
    }
  ]
}
```
