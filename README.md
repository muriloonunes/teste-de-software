# Bella Pizza — Projeto N2

Sistema full stack de vendas de pizzas para a disciplina de Testes de
Software. O backend é uma API REST em Spring Boot e o frontend é uma SPA
Angular que consome a API para apresentar o cardápio, autenticar usuários,
montar o carrinho, finalizar vendas e consultar o histórico.

## Integrantes

- [Davy Lopes Oliveira](https://github.com/DavyL0)
- [Hartur Sales Xavier](https://github.com/hartur-sales)
- [Murilo Nunes De Oliveira](https://github.com/muriloonunes)

## Tecnologias

Java 25, Spring Boot 4, Spring Data JPA, PostgreSQL 17, Docker, Angular 22,
TypeScript, JUnit 5, Mockito, REST-assured e Selenium.

## Estrutura

```text
backend/    API REST, entidades, serviços, persistência e testes
frontend/   SPA Angular; telas em frontend/src/app/screens
docker-compose.yml
```

## Funcionalidades

- CRUD de pizzas com nome, descrição, categoria e preço.
- CRUD de adicionais, clientes, pagamentos e usuários.
- Login com autenticação BCrypt.
- Cardápio com busca e filtro por categoria.
- Carrinho com quantidade, adicionais e total.
- Finalização de venda e histórico por usuário.
- Validação de entradas e respostas de erro padronizadas.

## Execução

Pré-requisitos: JDK 25, Node.js/npm e Docker.

```bash
docker compose up -d

cd backend
./mvnw spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`. O backend fica em `http://localhost:8080`.

## Testes automatizados

Testes do backend:

```bash
cd backend
./mvnw test
```

O projeto contém:

- JUnit/Mockito: cálculo de venda, tamanhos, quantidades, adicionais,
  persistência e busca.
- REST-assured: cadastro de pizza e validação de preço.
- Selenium: filtro do cardápio e atualização do contador do carrinho.

Para executar os testes Selenium com o frontend aberto:

```bash
./mvnw -Dfrontend.url=http://localhost:4200 test
```

Os testes Selenium são ignorados automaticamente quando o Angular ou o
Chrome não estão disponíveis; os testes unitários e de API continuam sendo
executados.
