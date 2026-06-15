package com.jovanne.smartApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_KEY", matches = ".+")
public class OpenAiSpeechModelIT {
    @Autowired
    OpenAiAudioSpeechModel speechModel;

    @Test
    void soud_produceAudio_when_textIsProvided(String fileName, String expectedKeyword) throws IOException {
        var response = speechModel.call("O valor total de serviços ficou 80 reais. Posso confirmar o pagamento?");

        assertThat(response).hasSizeGreaterThan(1024);
        var file = Files.createTempFile("AUDIO_", ".mp3");
        Files.write(file, response);
        System.out.println(file.toAbsolutePath());
    }
}
