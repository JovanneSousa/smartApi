# Mapeamento de Fluxos e Arquitetura — SmartApi (`docs/flows.md`)

Este documento detalha os fluxos reais implementados no **SmartApi**, cobrindo o Bot do Telegram, os endpoints REST, a integração com o modelo Google Gemini via Spring AI (Tool Calling), o gerenciamento de sessão no Redis e a comunicação via OpenFeign com `Auth.Api` e `Fin-API`.

---

## 1. Visão Arquitetural

```mermaid
flowchart LR
    TG["Usuário no Telegram (Texto / Voz)"] <-->|Long Polling| Bot["SmartApiBot / TelegramMessageHandler"]
    REST["Cliente HTTP (POST /transactions/*)"] <--> Ctrl["TransactionController"]
    Bot --> Transcriber["AudioTranscriber (Script Python)"]
    Ctrl --> Transcriber
    Bot --> AI["Spring AI ChatClient (Google Gemini)"]
    Ctrl --> AI
    AI -->|"@Tool register-transactions"| Svc["TransactionService"]
    Bot --> AuthSvc["AuthService"]
    Ctrl --> AuthSvc
    Svc --> AuthSvc
    AuthSvc <--> Redis[(Redis: TokenStore)]
    Svc <-->|Feign + FinanceClientConfig| Fin["Fin-API (.NET 10)"]
    AuthSvc <-->|Feign: IAuthClient| Auth["Auth.Api (.NET 10)"]
    Fin -.->|Lê JWT via TelegramContext| Redis
```

---

## 2. Fluxo Conversacional via Bot do Telegram

**Entry point:** `SmartApiBot.consume(List<Update>)` -> `TelegramMessageHandler.handle(Message)`

```mermaid
sequenceDiagram
    participant U as Usuário (Telegram)
    participant Bot as SmartApiBot / TelegramMessageHandler
    participant Ctx as TelegramContext & ToolResultHolder
    participant Auth as AuthService / TokenStore (Redis)
    participant Py as AudioTranscriber
    participant AI as ChatClient (Gemini)
    participant Svc as TransactionService (IFinanceClient)

    U->>Bot: Envia mensagem (Texto ou Voz)
    Bot->>Ctx: telegramContext.setChatId(chatId) & holderResult.clear()
    Bot->>Auth: hasVinculedUser(chatId)
    alt Usuário não vinculado (Primeiro Acesso)
        alt Mensagem contém "email:senha"
            Bot->>Auth: executeLogin(LoginDTO(chatId, email, senha, "fin"))
            Auth-->>Bot: Salva token (15m) e refreshToken (8d) no Redis
            Bot-->>U: "Login realizado! Pode começar a usar."
        else Formato inválido ou saudação inicial
            Bot-->>U: "Bem vindo! Envie seu login no formato: login:senha"
        end
    else Usuário vinculado
        alt Token expirado (!tokenStore.hasToken(chatId))
            Bot->>Auth: refreshToken(chatId)
            alt Refresh falhou (LOGIN_REQUIRED)
                Bot-->>U: "Sua sessão expirou. Envie seu login:"
            end
        end
        opt Mensagem é áudio (message.hasVoice())
            Bot->>Bot: GetFile na API do Telegram -> UrlResource
            Bot->>Py: transcriptionModel.transcribe(resource)
            Py-->>Bot: Texto transcrito
        end
        Bot->>Svc: listCategories() -> GET /api/categories (Fin-API)
        Svc-->>Bot: Lista de CategoryDTO formatada ("id - name")
        Bot->>AI: prompt().system(currentDateTime, categories).user(texto).call()
        AI->>Svc: Tool Call: registerTransaction(TransactionAiDTO)
        Svc->>Svc: POST /api/transacoes/novo (com retry 401 -> refreshToken)
        Svc->>Ctx: holderResult.set(ToolResult)
        Svc-->>AI: Retorna ToolResult para o modelo
        AI-->>Bot: Resposta em linguagem natural (result)
        alt ToolResult.success == false
            Bot-->>U: "Erro <statusCode>: <result>"
        else Sucesso
            Bot-->>U: <result>
        end
    end
    Bot->>Ctx: finally: telegramContext.clear() & holderResult.clear()
```

---

## 3. Fluxo via API REST (`TransactionController`)

### 3.1 Login e Vínculo de Sessão (`POST /transactions/login`)
1. Recebe `LoginDTO` (`{ chatId, email, password, system }`).
2. `AuthService.executeLogin(login)` chama `IAuthClient.executeLogin` (`POST api/auth/login` na `Auth.Api`).
3. Extrai `accessToken` e `refreshToken` do envelope `ExternalApiResponse<LoginResponse>`.
4. Salva no Redis via `TokenStore`:
   - `token:<chatId>` (TTL: 15 minutos)
   - `refreshToken:<chatId>` (TTL: 8 dias)
5. Retorna `200 OK` com `InternalApiResponse`:
   ```json
   {
     "success": true,
     "data": {
       "token": "<jwt>"
     }
   }
   ```

