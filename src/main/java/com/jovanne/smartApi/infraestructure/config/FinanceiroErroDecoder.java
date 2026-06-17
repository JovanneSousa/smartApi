package com.jovanne.smartApi.infraestructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovanne.smartApi.domain.exceptions.*;
import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class FinanceiroErroDecoder implements ErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        var errorResponse = extractBody(response);

        return switch (response.status()) {
            case 400 -> new FinanceiroBadRequestException(errorResponse);
            case 401 -> new FinanceiroUnauthorizedException(errorResponse);
            case 403 -> new FinanceiroForbbidenException(errorResponse);
            case 404 -> new FinanceiroNotFoundException(errorResponse);
            case 500 -> new FinanceiroServerErrorException(errorResponse);
            default -> new FinanceiroClientException(
                    response.status(),
                    "Erro inesperado do serviço financeiro",
                    errorResponse
            );
        };
    }

    private FinanceiroErrorResponse extractBody(Response response) {
        try {
            if (response.body() == null) return null;
            String json = new String(
                    response.body().asInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            return mapper.readValue(json, FinanceiroErrorResponse.class);
        } catch (IOException ex) {
            return new FinanceiroErrorResponse(false, List.of("Não foi possível ler o erro da resposta"));
        }
    }
}
