package com.jovanne.smartApi.infraestructure.telegram;

import com.jovanne.smartApi.application.dtos.CategoryDTO;
import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.entities.RennovationResult;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.infraestructure.data.TokenStore;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TelegramMessageHandler {
    private final String token;
    private final TelegramContext telegramContext;
    private final ToolResultHolder holderResult;
    private final ChatClient chatClient;
    private final TranscriptionModel transcriptionModel;
    private final IAuthService authService;
    private final TokenStore tokenStore;
    private final ITransactionService transactionService;
    private final TelegramClient telegramClient;

    public TelegramMessageHandler(
            @Value("${telegram.bot.token}") String token,
            @Value("classpath:/prompts/system.st") Resource systemPrompt,
            ITransactionService transactionService,
            TelegramContext telegramContext,
            IAuthService authService,
            TokenStore tokenStore,
            ToolResultHolder holderResult,
            ChatClient.Builder builder,
            TranscriptionModel transcriptionModel) throws IOException {
        this.telegramContext = telegramContext;
        this.authService = authService;
        this.tokenStore = tokenStore;
        this.holderResult = holderResult;
        this.transcriptionModel = transcriptionModel;
        this.transactionService = transactionService;
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
        this.chatClient = builder
                .defaultTools(transactionService)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    public void handle(Message message) {
        var chatId = message.getChatId();

        telegramContext.setChatId(chatId);
        holderResult.clear();

        try {
            if(!authService.hasVinculedUser(chatId)) {
                handleFirstAccess(chatId, message.getText());
                return;
            }

            if(!tokenStore.hasToken(chatId)) {
                var refreshed = authService.refreshToken(chatId);
                if(refreshed == RennovationResult.LOGIN_REQUIRED) {
                    sendMessage(chatId, "Sua sessão expirou. Envie seu login:");
                    return;
                }
            }

            if(message.hasVoice()) {
                handleVoice(chatId, message);
            } else if (message.hasText()) {
                handleText(chatId, message.getText());
            }
        } finally {
            telegramContext.clear();
            holderResult.clear();
        }
    }

    private void handleFirstAccess(Long chatId, String text) {
        if (text != null && text.contains(":")) {
            var parts = text.split(":", 2);
            try {
                authService.executeLogin(new LoginDTO(
                        chatId, parts[0].trim(), parts[1].trim(), "fin"));

                sendMessage(chatId, "Login realizado! Pode começar a usar.");
            } catch (Exception ex) {
                sendMessage(chatId, "Login inválido. Tente: login:senha");
            }
        } else {
            sendMessage(chatId, "Bem vindo! Envie seu login no formato: login:senha");
        }
    }

    private void handleText(Long chatId, String userMessage) {
        try {
            var categories = transactionService.listCategories();
            var currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            var result = chatClient.prompt()
                    .system(s -> s
                            .param("currentDateTime", currentDateTime)
                            .param("categories", CategoryDTO.formatSystemPrompt(categories)))
                    .user(userMessage)
                    .call()
                    .content();

            var toolResult = holderResult.get();
            if (toolResult != null && !toolResult.success()) {
                sendMessage(chatId, "Erro " + toolResult.statusCode() + ": " + result);
            } else {
                sendMessage(chatId, result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            sendMessage(chatId, "Erro ao processar sua mensagem. Tente novamente.");
        }
    }

    private void handleVoice(Long chatId, Message message) {
        try {
            var fileId = message.getVoice().getFileId();
            var file = telegramClient.execute(GetFile.builder().fileId(fileId).build());
            var fileUrl = "https://api.telegram.org/file/bot"
                    + token + "/" + file.getFilePath();

            var resource = new UrlResource(fileUrl);
            var userMessage = transcriptionModel.transcribe(resource);

            handleText(chatId, userMessage);

        } catch (Exception ex) {
            sendMessage(chatId, "Erro ao processar o áudio. Tente novamente.");
        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
