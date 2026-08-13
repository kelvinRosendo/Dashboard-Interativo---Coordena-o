# CHECKLIST DE TESTE MANUAL — FASE 2 (TESTE DOS PERFIS)
## Organiza+ Dashboard Interativo

> **Pré-requisitos:**
> - Aplicação rodando em `http://localhost:8081`
> - Banco PostgreSQL populado (DataInitializer já executa no startup)
> - Credenciais Google OAuth2 configuradas
> - Dois usuários admin: `alissandra@colegiosatelite.com.br`, `kelvin.rosendo@colegiosatelite.com.br`

---

## 1. TESTE DE LOGIN E REDIRECIONAMENTO POR PERFIL

| Perfil | Email de Teste | Rota Esperada | Status |
|--------|----------------|---------------|--------|
| ADMIN | alissandra@... | `/admin` | ☐ |
| ADMIN | kelvin.rosendo@... | `/admin` | ☐ |
| VICE_DIRETORA | (usuário criado no DataInitializer) | `/vice-diretora` | ☐ |
| COORDENADORA | (5 coordenadoras criadas no DataInitializer) | `/coordenadora/dashboard` | ☐ |

**Como testar:**
1. Acesse `http://localhost:8081/login`
2. Clique em "Entrar com Google"
3. Autentique com conta Google autorizada
4. Verifique se é redirecionado para a rota correta do perfil

---

## 2. TESTE DO PAINEL ADMIN (`/admin`)

### 2.1 Dados exibidos
- [ ] Indicadores: demandas abertas/pendentes/em andamento/concluídas/atrasadas
- [ ] Indicadores: usuários ativos/pendentes/total
- [ ] Indicadores: importações realizadas
- [ ] Semana em Foco ativa (título, descrição, período)
- [ ] Lista de demandas (TODAS — sem filtro de segmento)
- [ ] Lista de comunicados (TODOS)
- [ ] Lista de avisos (TODOS)
- [ ] Lista de eventos (TODOS)
- [ ] Cards de segmentos com progresso por segmento
- [ ] Pendências: demandas pendentes, próximas do prazo, **semanas não relatadas** (NOVO — deve mostrar número > 0 se houver semanas sem relatório)

### 2.2 Funcionalidades
- [ ] Criar nova Semana em Foco
- [ ] Criar novo Comunicado
- [ ] Criar nova Demanda
- [ ] Gerenciar Usuários (`/admin/usuarios`)
- [ ] Importar CSV (`/admin/importacao`)
- [ ] Criar/Editar Cards (`/novo-card`)
- [ ] Relatórios Admin (`/relatorio/admin/relatorios`)
- [ ] Google Agenda (`/agenda/eventos/novo`)

---

## 3. TESTE DO PAINEL VICE-DIRETORA (`/vice-diretora`)

