package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

public class FinanceiroBadRequestException extends FinanceiroClientException{
    public FinanceiroBadRequestException(FinanceiroErrorResponse errorResponse) {
        super(400, "Dados inválidos enviados ao serviço financeiro", errorResponse);
    }
}
