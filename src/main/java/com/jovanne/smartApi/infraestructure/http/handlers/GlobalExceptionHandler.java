package com.jovanne.smartApi.infraestructure.http.handlers;

import com.jovanne.smartApi.domain.exceptions.ApiClientException;
import com.jovanne.smartApi.infraestructure.http.response.ErrorResponse;
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
}