### 3.1 Dados exibidos (BUG FIX P2#1 — VALIDAÇÃO CRÍTICA)
- [ ] **Indicadores: demandas ativas/pendentes/próximas/concluídas/em andamento/total — devem corresponder APENAS aos segmentos da vice-diretora**
- [ ] **Comparar: número no indicador "total" deve ser igual à quantidade de itens na lista de demandas abaixo**
- [ ] Lista de demandas (filtrada por segmentos da vice)
- [ ] Lista de comunicados (TODOS — global, conforme design)
- [ ] Lista de avisos (TODOS)
- [ ] Lista de eventos (TODOS)
- [ ] Semana em Foco ativa (global)
- [ ] Cards de segmentos com progresso
- [ ] **Pendências: semanas não relatadas — deve mostrar contagem real (BUG FIX P2#2)**

### 3.2 Verificação de isolamento
- [ ] A vice-diretora vê todos os 6 segmentos (por design)
- [ ] Contadores de indicadores = dados filtrados por segmento (não globais)

---

## 4. TESTE DO PAINEL COORDENADORA (`/coordenadora/dashboard`)

### 4.1 Dados exibidos
- [ ] Indicadores: demandas filtradas APENAS pelos segmentos vinculados à coordenadora
- [ ] Lista de demandas (apenas segmentos vinculados)
- [ ] Lista de comunicados (TODOS — global, conforme design atual)
- [ ] Lista de avisos (Globais + segmentos vinculados)
- [ ] Lista de eventos (Segmentos vinculados + compartilhados)
- [ ] Semanas em Foco (apenas segmentos vinculados)
- [ ] **Pendências: semanas não relatadas — deve mostrar contagem apenas dos segmentos da coordenadora (BUG FIX P2#2)**
- [ ] Cards de segmentos vinculados

### 4.2 Perfil Coordenadora - Página Individual (`/coordenadoras/{slug}`)
- [ ] Acessar `/coordenadoras` → clicar em uma coordenadora
- [ ] Verificar: semana em foco, segmento, semanas, tarefas, comunicados, demandas, progresso

---

## 5. TESTE DO PAINEL TV (PÚBLICO)

### 5.1 Semana em Foco (`/tv` ou `/tv/semana`)
- [ ] Semana em foco exibida
- [ ] Demandas da semana
- [ ] Comunicados (limitados)
- [ ] Faltas/Substituições/Manutenção
- [ ] Timer funcionando

### 5.2 Calendário (`/tv/calendario`)
- [ ] Visualização mensal/semanal
- [ ] Cards com data evento agrupados por dia
- [ ] Eventos Google (se token válido)
- [ ] Modo dashboard lateral (eventos da semana, demandas da semana) — **verificar se não há query duplicada (BUG FIX P3#5)**

---

## 6. VALIDAÇÃO ESPECÍFICA DOS BUGS CORRIGIDOS

### P2#1 - VICE_DIRETORA contadores globais vs lista filtrada
```
AÇÃO: Login como vice-diretora → /vice-diretora
VERIFICAR:
  - indicadores.totalDemandas == dashboard.demandas.size()
  - indicadores.demandasPendentes == contagem de demandas com status PENDENTE na lista
  - indicadores.demandasEmAndamento == contagem de demandas com status EM_ANDAMENTO na lista
```

### P2#2 - semanasNaoRelatadas hardcoded = 0
```
AÇÃO: Login como ADMIN → /admin
VERIFICAR: dashboard.pendencias.semanasNaoRelatadas > 0 (se houver semanas sem relatório)

AÇÃO: Login como COORDENADORA → /coordenadora/dashboard
VERIFICAR: dashboard.pendencias.semanasNaoRelatadas >= 0 (apenas segmentos dela)

AÇÃO: Login como VICE_DIRETORA → /vice-diretora
VERIFICAR: dashboard.pendencias.semanasNaoRelatadas >= 0
```

### P3#3 - contarProximasDoPrazoPorSegmentos ignora data
```
AÇÃO: Criar demandas com dataPrazo nos próximos 7 dias e outras mais distantes
VERIFICAR: indicadores.proximasDoPrazo conta apenas as com prazo <= 7 dias
```

### P3#4 - formatarHorarioInterno sempre "Sem horario definido"
```
AÇÃO: Acessar /tv/calendario ou criar evento conflitante
VERIFICAR: Cards com dataEvento mostram a data formatada (dd/MM/yyyy)
```

### P3#5 - DashboardTvController query duplicada
```
AÇÃO: Acessar /tv/calendario?modo=dashboard
VERIFICAR: Logs SQL mostram apenas 1 query em `cards` (não 2)
```

---

## 7. TESTES DE SEGURANÇA E ISOLAMENTO

| Cenário | Esperado | Status |
|---------|----------|--------|
| Coordenadora acessa `/admin` | Redirect + mensagem erro | ☐ |
| Coordenadora acessa `/vice-diretora` | Redirect + mensagem erro | ☐ |
| Vice-diretora acessa `/admin` | Redirect + mensagem erro | ☐ |
| Admin acessa `/coordenadora/dashboard` | Redirect | ☐ |
| Usuário não logado acessa qualquer rota protegida | Redirect `/login` | ☐ |
| Coordenadora vê demanda de OUTRO segmento | NÃO deve ver | ☐ |
| Coordenadora vê comunicado de outro segmento | VÊ (design atual - global) | ☐ |

---

## 8. FLUXOS COMPLETOS

- [ ] **Demanda:** Criar → Pendente → Em Andamento → Concluída
- [ ] **Relatório:** Criar rascunho → Preencher → Finalizar → Visualizar
- [ ] **Usuário:** Login → Pendente → Admin aprova → Login funciona
- [ ] **Bloqueio:** Admin bloqueia usuário → Usuário não loga → Admin desbloqueia → Loga
- [ ] **Importação CSV:** Upload arquivo real → Preview → Executar → Verificar histórico
- [ ] **Google Agenda:** Listar eventos → Criar evento → Detectar conflito

---

## 9. REGRESSÃO - O QUE NÃO DEVE TER QUEBRADO

- [ ] Login Google OAuth2
- [ ] Logout (GET e POST)
- [ ] CSRF em todos formulários
- [ ] Sidebar dinâmica por perfil
- [ ] 97 testes unitários ainda passam

---

## 10. OBSERVAÇÕES / BUGS ENCONTRADOS

| # | Descrição | Severidade | Rota | Perfil |
|---|-----------|------------|------|--------|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |

---

**Data:** ___________  
**Testador:** ___________  
**Versão:** Build `mvn clean package` pós-correções P2/P3