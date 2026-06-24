package com.jovanne.smartApi.infraestructure.http.controllers;

import com.jovanne.smartApi.application.dtos.CategoryDTO;
import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.infraestructure.http.response.internal.ErrorResponse;
import com.jovanne.smartApi.infraestructure.http.response.internal.InternalAiApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.internal.InternalApiResponse;
import com.jovanne.smartApi.infraestructure.data.TokenStore;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final ChatClient chatClient;
    private final TranscriptionModel transcriptionModel;
    private final ToolResultHolder holder;
    private final TokenStore tokenStore;
    private final ITransactionService transactionService;
    private final IAuthService authService;
    public TransactionController(
            TranscriptionModel transcriptionModel,
            ToolResultHolder holder,
            TokenStore tokenStore,
            IAuthService authService,
            ITransactionService transactionService,
            ChatClient.Builder builder,
            @Value("classpath:/prompts/system.st") Resource systemPrompt
    ) throws IOException {
        this.transactionService = transactionService;
        this.authService = authService;
        this.holder = holder;
        this.tokenStore = tokenStore;
        this.transcriptionModel = transcriptionModel;
        this.chatClient = builder
                .defaultTools(transactionService)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> createTransaction(@RequestParam("file") MultipartFile file, @RequestParam Long chatId) {
        var categories = transactionService.listCategories();
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        var resoure = file.getResource();
        var userMessage = transcriptionModel.transcribe(resoure);

        var result = chatClient.prompt()
                .system(s -> s
                                .param("currentDateTime", currentDateTime)
                                .param("categories", CategoryDTO.formatSystemPrompt(categories)))
                .user(userMessage)
                .call()
                .content();

        if(!holder.get().success()) {
            return ResponseEntity
                    .status(holder.get().statusCode())
                    .body(ErrorResponse.fromToolResultHolder(holder, result));
        }

        return ResponseEntity.created(URI.create("/transactions"))
                .body(InternalAiApiResponse.fromToolResult(holder.get(), result));
    }

    @PostMapping(value = "/login")
    ResponseEntity<?> executaLogin(@RequestBody LoginDTO login) {
        authService.executeLogin(login);
        var result = tokenStore.getToken(login.chatId());

        var token = new HashMap<String, String>();
        token.put("token", result);

        return  ResponseEntity.ok()
                .body(
                        new InternalApiResponse<HashMap<String, String>>(
                                true,
                                token
                        )
                );
    }
}
