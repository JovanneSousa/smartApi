package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.ApiErrorResponse;

public class ApiNotFoundException extends ApiClientException {
    public ApiNotFoundException(ApiErrorResponse errorResponse) {
        super(404, "Recurso não encontrado no serviço externo", errorResponse);
    }
}
