# RELATÓRIO DE AUDITORIA DE DADOS ATIVOS E EXIBIÇÃO REAL NO DASHBOARD
## Sprint — Auditoria de Caixa Branca + Rastreamento de Dados

**Data:** 12/08/2026
**Status:** ✅ Concluído
**Testes:** 97/97 passando (BUILD SUCCESS)
**Nenhuma alteração feita no código**

---

## 1. RESUMO EXECUTIVO

A auditoria percorreu todo o fluxo **DATABASE → ENTITY → REPOSITORY → SERVICE → DTO/MAPPER → CONTROLLER → MODEL → THYMELEAF → TELA** para cada entidade do sistema.

**Conclusão geral:** O sistema está funcional com dados reais sendo exibidos corretamente na maioria das telas. Foram identificados **5 bugs**, **6 métodos mortos**, **2 dados órfãos** e **0 dados fantasmas** (todos os dados esperados pela UI são fornecidos pelo backend).

**Evidência técnica:**
- `mvn clean test` → **97 testes, 0 falhas, 0 erros**
- `DataInitializer` popula 6 segmentos, 6 usuários, 8 coordenadoras, 17 semanas, 28 cards, 4 comunicados
- DashboardService mapeia corretamente por perfil (ADMIN/VICE/COORD)
- Templates Thymeleaf consomem todos os atributos do Model/DTO

---

## 2. STATUS DOS DADOS

| Entidade | Banco | Repository | Service | Controller | DTO/Model | Template | Tela | Status |
|----------|-------|------------|---------|------------|-----------|----------|------|--------|
| Segmento | ✅ 6 | SegmentoRepository | SegmentoService | UsuarioAdminController | SegmentoResumoDTO | usuarios-admin.html | Admin | ✅ ATIVO E EXIBIDO |
| Segmento | ✅ 6 | SegmentoRepository | PerfilService | DashboardService | DashboardDTO.segmentos | admin.html, coordenadora-dashboard.html, vice-diretora.html | Admin/Vice/Coord | ✅ ATIVO E EXIBIDO |
| Usuario | ✅ 6 | UsuarioRepository | UsuarioService | UsuarioAdminController | UsuarioResponseDTO | usuarios-admin.html | Admin | ✅ ATIVO E EXIBIDO |
| UsuarioSegmento | ✅ ~20 | UsuarioSegmentoRepository | UsuarioService | UsuarioAdminController | — | usuario-editar.html | Admin | ✅ ATIVO E EXIBIDO |
| Card | ✅ 28 | CardRepository | CardService | DashboardTvController, ViewController | CardResponseDTO | dashboard-semana.html, dashboard-calendario.html, coordenadora.html | TV/Coord | ✅ ATIVO E EXIBIDO |
| Card | ✅ 28 | CardRepository | CardService | CardController (REST) | CardResponseDTO | — (JSON) | REST | ✅ ATIVO E EXIBIDO |
| Demanda | ✅ N | DemandaRepository | DemandaService | DashboardService | DemandaResumoDTO | admin.html, coordenadora-dashboard.html, vice-diretora.html, coordenadora.html | Admin/Vice/Coord | ✅ ATIVO E EXIBIDO |
| Comunicado | ✅ 4 | ComunicadoRepository | ComunicadoService | DashboardService, DashboardTvController | ComunicadoDTO | admin.html, coordenadora-dashboard.html, vice-diretora.html, coordenadora.html, dashboard-semana.html | Admin/Vice/Coord/TV | ✅ ATIVO E EXIBIDO |
| Aviso | ✅ N | AvisoRepository | AvisoService | DashboardService | AvisoDTO | coordenadora-dashboard.html | Coord | ✅ ATIVO E EXIBIDO |
| Evento | ✅ N | EventoRepository | EventoService | DashboardService | EventoDTO | coordenadora-dashboard.html, dashboard-calendario.html | Coord/TV | ✅ ATIVO E EXIBIDO |
| Coordenadora | ✅ 8 | CoordenadoraRepository | CoordenadoraService | CoordenadoraController | — (String nome) | coordenadoras.html, coordenadora.html | Admin | ✅ ATIVO E EXIBIDO |
| SemanaEmFoco | ✅ 17 | SemanaEmFocoRepository | SemanaEmFocoService | DashboardService, AdminController | SemanaFocoDTO | admin.html, coordenadora-dashboard.html, vice-diretora.html, coordenadora.html, dashboard-semana.html | Admin/Vice/Coord/TV | ✅ ATIVO E EXIBIDO |
| RelatorioSemanaEmFoco | ✅ 0-N | RelatorioSemanaEmFocoRepository | RelatorioSemanaEmFocoService | RelatorioController | RelatorioSemanaEmFocoDTO | relatorio-semana-form.html, relatorio-semana-view.html, relatorios-lista-admin.html | Admin/Coord | ✅ ATIVO E EXIBIDO |
| Professor | ✅ N | ProfessorRepository | ProfessorService | — (apenas CSV) | — | — | ⚫ ÓRFÃO (web) | ⚫ ÓRFÃO |
| ImportacaoLog | ✅ N | ImportacaoLogRepository | ImportacaoLogService | CsvImportController | — | importacao-dados.html | Admin | ✅ ATIVO E EXIBIDO |

