package com.jovanne.smartApi.infraestructure.http.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jovanne.smartApi.application.dtos.TransactionAiDTO;
import com.jovanne.smartApi.domain.entities.Category;
import com.jovanne.smartApi.domain.entities.TransacaoType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        String id,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        TransacaoType type,
        String description,
        BigDecimal amount,
        String categoryId,
        LocalDateTime transactionDate,
        boolean isRecurring,
        Integer parcelas,
        Integer parcelaAtual
) {
    public static TransactionRequest fromAi(TransactionAiDTO ai) {
        return new TransactionRequest(
                null,
                ai.type(),
                ai.titulo(),
                ai.valor(),
                ai.categoriaId(),
                ai.dataMovimentacao(),
                ai.isRecurring(),
                ai.parcelas(),
                ai.parcelaAtual()
        );
    }

    @Override
    public String toString() {
        return "TransacaoRequest{" +
                ", type=" + type +
                ", titulo='" + description + '\'' +
                ", valor=" + amount +
                ", categoriaId='" + categoryId + '\'' +
                ", dataMovimentacao=" + transactionDate +
                ", isRecurring=" + isRecurring +
                ", parcelas=" + parcelas +
                ", parcelaAtual=" + parcelaAtual +
                '}';
    }
}
