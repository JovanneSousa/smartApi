package com.jovanne.smartApi.application.dtos;

import com.jovanne.smartApi.domain.entities.TransacaoType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionAiDTO(
        TransacaoType type,
        String titulo,
        BigDecimal valor,
        String categoriaId,
        LocalDateTime dataMovimentacao,
        boolean isRecurring,
        Integer parcelas,
        Integer parcelaAtual
) {
}
