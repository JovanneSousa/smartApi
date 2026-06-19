package com.jovanne.smartApi.infraestructure.http.response.external;

import com.jovanne.smartApi.domain.entities.Token;

public record LoginResponse (Token token) implements BaseResponse {
    @Override
    public boolean isValid() {
        return token != null &&
                token().isValid() &&
                token.userToken().isValid();
    }

}
