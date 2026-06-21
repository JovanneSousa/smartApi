package com.jovanne.smartApi.infraestructure.http.handlers;

import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiClientException;
import com.jovanne.smartApi.infraestructure.http.response.external.ErrorResponse;
import com.jovanne.smartApi.infraestructure.http.response.external.ExternalApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.internal.InternalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiClientException.class)
    public ResponseEntity<ErrorResponse> handleApiClientError(ApiClientException ex){
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.fromApiExcetipn(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException(Exception ex){
        var errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        var body = new ErrorResponse(
                500,
                "Erro interno no servidor",
                List.of(errorMessage != null ? errorMessage : "")
        );

        // apenas para depurar momentaneamente
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }


}
