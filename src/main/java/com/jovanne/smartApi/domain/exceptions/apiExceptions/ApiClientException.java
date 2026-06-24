package com.jovanne.smartApi.domain.exceptions.apiExceptions;

import com.jovanne.smartApi.infraestructure.http.response.external.ApiErrorResponse;

import java.util.List;

public class ApiClientException extends RuntimeException {
    private final int statusCode;
    private final ApiErrorResponse errorResponse;

    public ApiClientException(
            int statusCode,
            String message,
            ApiErrorResponse errorResponse) {
        super(message);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }

    public int getStatusCode() { return statusCode; }
    public ApiErrorResponse getErrorResponse() { return errorResponse; }
    public List<String> getListOfErrors() {
        if (errorResponse.errors() == null) return List.of();
        return errorResponse.errors();
    }
}
