# SmartApi - Assistente Financeiro IA

SmartApi é um assistente financeiro inteligente construído com Java e Spring Boot, que utiliza Inteligência Artificial para processar comandos de voz, transcrevê-los e executar ações financeiras automaticamente, como o registro de transações.

## 🚀 Tecnologias

- **Java 25**
- **Spring Boot 4.0.6**
- **Spring AI**: Integração com modelos de linguagem e transcrição.
- **Google Gemini (GenAI)**: Motor de inteligência artificial para processamento de linguagem natural e tool calling.
- **OpenFeign**: Para comunicação com APIs externas.
- **Lombok**: Redução de código boilerplate.
- **SpringDoc OpenAPI (Swagger)**: Documentação da API.

## 🛠️ Funcionalidades Principais

- **Transcrição de Áudio**: Converte mensagens de voz em texto.
- **Extração Inteligente de Dados**: Identifica automaticamente valores, categorias, datas e parcelamentos a partir da fala do usuário.
- **Execução Automática de Ferramentas (Tool Calling)**: A IA decide e executa chamadas de serviço para registrar ou consultar transações diretamente no sistema.
- **Suporte a Datas Relativas**: Entende comandos como "ontem", "hoje" ou "semana passada".

## ⚙️ Configuração

### Pré-requisitos

- JDK 25
- Maven
- Uma chave de API do Google Gemini (GENAI_KEY)

### Variáveis de Ambiente

Para rodar o projeto, você precisará configurar a seguinte variável de ambiente:

```bash
GENAI_KEY=sua_chave_api_aqui
```

### Configuração do application.yml

O projeto espera uma API financeira rodando em `http://localhost:5087/api`. Você pode ajustar essa URL no arquivo `src/main/resources/application.yml`.

## 📌 API Endpoints

No momento, o principal endpoint funcional é:

### Transcrever e Processar Transação via IA

`POST /transactions/ai`

Este endpoint recebe um arquivo de áudio, transcreve o conteúdo e utiliza a IA para processar a intenção do usuário (ex: "Gastei 50 reais com pizza ontem").

- **Consumes**: `multipart/form-data`
- **Request Body**:
  - `file`: Arquivo de áudio (ex: `.m4a`, `.mp3`, `.wav`)

- **Exemplo de Resposta**:
  ```json
  "Transação registrada com sucesso! ID: 12345"
  ```

## 🤖 Comportamento da IA

A IA atua como um assistente financeiro e segue regras rigorosas de extração:
- **Data**: Se não informada, assume a data atual. Suporta "hoje", "ontem", "anteontem".
- **Categorização**: Escolhe a categoria que melhor se adapta ao contexto.
- **Parcelamento**: Identifica se a transação é recorrente ou parcelada e extrai os detalhes corretamente.

## 📄 Documentação

A documentação interativa da API (Swagger) pode ser acessada em:
`http://localhost:8080/swagger-ui/index.html` (assumindo a porta padrão).
