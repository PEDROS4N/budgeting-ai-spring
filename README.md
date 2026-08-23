# Desafio de Projeto — Spring AI (Budgeting API)

Evolução do projeto final do módulo Spring AI da trilha DIO Spring Boot
(base: https://github.com/digitalinnovationone/dio-spring-boot-learning-track/tree/main/05-spring-ai).

## O que o projeto faz

API de orçamento que recebe comandos de voz, transcreve o áudio, usa um
modelo de linguagem para entender a intenção (registrar ou consultar
transação) e responde por voz. Mantém a arquitetura em camadas
(domain / application / infrastructure) para que a mesma regra de negócio
sirva tanto o fluxo REST quanto o fluxo de IA (Tool Calling).

## Como executar a aplicação

**Mac/Linux/Git Bash:**
```bash
export OPENAI_API_KEY="sua_chave_aqui"
./gradlew bootRun
```

**Windows (cmd):**
```
set OPENAI_API_KEY=sua_chave_aqui
gradlew bootRun
```

**Windows (PowerShell):**
```
$env:OPENAI_API_KEY="sua_chave_aqui"
.\gradlew bootRun
```

A API sobe em `http://localhost:8080`. Banco H2 em memória, sem setup extra.

## Melhoria implementada

Duas evoluções pequenas e conectadas:

1. **Novo tipo de consulta financeira**: `QueryBalanceByCategoryUseCase`,
   que soma receitas e subtrai despesas de uma categoria (ex: "quanto eu
   gastei em alimentação?"). Exposta via REST
   (`GET /api/transactions/category/{categoria}/balance`) e via Tool
   Calling (`consultarSaldoPorCategoria`), usando o mesmo use case nos
   dois casos.
2. **Validações antes de salvar uma transação**: centralizadas no
   construtor de `Transaction` (domain), garantindo descrição, categoria e
   valor válidos e data não futura — independente de a transação vir do
   REST ou de um comando de voz.

## Tecnologias usadas

- Java 17, Spring Boot 3
- Spring AI (ChatClient, Tool Calling, TranscriptionModel, TextToSpeechModel)
- Spring Data JPA + H2
- JUnit 5

## Como testar o fluxo principal

Via REST (sem depender de áudio):

**Mac/Linux/Git Bash:**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"description":"Mercado","amount":150.00,"type":"EXPENSE","category":"alimentacao","date":"2026-08-20"}'

curl http://localhost:8080/api/transactions/category/alimentacao/balance
```

**Windows (cmd):**
```
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d "{\"description\":\"Mercado\",\"amount\":150.00,\"type\":\"EXPENSE\",\"category\":\"alimentacao\",\"date\":\"2026-08-20\"}"

curl http://localhost:8080/api/transactions/category/alimentacao/balance
```

(Categorias usadas nos exemplos sem acento para evitar problemas de encoding no terminal do Windows; a API aceita acentos normalmente.)

Via voz (fluxo completo IA) — requer um arquivo de áudio real na pasta atual, ex: grave um `comando.mp3` dizendo algo como "gastei 50 reais com transporte":

```bash
curl -X POST http://localhost:8080/api/voice-commands \
  -F "audio=@comando.mp3" --output resposta.mp3
```

Testes automatizados do use case principal da melhoria:

**Mac/Linux/Git Bash:** `./gradlew test`
**Windows (cmd):** `gradlew test`
**Windows (PowerShell):** `.\gradlew test`

## O que foi aprendido

- Como manter o Tool Calling fino: cada `@Tool` só traduz a intenção do
  modelo para um use case já existente, sem duplicar regra de negócio
  entre REST e IA.
- Onde colocar validação de domínio para que ela valha para qualquer
  ponto de entrada (REST ou comando de voz), evitando duas fontes de
  verdade.
- Como o fluxo áudio → texto → intenção → ação → texto → áudio se
  encaixa nos boundaries de Clean Architecture sem vazar detalhes de
  infraestrutura para o domínio.