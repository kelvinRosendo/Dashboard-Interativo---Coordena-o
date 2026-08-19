# 📚 Organiza+ — Sistema de Gestão e Coordenação Escolar

<p align="center">
  <img src="https://img.shields.io/badge/status-release%20candidate-f4b400?style=for-the-badge" alt="Status do projeto" />
  <img src="https://img.shields.io/badge/java-17+-1d6fdc?style=for-the-badge" alt="Java 17+" />
  <img src="https://img.shields.io/badge/spring%20boot-3.2.5-2ea043?style=for-the-badge" alt="Spring Boot 3.2.5" />
  <img src="https://img.shields.io/badge/postgresql-configurado-336791?style=for-the-badge" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/oauth2-google-4285F4?style=for-the-badge" alt="Google OAuth2" />
  <img src="https://img.shields.io/badge/thymeleaf-server--side-f28c28?style=for-the-badge" alt="Thymeleaf" />
  <img src="https://img.shields.io/badge/flyway-database%20migration-cc0200?style=for-the-badge" alt="Flyway" />
  <img src="https://img.shields.io/badge/tests-104%2F104-success?style=for-the-badge" alt="Testes" />
</p>

<p align="center">
  Plataforma web para gestão da rotina da coordenação escolar, acompanhamento de demandas,
  relatórios, comunicados, avisos, eventos e informações administrativas.
</p>

---

## 📌 Sobre o Organiza+

O **Organiza+** é uma plataforma desenvolvida para apoiar a gestão da coordenação escolar do Colégio Satélite.

O projeto começou como um dashboard voltado à visualização de informações da coordenação e evoluiu para uma aplicação web completa, com autenticação, controle de perfis, persistência de dados, módulos administrativos, relatórios, demandas, comunicados, avisos e recursos voltados à rotina escolar.

O sistema tem como objetivo centralizar informações que anteriormente ficavam distribuídas entre diferentes processos, planilhas, mensagens e controles manuais.

A plataforma foi construída utilizando **Java + Spring Boot + PostgreSQL + Thymeleaf**, com autenticação por Google OAuth 2.0 e arquitetura preparada para evolução dos módulos de gestão escolar.

---

# 🎯 Objetivos do Projeto

O Organiza+ busca:

* centralizar informações da coordenação;
* organizar a rotina administrativa;
* acompanhar demandas e tarefas;
* registrar informações da Semana em Foco;
* produzir e consultar relatórios;
* divulgar comunicados e avisos;
* facilitar a visualização de informações importantes;
* separar informações conforme o perfil do usuário;
* disponibilizar um dashboard para computadores e telas grandes;
* criar uma base tecnológica para futuras integrações e automações.

---

# 🏗️ Arquitetura

A aplicação segue uma arquitetura baseada em **Spring Boot**, organizada em camadas.

```text
Usuário
   │
   ▼
Navegador
   │
   ▼
Thymeleaf + CSS + JavaScript
   │
   ▼
Controllers
   │
   ▼
Services
   │
   ▼
Repositories
   │
   ▼
Entities
   │
   ▼
PostgreSQL
```

A autenticação utiliza Google OAuth 2.0:

```text
Usuário
   │
   ▼
Google OAuth 2.0
   │
   ▼
Spring Security
   │
   ▼
Usuário autenticado
   │
   ▼
Controle de perfil
   │
   ├── ADMIN
   ├── VICE_DIRETORA
   └── COORDENADORA
```

Em produção, a arquitetura planejada utiliza:

```text
Internet
   │
   ▼
HTTPS
   │
   ▼
Nginx
   │
   ▼
Spring Boot
   │
   ▼
PostgreSQL
   │
   ▼
Flyway
```

O Spring Boot deve permanecer atrás do proxy reverso, evitando exposição direta da porta interna da aplicação.

---

# 🛠️ Stack Tecnológica

## Backend

* Java 17+
* Spring Boot 3.2.5
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate
* Thymeleaf
* OAuth 2.0 / Google
* Flyway

