package com.jovanne.smartApi.infraestructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;
import feign.Response;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();

    private ApiErrorResponse voidError() {
        return new ApiErrorResponse(false, List.of("Não foi possível ler o erro da resposta"));
    }

    protected ApiErrorResponse extractBody(Response response) {
        try {
            if (response.body() == null) return voidError();
            String json = new String(
                    response.body().asInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return mapper.readValue(json, ApiErrorResponse.class);
        } catch (Exception ex) {
            return voidError();
        }
    }
}
