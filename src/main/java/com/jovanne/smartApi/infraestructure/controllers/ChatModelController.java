package com.jovanne.smartApi.infraestructure.controllers;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatModelController {
    @Autowired
    OpenAiChatModel chatModel;

    @GetMapping("/chat-model")
    ResponseEntity<String> chat(String prompt)
    {
        return ResponseEntity.ok(chatModel.call(prompt));
    }
}