## Banco de Dados

* PostgreSQL
* SQL
* Flyway migrations

## Frontend

* Thymeleaf
* HTML
* CSS
* JavaScript
* Layout responsivo

## Infraestrutura

* Linux / Ubuntu
* Nginx
* systemd
* SSH
* HTTPS
* Oracle Cloud VPS

---

# 🔐 Autenticação e Perfis

O sistema utiliza autenticação por conta Google através de OAuth 2.0.

Após o login, o usuário é direcionado conforme seu perfil de acesso.

Perfis principais:

### ADMIN

Responsável pelas funções administrativas do sistema.

Possui acesso a áreas como:

* administração;
* usuários;
* configurações;
* gestão de coordenadoras;
* comunicados;
* relatórios;
* demandas administrativas;
* recursos de gerenciamento.

### VICE_DIRETORA

Perfil destinado à vice-direção.

Possui acesso às funcionalidades relacionadas à gestão escolar conforme as permissões definidas pelo sistema.

### COORDENADORA

Perfil destinado às coordenadoras.

Possui acesso às funcionalidades relacionadas à sua rotina, demandas, Semana em Foco e demais recursos permitidos pelo sistema.

O controle de acesso é tratado tanto na interface quanto nas rotas e serviços que exigem autorização.

---

# 📊 Dashboard

O Dashboard é o núcleo visual do Organiza+.

Ele foi desenvolvido para permitir uma leitura rápida da situação da coordenação.

Entre as informações trabalhadas estão:

* cards;
* demandas;
* avisos;
* comunicados;
* eventos;
* Semana em Foco;
* informações da rotina;
* indicadores e resumos;
* navegação para módulos administrativos.

A interface também foi preparada considerando o uso em computadores e telas grandes.

---

# 📋 Demandas

O módulo de **Demandas** permite registrar e acompanhar atividades que precisam ser executadas pela coordenação.

O fluxo trabalha com estados de acompanhamento, incluindo:

```text
PENDENTE
    ↓
EM_ANDAMENTO
    ↓
CONCLUÍDA
```

Também existem estados para situações em que uma demanda não deve mais permanecer ativa.

A aplicação diferencia as demandas ativas das finalizadas para permitir acompanhamento da rotina.

Foram implementadas regras para:

* criação;
* atualização;
* alteração de status;
* acompanhamento de prazo;
* contagem de demandas;
* resumo geral;
* organização por segmento.

Também foram realizadas otimizações nas consultas relacionadas ao acompanhamento de prazos e status.

---

# 🗓️ Semana em Foco

A **Semana em Foco** é um dos módulos centrais do Organiza+.

Ela permite registrar informações relacionadas à rotina semanal da coordenação.

O módulo possui estrutura própria de persistência e fluxo de acompanhamento.

Foi implementado o conceito de:

```text
RASCUNHO
   ↓
FINALIZADO
```

A funcionalidade foi integrada ao backend utilizando:

* Entity;
* Repository;
* Service;
* Controller;
* DTOs;
* templates Thymeleaf.

Também foram corrigidos problemas relacionados à recuperação das entidades e carregamento de relacionamentos JPA.

---

# 📝 Relatórios da Semana em Foco

O Organiza+ possui um módulo específico para relatórios relacionados à Semana em Foco.

Foi implementada a estrutura:

```text
RelatorioSemanaEmFoco
        │
        ├── Entity
        ├── Repository
        ├── Service
        ├── Controller
        ├── DTO
        └── Templates
```

O fluxo permite trabalhar com relatórios associados às semanas cadastradas.

Durante as auditorias foram identificados e corrigidos problemas de:

* navegação;
* sidebar;
* links hardcoded;
* carregamento de relacionamentos;
* permissões;
* regras de propriedade;
* apresentação dos dados.

---

# 📢 Comunicados

O módulo de **Comunicados** permite registrar informações que devem ser divulgadas para os usuários do sistema.

O sistema possui área administrativa para:

* criação;
* visualização;
* gerenciamento;
* exibição no dashboard.

