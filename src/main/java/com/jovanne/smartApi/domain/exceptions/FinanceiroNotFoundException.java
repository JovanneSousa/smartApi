package com.jovanne.smartApi.domain.exceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceiroNotFoundException extends FinanceiroClientException{
    public FinanceiroNotFoundException(FinanceiroErrorResponse errorResponse) {
        super(404, "Recurso não encontrado no serviço financeiro", errorResponse);
    }
}
