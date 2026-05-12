# 📚 Sistema de Gestão Escolar – Dashboard com Calendário Integrado

<p align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-f4b400?style=for-the-badge" alt="Status do projeto" />
  <img src="https://img.shields.io/badge/java-17+-1d6fdc?style=for-the-badge" alt="Java 17+" />
  <img src="https://img.shields.io/badge/spring%20boot-3.2.5-2ea043?style=for-the-badge" alt="Spring Boot 3.2.5" />
  <img src="https://img.shields.io/badge/postgresql-configurado-336791?style=for-the-badge" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/oauth2-google-4285F4?style=for-the-badge" alt="Google OAuth2" />
  <img src="https://img.shields.io/badge/google%20agenda-integracao%20planejada-34A853?style=for-the-badge" alt="Google Agenda" />
  <img src="https://img.shields.io/badge/frontend-thymeleaf%20%2B%20css%20%2B%20js-f28c28?style=for-the-badge" alt="Frontend" />
</p>

<p align="center">
  Plataforma web para gestao escolar, organizacao da rotina da coordenacao e visualizacao centralizada de compromissos, avisos e tarefas.
</p>

---

## ✨ Visao Geral

O **Sistema de Gestao Escolar – Dashboard com Calendario Integrado** e uma aplicacao web em desenvolvimento para apoiar a rotina da coordenacao escolar.

O projeto evoluiu de um dashboard simples com cards para uma ferramenta mais robusta de gestao, com foco em:

- organizacao visual da rotina escolar;
- apoio a tomada de decisao;
- acompanhamento de tarefas dos coordenadores;
- exibicao clara de avisos importantes;
- integracao com Google Agenda;
- login com conta Google via OAuth 2.0;
- visualizacao em telas grandes, como TV ou monitor.

O elemento central do sistema sera um **calendario integrado ao Google Agenda**, ocupando a maior parte da tela e funcionando como nucleo principal da plataforma.

Cada coordenador devera acessar o sistema com sua propria conta Google, permitindo visualizar:

- seus compromissos individuais;
- eventos compartilhados da escola;
- tarefas vinculadas a sua rotina;
- avisos e comunicados importantes do dia.

---

## 🎯 Objetivo do Projeto

O objetivo principal do sistema e melhorar a organizacao da coordenacao escolar, reduzindo a dependencia de lembretes manuais, memoria individual ou comunicacoes soltas.

A plataforma busca reunir, em um unico ambiente:

- calendario escolar;
- compromissos dos coordenadores;
- tarefas diarias, semanais e mensais;
- avisos rapidos;
- rotina administrativa;
- alertas de conflito ou excesso de atividades;
- visualizacao clara para uso em computador, TV ou monitor.

---

## 🧭 Funcionalidades Principais

### Calendario integrado ao Google Agenda

O calendario sera o centro da interface.

Ele devera permitir:

- visualizar compromissos do coordenador logado;
- visualizar eventos compartilhados da escola;
- clicar em um dia especifico para consultar detalhes;
- adicionar novos eventos;
- adicionar tarefas relacionadas ao dia;
- identificar dias com atividades ja cadastradas;
- exibir alertas antes de salvar novos compromissos em datas ocupadas.

A integracao com o Google Agenda sera feita por meio da **Google Calendar API**.

---

### Login com Google / OAuth 2.0

O sistema utilizara autenticacao com conta Google.

Isso permite que cada usuario acesse o sistema com sua propria identidade, possibilitando separacao de dados e telas por perfil.

Perfis previstos:

- Administrador;
- Diretora;
- Coordenadora;
- outros perfis escolares, se necessario futuramente.

O login com Google tambem sera importante para acessar os compromissos e calendarios vinculados ao usuario.

---

### Dashboard em tela grande

O dashboard sera pensado para exibicao em:

- computador;
- TV;
- monitor da coordenacao;
- painel de acompanhamento interno.

O calendario devera ocupar a maior parte da tela, enquanto os avisos e resumos ficarao em areas laterais ou secundarias.

---

### Area lateral de avisos

A interface tera uma area secundaria com informacoes de leitura rapida.

Essa area podera exibir cards com:

- avisos importantes;
- faltas de professores;
- substituicoes;
- comunicados do dia;
- demandas urgentes;
- lembretes administrativos.

A ideia e manter a leitura simples, objetiva e sem poluir o dashboard principal.

---

### Tarefas individuais dos coordenadores

Cada coordenador tera uma lista propria de tarefas, funcionando como um checklist.

As tarefas poderao ser organizadas por frequencia:

- tarefas diarias;
- tarefas semanais;
- tarefas mensais.

A proposta e criar um fluxo de trabalho claro, evitando esquecimentos e facilitando o acompanhamento da rotina.

Essa area podera receber nomes como:

- Rotina dos Coordenadores;
- Plano de Acao;
- Fluxo de Trabalho.

O termo **Rotina de Auxiliares** foi removido do escopo principal do sistema.

---

### Rotina Administrativa

A **Rotina Administrativa** representa uma visao geral das atividades da escola.

Ela podera ser organizada por:

- dia;
- semana;
- mes.

Essa rotina deve reunir processos mais amplos da instituicao, apoiando a gestao escolar e a organizacao interna.

---

### Alerta antes de criar eventos ou tarefas

Um ponto critico do sistema e a validacao antes da criacao de novos eventos ou tarefas.

Sempre que o usuario tentar criar uma nova atividade em um dia que ja possui compromissos, o sistema devera exibir um alerta.

Esse alerta deve informar claramente quais atividades ja existem naquela data.

Exemplo de comportamento:

```text
Ja existem atividades cadastradas para este dia:

- Reuniao pedagogica - 09:00
- Atendimento aos responsaveis - 14:00
- Fechamento de relatorio mensal

Deseja continuar mesmo assim?
