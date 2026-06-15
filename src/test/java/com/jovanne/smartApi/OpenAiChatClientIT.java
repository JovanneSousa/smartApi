package com.jovanne.smartApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_KEY", matches = ".+")
public class OpenAiChatClientIT {
    @Autowired
    OpenAiChatModel chatModel;

    @Test
    void should_executeSum_when_prompted() {
        var chatClient = ChatClient.builder(chatModel)
                .defaultSystem("Voce é um matemático")
                .build();

        var meuPrompt = new Prompt("some 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado final sem explicações");

        var response = chatClient.prompt(meuPrompt)
                .call().content();

        assertThat(response).contains(String.valueOf((10 + 20) - 30));
        System.out.println(response);
    }
}