O módulo também passou por validações durante a homologação para garantir integração adequada com a navegação administrativa e o dashboard.

---

# ⚠️ Avisos

O sistema possui estrutura para gerenciamento e exibição de **Avisos**.

Os avisos funcionam como informações de destaque para a rotina escolar e podem ser utilizados para situações que precisam de visualização rápida.

---

# 📅 Eventos

O Organiza+ possui estrutura própria para trabalhar com eventos da rotina escolar.

Os eventos fazem parte da organização das atividades e estão preparados para futura expansão da integração com calendários externos.

---

# 👩‍🏫 Coordenação

O módulo de coordenação concentra funcionalidades específicas das coordenadoras.

Entre os conceitos trabalhados estão:

* coordenadoras;
* segmentos;
* rotina;
* demandas;
* Semana em Foco;
* relatórios;
* informações administrativas.

A estrutura do sistema permite relacionar dados de coordenação aos seus respectivos segmentos.

---

# 🏫 Segmentos

O sistema possui organização das coordenadoras e professores por segmento.

Essa estrutura permite trabalhar com diferentes áreas da instituição e facilita a filtragem e organização das informações.

---

# 📥 Importação de Dados

O Organiza+ possui estrutura para importação de dados através de arquivos CSV.

O módulo de importação foi desenvolvido para permitir processamento de informações em lote.

Também existe registro das operações de importação através do conceito de:

```text
ImportacaoLog
```

Durante as auditorias foram analisados pontos relacionados à performance da importação, incluindo consultas realizadas durante o processamento de cada registro.

---

# 🗄️ Banco de Dados

O PostgreSQL é o banco principal da aplicação.

A evolução do banco é controlada pelo **Flyway**.

As migrations possuem versionamento sequencial:

```text
V1
V2
V3
...
V14
```

O objetivo é permitir que o banco seja criado e evoluído de forma previsível entre ambientes.

A estratégia de produção evita utilizar credenciais administrativas do PostgreSQL diretamente pela aplicação.

---

# 🌱 Data Pack

O projeto possui um conjunto de dados inicial denominado **Data Pack 2026**.

Esse conjunto é utilizado para preparar o ambiente com dados necessários para a operação inicial do sistema.

A validação do Data Pack considerou informações como:

* semanas;
* cards;
* comunicados;
* coordenadoras;
* segmentos;
* usuários.

---

# 🧪 Testes e Validação

O projeto passou por ciclos de testes e auditorias técnicas.

Estado registrado da Release Candidate:

```text
Testes:       104/104
Build:        BUILD SUCCESS
Falhas:       0
```

Além dos testes automatizados, foram realizados processos de:

* homologação funcional;
* auditoria de UX;
* auditoria de dados;
* auditoria de segurança;
* auditoria estrutural;
* auditoria de caixa-branca.

---

# 🔍 Auditoria Técnica

Uma auditoria técnica foi realizada para mapear a estrutura interna da aplicação.

O levantamento identificou, entre outros componentes:

```text
Java main              79 arquivos
Java test               5 arquivos
Templates Thymeleaf    22
Controllers            13
Services               16
Repositories           13
Entities               13
DTOs                   11
Enums                   8
Config/Security         6
CSS                     1
JavaScript              2
```

A auditoria também analisou:

* rotas;
* controllers;
* templates;
* sidebar;
* services;
* entities;
* banco;
* migrations;
* perfis;
* fluxos;
* cobertura funcional;
* páginas órfãs.

Essa etapa foi importante para transformar o projeto em uma base mais conhecida e documentada antes da continuidade do desenvolvimento.

---

# 🛡️ Segurança

Durante as auditorias foram identificados problemas relacionados a autenticação e autorização.

Entre os pontos analisados estavam:

* proteção de dashboards;
* autenticação de endpoints;
* validação de perfil;
* controle de acesso administrativo;
* exposição de dados;
* validação de propriedade de registros.

Correções foram realizadas para fortalecer o controle de acesso das áreas administrativas e de coordenação.

