package com.jovanne.smartApi.infraestructure.config;

import com.jovanne.smartApi.domain.exceptions.*;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class ApiErroDecoder extends BaseErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        var errorResponse = extractBody(response);

        return switch (response.status()) {
            case 400 -> new ApiBadRequestException(errorResponse);
            case 401 -> new ApiUnauthorizedException(errorResponse);
            case 403 -> new ApiForbbidenException(errorResponse);
            case 404 -> new ApiNotFoundException(errorResponse);
            case 500 -> new ApiServerErrorException(errorResponse);
            default -> new ApiClientException(
                    response.status(),
                    "Erro inesperado do serviço externo",
                    errorResponse
            );
        };
    }

}
