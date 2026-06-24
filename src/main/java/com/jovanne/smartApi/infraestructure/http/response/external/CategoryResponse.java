package com.jovanne.smartApi.infraestructure.http.response.external;

import com.jovanne.smartApi.application.dtos.CategoryDTO;

import java.util.List;

public record CategoryResponse (List<CategoryDTO> categories) implements BaseResponse {

    @Override
    public boolean isValid() {
        return categories != null;
    }
}
