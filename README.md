# 📺 Dashboard Interativo para Coordenação Escolar

<p align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-yellow" />
  <img src="https://img.shields.io/badge/java-17+-blue" />
  <img src="https://img.shields.io/badge/spring%20boot-backend-green" />
  <img src="https://img.shields.io/badge/frontend-html%20css%20js-orange" />
</p>

---

## 🚧 Status do Projeto

> ⚠️ **Este projeto ainda está em desenvolvimento (v0.1)**
> Algumas funcionalidades estão em construção e podem sofrer alterações.

---

## 🎯 Sobre o Projeto

O **Dashboard Interativo para Coordenação Escolar** é uma aplicação web desenvolvida em **Java (Spring Boot)** com o objetivo de centralizar e exibir informações importantes da rotina escolar em um painel visual moderno e acessível.

O sistema foi pensado para ser exibido em **TVs ou monitores**, permitindo visualização rápida e intuitiva de dados essenciais como:

* 📅 Datas do trimestre
* 👩‍🏫 Horários de professores
* 🏫 Rotina administrativa
* 👥 Atividades de auxiliares

---

## 💡 Problema

Atualmente, informações importantes estão:

* espalhadas em diferentes lugares
* dependentes de comunicação verbal
* difíceis de acessar rapidamente

Isso gera:

❌ desorganização
❌ perda de tempo
❌ falhas na comunicação

---

## 🚀 Solução

O sistema propõe:

✔️ Visualização em **cards interativos**
✔️ Interface simples e otimizada para TV
✔️ Destaque de informações importantes
✔️ Organização por categorias

---

## 🧩 Funcionalidades (v0.1)

### 🟦 Dashboard Principal

* Exibição de cards organizados
* Layout responsivo para TV
* Informações centralizadas

### 🖱️ Modo Destaque (Focus Mode)

* Clique no card → exibição ampliada
* Melhor leitura à distância
* Navegação simples

### 🗂️ Gerenciamento Básico

* Cadastro manual de informações
* Estrutura simples de dados

---

## 🧠 Estrutura do Sistema

```text
Controller → recebe requisições
Service → regras de negócio
Repository → acesso a dados
Entity → modelo de dados
DTO → comunicação entre camadas
```

---

## 🏗️ Arquitetura

```text
src/main/java/br/com/escola/dashboard/
├── controller
├── service
├── repository
├── entity
├── dto
├── enums
├── config
└── exception
```

---

## 🗃️ Modelo de Dados

### 📌 Entidade: Card

```json
{
  "id": 1,
  "titulo": "Reunião Pedagógica",
  "conteudo": "Dia 25 às 14h",
  "categoria": "eventos",
  "prioridade": "alta",
  "dataAtualizacao": "2026-03-26"
}
```

---

## ⚙️ Tecnologias Utilizadas

### 🔙 Backend

* Java 17+
* Spring Boot
* Spring Web

### 🎨 Frontend

* HTML
* CSS
* JavaScript

### 🗄️ Banco (opcional)

* PostgreSQL / MySQL

---

## 📺 Interface (Conceito)

<p align="center">
  🧩 Cards organizados em grid  
  🖥️ Layout estilo dashboard moderno  
  🔍 Modo destaque para leitura ampliada  
</p>

---

## 🔮 Futuras Melhorias

### 🔥 Alto impacto

* Auto refresh em tempo real
* Rotação automática de cards
* Modo slideshow

### ⚡ Interação

* Filtros por categoria
* Pesquisa rápida
* Destaque automático

### 🧠 Inteligência

* IA para organização de dados
* IA para resumo de informações

### 📊 Gestão

* Painel administrativo
* Histórico de alterações

---

## 🧪 MVP (v0.1)

✔️ Dashboard funcional
✔️ Cards interativos
✔️ Modo destaque
✔️ Dados mockados

---

## 📌 Objetivo Final

Criar uma solução simples, eficiente e visual para melhorar a organização e o acesso à informação no ambiente escolar.

---

## 👨‍💻 Autor

Desenvolvido por **Kelvin** 🚀

---

<p align="center">
  <b>feito com café, estresse e muita vontade de fazer funcionar ☕💀</b>
</p>
