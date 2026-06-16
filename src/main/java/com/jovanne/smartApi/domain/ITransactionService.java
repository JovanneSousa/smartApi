package com.jovanne.smartApi.domain;

import com.jovanne.smartApi.application.TransactionAiDTO;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;

import java.util.List;

public interface ITransactionService {
    TransactionResponse registerTransaction(TransactionAiDTO request);

    List<TransactionResponse> listTransactionsByPeriod();
}
