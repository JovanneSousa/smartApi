package com.jovanne.smartApi.infraestructure.http.response.external;


import com.jovanne.smartApi.domain.entities.TransacaoType;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TransactionResponse(
        String id,
        String userId,
        TransacaoType type,
        String titulo,
        BigDecimal valor,
        String categoriaId,
        LocalDateTime dataMovimentacao,
        boolean isRecurring,
        Integer parcelas,
        Integer parcelaAtual
) implements BaseResponse {

    @Override
    public boolean isValid() {
        return id != null &&
                titulo != null &&
                categoriaId != null &&
                valor != null &&
                dataMovimentacao != null;
    }
}
