# Pizzaria DHM — Projeto N2

Sistema de vendas de pizzas desenvolvido para a disciplina de Testes de Software
(FATESG). A aplicação usa arquitetura cliente-servidor: uma API REST em Spring
Boot persiste os dados em H2 e uma interface web estática consome a API.

## Funcionalidades

- CRUD de **Pizzas** (`/api/pizzas`), com nome, descrição e preço-base.
- CRUD de **Adicionais** (`/api/adicionais`), com nome, descrição e preço.
- CRUD de **Clientes** (`/api/clientes`) e consulta/exclusão de vendas
  (`/api/vendas`); o total é calculado no servidor considerando tamanho,
  quantidade e adicionais.
- CRUD de **Usuários** (`/api/usuarios`) e login (`POST /api/auth/login`).
  Senhas são armazenadas com BCrypt e nunca são retornadas pela API.
- Interface disponível em `http://localhost:8080/`, com cardápio e tela de
  login.

## Tecnologias

Java 25, Spring Boot 4, Spring Web MVC, Spring Data JPA, Bean Validation,
H2 Database, BCrypt, JUnit 5 e Mockito.

## Execução

Pré-requisito: JDK 25. Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

Abra `http://localhost:8080/`. Para cadastrar o primeiro usuário:

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Administrador","email":"admin@pizzaria.com","senha":"123456","ativo":true}'
```

Depois, faça login em `http://localhost:8080/`.

## Testes

Execute os testes automatizados com:

```bash
./mvnw test
```

Os testes unitários cobrem o cálculo do total para tamanhos, quantidades,
adicionais, venda vazia e persistência. Os fluxos sugeridos para a entrega
E2E/API são: cadastrar e consultar uma pizza; autenticar e criar uma venda.

## Integrantes

- Murilo Nunes
- Hartur Sales Xavier
