# CONTEXT.md — SmartApi (Java 25 + Spring Boot 4 + Spring AI)

> **Propósito:** Fonte única de verdade (AI-First) para agentes de IA e desenvolvedores entenderem rapidamente a arquitetura, padrões de código, fluxos existentes, armadilhas conhecidas e regras de contribuição do **SmartApi**.

---

## 1. Visão Geral do Projeto

O **SmartApi** é o assistente financeiro inteligente do ecossistema, construído em **Java 25**, **Spring Boot 4.0.6** e **Spring AI 2.0.0-M8** (com **Google Gemini / GenAI**). Ele permite que usuários registrem e operem transações financeiras por comandos de **voz ou texto**, tanto via **Bot do Telegram (Long Polling)** quanto via **API REST**.

### Integrações no Ecossistema
- **`Auth.Api` (.NET 10):** Autenticação de usuários (`POST api/auth/login`) e renovação de sessão (`POST api/auth/refresh-token`) para o sistema `"fin"`, consumida via OpenFeign (`IAuthClient`).
- **`Fin-API` (.NET 10):** Consulta de categorias do usuário (`GET api/categories`) e criação de transações financeiras (`POST api/transacoes/novo`), consumida via OpenFeign (`IFinanceClient`).
- **Redis (`TokenStore`):** Cache de sessão indexado pelo `chatId` do Telegram (`token:<chatId>` com TTL de 15 minutos e `refreshToken:<chatId>` com TTL de 8 dias).
- **Telegram Bots API (`telegrambots-springboot-longpolling-starter` v10.0.0):** Canal conversacional que recebe mensagens de texto e voz (`.ogg`/`.m4a`), gerencia vínculo de conta (`login:senha`) e responde ao usuário.
- **Google Gemini (`spring-ai-starter-model-google-genai`) + Script Python (`AudioTranscriber`):** Transcrição de áudio e processamento de linguagem natural com **Tool Calling** (`@Tool`).

---

## 2. Mapa da Arquitetura (`src/main/`)

O projeto segue uma arquitetura em camadas inspirada em Clean Architecture / DDD (`application`, `domain`, `infraestructure`):

```text
src/main/
├── java/com/jovanne/smartApi/
│   ├── SmartApiApplication.java              # Entry point (@SpringBootApplication + @EnableFeignClients)
│   ├── application/
│   │   ├── dtos/                             # CategoryDTO, LoginDTO, TransactionAiDTO, UserDataDTO
│   │   ├── services/
│   │   │   ├── AudioTranscriber.java         # Implementa TranscriptionModel executando script Python externo
│   │   │   ├── AuthService.java              # Login, refresh token e vínculo de chatId no Redis (@Tool)
│   │   │   └── TransactionService.java       # Tools de IA (@Tool register-transactions) + retry com refresh
│   │   └── tool/
│   │       ├── ToolResult.java               # Record (statusCode, success, message, errors)
│   │       └── ToolResultHolder.java         # ThreadLocal<ToolResult> para capturar resultado da execução da tool
│   ├── domain/
│   │   ├── entities/                         # Category, Claim, RecorrenciaType, RennovationResult, Token, TransacaoType, Transaction, UserToken
│   │   ├── exceptions/apiExceptions/         # Hierarquia ApiClientException (400, 401, 403, 404, 500)
│   │   └── interfaces/                       # IAuthService, ITokenService, ITransactionService
│   └── infraestructure/                      # (Nota: grafia do pacote é 'infraestructure')
│       ├── config/                           # ApiClientConfig, ApiErroDecoder, BaseErrorDecoder, ChatClientConfig, FinanceClientConfig, SwaggerConfig, TelegramConfig
│       ├── data/TokenStore.java              # Persistência de JWT e RefreshToken no Redis por chatId
│       ├── http/
│       │   ├── clients/                      # OpenFeign: IAuthClient e IFinanceClient
│       │   ├── controllers/                  # TransactionController (/transactions/ai e /transactions/login)
│       │   ├── handlers/                     # GlobalExceptionHandler (@RestControllerAdvice)
│       │   ├── request/                      # LoginRequest, RefreshTokenRequest, TransactionRequest
│       │   └── response/
│       │       ├── external/                 # Envelopes da Auth.Api e Fin-API (ExternalApiResponse<T>, ApiErrorResponse, etc.)
│       │       └── internal/                 # Envelopes próprios (InternalApiResponse<T>, InternalAiApiResponse, ErrorResponse)
│       └── telegram/
│           ├── SmartApiBot.java              # LongPollingUpdateConsumer
│           ├── TelegramContext.java          # ThreadLocal<Long> para propagar o chatId até o interceptor do Feign
│           └── TelegramMessageHandler.java   # Orquestrador de primeiro acesso, refresh, voz, texto e ChatClient
└── resources/
    ├── application.yml                       # Configurações Spring, Redis, GenAI, URLs externas e Telegram
    └── prompts/
        ├── system.st                         # System Prompt principal injetado no ChatClient ({currentDateTime}, {categories})
        └── backup.st                         # Prompt alternativo/histórico
```

