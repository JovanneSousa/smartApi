package com.jovanne.smartApi.infraestructure.controllers.response;

import com.jovanne.smartApi.application.output.TransactionOutput;

import java.math.BigDecimal;

public record TransactionResponse(String id, String description, String category, BigDecimal amount) {
    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(
                output.id(),
                output.description(),
                output.category().toString(),
                BigDecimal.valueOf(output.amount())
        );
    }
}
