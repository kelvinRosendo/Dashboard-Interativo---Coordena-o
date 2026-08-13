# DEPLOY — Organiza+ (Dashboard Escolar)

Guia completo de implantação do sistema Organiza+ em produção.

---

## 1. Pré-requisitos

### Software

| Componente | Versão Mínima | Versão Testada |
|-----------|--------------|----------------|
| Java (JDK) | 17 | 17.0.x |
| PostgreSQL | 14 | 15.x |
| SO | Linux (recomendado) | Ubuntu 22.04 / Oracle Linux 9 |
| Maven | 3.8+ | 3.9.x |

### Hardware (VPS Oracle)

| Recurso | Mínimo | Recomendado |
|---------|--------|-------------|
| CPU | 1 core | 2 cores |
| RAM | 1 GB | 2 GB |
| Disco | 10 GB | 20 GB |
| Rede | 1 Gbps | 1 Gbps |

### Portas

| Porta | Serviço | Nota |
|-------|---------|------|
| 8081 | Aplicação Spring Boot | Pode ser alterada via `PORT` |
| 5432 | PostgreSQL | Interna, não expor diretamente |
| 443/80 | HTTPS/HTTP | Via reverse proxy (nginx/caddy) |

---

## 2. Configuração do PostgreSQL

### 2.1 Instalação

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# Oracle Linux / RHEL
sudo dnf install postgresql-server postgresql
sudo postgresql-setup --initdb
```

### 2.2 Criação do banco

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE dashboard_escolar;
CREATE USER dashboard_user WITH PASSWORD 'SUA_SENHA_FORTE';
ALTER ROLE dashboard_user SET client_encoding TO 'utf8';
ALTER ROLE dashboard_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE dashboard_user SET timezone TO 'America/Sao_Paulo';
GRANT ALL PRIVILEGES ON DATABASE dashboard_escolar TO dashboard_user;
\q
```

### 2.3 pg_hba.conf

Garantir que o PostgreSQL aceite conexões com senha:

```
# IPv4 local connections:
host    dashboard_escolar    dashboard_user    127.0.0.1/32    scram-sha-256
```

Reiniciar o PostgreSQL após alterações:

```bash
sudo systemctl restart postgresql
```

---

## 3. Variáveis de Ambiente

Criar o arquivo `/etc/organiza+/organiza.env` (ou usar systemd EnvironmentFile):

```env
# Banco de Dados
DATABASE_URL=jdbc:postgresql://localhost:5432/dashboard_escolar
DATABASE_USERNAME=dashboard_user
DATABASE_PASSWORD=SUA_SENHA_FORTE

# Google OAuth2 (obter no Google Cloud Console)
GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-seu-client-secret

# E-mails de administração (separados por vírgula)
ADMIN_EMAILS=admin@escola.com.br,vice@escola.com.br

# Porta da aplicação
PORT=8081

# Profile Spring
SPRING_PROFILES_ACTIVE=prod
```

### Segurança do arquivo .env

```bash
sudo mkdir -p /etc/organiza+
sudo touch /etc/organiza+/organiza.env
sudo chmod 600 /etc/organiza+/organiza.env
sudo chown organiza:organiza /etc/organiza+/organiza.env
```

**NUNCA** committar o arquivo `.env` no repositório. O `.gitignore` já exclui `.env` e `.env.*`.

---

## 4. Build

### 4.1 No ambiente de desenvolvimento

```bash
cd Dashboard-Interativo---Coordena-o
mvn clean package -DskipTests=false
```

### 4.2 Resultado esperado

```
[INFO] BUILD SUCCESS
[INFO] target/dashboard-escolar-0.0.1-SNAPSHOT.jar (~68 MB)
```

### 4.3 Verificações

- [ ] BUILD SUCCESS
- [ ] JAR gerado em `target/`
- [ ] Todos os testes passando (104 testes, 0 falhas)
- [ ] Nenhum erro de compilação

---

## 5. Transferência do JAR

### 5.1 Copiar para a VPS

```bash
scp target/dashboard-escolar-0.0.1-SNAPSHOT.jar \
  organiza@IP_DA_VPS:/opt/organiza+/app.jar
```

### 5.2 Alternativa via rsync

