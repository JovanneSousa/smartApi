package com.jovanne.smartApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_KEY", matches = ".+")
public class OpenAiSpeechModelIT {
    @Autowired
    TextToSpeechModel speechModel;

    @Test
    void soud_produceAudio_when_textIsProvided(String fileName, String expectedKeyword) throws IOException {
        var response = speechModel.call("O valor total de serviços ficou 80 reais. Posso confirmar o pagamento?");

        assertThat(response).hasSizeGreaterThan(1024);
        var file = Files.createTempFile("AUDIO_", ".mp3");
        Files.write(file, response);
        System.out.println(file.toAbsolutePath());
    }
}
