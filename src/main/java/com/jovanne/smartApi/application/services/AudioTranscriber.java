package com.jovanne.smartApi.application.services;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.ClassPathResource;
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
            tempFile = copyAudioToTempFile(audio);
            return executeScript(tempFile);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao transcrever áudio", e);
        } finally {
            deleteTempFile(tempFile);
        }
    }

    private String executeScript(Path audioPath) throws IOException, InterruptedException {
        String script = "C:/dev/backend/java/smartApi/src/main/resources/scripts/transcribe.py";
        Process process = new ProcessBuilder(
                "python",
                script,
                audioPath.toAbsolutePath().toString()
        ).start();

        String output;

        try (
                BufferedReader out = new BufferedReader(
                        new InputStreamReader(
                                process.getInputStream(),
                                StandardCharsets.UTF_8))
        ) {
            output = out.lines().collect(Collectors.joining("\n"));
        }

        int exitCode = process.waitFor();
        if(exitCode != 0) {
            throw new RuntimeException("Script de transcrição falhou com o código: " + exitCode);
        }

        return output;
    }

    private void deleteTempFile(@Nullable Path tempfile) {
            if(tempfile == null) return;

            try {
                Files.deleteIfExists(tempfile);
            } catch (IOException e) {
                System.out.println("Erro ao excluir arquivo: " + e.getMessage());
            }
    }

    private Path copyAudioToTempFile(Resource audio) throws IOException {
        Path tempFile = Files.createTempFile("audio", ".m4a");

        Files.copy(
                audio.getInputStream(),
                tempFile,
                StandardCopyOption.REPLACE_EXISTING
        );
        return tempFile;
    }
}
