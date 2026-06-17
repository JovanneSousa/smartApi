package com.jovanne.smartApi.domain.interfaces;

import com.jovanne.smartApi.application.tool.ToolResult;
import com.jovanne.smartApi.application.dtos.TransactionAiDTO;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;

import java.util.List;

public interface ITransactionService {
    ToolResult registerTransaction(TransactionAiDTO request);

    List<TransactionResponse> listTransactionsByPeriod();
}
