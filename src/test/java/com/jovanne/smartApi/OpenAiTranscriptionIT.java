package com.jovanne.smartApi;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_KEY", matches = ".+")
public class OpenAiTranscriptionIT {
    @Autowired
    TranscriptionModel transcriptionModel;

    @ParameterizedTest
    @CsvSource({
            "recording-1.m4a, 80 reais",
            "recording-2.m4a, 40 reais",
            "recording-3.m4a, 120 reais",
            "recording-4.m4a, 90 reais",
            "recording-5.m4a, 200 reais",
            "recording-6.m4a, 60 reais",
    })
    void should_containExpectedKeywords_when_audioFilesAreProcessed(String fileName, String expectedKeyword) {
        var record = new ClassPathResource("audio/" + fileName);

        var result =  transcriptionModel.transcribe(record);

        assertThat(result).contains(expectedKeyword);
        System.out.println(result);
    }
}
