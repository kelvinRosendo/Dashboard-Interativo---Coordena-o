# 📺 Dashboard Interativo para Coordenacao Escolar

![Status do projeto](https://img.shields.io/badge/status-em%20desenvolvimento-f4b400?style=for-the-badge)
![Java 17+](https://img.shields.io/badge/java-17+-1d6fdc?style=for-the-badge)
![Spring Boot 3.2.5](https://img.shields.io/badge/spring%20boot-3.2.5-2ea043?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/postgresql-configurado-336791?style=for-the-badge)
![Frontend](https://img.shields.io/badge/frontend-thymeleaf%20%2B%20css%20%2B%20js-f28c28?style=for-the-badge)

<p align="center">
  Painel visual para coordenacao escolar com cards organizados por categoria, prioridade e status.
</p>

---

## ✨ Visao Geral

O **Dashboard Interativo para Coordenacao Escolar** e uma aplicacao web feita com **Spring Boot**, **Thymeleaf** e **PostgreSQL** para concentrar informacoes operacionais da escola em um unico painel.

O foco do projeto e entregar leitura rapida para a coordenacao em telas grandes, como:

- 📌 avisos e notas
- 👩‍🏫 horarios de professores
- 🚨 faltas de professores
- 📅 eventos principais
- 🗂️ rotina administrativa
- 👥 rotina de auxiliares

---

## 🧭 O Que Ja Funciona

### Dashboard principal

- exibicao de cards por categoria
- resumo lateral por status
- destaque visual por prioridade e status
- acoes de editar e excluir diretamente no painel

### Gerenciamento web

- criacao de cards por formulario
- edicao de cards existentes
- exclusao de cards
- validacao de campos obrigatorios

### API REST

- `GET /cards`
- `GET /cards/{id}`
- `POST /cards`
- `PUT /cards/{id}`
- `DELETE /cards/{id}`

### Dados iniciais

- o projeto possui seed automatica
- categorias vazias sao preenchidas com cards de exemplo
- categorias que ja possuem dados nao sao sobrescritas

---

## 🖥️ Estrutura Visual do Dashboard

| Area | Conteudo |
| --- | --- |
| Coluna esquerda | Avisos / Notas |
| Miolo superior | Horarios e Faltas de Professores |
| Miolo central | Eventos Principais |
| Miolo inferior | Rotina Administrativa e Rotina de Auxiliares |
| Coluna direita | Resumo do Dia, Prioridades e Status Operacional |

### Destaques da interface atual

- cards com leitura visual por cor de prioridade
- badges de status operacional
- rolagem em blocos extensos
- preenchimento inicial automatico para demonstracao

---

## 🧩 Categorias Disponiveis

```text
AVISO_NOTA
HORARIO_PROFESSOR
FALTA_PROFESSOR
EVENTO
ROTINA_ADMINISTRATIVA
ROTINA_AUXILIAR
```

### Prioridades

```text
BAIXA
MEDIA
ALTA
```

### Status

```text
PENDENTE
EM_ANDAMENTO
CONCLUIDO
```

---

## 🧠 Regras de Negocio

- todo card precisa ter `titulo`
- todo card precisa ter `categoria`
- todo card precisa ter `prioridade`
- todo card precisa ter `status`
- `EVENTO`, `FALTA_PROFESSOR` e `HORARIO_PROFESSOR` exigem `dataEvento`
- `EVENTO`, `FALTA_PROFESSOR` e `HORARIO_PROFESSOR` exigem `responsavel`
- `AVISO_NOTA` exige `descricao`

---

## 🏗️ Stack do Projeto

### Backend

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Validation
- Thymeleaf

### Banco de dados

- PostgreSQL

### Frontend

- HTML
- CSS
- JavaScript

---

## 🗃️ Modelo de Dados

Entidade principal: `Card`

```json
{
  "id": 1,
  "titulo": "Conselho de classe",
  "descricao": "Reuniao de fechamento do trimestre com professores.",
  "categoria": "EVENTO",
  "prioridade": "ALTA",
  "dataCriacao": "2026-04-17T09:00:00",
  "dataEvento": "2026-04-20",
  "responsavel": "Coordenacao Pedagogica",
  "status": "PENDENTE",
  "observacoes": "Levar relatorios de desempenho."
}
```

---

## 🧱 Arquitetura

```text
src/main/java/br/com/escola/dashboard/
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── service
└── utils
```

### Camadas

- `controller`: endpoints web e API
- `service`: regras de negocio
- `repository`: persistencia com JPA
- `entity`: entidades do sistema
- `dto`: entrada e saida de dados
- `config`: configuracoes e carga inicial
- `exception`: tratamento de erros

---

## 🚀 Como Executar

### 1. Requisitos

- Java 17+
- Maven
- PostgreSQL em execucao

### 2. Configure o banco

Edite `src/main/resources/application.properties` conforme seu ambiente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dashboard_escolar
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8081
```

### 3. Rode o projeto

```bash
mvn spring-boot:run
```

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

### 4. Acesse

- Dashboard web: [http://localhost:8081](http://localhost:8081)
- API REST: [http://localhost:8081/cards](http://localhost:8081/cards)

---

## 🔄 Fluxo de Uso

### Pela interface

1. abra o dashboard
2. clique no botao `+` da categoria desejada
3. preencha o formulario
4. salve
5. acompanhe o card no painel principal

### Pela API

Exemplo de criacao:

```json
{
  "titulo": "Reuniao pedagogica",
  "descricao": "Encontro com os professores do fundamental.",
  "categoria": "EVENTO",
  "prioridade": "ALTA",
  "dataEvento": "2026-04-25",
  "responsavel": "Coordenacao Pedagogica",
  "status": "PENDENTE",
  "observacoes": "Levar pauta impressa."
}
```

---

## 📌 Estado Atual do Projeto

### Ja implementado

- dashboard funcional
- CRUD web de cards
- CRUD REST de cards
- regras de validacao por categoria
- preenchimento inicial automatico
- organizacao visual por prioridade e status

### Ainda pode evoluir

- filtros e busca
- autenticacao
- historico de alteracoes
- testes automatizados
- atualizacao em tempo real
- lapidacao visual fina do dashboard

---

## 🛣️ Proximos Passos

- melhorar a legibilidade dos cards em todos os blocos
- reduzir cortes e truncamentos em telas menores
- criar testes para service e controller
- separar configuracoes sensiveis por ambiente
- evoluir a experiencia de cadastro e edicao

---

## 👨‍💻 Autor

Desenvolvido por **Kelvin**.

---

<p align="center">
  <b>feito com cafe, tentativa, ajuste visual e vontade de fazer funcionar</b>
</p>