```bash
rsync -avz --progress \
  target/dashboard-escolar-0.0.1-SNAPSHOT.jar \
  organiza@IP_DA_VPS:/opt/organiza+/app.jar
```

### 5.3 Diretório na VPS

```bash
sudo mkdir -p /opt/organiza+
sudo mkdir -p /var/log/organiza+
sudo useradd -r -s /bin/false organiza
sudo chown -R organiza:organiza /opt/organiza+
sudo chown -R organiza:organiza /var/log/organiza+
```

---

## 6. Configuração do Serviço systemd

Criar o arquivo `/etc/systemd/system/organiza.service`:

```ini
[Unit]
Description=Organiza+ Dashboard Escolar
After=network.target postgresql.service
Requires=postgresql.service

[Service]
Type=simple
User=organiza
Group=organiza
WorkingDirectory=/opt/organiza+
EnvironmentFile=/etc/organiza+/organiza.env
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/organiza+/app.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=organiza

# Segurança
NoNewPrivileges=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/var/log/organiza+

[Install]
WantedBy=multi-user.target
```

### Ativar e iniciar

```bash
sudo systemctl daemon-reload
sudo systemctl enable organiza
sudo systemctl start organiza
```

---

## 7. Inicialização

### 7.1 Verificar status

```bash
sudo systemctl status organiza
```

### 7.2 Verificar logs iniciais

```bash
sudo journalctl -u organiza -f
```

### 7.3 Verificar que a aplicação subiu

```bash
curl -I http://localhost:8081/login
# Esperado: HTTP 200 ou 302
```

### 7.4 Flyway

As migrations (V1-V14) são executadas automaticamente na primeira inicialização. Verificar nos logs:

```
Successfully applied 14 migrations
```

---

## 8. Logs

### 8.1 Logs da aplicação

```bash
# Logs em tempo real
sudo journalctl -u organiza -f

# Logs das últimas 100 linhas
sudo journalctl -u organiza -n 100

# Logs de uma data específica
sudo journalctl -u organiza --since "2026-08-13"
```

### 8.2 Logs do PostgreSQL

```bash
sudo journalctl -u postgresql -f
```

### 8.3 Configuração de log

Em `application-prod.properties`, o logging está configurado para SAÍDA PADRÃO (journal). Para arquivos, adicionar:

```properties
logging.file.name=/var/log/organiza+/organiza.log
logging.file.max-size=10MB
logging.file.max-history=30
```

---

## 9. Health Check

### 9.1 Endpoint básico

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/login
# Esperado: 200
```

### 9.2 Verificação completa

```bash
# Login page
curl -s -o /dev/null -w "Login: %{http_code}\n" http://localhost:8081/login

# TV dashboard (público)
curl -s -o /dev/null -w "TV Semana: %{http_code}\n" http://localhost:8081/tv/semana

# Health check via systemd
sudo systemctl is-active organiza
```

### 9.3 Monitoramento contínuo (opcional)

Adicionar cron job para verificação:

```bash
# /etc/cron.d/organiza-healthcheck
*/5 * * * * organiza curl -sf http://localhost:8081/login > /dev/null || sudo systemctl restart organiza
```

---

## 10. Rollback

### 10.1 Rollback da aplicação

```bash
# Parar o serviço
sudo systemctl stop organiza

# Restaurar JAR anterior (se mantido como backup)
cp /opt/organiza+/app.jar.bak /opt/organiza+/app.jar

# Reiniciar
sudo systemctl start organiza
```

### 10.2 Rollback do banco (Flyway)

**ATENÇÃO:** O Flyway não suporta rollback automático. Em caso de problema com migrations:

```bash
# 1. Parar a aplicação
sudo systemctl stop organiza

# 2. Conectar ao banco
sudo -u postgres psql dashboard_escolar

# 3. Remover registro da migration problemática
DELETE FROM flyway_schema_history WHERE version = '14';

# 4. Restaurar backup do banco (se disponível)
# pg_restore -d dashboard_escolar backup.dump

# 5. Reiniciar (Flyway re-executará a migration)
sudo systemctl start organiza
```

### 10.3 Backup do banco

```bash
# Backup manual
sudo -u postgres pg_dump dashboard_escolar > /backup/organiza_$(date +%Y%m%d).sql