---

# ⚡ Performance

Foram realizadas análises específicas de performance.

Entre os problemas identificados durante a auditoria estavam consultas N+1 em diferentes partes do sistema.

Foram analisados pontos relacionados a:

* Demandas;
* Semana em Foco;
* importação CSV;
* carregamento de entidades;
* consultas por segmento.

Também foram implementadas melhorias de consulta, incluindo consultas JPQL específicas para determinadas contagens e filtros.

---

# 🔧 Correções Estruturais Importantes

Durante o desenvolvimento foram realizadas diversas correções e refatorações.

Entre elas:

* correção da nomenclatura de entidades;
* correção de relacionamentos JPA;
* correção de validações de propriedade;
* inclusão de verificações de perfil;
* proteção de endpoints;
* inclusão de `@Transactional` em operações de escrita;
* correções de links da sidebar;
* substituição de URLs hardcoded por `th:href`;
* correção de templates Thymeleaf;
* correção de carregamento lazy;
* uso de `@EntityGraph` em consultas específicas;
* correção de regras de status;
* melhorias de consultas;
* correções de navegação administrativa.

---

# 🧭 Navegação e Sidebar

A sidebar passou por diversas revisões durante o desenvolvimento.

Foram corrigidos problemas relacionados a:

* links incorretos;
* rotas administrativas;
* navegação por perfil;
* placeholders;
* links hardcoded;
* comportamento visual;
* acesso entre módulos.

A navegação foi analisada considerando os diferentes perfis do sistema.

---

# 📺 Painel para Tela Grande

O projeto possui conceito de utilização em **TV ou monitor de acompanhamento**.

A proposta é permitir que informações importantes da coordenação possam ser exibidas de forma visual e centralizada.

Esse recurso faz parte da visão do Organiza+ como uma ferramenta de acompanhamento contínuo da rotina escolar.

---

# 📅 Google Calendar

A integração com o **Google Calendar** faz parte da evolução planejada do projeto.

A proposta é transformar o calendário em um dos elementos centrais da experiência do usuário.

Funcionalidades previstas:

* visualização dos compromissos;
* eventos compartilhados;
* criação de eventos;
* identificação de dias ocupados;
* consulta de detalhes;
* alertas de conflito;
* associação entre atividades e datas.

A integração deverá utilizar a **Google Calendar API**.

> **Status:** integração planejada/evolutiva. A base atual de autenticação Google não deve ser confundida com a integração completa do Google Calendar.

---

# 🔔 Validação de Conflitos de Agenda

Como evolução do módulo de calendário, está prevista uma validação antes da criação de novas atividades.

Exemplo:

```text
Já existem atividades cadastradas para este dia:

- Reunião pedagógica - 09:00
- Atendimento aos responsáveis - 14:00
- Fechamento de relatório mensal

Deseja continuar mesmo assim?
```

O objetivo é evitar sobreposição de atividades e melhorar a organização da rotina.

---

# 🚀 Deploy e Produção

O Organiza+ já passou da etapa em que o principal objetivo era validar o código.

A Release Candidate possui:

```text
Código                  ✅
Arquitetura             ✅
Funcionalidades         ✅
Testes                  104/104
Build                   BUILD SUCCESS
Data Pack 2026          ✅
Flyway V1–V14           ✅
Homologação             ✅
Auditoria UX            ✅
Auditoria de dados      ✅
Auditoria de segurança  ✅
Deploy                  🔄
Produção                🔄
```

A infraestrutura utilizada envolve:

* Ubuntu;
* Oracle Cloud VPS;
* Java 17;
* PostgreSQL;
* Nginx;
* systemd;
* SSH;
* HTTPS.

A aplicação utiliza o serviço:

```text
organiza.service
```

O serviço é responsável por manter o Spring Boot executando no servidor.

---

# 🖥️ Estrutura de Produção

A estrutura planejada para o servidor é:

```text
/opt/organiza/
│
├── app/
│   └── organiza.jar
│
├── releases/
│   └── versões do sistema
│
├── logs/
│
└── .env
```