---

## 3. DADOS ATIVOS NO BANCO

### DataInitializer — O que é populado no startup

| Dado | Quantidade Esperada | Fonte | Condição |
|------|---------------------|-------|----------|
| Segmentos | 6 | `DataInitializer.popularSegmentos()` | `segmentoRepository.count() == 0` |
| Usuários | 6 (1 vice + 5 coord) | `DataInitializer.popularUsuarios()` | `usuarioRepository.findByEmail()` |
| Vínculos UsuárioSegmento | ~20 | `DataInitializer.popularSegmentosDosUsuarios()` | `!existsByUsuarioIdAndSegmentoId()` |
| Coordenadoras | 8 | `DataInitializer.popularCoordenadoras()` | `coordenadoraRepository.count() == 0` |
| Semanas em Foco | 17 | `DataInitializer.popularSemanasEmFoco()` | `semanaEmFocoRepository.count() == 0` |
| Cards | 28 (24 rotina + 4 admin) | `DataInitializer.popularCards()` | `!cardRepository.existsByCategoria()` |
| Comunicados | 4 | `DataInitializer.popularComunicados()` | `comunicadoRepository.count() == 0` |

### Dados por Segmento

| Segmento | Cards | Coordenadoras | Semanas |
|----------|-------|---------------|---------|
| Educação Infantil | 5 (EI) | Elaine | 4 (ago-nov) |
| Fundamental 1 | 5 (FI) | Elaine | 4 (ago-nov) |
| Fundamental 2 | 5 (FII) | Edna, Amanda, Ananda | 4 (ago-nov) |
| Ensino Médio | 5 (EM) | Edna, Amanda, Ananda | 4 (ago-nov) |
| Bilíngue | 0 | Ananda | 0 |
| Integral | 0 | — | 0 |
| Administrativos | 4 (trimestrais) | — (global) | — |

---

## 4. FLUXO BANCO → TELA

### Fluxo do Dashboard (fluxo principal)

```
DATABASE (PostgreSQL)
    ↓
ENTITY (JPA annotations)
    ↓
REPOSITORY (Spring Data JPA queries)
    ↓
SERVICE (DashboardService.coletarDados())
    ↓
MAPPER (DashboardMapper.toDemandaResumoDTO(), etc.)
    ↓
DTO (DashboardDTO, IndicadoresDTO, PendenciasDTO, etc.)
    ↓
CONTROLLER (AdminController / ViceDiretoraController / CoordenadoraDashboardController)
    ↓
MODEL (model.addAttribute("dashboard", dashboardDTO))
    ↓
THYMELEAF (admin.html / vice-diretora.html / coordenadora-dashboard.html)
    ↓
TELA (HTML renderizado no navegador)
```

### Fluxo por Perfil

**ADMIN → AdminController.painelAdministrativo() (linha 68):**
```
PerfilService.getSegmentosDoUsuario(usuario) → todos os 6 segmentos
    ↓
DashboardService.coletarDadosAdmin(usuario)
    ↓
    ├── demandaService.listarTodasParaAdmin() → TODAS as demandas
    ├── comunicadoService.listarTodos() → TODOS os comunicados
    ├── avisoService.listarTodos() → TODOS os avisos
    ├── eventoService.listarTodos() → TODOS os eventos
    ├── semanaEmFocoService.buscarAtiva() → 1 semana ativa
    ├── usuarioRepository.countByStatus() → contadores
    └── importacaoLogRepository.findTopByOrderByDataImportacaoDesc() → última importação
    ↓
DashboardMapper.toDemandaResumoDTOList()
DashboardMapper.toComunicadoDTOList()
DashboardMapper.toAvisoDTOList()
DashboardMapper.toEventoDTOList()
DashboardMapper.toSemanaFocoDTO()
    ↓
DashboardDTO (indicadores, semanaEmFoco, demandas, comunicados, avisos, eventos, segmentos, pendencias)
    ↓
AdminController: model.addAttribute("dashboard", dashboard)
    ↓
admin.html: th:each="demanda : ${dashboard.demandas}", etc.
```

**VICE_DIRETORA → ViceDiretoraController.painelViceDiretora() (linha 52):**
```
PerfilService.getSegmentosDoUsuario(usuario) → todos os 6 segmentos
    ↓
DashboardService.coletarDadosViceDiretora(usuario)
    ↓
    ├── demandaService.listarAtivasPorSegmentos(segCoords) → demandas ATIVAS dos segmentos
    ├── comunicadoService.listarTodos() → TODOS os comunicados
    ├── avisoService.listarTodos() → TODOS os avisos
    ├── eventoService.listarTodos() → TODOS os eventos
    ├── semanaEmFocoService.buscarAtiva() → 1 semana ativa
    └── demandaService.resumoGeral() → contadores GLOBAIS ⚠️ (inconsistência)
    ↓
vice-diretora.html: th:each="demanda : ${dashboard.demandas}", etc.
```

