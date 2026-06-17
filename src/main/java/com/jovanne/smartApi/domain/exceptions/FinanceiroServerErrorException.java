package com.jovanne.smartApi.domain.exceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceiroServerErrorException extends FinanceiroClientException{
    public FinanceiroServerErrorException(FinanceiroErrorResponse errorResponse) {
        super(500, "Erro interno no serviço financeiro", errorResponse);
    }
}
