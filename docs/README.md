# Documentacao Oficial do Organiza+ V1

> **Sistema de Gestao Escolar -- Dashboard com Calendario Integrado**
> Colégio Satélite

**Versao:** V1
**Status:** Ativo
**Ultima atualizacao:** 21/08/2026

---

## Estrutura da Documentacao

A documentacao do Organiza+ esta dividida em quatro grandes areas:

### 1. [Documentacao de Utilizacao](01-utilizacao/)

Destinada aos **usuarios** do sistema. Explica como usar cada funcionalidade.

| Arquivo | Descricao |
|---------|-----------|
| [00-visao-geral](01-utilizacao/00-visao-geral.md) | Visao geral do Organiza+ |
| [01-acesso-login](01-utilizacao/01-acesso-login.md) | Acesso e login via Google OAuth2 |
| [02-dashboard](01-utilizacao/02-dashboard.md) | Dashboard principal por perfil |
| [03-navegacao](01-utilizacao/03-navegacao.md) | Navegacao e sidebar |
| [04-demandas](01-utilizacao/04-demandas.md) | Gestao de demandas |
| [05-comunicados](01-utilizacao/05-comunicados.md) | Comunicados |
| [06-semana-em-foco](01-utilizacao/06-semana-em-foco.md) | Semana em Foco |
| [07-relatorios](01-utilizacao/07-relatorios.md) | Relatorios semanais |
| [08-calendario](01-utilizacao/08-calendario.md) | Calendario integrado |
| [09-painel-tv](01-utilizacao/09-painel-tv.md) | Painel para TV |
| [10-gestao-usuarios](01-utilizacao/10-gestao-usuarios.md) | Gestao de usuarios |
| [11-perfis-acesso](01-utilizacao/11-perfis-acesso.md) | Perfis e permissoes |
| [12-funcionalidades-admin](01-utilizacao/12-funcionalidades-admin.md) | Funcionalidades administrativas |
| [13-perguntas-frequentes](01-utilizacao/13-perguntas-frequentes.md) | Perguntas frequentes |

### 2. [Documentacao Tecnica](02-tecnica/)

Destinada ao **responsavel tecnico** e **desenvolvedores**. Detalha a arquitetura, codigo e configuracoes.

| Arquivo | Descricao |
|---------|-----------|
| [00-visao-tecnica](02-tecnica/00-visao-tecnica.md) | Visao tecnica geral |
| [01-stack](02-tecnica/01-stack.md) | Stack tecnologica |
| [02-arquitetura](02-tecnica/02-arquitetura.md) | Arquitetura MVC |
| [03-estrutura-backend](02-tecnica/03-estrutura-backend.md) | Estrutura do Backend (101 arquivos Java) |
| [04-estrutura-frontend](02-tecnica/04-estrutura-frontend.md) | Estrutura do Frontend |
| [05-banco-dados](02-tecnica/05-banco-dados.md) | Banco de dados PostgreSQL |
| [06-flyway](02-tecnica/06-flyway.md) | Migrations Flyway (V1-V14) |
| [07-autenticacao](02-tecnica/07-autenticacao.md) | Autenticacao Google OAuth2 |
| [08-autorizacao](02-tecnica/08-autorizacao.md) | Autorizacao e perfis |
| [09-integracoes](02-tecnica/09-integracoes.md) | Integracoes externas |
| [10-configuracoes](02-tecnica/10-configuracoes.md) | Configuracoes da aplicacao |
| [11-logs](02-tecnica/11-logs.md) | Sistema de logs |
| [12-tratamento-erros](02-tecnica/12-tratamento-erros.md) | Tratamento de erros |
| [13-diagnostico](02-tecnica/13-diagnostico.md) | Procedimentos de diagnostico |
| [14-problemas-conhecidos](02-tecnica/14-problemas-conhecidos.md) | Problemas conhecidos |
| [15-recuperacao](02-tecnica/15-recuperacao.md) | Recuperacao e rollback |

### 3. [Documentacao Pratica](03-pratica/)

Responde: **"Preciso fazer X. Qual e o procedimento?"**