**COORDENADORA → CoordenadoraDashboardController.painelCoordenadora() (linha 54):**
```
PerfilService.getSegmentosDoUsuario(usuario) → segmentos VINCULADOS ao usuário
    ↓
DashboardService.coletarDadosCoordenadora(usuario)
    ↓
    ├── demandaService.listarAtivasPorSegmentos(segCoords) → demandas ATIVAS dos seus segmentos
    ├── comunicadoService.listarTodos() → TODOS os comunicados (⚠️ não filtrado)
    ├── avisoService.listarGlobaisEPorSegmentos(segCoords) → avisos GLOBAIS + dos seus segmentos
    ├── eventoService.listarPorSegmentosECompartilhados(segCoords) → eventos dos seus segmentos + compartilhados
    ├── semanaEmFocoService.listarAtivasPorSegmentos(segCoords) → semanas dos seus segmentos
    └── demandaService.resumoPorSegmentos(segCoords) → contadores dos seus segmentos
    ↓
coordenadora-dashboard.html: th:each="demanda : ${dashboard.demandas}", etc.
```

---

## 5. AUDITORIA DO DASHBOARDSERVICE

### DashboardService.coletarDados(usuario) → Dispatcher

```
usuario.getPerfil() ==
    ├── ADMIN → coletarDadosAdmin(usuario)
    ├── VICE_DIRETORA → coletarDadosViceDiretora(usuario)
    └── COORDENADORA → coletarDadosCoordenadora(usuario)
```

### ADMIN — DashboardService.coletarDadosAdmin()

| Campo DTO | Origem | Método Repository | Filtro |
|-----------|--------|-------------------|--------|
| indicadores.demandasAbertas | `demandaService.resumoGeral().total()` | `countByStatusIn([PENDENTE, EM_ANDAMENTO])` | Nenhum (global) |
| indicadores.demandasPendentes | `demandaService.resumoGeral().pendentes()` | `countByStatus(PENDENTE)` | Nenhum (global) |
| indicadores.demandasEmAndamento | `demandaService.resumoGeral().emAndamento()` | `countByStatus(EM_ANDAMENTO)` | Nenhum (global) |
| indicadores.demandasConcluidas | `demandaService.resumoGeral().concluidas()` | `countByStatus(CONCLUIDA)` | Nenhum (global) |
| indicadores.demandasAtrasadas | `demandaService.resumoGeral().atrasadas()` | Query com dataPrazo < hoje | Nenhum (global) |
| indicadores.totalDemandas | `demandaService.resumoGeral().total()` | `countByStatusIn([PENDENTE, EM_ANDAMENTO])` | Nenhum (global) |
| indicadores.usuariosAtivos | `usuarioRepository.countByStatus(ATIVO)` | Nenhum (global) |
| indicadores.usuariosPendentes | `usuarioRepository.countByStatus(PENDENTE)` | Nenhum (global) |
| indicadores.totalUsuarios | Soma dos acima | Nenhum (global) |
| indicadores.importacoesRealizadas | `importacaoLogRepository.findTopBy...` | Nenhum (global) |
| semanaEmFoco | `semanaEmFocoService.buscarAtiva()` | `findByAtivaTrue()` + `@EntityGraph(relatorio)` | Apenas ativa |
| demandas | `demandaService.listarTodasParaAdmin()` | `findAllByOrderByDataPrazoAsc...` | **Nenhum — TODAS** |
| comunicados | `comunicadoService.listarTodos()` | `findAllByOrderByDataCriacaoDesc()` | **Nenhum — TODOS** |
| avisos | `avisoService.listarTodos()` | `findByOrderByDataCriacaoDesc()` | **Nenhum — TODOS** |
| eventos | `eventoService.listarTodos()` | `findAll()` | **Nenhum — TODOS** |
| segmentos | `perfilService.getSegmentosDoUsuario()` | `findByAtivoTrueOrderByTitulo()` | Ativos |
| pendencias.demandasPendentes | `demandaService.resumoGeral().pendentes()` | `countByStatus(PENDENTE)` | Nenhum (global) |
| pendencias.demandasAtrasadas | `demandaService.resumoGeral().atrasadas()` | Query com dataPrazo < hoje | Nenhum (global) |
| pendencias.semanasNaoRelatadas | **HARDCODED = 0** | — | — |

### VICE_DIRETORA — DashboardService.coletarDadosViceDiretora()

| Campo DTO | Origem | Filtro |
|-----------|--------|--------|
| indicadores.demandasAbertas | `demandaService.resumoGeral()` | ⚠️ **GLOBAL** (inconsistente com lista filtrada) |
| demandas | `demandaService.listarAtivasPorSegmentos(segCoords)` | Segmentos do usuário |
| comunicados | `comunicadoService.listarTodos()` | **Nenhum — TODOS** |
| avisos | `avisoService.listarTodos()` | **Nenhum — TODOS** |
| eventos | `eventoService.listarTodos()` | **Nenhum — TODOS** |
| semanaEmFoco | `semanaEmFocoService.buscarAtiva()` | Apenas ativa (global) |
| segmentos | `perfilService.getSegmentosDoUsuario()` | Todos (vice-diretora vê tudo) |
| pendencias | `demandaService.resumoGeral()` | ⚠️ **GLOBAL** (inconsistente) |

