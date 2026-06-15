package com.jovanne.smartApi.infraestructure.controllers;

import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TextToSpeechController {

    @Autowired
    TextToSpeechModel textToSpeechModel;

    @PostMapping(value = "sinthesize", produces = "audio/mp3")
    ResponseEntity<Resource> sinthesize(@RequestBody SynthesizeRequest request) {

        var audio = textToSpeechModel.call(request.text);

        var response = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("audio.mp3")
                                .build()
                                .toString())
                .body(response);
    }

    record  SynthesizeRequest(String text) {}
}
