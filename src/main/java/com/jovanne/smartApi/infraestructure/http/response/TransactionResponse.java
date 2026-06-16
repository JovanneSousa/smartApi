package com.jovanne.smartApi.infraestructure.http.response;


import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;

import java.math.BigDecimal;


public record TransactionResponse(String id, String description, String category, BigDecimal amount) {
    public static TransactionResponse fromRequest(TransactionRequest request) {
        return new TransactionResponse(
                request.id(),
                request.titulo(),
                request.category().toString(),
                request.valor()
        );
    }
}
