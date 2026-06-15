package com.jovanne.smartApi.infraestructure.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatClientController {
    @Autowired
    ChatClient chatClient;

    @GetMapping("/chat-client")
    ResponseEntity<String> chat(String prompt)
    {

        var response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return ResponseEntity.ok(response);
    }
}