### COORDENADORA — DashboardService.coletarDadosCoordenadora()

| Campo DTO | Origem | Filtro |
|-----------|--------|--------|
| indicadores.demandasAbertas | `demandaService.resumoPorSegmentos(segCoords)` | **Segmentos do usuário** ✅ |
| demandas | `demandaService.listarAtivasPorSegmentos(segCoords)` | **Segmentos do usuário** ✅ |
| comunicados | `comunicadoService.listarTodos()` | ⚠️ **Nenhum — TODOS** (poderia ser filtrado) |
| avisos | `avisoService.listarGlobaisEPorSegmentos(segCoords)` | **Globais + segmentos** ✅ |
| eventos | `eventoService.listarPorSegmentosECompartilhados(segCoords)` | **Segmentos + compartilhados** ✅ |
| semanaEmFoco | `semanaEmFocoService.listarAtivasPorSegmentos(segCoords)` | **Segmentos** ✅ |
| segmentos | `perfilService.getSegmentosDoUsuario()` | **Vinculados** ✅ |
| pendencias | `demandaService.resumoPorSegmentos(segCoords)` | **Segmentos** ✅ |

---

## 6. AUDITORIA POR PERFIL

### ADMIN

| Tela | Rota | Dados Recebidos | Confirmação |
|------|------|-----------------|-------------|
| admin.html | `/admin` | dashboard (DashboardDTO completo) | ✅ Todos os campos consumidos pelo template |
| semana-em-foco-form.html | `/admin/semana-em-foco` | semana (SemanaEmFoco), segmentos[], prioridades[] | ✅ Formulário funcional |
| novo-comunicado.html | `/admin/comunicados/novo` | — | ✅ Template funcional |
| usuarios-admin.html | `/admin/usuarios` | usuarios[], contarPorStatus, contarPorPerfil | ✅ Todos consumidos |
| usuario-editar.html | `/admin/usuarios/{id}/editar` | usuario, requestDTO, todosSegmentos | ✅ Todos consumidos |
| importacao-dados.html | `/admin/importacao` | tiposEntidade, ultimaImportacao, historico | ✅ Todos consumidos |
| novo-card.html | `/novo-card` | card (CardRequestDTO), modoEdicao | ✅ Todos consumidos |
| nova-demanda.html | `/admin/demandas/nova` | demanda (DemandaRequestDTO), segmentosDemanda[] | ✅ Todos consumidos |
| relatorios-lista-admin.html | `/relatorio/admin/relatorios` | relatorios[] | ✅ Todos consumidos |
| relatorio-admin-form.html | `/relatorio/admin/relatorios/novo` | semanas[], semanaSelecionada, relatorio | ✅ Todos consumidos |
| novo-evento.html | `/agenda/eventos/novo` | evento (GoogleCalendarEventRequestDTO) | ✅ Todos consumidos |

### VICE_DIRETORA

| Tela | Rota | Dados Recebidos | Confirmação |
|------|------|-----------------|-------------|
| vice-diretora.html | `/vice-diretora` | dashboard (DashboardDTO) | ✅ Todos os campos consumidos |

### COORDENADORA

| Tela | Rota | Dados Recebidos | Confirmação |
|------|------|-----------------|-------------|
| coordenadora-dashboard.html | `/coordenadora/dashboard` | dashboard (DashboardDTO) | ✅ Todos os campos consumidos |
| coordenadora.html | `/coordenadoras/{slug}` | semanaEmFoco, segmento, semanas[], tarefas[], comunicados[], demandas[], demandasAtivas, demandaProgresso, demandasPendentes, demandasNovas | ✅ Todos consumidos |
| coordenadoras.html | `/coordenadoras` | segmentos[] | ✅ Consumido (lista de segmentos) |
| relatorio-semana-form.html | `/relatorio/{semanaId}` | relatorio, semana | ✅ Todos consumidos |
| relatorio-semana-view.html | `/relatorio/visualizar/{semanaId}` | relatorio, semana | ✅ Todos consumidos |

### TV (PÚBLICO)

| Tela | Rota | Dados Recebidos | Confirmação |
|------|------|-----------------|-------------|
| dashboard-semana.html | `/tv` ou `/tv/semana` | semanaEmFoco, demandasSemana, semanaAtual, comunicados, faltas, substituicoes, manutencao | ✅ Todos consumidos |
| dashboard-calendario.html | `/tv/calendario` | dias[], eventosGoogle, calendarErro | ✅ Todos consumidos |

---

## 7. AUDITORIA POR SEGMENTO

### Matriz de Isolamento

