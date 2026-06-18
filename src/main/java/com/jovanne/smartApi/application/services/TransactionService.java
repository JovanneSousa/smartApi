package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.LoginDTO;
import com.jovanne.smartApi.application.tool.ToolResult;
import com.jovanne.smartApi.application.dtos.TransactionAiDTO;
import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.domain.exceptions.ApiClientException;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
import com.jovanne.smartApi.infraestructure.http.clients.IFinanceClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService implements ITransactionService {
    @Autowired
    IFinanceClient financeClient;

    @Autowired
    ToolResultHolder holderResult;

    @Autowired
    IAuthService authService;

    @Value("${api.auth.login}")
    private String userDemo;

    @Value("${api.auth.password}")
    private String passDemo;

    @Override
    @Tool(name = "register-transactions", description = "Registra uma nova transação financeira")
    public ToolResult registerTransaction(TransactionAiDTO dtoAi) {


        try {
            var login = new LoginDTO(
                    userDemo,
                    passDemo,
                    "fin"
            );
            var token = "Bearer " + authService.executeLogin(login);
            var request = TransactionRequest.fromAi(dtoAi);
            var result = financeClient.createTransaction(request, token);
            holderResult.set(
                    ToolResult.ok(201,"Transação registrada com sucesso! ID: " + result.id())
            );
            return holderResult.get();

        } catch (ApiClientException ex) {
            return returnsError(ex);
        }
    }

    private ToolResult returnsError(ApiClientException ex) {
        String message = ex.getCause() != null ?
                ex.getCause().getMessage() :
                ex.getMessage();
        holderResult.set(
                ToolResult.error(ex.getStatusCode(), message, ex.getListOfErrors())
        );
        return holderResult.get();
    }

    @Override
    @Tool(name = "list-transactions-by-period", description = "Lista transações do usuário com um período base")
    public List<TransactionResponse> listTransactionsByPeriod() {
        System.out.println("listou");
        return List.of();
    }
}
