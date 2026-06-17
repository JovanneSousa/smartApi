package com.jovanne.smartApi.domain.exceptions.financeApiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.FinanceiroErrorResponse;

import java.util.List;

public class FinanceiroClientException extends RuntimeException{
    private final int statusCode;
    private final FinanceiroErrorResponse errorResponse;

    public FinanceiroClientException(
            int statusCode,
            String message,
            FinanceiroErrorResponse errorResponse) {
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        super(message);
    }

    public int getStatusCode() { return statusCode; }
    public FinanceiroErrorResponse getErrorResponse() { return errorResponse; }
    public List<String> getListOfErrors() {
        if (errorResponse.errors() == null) return List.of();
        return errorResponse.errors();
    }
}
