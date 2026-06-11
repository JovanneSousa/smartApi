package com.jovanne.smartApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GENAI_KEY", matches = ".+")
class SmartApiApplicationTests {
    @Autowired
    GoogleGenAiChatModel chatModel;

	@Test
    void should_recieveResponse_when_chatModelIsCalled() {
        System.out.println(System.getenv("GENAI_KEY"));
        var response = chatModel.call("Gere um valor de budgeting, com descrição de gasto, valor em reais e local");
        System.out.println(response);
        assertThat(response).isNotEmpty();
    }

}
