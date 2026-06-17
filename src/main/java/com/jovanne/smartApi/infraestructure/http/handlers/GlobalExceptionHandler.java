package com.jovanne.smartApi.infraestructure.http.handlers;

import com.jovanne.smartApi.domain.exceptions.financeApiExceptions.FinanceClientException;
import com.jovanne.smartApi.infraestructure.http.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FinanceClientException.class)
    public ResponseEntity<ErrorResponse> handleFinancieroError(FinanceClientException ex){
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        new ErrorResponse(
                                ex.getStatusCode(),
                                ex.getMessage(),
                                ex.getErrorResponse() != null ? ex.getErrorResponse().errors() : List.of()
                        )
                );
    }
}
