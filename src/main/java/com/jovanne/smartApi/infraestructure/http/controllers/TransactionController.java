package com.jovanne.smartApi.infraestructure.http.controllers;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.infraestructure.http.response.external.ErrorResponse;
import com.jovanne.smartApi.infraestructure.http.response.internal.InternalApiResponse;
import com.jovanne.smartApi.infraestructure.redis.TokenStore;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    TranscriptionModel transcriptionModel;
    @Autowired
    ToolResultHolder holder;

    @Autowired
    IAuthService authService;

    @Autowired
    TokenStore tokenStore;

    public TransactionController(
            ITransactionService transactionService,
            ChatClient.Builder builder,
            @Value("classpath:/prompts/system.st") Resource systemPrompt
    ) throws IOException {
        this.chatClient = builder
                .defaultTools(transactionService)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }
//
//    @PostMapping
//    ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionAiDTO request) {
//        var transaction = transactionService.registerTransaction(request);
//        return ResponseEntity.created(URI.create("/transactions/" + transaction.id()))
//                .body(transaction);
//    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> trancribe (@RequestParam("file") MultipartFile file, @RequestParam Long chatId) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        var resoure = file.getResource();
        var userMessage = transcriptionModel.transcribe(resoure);
        var result = chatClient.prompt()
                .system(s -> s.param("currentDateTime", currentDateTime))
                .user(userMessage)
                .call()
                .content();

        if(!holder.get().success()) {
            return ResponseEntity
                    .status(holder.get().statusCode())
                    .body(ErrorResponse.fromToolResultHolder(holder));
        }

        return ResponseEntity.created(URI.create("/transactions"))
                .body(result.toString());
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
