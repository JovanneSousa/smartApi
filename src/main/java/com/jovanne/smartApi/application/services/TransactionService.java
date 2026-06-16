package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.TransactionAiDTO;
import com.jovanne.smartApi.domain.ITransactionService;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
import com.jovanne.smartApi.infraestructure.http.services.IFinanceiroClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService implements ITransactionService {
    @Autowired
    IFinanceiroClient client;

    @Override
    @Tool(name = "register-transactions", description = "Registra uma nova transação financeira")
    public TransactionResponse registerTransaction(TransactionAiDTO dtoAi) {
        System.out.println("Criando");
        var request = TransactionRequest.fromAi(dtoAi);
        System.out.println(request.toString());
        return TransactionResponse.fromRequest(request);
    }

    @Override
    @Tool(name = "list-transactions-by-period", description = "Lista transações do usuário com um período base")
    public List<TransactionResponse> listTransactionsByPeriod() {
        System.out.println("listou");
        return List.of();
    }
}
