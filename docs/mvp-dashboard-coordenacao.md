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
- login com OAuth 2.0 / Google;
- painel administrativo limpo em `/admin`;
- migracao das categorias antigas `ROTINA_AUXILIAR` e `HORARIO_PROFESSOR`;
- dados iniciais para Semana em Foco e Rotina dos Coordenadores.

## Rotas principais

- `/` - redireciona para `/tv/semana`;
- `/login` - tela de acesso com Google;
- `/admin` - painel administrativo interno;
- `/tv/semana` - tela da Semana em Foco para TV;
- `/tv/calendario?modo=semanal` - calendario em modo semanal;
- `/tv/calendario?modo=mensal` - calendario em modo mensal;
- `/novo-card` - cadastro de card;
- `/editar-card/{id}` - edicao de card.

## Limpeza da tela antiga

A tela antiga de cards foi removida do fluxo para evitar duplicidade visual e manter o projeto apontando para o MVP novo.
Os servicos e a entidade `Card` continuam existindo porque ainda alimentam a Semana em Foco e o Calendario Integrado.

## Ajuste de conceito

O sistema deixa de tratar a antiga rotina de auxiliares como parte central do fluxo.
O novo conceito principal passa a ser `Rotina dos Coordenadores`.

O antigo bloco de horario de professores tambem deixa de ser prioridade.
No MVP visual, ele foi substituido por `Substituicoes`, que e uma informacao mais util para a rotina da coordenacao.

## Proximas etapas de desenvolvimento

1. Criar entidade de usuarios e perfis de acesso.
2. Criar painel da diretora.
3. Criar painel da coordenadora.
4. Criar tarefas/checklists individuais por coordenadora.
5. Criar solicitacoes de explicacao ou alteracao.
6. Criar historico de acoes.
7. Preparar futuro resumo semanal por e-mail para a diretora.

## Observacao

Nesta primeira entrega, a Semana em Foco e o Calendario Integrado ainda reaproveitam a entidade `Card`.
Isso permite validar rapidamente o visual e o fluxo do dashboard antes de criar entidades mais especificas para tarefas, calendario e usuarios.
