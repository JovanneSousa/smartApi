package com.jovanne.smartApi.infraestructure.http.request;

import com.jovanne.smartApi.application.TransactionAiDTO;
import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.TransacaoType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        String id,
        String userId ,
        TransacaoType type,
        String titulo,
        BigDecimal valor,
        Category category,
        LocalDateTime dataMovimentacao,
        boolean isRecurring,
        Integer parcelas,
        Integer parcelaAtual
) {
    public static TransactionRequest fromAi(TransactionAiDTO ai) {
        return new TransactionRequest(
                null,
                null,
                ai.type(),
                ai.titulo(),
                ai.valor(),
                ai.category(),
                ai.dataMovimentacao(),
                ai.isRecurring(),
                ai.parcelas(),
                ai.parcelaAtual()
        );
    }

    @Override
    public String toString() {
        return "TransacaoRequest{" +
                "userId='" + userId + '\'' +
                ", type=" + type +
                ", titulo='" + titulo + '\'' +
                ", valor=" + valor +
                ", categoriaId='" + category + '\'' +
                ", dataMovimentacao=" + dataMovimentacao +
                ", isRecurring=" + isRecurring +
                ", parcelas=" + parcelas +
                ", parcelaAtual=" + parcelaAtual +
                '}';
    }
}
