package com.jovanne.smartApi.infraestructure.http;

import com.jovanne.smartApi.application.TransactionAiDTO;
import com.jovanne.smartApi.application.services.TransactionService;
import com.jovanne.smartApi.domain.ITransactionService;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
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

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final ITransactionService transactionService;
    private final ChatClient chatClient;

    @Autowired
    TranscriptionModel transcriptionModel;

    public TransactionController(
            ITransactionService transactionService,
            ChatClient.Builder builder,
            @Value("classpath:/prompts/system.st") Resource systemPrompt
    ) throws IOException {
        this.transactionService = transactionService;
        this.chatClient = builder
                .defaultTools(transactionService)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @PostMapping
    ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionAiDTO request) {
        var transaction = transactionService.registerTransaction(request);
        return ResponseEntity.created(URI.create("/transactions/" + transaction.id()))
                .body(transaction);
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> trancribe (@RequestParam("file") MultipartFile file) {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        try {
            var resoure = file.getResource();
            var userMessage = transcriptionModel.transcribe(resoure);
            var result = chatClient.prompt()
                    .system(s -> s.param("currentDateTime", currentDateTime))
                    .user(userMessage)
                    .call()
                    .content();

            return ResponseEntity.created(URI.create("/transactions"))
                    .body(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ex.getCause() != null ?
                            ex.getCause().getMessage() :
                            ex.getMessage());
        }
    }
}
