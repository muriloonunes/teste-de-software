# Pizzaria DHM — Teste de Software (Monorepo)

Monorepo desenvolvido para a disciplina de Testes de Software (FATESG).

## 📁 Estrutura do Monorepo

```plaintext
teste-de-software/
├── backend/            # API REST em Java 25 & Spring Boot 4
│   ├── src/            # Código-fonte Java e resources
│   ├── pom.xml         # Configuração do Maven
│   ├── mvnw            # Maven Wrapper (Linux/macOS)
│   └── mvnw.cmd        # Maven Wrapper (Windows)
├── frontend/           # Aplicação SPA em Angular & TypeScript
│   ├── src/            # Código-fonte TypeScript/HTML/CSS
│   ├── angular.json    # Configuração do Angular CLI
│   └── package.json    # Dependências e scripts npm
├── .editorconfig       # Padronização de formatação (Backend 4 espaços, Frontend 2 espaços)
├── .gitattributes      # Normalização de finais de linha (LF/CRLF)
└── .gitignore          # Regras unificadas para Java, Angular, IDEs e SO
```

---

## 🚀 Como Executar

### 1. Backend (Spring Boot)
Requisitos: JDK 25 (ou compatível).

```bash
cd backend
./mvnw spring-boot:run
```
A API estará disponível em `http://localhost:8080`.  
Console do H2 em `http://localhost:8080/h2-console`.

Executar testes do backend:
```bash
cd backend
./mvnw test
```

### 2. Frontend (Angular)
Requisitos: Node.js (v20+) e npm.

```bash
cd frontend
npm install
npm start
```
A aplicação estará disponível em `http://localhost:4200`.

Executar testes do frontend:
```bash
cd frontend
npm test
```

---

## 👥 Integrantes

- Murilo Nunes
- Hartur Sales Xavier
