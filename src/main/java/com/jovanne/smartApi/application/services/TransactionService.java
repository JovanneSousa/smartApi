package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.tool.ToolResult;
import com.jovanne.smartApi.application.dtos.TransactionAiDTO;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.domain.exceptions.FinanceiroClientException;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
import com.jovanne.smartApi.infraestructure.http.clients.IFinanceiroClient;
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
    public ToolResult registerTransaction(TransactionAiDTO dtoAi) {
        System.out.println("Criando");
        try {
            var request = TransactionRequest.fromAi(dtoAi);
            var result = client.createTransaction(request);
            return ToolResult.ok("Transação registrada com sucesso! ID: " + result.id());
//        } catch (FeignException e) {
//            e.printStackTrace();
//            String detail = e.contentUTF8() != null && !e.contentUTF8().isBlank()
//                    ? e.contentUTF8()
//                    : "status " + e.status();
//            return ToolResult.error("Erro ao registrar transação", List.of(detail));
        } catch (FinanceiroClientException ex) {
            String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            return ToolResult.error("Erro genérico: " + message, List.of());
        }
    }

    @Override
    @Tool(name = "list-transactions-by-period", description = "Lista transações do usuário com um período base")
    public List<TransactionResponse> listTransactionsByPeriod() {
        System.out.println("listou");
        return List.of();
    }
}