# Backup automático (cron diário)
# 0 2 * * * sudo -u postgres pg_dump dashboard_escolar | gzip > /backup/organiza_$(date +\%Y\%m\%d).sql.gz
```

---

## 11. Atualização Futura

### 11.1 Processo de atualização

```bash
# 1. Build no dev
mvn clean package -DskipTests=false

# 2. Backup do banco
sudo -u postgres pg_dump dashboard_escolar > /backup/pre_update_$(date +%Y%m%d).sql

# 3. Backup do JAR atual
cp /opt/organiza+/app.jar /opt/organiza+/app.jar.bak

# 4. Transferir novo JAR
scp target/dashboard-escolar-*.jar organiza@VPS:/opt/organiza+/app.jar

# 5. Reiniciar
sudo systemctl restart organiza

# 6. Verificar logs
sudo journalctl -u organiza -f
```

### 11.2 Novas migrations

O Flyway executa automaticamente novas migrations (V15, V16, etc.) na inicialização. Não é necessária intervenção manual.

### 11.3 Versionamento

- Cada versão deve ser taggeada no Git: `git tag -a v1.0.0 -m "Release 1.0.0"`
- Manter backup de cada versão do JAR em local seguro

---

## 12. Variáveis de Ambiente — Referência

| Variável | Obrigatória | Default | Descrição |
|----------|:-----------:|---------|-----------|
| `DATABASE_URL` | Sim | `jdbc:postgresql://localhost:5432/dashboard_escolar` | URL de conexão JDBC |
| `DATABASE_USERNAME` | Sim | `postgres` | Usuário do PostgreSQL |
| `DATABASE_PASSWORD` | Sim | `postgres` | Senha do PostgreSQL |
| `GOOGLE_CLIENT_ID` | Sim | — | Client ID do Google OAuth2 |
| `GOOGLE_CLIENT_SECRET` | Sim | — | Client Secret do Google OAuth2 |
| `ADMIN_EMAILS` | Sim | — | E-mails admin, separados por vírgula |
| `PORT` | Não | `8081` | Porta da aplicação |
| `SPRING_PROFILES_ACTIVE` | Não | `dev` | Profile ativo (`prod` para produção) |

---

## 13. Solução de Problemas

### Aplicação não inicia

```bash
# Verificar logs
sudo journalctl -u organiza -n 50

# Verificar se PostgreSQL está rodando
sudo systemctl status postgresql

# Testar conexão manual
psql -h localhost -U dashboard_user -d dashboard_escolar
```

### Erro de porta em uso

```bash
# Verificar o que usa a porta 8081
sudo lsof -i :8081

# Alterar porta via variável de ambiente
PORT=8082 sudo systemctl restart organiza
```

### Erro de OAuth2

```bash
# Verificar se as credenciais estão corretas
cat /etc/organiza+/organiza.env | grep GOOGLE

# Verificar no Google Cloud Console:
# - Redirect URI deve ser: https://SEU_DOMINIO/login/oauth2/code/google
# - Client ID e Secret devem estar corretos
```

### Banco de dados não conecta

```bash
# Verificar pg_hba.conf
sudo cat /etc/postgresql/15/main/pg_hba.conf | grep dashboard

# Reiniciar PostgreSQL
sudo systemctl restart postgresql
```

---

## 14. Checklist Pré-Deploy

- [ ] Java 17 instalado na VPS
- [ ] PostgreSQL 14+ instalado e rodando
- [ ] Banco `dashboard_escolar` criado
- [ ] Usuário do banco criado com permissões
- [ ] Arquivo `.env` criado em `/etc/organiza+/organiza.env`
- [ ] Credenciais Google OAuth2 configuradas
- [ ] `ADMIN_EMAILS` configurado
- [ ] JAR transferido para `/opt/organiza+/app.jar`
- [ ] Serviço systemd criado e habilitado
- [ ] Firewall configurado (porta 8081 ou reverse proxy)
- [ ] SSL/HTTPS configurado (recomendado)
- [ ] Backup do banco realizado
- [ ] Teste de health check passando
- [ ] Logs sendo capturados
