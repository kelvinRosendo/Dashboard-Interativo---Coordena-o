# MVP do Dashboard da Coordenacao

## Base tecnica

O projeto atual usa Java 17, Spring Boot, Thymeleaf, CSS, JavaScript e PostgreSQL.
O MVP deve evoluir essa base em vez de iniciar um sistema novo.

## Primeira entrega implementada

Foram adicionadas as primeiras estruturas do MVP visual para TV:

- categoria `SEMANA_EM_FOCO`;
- categoria `ROTINA_COORDENADORES`;
- categoria `SUBSTITUICAO`;
- tela de TV para Semana em Foco;
- tela de TV para Calendario Integrado;
- modo semanal e mensal no calendario;
- migracao das categorias antigas `ROTINA_AUXILIAR` e `HORARIO_PROFESSOR`;
- dados iniciais para Semana em Foco e Rotina dos Coordenadores.

## Rotas principais

- `/` - dashboard atual com cards gerais;
- `/tv/semana` - tela da Semana em Foco para TV;
- `/tv/calendario?modo=semanal` - calendario em modo semanal;
- `/tv/calendario?modo=mensal` - calendario em modo mensal;
- `/novo-card` - cadastro de card;
- `/editar-card/{id}` - edicao de card.

## Ajuste de conceito

O sistema deixa de tratar a antiga rotina de auxiliares como parte central do fluxo.
O novo conceito principal passa a ser `Rotina dos Coordenadores`.

O antigo bloco de horario de professores tambem deixa de ser prioridade.
No MVP visual, ele foi substituido por `Substituicoes`, que e uma informacao mais util para a rotina da coordenacao.

## Proximas etapas de desenvolvimento

1. Criar entidade de usuarios e perfis de acesso.
2. Implementar login.
3. Criar painel da diretora.
4. Criar painel da coordenadora.
5. Criar tarefas/checklists individuais por coordenadora.
6. Criar solicitacoes de explicacao ou alteracao.
7. Criar historico de acoes.
8. Preparar futura autenticacao com Google.
9. Preparar futuro resumo semanal por e-mail para a diretora.

## Observacao

Nesta primeira entrega, a Semana em Foco e o Calendario Integrado ainda reaproveitam a entidade `Card`.
Isso permite validar rapidamente o visual e o fluxo do dashboard antes de criar entidades mais especificas para tarefas, calendario e usuarios.
