package com.jovanne.smartApi.application.services;

import com.jovanne.smartApi.application.dtos.CategoryDTO;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class TransactionService implements ITransactionService {
    private final IFinanceClient financeClient;
    private final ToolResultHolder holderResult;
    private final IAuthService authService;
    private final TelegramContext telegramContext;
    public TransactionService(
            IFinanceClient financeClient,
            ToolResultHolder holderResult,
            IAuthService authService,
            TelegramContext telegramContext) {
        this.financeClient = financeClient;
        this.authService = authService;
        this.holderResult = holderResult;
        this.telegramContext = telegramContext;
    }

    @Override
    @Tool(name = "register-transactions", description = "Registra uma nova transação financeira")
    public ToolResult registerTransaction(TransactionAiDTO dtoAi) {
        var request = TransactionRequest.fromAi(dtoAi);
        var chatId = 1L;
        telegramContext.setChatId(chatId);
        try {
            var response = executeWithRetry(
                    chatId,
                    () -> financeClient.createTransaction(request)
            );
            if (!response.isValid())  {
                return returnsError(400, "Falha ao capturar dados de retorno do serviço externo", List.of());
            }

            holderResult.set(
                    ToolResult.ok(
                            201, "Transação registrada com sucesso! ID: " + response.data().id())
            );
            return holderResult.get();

        } catch (ApiClientException ex) {
            return returnsError(ex.getStatusCode(), ex.getMessage(), ex.getListOfErrors());
        }
    }



    @Override
    @Tool(name = "list-transactions-by-period", description = "Lista transações do usuário com um período base")
    public List<TransactionResponse> listTransactionsByPeriod() {
        throw new UnsupportedOperationException("Método não implementado");
    }

    public List<CategoryDTO> listCategories() {
        var chatId = 1L;
        telegramContext.setChatId(chatId);
        var response = executeWithRetry(chatId, financeClient::getCategories);

        if (response != null && response.isValid() && response.success())
            return response.data();

        return Collections.emptyList();
    }

    private <T> T executeWithRetry(long chatId, Supplier<T> action) {
        try {
            return action.get();
        } catch (ApiUnauthorizedException ex) {
            var result = authService.refreshToken(chatId);

            if(result == RennovationResult.SUCCESS)
                return action.get();

            throw ex;
        }
    }

//    private ToolResult executeCreateTransaction(TransactionRequest request, boolean retry) {
//        try {
//            var response = financeClient.createTransaction(request);
//            if (!response.isValid()) {
//                return ToolResult.error(400, "Falha ao capturar dados de retorno do serviço externo", List.of());
//            }
//            holderResult.set(
//                    ToolResult.ok(201, "Transação registrada com sucesso! ID: " + response.data().id())
//            );
//
//            return holderResult.get();
//
//        } catch (ApiUnauthorizedException ex) {
//            if (retry) {
//                var refreshed = authService.refreshToken(1L);
//                if(refreshed == RennovationResult.SUCCESS) {
//                    return executeCreateTransaction(request, false);
//                }
//            }
//            return returnsError(ex.getStatusCode(), ex.getMessage(), ex.getListOfErrors());
//        } catch (ApiClientException ex) {
//            return returnsError(ex.getStatusCode(), ex.getMessage(), ex.getListOfErrors());
//        }
//    }

    private ToolResult returnsError(int code, String errorMessage, List<String> listErrorMessages) {
        holderResult.set(ToolResult.error(
                code, errorMessage, listErrorMessages
        ));

        return holderResult.get();
    }
}