| Arquivo | Descricao |
|---------|-----------|
| [00-ambiente-local](03-pratica/00-ambiente-local.md) | Configurar ambiente local |
| [01-iniciar-parar](03-pratica/01-iniciar-parar.md) | Iniciar e parar o sistema |
| [02-build-deploy](03-pratica/02-build-deploy.md) | Build e deploy |
| [03-comandos-vps](03-pratica/03-comandos-vps.md) | Comandos comuns do VPS |
| [04-logs](03-pratica/04-logs.md) | Consultar logs |
| [05-banco-dados](03-pratica/05-banco-dados.md) | Operacoes com banco de dados |
| [06-importacao-csv](03-pratica/06-importacao-csv.md) | Importacao de CSV |
| [07-diagnostico](03-pratica/07-diagnostico.md) | Diagnostico de problemas |
| [08-backup-recuperacao](03-pratica/08-backup-recuperacao.md) | Backup e recuperacao |
| [09-manutencao](03-pratica/09-manutencao.md) | Manutencao do sistema |

### 4. [Documentacao Estrutural / Decisoes](04-estrutural/)

Explica **COMO** e **POR QUE** o sistema foi estruturado dessa maneira.

| Arquivo | Descricao |
|---------|-----------|
| [00-visao-arquitetural](04-estrutural/00-visao-arquitetural.md) | Visao arquitetural |
| [01-principios](04-estrutural/01-principios.md) | Principios do projeto |
| [02-estrutura-diretorios](04-estrutural/02-estrutura-diretorios.md) | Estrutura de diretorios |
| [03-organizacao-backend](04-estrutural/03-organizacao-backend.md) | Organizacao do Backend |
| [04-organizacao-frontend](04-estrutural/04-organizacao-frontend.md) | Organizacao do Frontend |
| [05-modelagem-banco](04-estrutural/05-modelagem-banco.md) | Modelagem do banco de dados |
| [06-fluxo-autenticacao](04-estrutural/06-fluxo-autenticacao.md) | Fluxo de autenticacao |
| [07-fluxo-autorizacao](04-estrutural/07-fluxo-autorizacao.md) | Fluxo de autorizacao |
| [08-fluxo-dados](04-estrutural/08-fluxo-dados.md) | Fluxo de dados |
| [09-integracoes-estrutura](04-estrutural/09-integracoes-estrutura.md) | Integracoes estruturais |
| [10-infraestrutura](04-estrutural/10-infraestrutura.md) | Infraestrutura |
| [11-decisoes-arquiteturais](04-estrutural/11-decisoes-arquiteturais.md) | Decisoes arquiteturais (ADRs) |
| [12-decisoes-negocio](04-estrutural/12-decisoes-negocio.md) | Decisoes de negocio |
| [13-decisoes-infra](04-estrutural/13-decisoes-infra.md) | Decisoes de infraestrutura |
| [14-historico](04-estrutural/14-historico.md) | Historico de decisoes |
| [15-evolucao-planejada](04-estrutural/15-evolucao-planejada.md) | Evolucao planejada |
| [16-adr-template](04-estrutural/16-adr-template.md) | Template para novos ADRs |

---

## Documentacao Existente (pre-existente)

| Arquivo | Descricao |
|---------|-----------|
| [mvp-dashboard-coordenacao](mvp-dashboard-coordenacao.md) | Documentacao do MVP original |
| [guia-operacional](guia-operacional.md) | Guia operacional de uso do dashboard |

---

## Como Usar Esta Documentacao

### Para usuarios
Comece pela [Visao Geral](01-utilizacao/00-visao-geral.md) e navegue pelos topicos de interesse.

### Para o TI
Consulte a [Documentacao Pratica](03-pratica/) para procedimentos operacionais e a [Documentacao Tecnica](02-tecnica/) para diagnósticos.

### Para desenvolvedores
Consulte a [Documentacao Tecnica](02-tecnica/) para entender a arquitetura e a [Documentacao Estrutural](04-estrutural/) para entender as decisoes de design.

### Para deploy
Consulte [Build e Deploy](03-pratica/02-build-deploy.md) e [Comandos VPS](03-pratica/03-comandos-vps.md).

---

## Manutencao da Documentacao

Sempre que uma alteracao estrutural relevante for realizada no sistema:

1. Atualizar documentacao tecnica
2. Atualizar documentacao pratica, se necessario
3. Atualizar documentacao de utilizacao, se afetar usuarios
4. Registrar decisao arquitetural quando necessario

---

## Seguranca

Nenhuma senha, secret ou token real e documentado. Credenciais sao referenciadas apenas como variaveis de ambiente.

---

**Responsavel:** Projeto Organiza+
**Contato:** kelvin.rosendo@colegiosatelite.com.br