Fluxo:

```text
Usuário
   │
   ▼
HTTPS
   │
   ▼
Nginx
   │
   ▼
Spring Boot
   │
   ▼
PostgreSQL
   │
   ▼
Flyway
```

As credenciais de produção devem permanecer fora do código-fonte e fora do Git.

---

# 🌐 Nginx

O Nginx é utilizado como proxy reverso.

A arquitetura esperada é:

```text
Internet
    │
    ▼
HTTPS
    │
    ▼
Nginx
    │
    ▼
localhost:8081
    │
    ▼
Spring Boot
```

A porta interna da aplicação não deve ficar diretamente exposta à internet.

---

# 🔑 Configuração de Ambiente

As configurações sensíveis devem ser mantidas no ambiente de execução.

Entre elas:

* conexão PostgreSQL;
* senha do banco;
* Google Client ID;
* Google Client Secret;
* URLs de produção;
* configurações específicas do servidor.

O `.env` de produção não deve ser versionado no Git.

---

# 📦 Release Candidate

O projeto chegou a uma versão considerada **Release Candidate** para o primeiro lançamento.

Isso significa que a maior parte do trabalho atual está concentrada em:

```text
INFRAESTRUTURA
      ↓
DEPLOY
      ↓
CONFIGURAÇÃO
      ↓
TESTE REAL
      ↓
GO LIVE
```

O foco deixa de ser implementar grandes funcionalidades e passa a ser garantir uma implantação estável, segura e reproduzível.

---

# 🗺️ Roadmap

## Concluído

* [x] Estrutura principal do projeto
* [x] Spring Boot
* [x] PostgreSQL
* [x] Spring Security
* [x] Google OAuth 2.0
* [x] Dashboard
* [x] Sidebar
* [x] Controle de perfis
* [x] Demandas
* [x] Comunicados
* [x] Avisos
* [x] Eventos
* [x] Semana em Foco
* [x] Relatórios da Semana em Foco
* [x] Gestão de coordenadoras
* [x] Segmentos
* [x] Importação de dados
* [x] Histórico de importações
* [x] Data Pack 2026
* [x] Flyway
* [x] Auditoria técnica
* [x] Auditoria de UX
* [x] Auditoria de dados
* [x] Auditoria de segurança
* [x] Auditoria de caixa-branca
* [x] Correções estruturais
* [x] Otimizações de consultas
* [x] Testes automatizados
* [x] Build de produção
* [x] Geração do JAR
* [x] Validação definitiva do Google OAuth em produção
* [x] HTTPS de produção

## Em andamento

* [ ] Estabilização definitiva do ambiente de produção
* [ ] Configuração definitiva de domínio
* [ ] Homologação final no ambiente produtivo
* [ ] Go Live

## Próximas evoluções

* [ ] Integração completa com Google Calendar
* [ ] Calendário como elemento central do Dashboard
* [ ] Criação de eventos pelo sistema
* [ ] Alertas de conflito de agenda
* [ ] Evolução do Painel TV
* [ ] Novas automações
* [ ] Integrações externas
* [ ] Melhorias contínuas de performance
* [ ] Expansão dos recursos administrativos

---

# 🧑‍💻 Metodologia de Desenvolvimento

O Organiza+ possui um processo próprio de desenvolvimento assistido por IA.

O fluxo oficial é:

```text
Direção / Coordenação
        ↓
Responsável pelo Projeto
        ↓
ChatGPT — Análise e Planejamento
        ↓
Definição Técnica
        ↓
Implementação
        ↓
Validação
        ↓
Diário de Bordo
```

O responsável pelo projeto exerce simultaneamente os papéis de:

* Product Owner Técnico;
* Desenvolvedor.

O ChatGPT atua como:

* Analista de Sistemas;
* Arquiteto de Software;
* Product Owner Técnico;
* Revisor Técnico;
* apoio à documentação;
* apoio à geração de prompts;
* apoio ao desenvolvimento.