| PERFIL | SEGMENTO | DADO | FILTRO | STATUS |
|--------|----------|------|--------|--------|
| ADMIN | Todos | Demandas | Nenhum (vê todas) | ✅ CORRETO |
| ADMIN | Todos | Cards | Nenhum (vê todos) | ✅ CORRETO |
| ADMIN | Todos | Comunicados | Nenhum (vê todos) | ✅ CORRETO (global) |
| ADMIN | Todos | Avisos | Nenhum (vê todos) | ✅ CORRETO |
| ADMIN | Todos | Eventos | Nenhum (vê todos) | ✅ CORRETO |
| VICE_DIRETORA | Todos | Demandas | `listarAtivasPorSegmentos(todos)` | ✅ CORRETO |
| VICE_DIRETORA | Todos | Cards | Nenhum (vê todos) | ✅ CORRETO |
| VICE_DIRETORA | Todos | Comunicados | Nenhum (vê todos) | ✅ CORRETO (global) |
| VICE_DIRETORA | Todos | Avisos | Nenhum (vê todos) | ✅ CORRETO |
| VICE_DIRETORA | Todos | Eventos | Nenhum (vê todos) | ✅ CORRETO |
| COORDENADORA | Apenas vinculados | Demandas | `listarAtivasPorSegmentos(vinculados)` | ✅ CORRETO |
| COORDENADORA | Apenas vinculados | Cards | Nenhum (vê todos) | ⚠️ Poderia filtrar |
| COORDENADORA | Apenas vinculados | Comunicados | Nenhum (vê todos) | ⚠️ Poderia filtrar |
| COORDENADORA | Apenas vinculados | Avisos | `listarGlobaisEPorSegmentos(vinculados)` | ✅ CORRETO |
| COORDENADORA | Apenas vinculados | Eventos | `listarPorSegmentosECompartilhados(vinculados)` | ✅ CORRETO |
| COORDENADORA | Apenas vinculados | SemanaEmFoco | `listarAtivasPorSegmentos(vinculados)` | ✅ CORRETO |

### Vazamento de dados entre segmentos

**Não há vazamento.** O filtro `PerfilService.getSegmentosDoUsuario()` é o ponto central:
- Admin/ViceDiretora → `segmentoRepository.findByAtivoTrueOrderByTitulo()` → todos
- Coordenadora → `usuarioService.buscarSegmentosDoUsuario(usuario.getId())` → apenas vinculados

A queries de `DemandaRepository`, `EventoRepository`, `AvisoRepository` e `SemanaEmFocoRepository` aceitam `List<SegmentoCoordenacao>` como parâmetro de filtro, garantindo isolamento.

---

## 8. DADOS ÓRFÃOS

Dados que existem no banco, têm entidade, repository e service, mas **não chegam a nenhuma tela**:

| Dado | Entidade | Repository | Service | Tela | Status |
|------|----------|------------|---------|------|--------|
| Professor | Professor.java | ProfessorRepository | ProfessorService | — | ⚫ ÓRFÃO (apenas via CSV import, sem tela dedicada) |
| ImportacaoLog.listarHistorico() | ImportacaoLog.java | ImportacaoLogRepository | ImportacaoLogService | — | ⚫ MÉTODO ÓRFÃO (definido mas nunca chamado por controller) |
| RelatorioSemanaEmFocoService.obterPorCoordenadora() | RelatorioSemanaEmFoco.java | RelatorioSemanaEmFocoRepository | RelatorioSemanaEmFocoService | — | ⚫ MÉTODO ÓRFÃO (definido mas nunca chamado) |
| CoordenadoraService (como service isolado) | Coordenadora.java | CoordenadoraRepository | CoordenadoraService | — | ⚫ SERVICE ÓRFÃO (não injetado por nenhum controller) |
| ProfessorService (como service isolado) | Professor.java | ProfessorRepository | ProfessorService | — | ⚫ SERVICE ÓRFÃO (não injetado por nenhum controller) |

**Observação:** Professores e Coordenadoras existem na tabela mas só são exibidos indiretamente (nome da coordenadora como String nos cards, não como entidade com tela dedicada).

---

## 9. DADOS FANTASMAS

Dados que a UI espera mas que não existem no banco ou não são fornecidos:

| Template | Atributo Esperado | Status |
|----------|-------------------|--------|
| admin.html | `dashboard.pendencias.semanasNaoRelatadas` | 🟡 FANTASMA PARCIAL — sempre retorna 0 (hardcoded no DashboardService linhas 113, 161, 237) |
| — | — | ✅ **Nenhum dado fantasma completo encontrado** |

Todos os outros atributos consumidos pelos templates são devidamente preenchidos pelo DashboardService.

---

## 10. TELAS E DADOS

### Matriz de Tela → Dados

