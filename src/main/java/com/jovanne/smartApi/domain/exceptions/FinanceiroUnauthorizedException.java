package com.jovanne.smartApi.domain.exceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceiroUnauthorizedException extends FinanceiroClientException{
    public FinanceiroUnauthorizedException(FinanceiroErrorResponse errorResponse) {
        super(401, "Não autorizado no serviço financeiro", errorResponse);
    }
}
