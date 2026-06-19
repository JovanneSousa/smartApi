package com.jovanne.smartApi.infraestructure.http.clients;

import com.jovanne.smartApi.infraestructure.config.ApiClientConfig;
import com.jovanne.smartApi.infraestructure.config.FinanceClientConfig;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.external.ExternalApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.external.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "Finance",
        url = "${api.financeira.url}",
        configuration = {
                FinanceClientConfig.class,
                ApiClientConfig.class
        }
)
public interface IFinanceClient {

    @PostMapping("api/transacoes/novo")
    ExternalApiResponse<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request);

    @GetMapping("api/transacoes/periodo")
    List<TransactionResponse> transactionsByPeriod();
}
