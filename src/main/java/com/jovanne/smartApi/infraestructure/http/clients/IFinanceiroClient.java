package com.jovanne.smartApi.infraestructure.http.clients;

import com.jovanne.smartApi.infraestructure.config.FinanceiroClientConfig;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
        name = "Financeiro",
        url = "${api.financeira.url}",
        configuration = FinanceiroClientConfig.class
)
public interface IFinanceiroClient {

    @PostMapping("/transacoes/novo")
    TransactionResponse createTransaction(TransactionRequest request);

    @GetMapping("/transacoes/periodo")
    List<TransactionResponse> transactionsByPeriod();
}