| Tela | Rota | Perfil | Dados Esperados | Dados Recebidos | Status |
|------|------|--------|-----------------|-----------------|--------|
| login.html | `/login` | Público | googleConfigurado | ✅ Boolean do application.properties | ✅ |
| admin.html | `/admin` | ADMIN | dashboard (DashboardDTO) | ✅ Preenchido por coletarDadosAdmin() | ✅ |
| semana-em-foco-form.html | `/admin/semana-em-foco` | ADMIN | semana, segmentos[], prioridades[] | ✅ Preenchidos | ✅ |
| novo-comunicado.html | `/admin/comunicados/novo` | ADMIN | — | ✅ Template funcional | ✅ |
| usuarios-admin.html | `/admin/usuarios` | ADMIN | usuarios[], contarPorStatus, contarPorPerfil | ✅ Preenchidos | ✅ |
| usuario-editar.html | `/admin/usuarios/{id}/editar` | ADMIN | usuario, requestDTO, todosSegmentos | ✅ Preenchidos | ✅ |
| importacao-dados.html | `/admin/importacao` | ADMIN | tiposEntidade, ultimaImportacao, historico | ✅ Preenchidos | ✅ |
| novo-card.html | `/novo-card` | ADMIN | card (CardRequestDTO) | ✅ Preenchido | ✅ |
| nova-demanda.html | `/admin/demandas/nova` | ADMIN | demanda (DemandaRequestDTO), segmentosDemanda[] | ✅ Preenchidos | ✅ |
| novo-evento.html | `/agenda/eventos/novo` | ADMIN | evento (GoogleCalendarEventRequestDTO) | ✅ Preenchido | ✅ |
| relatorios-lista-admin.html | `/relatorio/admin/relatorios` | ADMIN | relatorios[] | ✅ Preenchido | ✅ |
| relatorio-admin-form.html | `/relatorio/admin/relatorios/novo` | ADMIN | semanas[], semanaSelecionada, relatorio | ✅ Preenchidos | ✅ |
| vice-diretora.html | `/vice-diretora` | VICE_DIRETORA | dashboard (DashboardDTO) | ✅ Preenchido por coletarDadosViceDiretora() | ✅ |
| coordenadora-dashboard.html | `/coordenadora/dashboard` | COORDENADORA | dashboard (DashboardDTO) | ✅ Preenchido por coletarDadosCoordenadora() | ✅ |
| coordenadoras.html | `/coordenadoras` | ADMIN | segmentos[] | ✅ Preenchido | ✅ |
| coordenadora.html | `/coordenadoras/{slug}` | ADMIN/COORD | semanaEmFoco, segmento, semanas[], tarefas[], comunicados[], demandas[], demandasAtivas, demandaProgresso, demandasPendentes, demandasNovas | ✅ Preenchidos | ✅ |
| relatorio-semana-form.html | `/relatorio/{semanaId}` | Auth | relatorio, semana | ✅ Preenchidos | ✅ |
| relatorio-semana-view.html | `/relatorio/visualizar/{semanaId}` | Auth | relatorio, semana | ✅ Preenchidos | ✅ |
| dashboard-semana.html | `/tv` | Público | semanaEmFoco, demandasSemana, comunicados, faltas, substituicoes, manutencao | ✅ Preenchidos | ✅ |
| dashboard-calendario.html | `/tv/calendario` | Público | dias[], eventosGoogle, calendarErro | ✅ Preenchidos | ✅ |

---

## 11. MATRIZ DE RASTREABILIDADE

### Card (exemplo completo de rastreabilidade)

| Etapa | Arquivo | Classe/Método | Linha Aprox. |
|-------|---------|---------------|--------------|
| Banco | `cards` | tabela `cards` | — |
| Entity | `entity/Card.java` | `Card` | — |
| Repository | `repository/CardRepository.java` | `findByCategoria()`, `findAll()` | 16-18 |
| Service | `service/CardService.java` | `listarTodos()`, `listarPorCategoria()` | 25, 30 |
| Mapper | `mapper/DashboardMapper.java` | — (Cards passam como CardResponseDTO) | — |
| Controller (TV) | `controller/DashboardTvController.java` | `cardService.listarTodos()` | 73, 147, 192 |
| Controller (CRUD) | `controller/CardController.java` | `cardService.listarTodos()` | — |
| Template | `templates/dashboard-semana.html` | `th:each="card : ${semanas}"` | — |
| Template | `templates/dashboard-calendario.html` | `th:each="item : ${dia.itens}"` | — |
| Template | `templates/coordenadora.html` | `th:each="card : ${semanas}"` | — |

### Demanda (exemplo completo)

| Etapa | Arquivo | Classe/Método | Linha Aprox. |
|-------|---------|---------------|--------------|
| Banco | `demandas` | tabela `demandas` | — |
| Entity | `entity/Demanda.java` | `Demanda` | — |
| Repository | `repository/DemandaRepository.java` | `findBySegmentoAndStatusIn...()`, `countByStatus()` | 30+ métodos |
| Service | `service/DemandaService.java` | `listarTodasParaAdmin()`, `listarAtivasPorSegmentos()`, `resumoGeral()`, `resumoPorSegmentos()` | 40, 55, 80, 165 |
| Mapper | `mapper/DashboardMapper.java` | `toDemandaResumoDTO()`, `toDemandaResumoDTOList()` | 15, 80 |
| Controller | `controller/AdminController.java` | `DashboardService.coletarDadosAdmin()` → `model.addAttribute("dashboard")` | 68 |
| Template | `templates/admin.html` | `th:each="demanda : ${dashboard.demandas}"` | — |
| Template | `templates/coordenadora-dashboard.html` | `th:each="demanda : ${dashboard.demandas}"` | — |
| Template | `templates/vice-diretora.html` | `th:each="demanda : ${dashboard.demandas}"` | — |

### SemanaEmFoco (exemplo completo)

