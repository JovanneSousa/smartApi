package com.jovanne.smartApi.application.output;

import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionOutput(String id,String description, String category, double amount) {
    public static TransactionOutput from(Transaction transacao) {
        return new TransactionOutput(
                transacao.getTransactionId(),
                transacao.getDescription(),
                transacao.getCategory().name(),
                BigDecimal.valueOf(transacao.getAmount())
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue());
    }
}
