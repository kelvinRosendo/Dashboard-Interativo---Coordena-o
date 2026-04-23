# Nginx

Use o arquivo `dashboard-escolar.conf` como base para o servidor Nginx.

Fluxo sugerido no Windows Server:

1. Instale o Nginx na maquina.
2. Copie `dashboard-escolar.conf` para a pasta de configuracao do Nginx.
3. Inclua esse arquivo no `nginx.conf`, caso sua instalacao nao use `conf.d` automaticamente.
4. Rode a aplicacao Spring Boot localmente em `127.0.0.1:8081`.
5. Recarregue o Nginx para aplicar o proxy reverso.

Com isso, os usuarios acessam o sistema pelo IP do servidor na porta 80, enquanto a aplicacao continua exposta apenas localmente.

Nome amigavel sugerido na rede interna:

- `http://dashboard-coordenacao/`
- `http://dashboard-coordenacao.local/`

Observacao:

- Evite usar espacos e acentos no endereco do navegador. Por isso, `DashBoard Coordenacao` vira `dashboard-coordenacao`.
- Para esse nome funcionar em outras maquinas, crie uma entrada no DNS interno da empresa ou no arquivo `hosts` apontando para `192.168.1.20`.