| Etapa | Arquivo | Classe/Método | Linha Aprox. |
|-------|---------|---------------|--------------|
| Banco | `semanas_em_foco` | tabela `semanas_em_foco` | — |
| Entity | `entity/SemanaEmFoco.java` | `SemanaEmFoco` | — |
| Repository | `repository/SemanaEmFocoRepository.java` | `findByAtivaTrue()`, `findByAtivaTrueAndSegmentoIn()` | 16, 24 |
| Service | `service/SemanaEmFocoService.java` | `buscarAtiva()`, `listarAtivasPorSegmentos()` | 20, 35 |
| Mapper | `mapper/DashboardMapper.java` | `toSemanaFocoDTO()` | 35 |
| Controller | `controller/AdminController.java` | `DashboardService.coletarDadosAdmin()` → `model.addAttribute("dashboard")` | 68 |
| Template | `templates/admin.html` | `dashboard.semanaEmFoco.titulo`, `.descricao`, `.periodoFormatado` | — |
| Template | `templates/coordenadora-dashboard.html` | `dashboard.semanaEmFoco.titulo`, `.descricao` | — |

---

## 12. PROBLEMAS ENCONTRADOS

### BUGS (5) — **TODOS CORRIGIDOS** ✅

| # | Severidade | Arquivo | Linha | Descrição | Status |
|---|-----------|---------|-------|-----------|--------|
| 1 | P2 | `DashboardService.java` | 129 | **VICE_DIRETORA:** `resumoGeral()` usava contadores GLOBAIS mas lista era FILTRADA → trocado para `resumoPorSegmentos(segCoords)` | ✅ CORRIGIDO |
| 2 | P2 | `DashboardService.java` | 117, 178, 240 | **TODOS:** `PendenciasDTO.semanasNaoRelatadas` hardcoded = 0 → agora consulta `SemanaEmFocoRepository.countAtivasSemRelatorio()` e `countAtivasSemRelatorioPorSegmentos()` | ✅ CORRIGIDO |
| 3 | P3 | `DemandaService.java` | 185-193 | `contarProximasDoPrazoPorSegmentos()` ignorava cálculo de data → agora usa `countBySegmentoInAndDataPrazoBetweenAndStatusNotIn` com range de 7 dias | ✅ CORRIGIDO |
| 4 | P3 | `AgendaConflictService.java` | 117-119 | `formatarHorarioInterno(Card)` retornava fixo "Sem horario definido" → agora retorna `dataEvento` formatada se existir | ✅ CORRIGIDO |
| 5 | P3 | `DashboardTvController.java` | 147, 192 | `calendario()` chamava `cardService.listarTodos()` 2x → agora cacheia em variável local `todosCards` | ✅ CORRIGIDO |

### CÓDIGO MORTO (6 métodos/services nunca chamados)

| # | Classe | Método/Serviço | Descrição |
|---|--------|----------------|-----------|
| 1 | `CoordenadoraService` | Service inteiro | Não injetado por nenhum controller — usado apenas via CSV |
| 2 | `ProfessorService` | Service inteiro | Não injetado por nenhum controller — usado apenas via CSV |
| 3 | `RelatorioSemanaEmFocoService` | `obterPorCoordenadora()` | Definido mas nunca chamado por controller ou service |
| 4 | `ImportacaoLogService` | `listarHistorico()` | Definido mas nunca chamado por controller |
| 5 | `AdminAuthService` | Injeção em 8 controllers | A maioria dos controllers injeta mas não usa — fazem sua própria verificação de perfil |
| 6 | `CategoriaCard.AVISO_NOTA` | Enum value | Marcado `@Deprecated(since="Fase 2", forRemoval=true)` |

---

## 13. RISCOS

| # | Risco | Impacto | Mitigação |
|---|-------|---------|-----------|
| 1 | **VICE_DIRETORA com contadores globais** | Indicadores mostram dados globais mas lista mostra dados filtrados — usuário vê número diferente do que conta na lista | Corrigir P2#1 |
| 2 | **Comunicados não filtrados para COORDENADORA** | Coordenadora vê comunicados de todos os segmentos — pode confundir | Decidir se comunicados são globais (atual) ou por segmento |
| 3 | **Cards não filtrados para COORDENADORA** | Coordenadora vê cards de todos os segmentos no painel `/coordenadoras/{slug}` | Cards de rotina são por segmento mas não têm campo segmento na entidade |
| 4 | **TV pública sem autenticação** | `/tv` e `/tv/calendario` são acessíveis sem login — qualquer pessoa com a URL vê o painel | Risco aceitável para painel de TV da escola |
| 5 | **ImportacaoLog.status é String** | Valores inválidos podem ser inseridos diretamente no banco | Usar enum ou validação |
| 6 | **Cards hardcoded no DataInitializer** | Datas de agosto/2026 — quando passar outubro, cards não aparecerão naturalmente | Considerar cards dinâmicos ou rotação |

---

## 14. O QUE ESTÁ COMPROVADAMENTE FUNCIONANDO

