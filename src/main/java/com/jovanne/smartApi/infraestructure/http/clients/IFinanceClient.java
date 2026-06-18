package com.jovanne.smartApi.infraestructure.http.clients;

import com.jovanne.smartApi.infraestructure.config.ApiClientConfig;
import com.jovanne.smartApi.infraestructure.http.request.TransactionRequest;
import com.jovanne.smartApi.infraestructure.http.response.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "Finance",
        url = "${api.financeira.url}",
        configuration = ApiClientConfig.class
)
public interface IFinanceClient {

    @PostMapping("api/transacoes/novo")
    TransactionResponse createTransaction(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String header);

    @GetMapping("api/transacoes/periodo")
    List<TransactionResponse> transactionsByPeriod();
}
