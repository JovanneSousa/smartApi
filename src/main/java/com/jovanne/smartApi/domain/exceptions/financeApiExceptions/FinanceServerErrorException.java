package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceServerErrorException extends FinanceClientException {
    public FinanceServerErrorException(FinanceiroErrorResponse errorResponse) {
        super(500, "Erro interno no serviço financeiro", errorResponse);
    }
}
