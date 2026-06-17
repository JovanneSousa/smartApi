package com.jovanne.smartApi.infraestructure.http.clients;

import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "Auth",
        url = "${api.auth.url}"
)
public interface IAuthClient {

    @PostMapping("api/login")
    boolean executeLogin(LoginRequest request);
}
