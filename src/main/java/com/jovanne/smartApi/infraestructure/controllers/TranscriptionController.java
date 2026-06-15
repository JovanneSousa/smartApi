package com.jovanne.smartApi.infraestructure.controllers;

import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TranscriptionController {
    @Autowired
    TranscriptionModel transcriptionModel;

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String trancribe (@RequestParam("file")MultipartFile file) {
        var resoure = file.getResource();

        return transcriptionModel.transcribe(resoure);
    }

}