---

## 3. Padrões Arquiteturais Implementados (A Serem Seguidos)

Ao evoluir ou dar manutenção no **SmartApi**, siga rigorosamente os padrões já estabelecidos no código:

1. **Tool Calling com Captura de Estado via `ThreadLocal` (`ToolResultHolder`):**
   - Métodos expostos ao LLM são anotados com `@Tool(name = "...", description = "...")` nos serviços (`TransactionService`, `AuthService`).
   - Como o `ChatClient` do Spring AI retorna o texto final gerado pelo modelo (`String`), o resultado estruturado da chamada à API externa (status HTTP, sucesso/falha, mensagens de erro da `Fin-API`) é armazenado no `ToolResultHolder` (`ThreadLocal<ToolResult>`) durante a execução da tool.
   - Quem invoca o `ChatClient` (`TransactionController` ou `TelegramMessageHandler`) lê `holder.get()` após `.call().content()` para decidir o status HTTP ou a mensagem de erro enviada ao usuário, e **sempre** limpa o `ThreadLocal` no bloco `finally` (`holderResult.clear()`).
2. **Propagação de Contexto de Sessão via `TelegramContext` + `FinanceClientConfig`:**
   - O `chatId` (`Long`) identifica a sessão do usuário tanto no Telegram quanto nos endpoints REST.
   - `TelegramContext` armazena o `chatId` em um `ThreadLocal<Long>`.
   - O interceptor do OpenFeign (`FinanceClientConfig.financeInterceptor()`) lê `telegramContext.getChatId()`, busca o JWT no Redis (`tokenStore.getToken(chatId)`) e injeta o header `Authorization: Bearer <token>` automaticamente em todas as chamadas do `IFinanceClient`.
   - **Sempre** limpe `telegramContext.clear()` em um bloco `finally` ao encerrar o processamento da requisição/mensagem.
3. **Renovação Transparente de Token (`executeWithRetry` + `RennovationResult`):**
   - O OpenFeign está configurado com `Retryer.NEVER_RETRY` e um `ApiErroDecoder` customizado que deserializa `{ "success": false, "errors": [...] }` das APIs .NET e lança `ApiUnauthorizedException` no HTTP `401`.
   - Chamadas autenticadas em `TransactionService` usam `executeWithRetry(chatId, () -> ...)`: se ocorrer `ApiUnauthorizedException`, o serviço chama `authService.refreshToken(chatId)` (`POST api/auth/refresh-token` com `system = "fin"`), salva o novo JWT no Redis e reexecuta a ação uma única vez.
4. **Tradução de Contratos entre IA e `Fin-API` (`TransactionAiDTO` -> `TransactionRequest`):**
   - O modelo Gemini preenche `TransactionAiDTO` com nomes semânticos em português alinhados ao prompt (`type`, `titulo`, `valor`, `categoriaId`, `dataMovimentacao`, `isRecurring`, `parcelas`, `parcelaAtual`).
   - Antes de enviar via Feign para `POST api/transacoes/novo`, `TransactionRequest.fromAi(dtoAi)` converte para o contrato esperado pela `Fin-API` (`type` serializado como número via `@JsonFormat(shape = JsonFormat.Shape.NUMBER)`, `description`, `amount`, `categoryId`, `transactionDate`, `isRecurring`, `parcelas`, `parcelaAtual`).
