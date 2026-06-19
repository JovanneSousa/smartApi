package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.tool.ToolResult;
import com.jovanne.smartApi.application.dtos.TransactionAiDTO;
import com.jovanne.smartApi.application.tool.ToolResultHolder;
import com.jovanne.smartApi.domain.entities.RennovationResult;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiBadRequestException;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiUnauthorizedException;
import com.jovanne.smartApi.domain.interfaces.IAuthService;
import com.jovanne.smartApi.domain.interfaces.ITransactionService;
import com.jovanne.smartApi.domain.exceptions.apiExceptions.ApiClientException;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.external.TransactionResponse;
import com.jovanne.smartApi.infraestructure.http.clients.IFinanceClient;
import com.jovanne.smartApi.infraestructure.telegram.TelegramContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TransactionService implements ITransactionService {
    @Autowired
    IFinanceClient financeClient;

    @Autowired
    ToolResultHolder holderResult;

    @Autowired
    IAuthService authService;

    @Autowired
    TelegramContext telegramContext;

    @Value("${api.auth.login}")
    private String userDemo;

    @Value("${api.auth.password}")
    private String passDemo;

    @Override
    @Tool(name = "register-transactions", description = "Registra uma nova transação financeira")
    public ToolResult registerTransaction(TransactionAiDTO dtoAi) {
        var request = TransactionRequest.fromAi(dtoAi);
        var chatId = 1l;
        telegramContext.setChatId(chatId);

        try {
            return executeCreateTransaction(request);
        } catch (ApiUnauthorizedException ex) {
            RennovationResult refreshed = authService.refreshToken(chatId);
            if(refreshed == RennovationResult.SUCCESS) {
                return executeCreateTransaction(request);
            }
            return returnsError(ex);
        } catch (ApiClientException ex) {
            return returnsError(ex);
        }
    }

    private ToolResult executeCreateTransaction(TransactionRequest request) {
        var response = financeClient.createTransaction(request);
        if (!response.isValid())
            throw new ApiBadRequestException("Dados inválidos retornados do serviço externo");

        holderResult.set(
                ToolResult.ok(201,"Transação registrada com sucesso! ID: " + response.data().id())
        );
        return holderResult.get();
    }

    private ToolResult returnsError(ApiClientException ex) {
        String message = ex.getMessage();
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
