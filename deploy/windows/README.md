# Deploy no Windows Server

Use o script `start-dashboard-server.ps1` para subir o dashboard no servidor da escola.

## Cenario recomendado

- servidor da aplicacao: `192.168.1.20`
- PostgreSQL 18 rodando no mesmo servidor na porta `5433`
- dashboard respondendo em `http://192.168.1.20:8081`

## Ordem de implantacao

1. Instale Java 17+ no servidor.
2. Instale e configure o PostgreSQL no servidor.
3. Crie o banco `dashboard_escolar`.
4. Confirme que o usuario e senha do PostgreSQL estao funcionando localmente no servidor.
5. Gere o `.jar` do projeto.
6. Suba o dashboard liberando a porta no firewall.

## Subida pelo jar

No PowerShell do servidor, dentro da pasta do projeto:

```powershell
.\deploy\windows\start-dashboard-server.ps1 `
  -DbPassword "SUA_SENHA_DO_POSTGRES" `
  -Build `
  -OpenFirewall
```

O script ja sobe com:

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL=jdbc:postgresql://localhost:5433/dashboard_escolar`
- `SERVER_ADDRESS=0.0.0.0`
- `SERVER_PORT=8081`

## Subida pelo Maven

Se preferir rodar direto do codigo-fonte:

```powershell
.\deploy\windows\start-dashboard-server.ps1 `
  -DbPassword "SUA_SENHA_DO_POSTGRES" `
  -Mode maven `
  -OpenFirewall
```

## Validacao

Quando a aplicacao subir corretamente, acesse:

```text
http://192.168.1.20:8081
```

Se o navegador nao abrir em outra maquina da rede, confira:

- se o log terminou com `Started DashboardEscolarApplication`
- se a porta `8081` foi liberada no firewall
- se o PostgreSQL aceitou a autenticacao
- se outra maquina da rede consegue pingar `192.168.1.20`