5. **Injeção Dinâmica de Contexto no System Prompt (`system.st`):**
   - Nunca deixe o LLM adivinhar IDs de categorias ou a data de hoje. Antes de cada chamada ao `chatClient.prompt()`, busque as categorias reais do usuário (`transactionService.listCategories()`), formate como `"id - nome"` (`CategoryDTO.formatSystemPrompt`) e injete os parâmetros `currentDateTime` (ISO DateTime) e `categories` no template `classpath:/prompts/system.st`.

---

## 4. Variáveis de Ambiente e Configuração (`application.yml`)

| Variável / Propriedade | Descrição | Onde é usada |
| :--- | :--- | :--- |
| `GENAI_KEY` | Chave de API do Google Gemini (`spring.ai.google.genai.api-key`) | `ChatClient` (Spring AI) |
| `FINANCE_URL` | Base URL da `Fin-API` (`api.financeira.url`) | `IFinanceClient` |
| `AUTH_URL` | Base URL da `Auth.Api` (`api.auth.url`) | `IAuthClient` |
| `TELEGRAM_BOT_TOKEN` | Token do Bot no BotFather (`telegram.bot.token`) | `TelegramConfig` e `TelegramMessageHandler` |
| `TELEGRAM_BOT_USERNAME` | Username do Bot (`telegram.bot.username`) | `application.yml` |
| `api.scriptPath` | Caminho do script Python de transcrição (padrão local: `C:/dev/backend/python/transcription/transcribe.py`) | `AudioTranscriber` |
| `spring.data.redis.*` | Host (`localhost`) e porta (`6379`) do Redis | `TokenStore` |

---

## 5. Mapa Resumido dos Fluxos Existentes

> Detalhamento completo com diagramas de sequência em **[docs/flows.md](docs/flows.md)**.

1. **Jornada via Bot do Telegram (`SmartApiBot` -> `TelegramMessageHandler`):**
   - Verifica se o `chatId` possui sessão ativa (`authService.hasVinculedUser(chatId)`).
   - **Primeiro acesso:** solicita credenciais no formato `email:senha`, autentica na `Auth.Api` (`system: "fin"`) e salva JWT (15 min) + RefreshToken (8 dias) no Redis.
   - **Renovação preventiva:** se o JWT expirou no Redis, tenta `authService.refreshToken(chatId)`. Se o refresh falhar (`LOGIN_REQUIRED`), pede novo login.
   - **Mensagem de Voz:** baixa a URL do arquivo de voz na API do Telegram (`GetFile`), transcreve via `TranscriptionModel.transcribe` (`AudioTranscriber`) e encaminha o texto transcrito para `handleText`.
   - **Mensagem de Texto:** carrega categorias do usuário na `Fin-API`, chama o Gemini (`ChatClient`) com as tools do `TransactionService`, verifica `ToolResultHolder` e responde ao usuário no Telegram.
2. **Jornada via API REST (`TransactionController`):**
   - `POST /transactions/login`: autentica um `LoginDTO` (`chatId`, `email`, `password`, `system`) na `Auth.Api`, salva os tokens no Redis e retorna `{ "success": true, "data": { "token": "<jwt>" } }`.
   - `POST /transactions/ai` (`multipart/form-data` com `file` e `chatId`): transcreve o áudio enviado, executa o `ChatClient` com as categorias disponíveis e retorna `201 Created` (`InternalAiApiResponse`) ou o status de erro capturado no `ToolResultHolder` (`ErrorResponse`).

---

## 6. Pontos de Atenção e Armadilhas Conhecidas (Tech Debt)

Ao alterar código ou investigar bugs no **SmartApi**, leve em conta estes detalhes reais da implementação atual:

- **`chatId = 1L` hardcoded em `TransactionService.listCategories()`:**
  - Em `TransactionService.java` (linhas 80-81), `listCategories()` define `var chatId = 1L; telegramContext.setChatId(chatId);` antes de chamar `financeClient::getCategories`.
  - **Impacto:**
    1. Se o Redis não tiver um token válido salvo especificamente para a chave `token:1` (ou `refreshToken:1`), `listCategories()` falhará com `401 Unauthorized` mesmo que o usuário real no Telegram tenha outro `chatId`!
    2. Além disso, `telegramContext.setChatId(1L)` sobrescreve o `ThreadLocal` da thread atual com `1L`, fazendo com que a chamada subsequente de `registerTransaction` na mesma thread também utilize o `chatId = 1L`!
  - **Como corrigir quando solicitado:** Em `TransactionController.createTransaction`, deve-se chamar `telegramContext.setChatId(chatId)` no início (e `clear()` no `finally`), e em `TransactionService.listCategories()` deve-se ler `var chatId = telegramContext.getChatId();` em vez de fixar `1L`.
