package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;

public class ApiServerErrorException extends ApiClientException {
    public ApiServerErrorException(ApiErrorResponse errorResponse) {
        super(500, "Erro interno no serviço externo", errorResponse);
    }
}
