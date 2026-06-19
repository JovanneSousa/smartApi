package com.jovanne.smartApi.infraestructure.http.response.external;


import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;

import java.math.BigDecimal;


public record TransactionResponse(String id, String title, String category, BigDecimal valor) implements BaseResponse {
    public static TransactionResponse fromRequest(TransactionRequest request) {
        return new TransactionResponse(
                request.id(),
                request.titulo(),
                request.category().toString(),
                request.valor()
        );
    }

    @Override
    public boolean isValid() {
        return id != null &&
                title != null &&
                category != null &&
                valor != null;
    }
}