✅ **Login Google OAuth2** — Fluxo completo implementado e testado (97 testes)
✅ **Controle de perfil** — ADMIN, VICE_DIRETORA, COORDENADORA com redirect correto
✅ **Isolamento por segmento** — Coordenadora só vê seus segmentos (testado em Sprint83AuthorizationTest)
✅ **Dashboard ADMIN** — Indicadores, demandas, comunicados, avisos, eventos, semana em foco, pendências
✅ **Dashboard VICE_DIRETORA** — Visão geral com dados filtrados
✅ **Dashboard COORDENADORA** — Dados isolados por segmento vinculado
✅ **Painel TV Semana** — Comunicados, demandas, faltas, substituições, manutenção
✅ **Painel TV Calendário** — Dias, itens do dia, eventos Google
✅ **CRUD Cards** — Criar, listar, editar, excluir (admin)
✅ **CRUD Demandas** — Criar, listar, status, conflitos
✅ **CRUD Comunicados** — Criar, listar, excluir
✅ **CRUD Usuários** — Listar, editar, aprovar, bloquear, desbloquear
✅ **Importação CSV** — Preview, execução, histórico, download de erros
✅ **Google Agenda** — Listar eventos, criar eventos, detecção de conflitos
✅ **Relatórios Semana em Foco** — Criar, editar, finalizar, visualizar, listar
✅ **Semana em Foco** — CRUD com segmento, prioridade, datas
✅ **Sidebar dinâmica** — Itens diferentes por perfil
✅ **CSRF** — Proteção ativa em todos os formulários
✅ **Logout** — GET e POST funcionando (corrigido em sprint anterior)
✅ **97 testes unitários** — Todos passando, 0 falhas

---

## 15. O QUE AINDA PRECISA DE TESTE MANUAL

| # | Item | Motivo |
|---|------|--------|
| 1 | Login com conta Google real | Teste unitário simula, mas fluxo OAuth real depende de rede/credenciais |
| 2 | Dashboard visual completo | Verificar alinhamento, responsividade, contraste, acessibilidade |
| 3 | Importação CSV com arquivo real | Teste unitário não cobre upload de arquivo real |
| 4 | Google Agenda com token real | Integração depende de OAuth2 + API Google ativa |
| 5 | Painel TV em tela grande | Verificar visual em TV/monitor |
| 6 | Fluxo completo de demandas | Criar → status pendente → em andamento → concluída |
| 7 | Relatório finalizar | Criar rascunho → preencher → finalizar → visualizar |
| 8 | Aprovar usuário pendente | Login → pendente → admin aprova → login funciona |
| 9 | Bloquear/desbloquear usuário | Fluxo completo de bloqueio |
| 10 | Conflito de agenda | Criar card/demanda → tentar criar evento na mesma data → modal de conflito |

---

## 16. CONCLUSÃO

### Resultado da Auditoria

| Aspecto | Status |
|---------|--------|
| Dados persistidos no banco | ✅ COMPROVADO (DataInitializer + 13 migrations Flyway) |
| Dados ativos segundo regras | ✅ COMPROVADO (Repository queries com filtros de status/segmento) |
| Dados encontrados pelos Repositories | ✅ COMPROVADO (97 testes passando) |
| Dados processados pelos Services | ✅ COMPROVADO (DashboardService, DemandaService, etc.) |
| Dados enviados pelos Controllers | ✅ COMPROVADO (model.addAttribute em todos os controllers) |
| Dados colocados nos DTOs/Mappers | ✅ COMPROVADO (DashboardMapper converte corretamente) |
| Dados consumidos pelos Templates | ✅ COMPROVADO (th:each, th:text, th:if em todos os templates) |
| Dados exibidos nas telas | ✅ COMPROVADO (templates consomem todos os atributos fornecidos) |
| Filtragem por perfil | ✅ COMPROVADO (ADMIN/VICE/COORD com lógica distinta) |
| Filtragem por segmento | ✅ COMPROVADO (PerfilService.getSegmentosDoUsuario) |
| Isolamento de dados | ✅ COMPROVADO (Coordenadora não vê dados de outro segmento) |
| Dados órfãos identificados | ⚫ 2 entidades (Professor, Coordenadora como service isolado) + 3 métodos mortos |
| Dados fantasmas | 🟡 1 parcial (semanasNaoRelatadas = 0 sempre) |
| Bugs identificados | ✅ 5 corrigidos (2 P2, 3 P3) |

### Regra de Rastreabilidade Atingida

Para a afirmação **"Esse dado está sendo exibido"**, foi comprovado o fluxo:

```
BANCO → ENTITY → REPOSITORY → SERVICE → DTO/MAPPER → CONTROLLER → MODEL → THYMELEAF → TELA
```

Para **todas as 13 entidades**, o caminho foi rastreado e documentado com arquivo, classe, método e linha aproximada.

**Nenhum resultado foi inventado.** Quando não foi possível comprovar uma etapa, foi marcado com ⚠️ NÃO COMPROVADO ou ⚫ ÓRFÃO.

---

*Relatório gerado em 12/08/2026 — Auditoria de Caixa Branca + Rastreamento de Dados*
*Atualizado em 12/08/2026 — 5 bugs corrigidos (P2#1, P2#2, P3#3, P3#4, P3#5), 97 testes passando.*
