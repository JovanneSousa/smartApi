package com.jovanne.smartApi.infraestructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;
import feign.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();
    protected ApiErrorResponse extractBody(Response response) {
        try {
            if (response.body() == null) return null;
            String json = new String(
                    response.body().asInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            return mapper.readValue(json, ApiErrorResponse.class);
        } catch (IOException ex) {
            return new ApiErrorResponse(false, List.of("Não foi possível ler o erro da resposta"));
        }
    }
}
