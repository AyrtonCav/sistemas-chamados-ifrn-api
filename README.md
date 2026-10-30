# API REST — Sistema de Chamados IFRN

API REST para registrar, acompanhar e tratar chamados de suporte do IFRN. O projeto usa Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL e Flyway.

## Descrição do projeto

A API organiza chamados por setor e categoria. Solicitantes abrem chamados; técnicos do setor responsável podem assumi-los e resolvê-los; o solicitante pode cancelá-los enquanto estiverem abertos. O ciclo de vida permitido é:

`ABERTO → EM_ATENDIMENTO → RESOLVIDO`

`ABERTO → CANCELADO`

## Como executar

Pré-requisito: Docker Desktop ou Docker Engine com Docker Compose v2.

1. Clone o repositório e entre na pasta do projeto.

2. Suba a API e o PostgreSQL com um único comando:

   ```bash
   docker compose up
   ```

   Para reconstruir a imagem após mudar o código-fonte, use opcionalmente `docker compose up --build`.

3. Aguarde os logs indicarem que a aplicação iniciou. A API estará disponível em `http://localhost:8080` e o PostgreSQL em `localhost:5432`.

4. Para encerrar os containers, pressione `Ctrl+C` ou execute:

   ```bash
   docker compose down
   ```

5. Para remover também os dados persistidos do banco, execute:

   ```bash
   docker compose down -v
   ```

O Compose configura as variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` para a API. As credenciais podem ser alteradas antes da execução por variáveis de ambiente ou por um arquivo `.env` não versionado.

## Exemplos de requisição

Criar um setor:

```bash
curl -X POST http://localhost:8080/setores \
  -H "Content-Type: application/json" \
  -d '{"nome":"Tecnologia da Informação","sigla":"TI"}'
```

Criar um técnico vinculado ao setor de ID `1`:

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria Técnica","email":"maria.tecnica@ifrn.edu.br","matricula":100003,"tipo":"TECNICO","setor":{"id":1}}'
```

Abrir um chamado com a categoria `2` e o solicitante `3`:

```bash
curl -X POST http://localhost:8080/chamados \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Projetor queimado","descricao":"O projetor do laboratório 3 não liga.","local":"Laboratório 3","prioridade":"ALTA","categoria":{"id":2},"solicitante":{"id":3}}'
```

Um técnico assume o chamado `1`:

```bash
curl -X PATCH http://localhost:8080/chamados/1/assumir \
  -H "Content-Type: application/json" \
  -d '{"tecnicoId":1}'
```

## Estudos obrigatórios: Flyway e evolução de schema

### Histórico de migrations

O Flyway registra a execução das migrations na tabela `flyway_schema_history`, criada no schema padrão da conexão, salvo configuração diferente. Ela funciona como trilha de auditoria das alterações do banco: guarda, entre outros dados, a versão, a descrição, o tipo, o script, a data de instalação, o tempo de execução, o checksum e o resultado de cada migration. A partir desse histórico, o Flyway identifica migrations pendentes e aplica as versionadas em ordem, uma única vez (Redgate Software, 2025a; Redgate Software, 2024).

### Checksum e imutabilidade

Ao executar uma migration SQL, o Flyway armazena seu checksum. Em validações posteriores, ele compara esse valor com o checksum do arquivo presente no projeto. Se uma migration versionada que já foi aplicada for alterada, a validação falha por incompatibilidade de checksum. Essa proteção impede que diferentes ambientes tenham a mesma versão registrada, mas estruturas ou dados produzidos por scripts distintos. Portanto, uma migration aplicada não deve ser editada; a correção deve ser entregue em uma nova migration versionada. O comando `repair` só deve ser usado conscientemente, após investigar e formalizar a alteração intencional (Redgate Software, 2025a; Redgate Software, 2026).

### Por que não usar `ddl-auto=update` em produção

`ddl-auto=update` delega ao Hibernate a tentativa de alterar o schema a partir do modelo de entidades durante a inicialização. Ele não substitui migrations revisáveis, não fornece um histórico auditável de mudanças de DDL/DML e pode produzir alterações implícitas sem revisão ou plano explícito de rollback. Em produção, essas características aumentam o risco de divergência entre ambientes, alterações destrutivas ou incompletas e quebra de restrições e dados existentes. Nesta aplicação, `spring.jpa.hibernate.ddl-auto=validate` apenas verifica a compatibilidade entre entidades e schema; o Flyway é o único mecanismo responsável por criar e evoluir a estrutura (Spring, 2026; Redgate Software, 2024).

### Referências

Redgate Software. (2024). *Versioned migrations*. Flyway documentation. https://documentation.red-gate.com/fd/versioned-migrations-273973333.html

Redgate Software. (2025a). *Flyway schema history table*. Flyway documentation. https://documentation.red-gate.com/flyway/flyway-concepts/migrations/flyway-schema-history-table

Redgate Software. (2026). *Validate*. Flyway documentation. https://documentation.red-gate.com/flyway/reference/commands/validate

Spring. (2026). *Database initialization*. Spring Boot reference documentation. https://docs.spring.io/spring-boot/4.2/how-to/data-initialization.html
