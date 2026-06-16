package com.jovanne.smartApi.infraestructure.controllers;

import com.jovanne.smartApi.application.ListTransactionsByCategoryUseCase;
import com.jovanne.smartApi.application.PersistTransactionUseCase;
import com.jovanne.smartApi.application.services.AudioTranscriber;
import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.infraestructure.controllers.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.controllers.response.TransactionResponse;
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
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persist;
    private final ListTransactionsByCategoryUseCase list;
    private final ChatClient chatClient;

    @Autowired
    TranscriptionModel transcriptionModel;

//    @Autowired
//    TextToSpeechModel speechModel;

    public TransactionController(
            PersistTransactionUseCase persist,
            ListTransactionsByCategoryUseCase list,
            ChatClient.Builder builder,
            @Value("classpath:/prompts/system.st") Resource systemPrompt
    ) throws IOException {
        this.persist = persist;
        this.list = list;
        this.chatClient = builder
                .defaultTools(persist, list)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @PostMapping
    ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persist.execute(request.toInput());
        return ResponseEntity.created(URI.create("/transactions/" + transaction.id()))
                .body(TransactionResponse.from(transaction));
    }

    @GetMapping("/{category}")
    ResponseEntity<List<TransactionResponse>> listByCategory(@PathVariable Category category) {
        return ResponseEntity.ok()
                .body(list.execute(category)
                        .stream()
                        .map(TransactionResponse::from)
                        .toList());
    };

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    ResponseEntity<?> trancribe (@RequestParam("file") MultipartFile file) {
        try {
            var resoure = file.getResource();
            var userMessage = transcriptionModel.transcribe(resoure);
            var result = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();

            return ResponseEntity.created(URI.create("/transactions"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("audio.mp3")
                                    .build()
                                    .toString() )
                    .body(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
