package com.jovanne.smartApi.infraestructure.http.clients;

import com.jovanne.smartApi.domain.entities.Token;
import com.jovanne.smartApi.infraestructure.config.ApiClientConfig;
import com.jovanne.smartApi.infraestructure.http.request.LoginRequest;
import com.jovanne.smartApi.infraestructure.http.request.RefreshTokenRequest;
import com.jovanne.smartApi.infraestructure.http.response.ApiResponse;
import com.jovanne.smartApi.infraestructure.http.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "Auth",
        url = "${api.auth.url}",
        configuration = ApiClientConfig.class
)
public interface IAuthClient {

    @PostMapping("api/auth/login")
    ApiResponse<LoginResponse> executeLogin(LoginRequest request);

    @PostMapping("api/auth/refresh-token")
    ApiResponse<String> refreshToken(RefreshTokenRequest request);
}
