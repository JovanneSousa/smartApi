package com.jovanne.smartApi.application.services;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

@Service
public class AudioTranscriber implements TranscriptionModel {

    @Override
    public AudioTranscriptionResponse call(AudioTranscriptionPrompt audioTranscriptionPrompt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String transcribe(Resource audio, @Nullable AudioTranscriptionOptions options) {
        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("audio", ".m4a");

            Files.copy(
                    audio.getInputStream(),
                    tempFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String script = "C:/dev/backend/python/trancription/transcribe.py";
            Process process = new ProcessBuilder(
                    "python",
                    script,
                    tempFile.toAbsolutePath().toString()
            ).start();

            try (
                    BufferedReader out = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream(),
                                    StandardCharsets.UTF_8))
            ) {
                process.waitFor();
                return out.lines().collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao transcrever áudio", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
