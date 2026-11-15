# Backend — Bella Pizza

API REST em Java 25 e Spring Boot 4 para o sistema de vendas da Bella Pizza.

## Funcionalidades

- CRUD de pizzas: `GET/POST /api/pizzas`, `GET/PUT/DELETE /api/pizzas/{id}`.
- CRUD de adicionais, clientes, pagamentos e usuários.
- Login em `POST /api/auth/login`, com senha protegida por BCrypt.
- Criação de vendas em `POST /api/vendas` e histórico por usuário em
  `GET /api/vendas/historico/{usuarioId}`.
- Validação de DTOs, tratamento centralizado de erros e cálculo de total por
  tamanho, quantidade e adicionais.

## Execução

Na raiz do projeto:

```bash
docker compose up -d
cd backend
./mvnw spring-boot:run
```

O banco de desenvolvimento é PostgreSQL 17 no Docker (`localhost:5432`).
As propriedades de conexão podem ser substituídas por `DB_URL`,
`DB_USERNAME` e `DB_PASSWORD`.

## Testes

```bash
./mvnw test
```

O projeto contém testes unitários JUnit/Mockito, testes de API REST-assured e
dois fluxos E2E Selenium. Para executar os E2E com o frontend, inicie também
o Angular (`cd ../frontend && npm start`) e use:

```bash
./mvnw -Dfrontend.url=http://localhost:4200 test
```

Se o Angular ou Chrome não estiverem disponíveis, os testes Selenium são
automaticamente ignorados; os testes unitários e de API continuam sendo
executados.