- **`hasVinculedUser` verifica apenas o Access Token (`hasToken`):**
  - Em `AuthService.hasVinculedUser(Long chatId)`, a implementação retorna `tokenStore.hasToken(chatId)` (que expira em 15 minutos no Redis), em vez de verificar também se existe um `refreshToken:<chatId>` (que dura 8 dias). Por isso, após 15 minutos sem uso no Telegram, `hasVinculedUser` retorna `false` e cai em `handleFirstAccess` antes de alcançar o bloco de `refreshToken` na linha 76 do `TelegramMessageHandler`.
- **Contrato de Resposta do Refresh Token (`AuthService.refreshToken`):**
  - `Auth.Api` retorna `{ "success": true, "data": { "token": { "accessToken": "...", "refreshToken": "...", ... } } }` tanto no `/login` quanto no `/refresh-token`.
  - Enquanto `LoginResponse` mapeia `Token token` (objeto aninhado), `RefreshTokenResponse` mapeia `String token`. Se `Auth.Api` retornar o objeto `LoginResponseViewModel` dentro de `data.token`, a deserialização de `RefreshTokenResponse` no Feign pode falhar ou exigir o mesmo modelo de `LoginResponse`, além de não rotacionar o novo `refreshToken` no Redis.
- **Dependência de Python Local no `AudioTranscriber`:**
  - `AudioTranscriber` implementa `TranscriptionModel` executando um processo externo `python C:/dev/backend/python/transcription/transcribe.py <tempFile.m4a>`.
  - No `Dockerfile` de produção (Linux), apenas `redis-server` é instalado na imagem `eclipse-temurin:25-jdk`; o caminho Windows `C:/dev/...` e o binário `python` não existem dentro do container caso o endpoint de áudio/voz seja acionado sem sobrescrever `api.scriptPath` ou migrar para transcrição nativa via API.
- **Método `list-transactions-by-period` ainda não implementado:**
  - `TransactionService.listTransactionsByPeriod()` está anotado com `@Tool(name = "list-transactions-by-period")`, mas lança `UnsupportedOperationException("Método não implementado")`. Se o usuário pedir ao bot para "listar gastos da semana", o Gemini invocará essa tool e causará erro.
- **Redis Embutido no Container vs Docker Compose:**
  - Em desenvolvimento local (`compose.yml`), o Redis sobe na porta `6379` com `--requirepass minhaSenha` (mas `spring.data.redis.password` está comentado no `application.yml`). No `Dockerfile` (`entrypoint.sh`), um `redis-server` local sem senha sobe dentro do próprio container em `127.0.0.1:6379` antes do `java -jar app.jar`.

---

## 7. Comandos Úteis

```powershell
# Compilar o projeto (sem rodar testes de integração que exigem Redis/OpenAI/Gemini ativos)
.\mvnw.cmd clean compile

# Gerar pacote JAR de produção
.\mvnw.cmd clean package -DskipTests

# Executar a aplicação localmente (requer Redis ativo e variáveis GENAI_KEY, FINANCE_URL, AUTH_URL, TELEGRAM_BOT_TOKEN)
.\mvnw.cmd spring-boot:run

# Subir Redis via Docker Compose
docker compose up -d

# Build da imagem Docker (inclui Redis + Spring Boot via entrypoint.sh na porta 8080)
docker build -t smart-api .
```

---

## 8. Fluxo Git, Branches, Commits e PRs

Este repositório segue a padronização de branches intermediárias (`preview`), Conventional Commits e Pull Requests estruturados do ecossistema. Consulte o guia completo em **[docs/git-workflow.md](docs/git-workflow.md)** e o template de PR em **[.github/pull_request_template.md](.github/pull_request_template.md)**.