Claude, Cursor e Antigravity podem ser utilizados conforme a complexidade da demanda.

---

# 🧠 Filosofia do Projeto

A regra principal do desenvolvimento é:

> **Nunca implementar primeiro e pensar depois.**

O processo deve seguir:

```text
Entender
   ↓
Planejar
   ↓
Especificar
   ↓
Implementar
   ↓
Validar
   ↓
Documentar
```

O objetivo não é apenas produzir código.

O objetivo é construir um sistema sustentável, compreensível, documentado e capaz de evoluir sem acumular retrabalho.

---

# 📖 Aprendizado e Evolução Técnica

O desenvolvimento do Organiza+ também possui como objetivo a evolução técnica do responsável pelo projeto.

As alterações devem, sempre que possível, ser acompanhadas de explicações sobre:

* diagnóstico do problema;
* arquitetura envolvida;
* motivo da solução;
* arquivos afetados;
* impacto no sistema;
* funcionamento do código;
* validação;
* possíveis regressões.

A intenção é que a implementação assistida por IA também contribua para a formação técnica em **Java, Spring Boot, banco de dados, arquitetura e desenvolvimento web**.

---

# 📚 Documentação

A documentação do projeto é composta por:

* README;
* documentação técnica;
* auditorias;
* registros de Sprint;
* migrations;
* Diário de Bordo;
* documentação do processo de desenvolvimento assistido por IA.

O projeto possui um **Manual Operacional de Desenvolvimento Assistido por IA**, que define o processo de levantamento, análise, implementação, revisão e documentação das demandas.

---

# 📈 Estado Atual do Projeto

## Organiza+ — Agosto de 2026

```text
┌──────────────────────────────────────┐
│         ORGANIZA+                    │
│     RELEASE CANDIDATE                │
├──────────────────────────────────────┤
│ Código                  ✅           │
│ Arquitetura             ✅           │
│ Funcionalidades         ✅           │
│ Banco PostgreSQL        ✅           │
│ Flyway V1–V14           ✅           │
│ Data Pack 2026          ✅           │
│ Testes 104/104          ✅           │
│ Build                   ✅           │
│ Auditoria técnica      ✅           │
│ Auditoria UX           ✅           │
│ Auditoria dados        ✅           │
│ Auditoria segurança    ✅           │
│ Deploy                  ✅           │
│ Produção                ✅           │
│ Google Calendar         🔜           │
└──────────────────────────────────────┘
```

---

# 🎯 Objetivo Final

O objetivo do Organiza+ é chegar a uma plataforma em que a coordenação escolar consiga centralizar sua rotina em um único ambiente:

```text
                    ORGANIZA+
                       │
       ┌───────────────┼────────────────┐
       │               │                │
       ▼               ▼                ▼
   Dashboard       Coordenação      Administração
       │               │                │
       ├── Cards       ├── Demandas     ├── Usuários
       ├── Avisos      ├── Semana       ├── Comunicados
       ├── Eventos     ├── Relatórios   ├── Configurações
       └── TV          └── Rotina       └── Gestão
                       │
                       ▼
                Google Calendar
```

A visão de longo prazo é transformar o Organiza+ em uma plataforma central para organização, acompanhamento e visualização da rotina escolar.

---

# 🚦 Situação do Projeto

**Status:** Release Candidate / preparação para operação produtiva.

O desenvolvimento funcional principal está consolidado.

O foco atual é:

```text
ESTABILIZAR
    ↓
PUBLICAR
    ↓
HOMOLOGAR
    ↓
COLOCAR EM PRODUÇÃO
    ↓
EVOLUIR
```

O próximo grande marco do projeto é o **Go Live**.

Após a estabilização da produção, o desenvolvimento poderá voltar a priorizar novas funcionalidades, principalmente a integração com o Google Calendar e a evolução do painel central de rotina escolar.

---

## 🏫 Organiza+

**Sistema de Gestão e Coordenação Escolar**

Desenvolvido para apoiar a organização, acompanhamento e tomada de decisão da coordenação escolar.