### 3.2 Registro de Transação por Áudio com IA (`POST /transactions/ai`)
1. Recebe `multipart/form-data` com `file` (`MultipartFile`) e `chatId` (`@RequestParam Long`).
2. Consulta categorias disponíveis na `Fin-API` via `transactionService.listCategories()`.
3. Converte o arquivo de áudio em texto via `transcriptionModel.transcribe(file.getResource())` (`AudioTranscriber`).
4. Invoca o `ChatClient` injetando `currentDateTime` e `categories` no template `classpath:/prompts/system.st`.
5. O modelo Google Gemini extrai os dados da fala seguindo as regras do prompt (datas relativas como "ontem", "hoje", categoria mais adequada, parcelamento/recorrência) e aciona a tool `register-transactions`.
6. Verifica o `ToolResultHolder`:
   - **Se `!holder.get().success()`:** retorna o `statusCode` original da falha com `ErrorResponse` (`{ statusCode, message, errors }`).
   - **Se `holder.get().success()`:** retorna `201 Created` (`Location: /transactions`) com `InternalAiApiResponse`:
     ```json
     {
       "success": true,
       "aiResponse": "Resposta amigável gerada pelo Gemini",
       "data": "Transação registrada com sucesso! ID: <id>"
     }
     ```

---

## 4. Fluxo de Autenticação, Propagação de Token e Retry (`OpenFeign`)

```mermaid
flowchart TD
    A["Chamada a método do IFinanceClient"] --> B["FinanceClientConfig (RequestInterceptor)"]
    B --> C["Lê chatId = TelegramContext.getChatId()"]
    C --> D["Busca JWT = TokenStore.getToken(chatId) no Redis"]
    D --> E["Adiciona Header Authorization: Bearer <JWT>"]
    E --> F["Executa requisição HTTP para a Fin-API"]
    F --> G{"Status HTTP?"}
    G -- "200 OK" --> H["Deserializa ExternalApiResponse<T>"]
    G -- "401 Unauthorized" --> I["ApiErroDecoder lança ApiUnauthorizedException"]
    I --> J["TransactionService.executeWithRetry captura 401"]
    J --> K["AuthService.refreshToken(chatId) -> POST api/auth/refresh-token"]
    K --> L{"Refresh OK?"}
    L -- Sim --> M["Salva novo JWT no Redis e reexecuta action.get()"]
    L -- Não --> N["Propaga ApiUnauthorizedException / ToolResult de erro"]
    G -- "400 / 403 / 404 / 500" --> O["ApiErroDecoder extrai ApiErrorResponse e lança exceção tipada"]
```

---

## 5. Regras de Extração da IA (`prompts/system.st`)

O System Prompt (`src/main/resources/prompts/system.st`) instrui o modelo a seguir regras determinísticas antes de acionar `register-transactions`:

- **Data da Transação (`dataMovimentacao`):**
  - Se omitida ou `"hoje"` -> usa `{currentDateTime}`.
  - `"ontem"` -> `{currentDateTime} - 1 dia`.
  - `"anteontem"` -> `{currentDateTime} - 2 dias`.
  - `"semana passada"` -> calcula data correspondente a partir de `{currentDateTime}`.
  - Formato obrigatório: `yyyy-MM-dd'T'HH:mm`.
- **Categoria (`categoriaId`):**
  - Deve escolher exclusivamente um dos IDs fornecidos em `{categories}` (`id - name`).
- **Recorrência e Parcelamento (`isRecurring`, `parcelas`, `parcelaAtual`):**
  - Se `isRecurring == false` -> `parcelas = null`, `parcelaAtual = null`, `recorrenciaType = null`, `recorrenciaEndDate = null` (nunca enviar `1/1` para transações únicas).
  - Se `isRecurring == true` e parcelado -> extrai total de `parcelas` e `parcelaAtual`.

---

## 6. Mapa Consolidado de Endpoints e Tools

### Endpoints Expostos pelo SmartApi
| Método & Rota | Consumo | Descrição |
| :--- | :--- | :--- |
| `POST /transactions/login` | `application/json` (`LoginDTO`) | Autentica usuário na `Auth.Api` e vincula tokens ao `chatId` no Redis. |
| `POST /transactions/ai` | `multipart/form-data` (`file`, `chatId`) | Transcreve áudio, processa intenção financeira via Gemini e registra transação na `Fin-API`. |
| `GET /actuator/health` | - | Health check do Spring Boot Actuator (usado no deploy Render/Docker). |
| `GET /swagger-ui/index.html` | - | Documentação interativa OpenAPI / Swagger. |

### Tools Spring AI Registradas (`@Tool`)
| Nome da Tool | Classe / Método | Status Atual |
| :--- | :--- | :--- |
| `register-transactions` | `TransactionService.registerTransaction(TransactionAiDTO)` | **Ativa** — converte para `TransactionRequest` e chama `POST api/transacoes/novo` na `Fin-API`. |
| `list-transactions-by-period` | `TransactionService.listTransactionsByPeriod()` | **Pendente** — lança `UnsupportedOperationException("Método não implementado")`. |
| `execute-login` | `AuthService.executeLogin(LoginDTO)` | **Ativa** — anotada com `@Tool`, embora seja chamada diretamente pelo controller e pelo handler do Telegram. |
