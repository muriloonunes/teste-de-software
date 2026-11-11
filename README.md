# Pizzaria DHM — Teste de Software

Projeto desenvolvido para a prova N2 de Teste de Software.

## 👥 Alunos
- [Davy Lopes Oliveira](https://github.com/DavyL0)
- [Hartur Sales Xavier](https://github.com/hartur-sales)
- [Murilo Nunes De Oliveira](https://github.com/muriloonunes)

## 📁 Estrutura do Monorepo

```plaintext
teste-de-software/
├── .idea/
│   └── runConfigurations/    # Configurações de execução compartilhadas da IDE
├── backend/                  # API REST em Java 25 & Spring Boot 4
│   ├── src/                  # Código-fonte Java e resources
│   ├── pom.xml               # Configuração do Maven
│   ├── mvnw                  # Maven Wrapper (Linux/macOS)
│   └── mvnw.cmd              # Maven Wrapper (Windows)
├── frontend/                 # Aplicação SPA em Angular & TypeScript
│   ├── src/                  # Código-fonte TypeScript/HTML/CSS
│   ├── angular.json          # Configuração do Angular CLI
│   └── package.json          # Dependências e scripts npm
├── docker-compose.yml        # Orquestração do PostgreSQL em container
├── .editorconfig             # Padronização de formatação (Backend 4 espaços, Frontend 2 espaços)
├── .gitattributes            # Normalização de finais de linha (LF/CRLF)
└── .gitignore                # Regras unificadas para Java, Angular, IDEs e SO
```

---

## 🐳 Banco de Dados (PostgreSQL no Docker)

O projeto utiliza **PostgreSQL 17** executado via Docker através do [`docker-compose.yml`](docker-compose.yml).

### Dados de Conexão

| Propriedade | Valor |
| :--- | :--- |
| **Host** | `localhost` |
| **Porta** | `5432` |
| **Database** | `pizzaria_db` |
| **Usuário** | `postgres` |
| **Senha** | `postgres` |
| **Nome do Container** | `pizzaria-teste-software` |

---

### Comandos de Terminal

Execute estes comandos na raiz do monorepo:

#### 1. Subir o banco de dados (em segundo plano):
```bash
docker compose up -d
```

#### 2. Derrubar o container:
```bash
docker compose down
```
> [!NOTE]
> Seus dados permanecem salvos no volume `postgres_data`. Para apagar o banco completamente e recriar do zero, use a flag `-v`:
> ```bash
> docker compose down -v
> ```

#### 3. Comandos úteis:
```bash
docker compose ps          # Verificar se o container está saudável (healthy)
docker compose logs -f     # Acompanhar logs do Postgres em tempo real
docker compose restart     # Reiniciar o banco
```
---

## 💡 Integração com o IntelliJ IDEA

As configurações de execução do projeto já estão salvas e versionadas em `.idea/runConfigurations/`, disponíveis automaticamente para qualquer desenvolvedor que abrir o repositório na IDE:

1. **Subida Automática (*Before Launch*)**:
   - Ao executar a aplicação Spring Boot (**`TesteDeSoftwareApplication`**), o IntelliJ dispara automaticamente a tarefa **`docker banco`** antes de iniciar o backend.
   - O container do PostgreSQL sobe sozinho em background, sem necessidade de comandos manuais.

2. **Play direto no arquivo (Gutter Icons)**:
   - Ao abrir o [`docker-compose.yml`](docker-compose.yml), clique nas setas de play verdes (**▶▶**) na margem esquerda das linhas `services:` ou `postgres:` para iniciar ou reiniciar o serviço.

3. **Painel de Serviços (*Services Tool Window*)**:
   - Pressione `Alt + 8` (ou menu `View` > `Tool Windows` > `Services`).
   - Na seção **Docker**, você tem botões de *Start/Stop/Restart*, visualização de logs em tempo real e terminal direto no container.

4. **Database Tools**:
   - Abra a aba **Database** à direita no IntelliJ (`+` > **Data Source** > **PostgreSQL**).
   - Conecte em `localhost:5432`, database `pizzaria_db`, user `postgres` e senha `postgres` para inspecionar tabelas e rodar queries graficamente.

---

## 🚀 Como Executar o Projeto

### 1. Backend (Spring Boot)
Requisitos: JDK 25 (ou compatível) e Docker em execução.

```bash
# 1. Garanta que o banco está rodando (caso não execute pelo IntelliJ)
docker compose up -d

# 2. Inicie a API
cd backend
./mvnw spring-boot:run
```
A API estará disponível em `http://localhost:8080`.

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
