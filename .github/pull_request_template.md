## 📌 Resumo
<!-- Descreva de forma objetiva o que foi alterado e qual problema resolve. -->

## 🎯 Tipo de Alteração
- [ ] `feat` (Nova funcionalidade / `@Tool` / fluxo Telegram)
- [ ] `fix` / `hotfix` (Correção de bug)
- [ ] `refactor` (Refatoração sem mudança de comportamento)
- [ ] `docs` (Documentação / `CONTEXT.md` / `docs/`)
- [ ] `test` (Testes unitários ou de integração `*IT.java`)
- [ ] `chore` (Dependências Maven, Docker, `entrypoint.sh`, configs)
- [ ] `release` (Consolidação `preview` -> `main`)

## 🔄 Componentes e Fluxos Impactados
<!-- Ex: TelegramMessageHandler, TransactionService (@Tool), AuthService, TokenStore (Redis), IFinanceClient, system.st -->
- 

## 🧪 Como foi testado
- [ ] Compilação Maven (`.\mvnw.cmd clean compile`) sem erros
- [ ] Testado via Bot do Telegram (texto e/ou voz)
- [ ] Testado via REST / Swagger (`/transactions/login`, `/transactions/ai`)
- [ ] Testes automatizados executados *(quando aplicável)*

## ⚠️ Notas de Deploy / Variáveis de Ambiente
<!-- Ex: Novas variáveis no application.yml / Render (GENAI_KEY, FINANCE_URL, AUTH_URL, TELEGRAM_BOT_TOKEN), mudanças no Dockerfile ou Redis. -->
- Nenhuma.

## ✅ Checklist
- [ ] `TelegramContext.clear()` e `ToolResultHolder.clear()` garantidos em blocos `finally`
- [ ] `CONTEXT.md` e `docs/flows.md` atualizados caso algum fluxo, prompt, tool ou contrato tenha mudado
- [ ] Sem chaves de API, tokens de bot ou senhas hardcoded nos arquivos alterados
