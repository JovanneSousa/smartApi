package com.jovanne.smartApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_KEY", matches = ".+")
class SmartApiApplicationTests {
    @Autowired
    ChatModel chatModel;

	@Test
    void should_recieveResponse_when_chatModelIsCalled() {
        System.out.println(System.getenv("GENAI_KEY"));
        var response = chatModel.call("Gere um valor de budgeting, com descrição de gasto, valor em reais e local");
        System.out.println(response);
        assertThat(response).isNotEmpty();
    }

}
