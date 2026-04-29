# Guia Operacional do Dashboard

## Objetivo

Este guia concentra os criterios operacionais de uso do dashboard fora da interface principal. A tela passa a focar em leitura rapida, enquanto as definicoes de prioridade e status ficam documentadas aqui.

## Como ler a tela

- `status` aparece como badge e indica em que etapa o item esta
- `prioridade` aparece como apoio visual por cor e faixa lateral
- `responsavel`, `data` e `proxima acao` ajudam a decidir o que fazer em seguida

## Status operacionais

### PENDENTE

Use quando o item ainda nao foi iniciado ou depende de decisao, alinhamento ou confirmacao.

Exemplos:

- cobertura de aula ainda nao confirmada
- reuniao planejada, mas sem execucao iniciada
- tarefa administrativa aguardando validacao

### EM_ANDAMENTO

Use quando o item ja esta sendo tratado, acompanhado ou organizado pela equipe.

Exemplos:

- ajuste de horario em articulacao com professor
- conferencia de frequencia em andamento
- preparacao de evento com tarefas distribuidas

### CONCLUIDO

Use quando a entrega foi finalizada e o registro nao exige nova acao imediata.

Exemplos:

- substituicao confirmada e executada
- material revisado e encaminhado
- atendimento encerrado e registrado

## Prioridades

### ALTA

Use para itens com impacto imediato na operacao do dia ou com risco claro se nao forem tratados logo.

Sinais comuns:

- afeta aula, fluxo de alunos ou responsavel-chave
- exige acao no mesmo turno
- depende de retorno rapido da coordenacao

### MEDIA

Use para itens importantes, mas que permitem acompanhamento e organizacao antes da execucao.

Sinais comuns:

- precisa entrar no radar do dia
- possui prazo proximo
- exige articulacao, mas sem urgencia critica

### BAIXA

Use para rotina, apoio ou registros sem urgencia imediata.

Sinais comuns:

- nao bloqueia a operacao atual
- pode ser resolvido apos itens mais sensiveis
- funciona melhor como acompanhamento

## Quem atualiza

- quem cria o card deve preencher o contexto minimo com clareza
- quem assume a execucao deve atualizar o status quando houver mudanca real
- a coordenacao pode revisar prioridade quando o impacto operacional mudar

## Boas praticas de preenchimento

- use titulos curtos e objetivos
- descreva o contexto em uma frase
- preencha `responsavel` sempre que houver dono claro
- use `observacoes` como `proxima acao`, nao como historico longo
- evite repetir no texto aquilo que a cor ou o status ja comunicam

## Exemplos praticos

### Horario de professor

- titulo: `Substituicao 1oA`
- status: `PENDENTE`
- prioridade: `ALTA`
- proxima acao: `Confirmar material com a coordenacao`

### Evento

- titulo: `Conselho de classe`
- status: `EM_ANDAMENTO`
- prioridade: `MEDIA`
- proxima acao: `Fechar pauta e confirmar lista de participantes`

### Rotina administrativa

- titulo: `Conferir frequencia diaria`
- status: `EM_ANDAMENTO`
- prioridade: `ALTA`
- proxima acao: `Finalizar validacao ate 11h`
