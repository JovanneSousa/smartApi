package com.jovanne.smartApi.application;

import com.jovanne.smartApi.domain.Category;
import com.jovanne.smartApi.domain.TransacaoType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionAiDTO(
        TransacaoType type,
        String titulo,
        BigDecimal valor,
        Category category,
        LocalDateTime dataMovimentacao,
        boolean isRecurring,
        Integer parcelas,
        Integer parcelaAtual
) {
}
