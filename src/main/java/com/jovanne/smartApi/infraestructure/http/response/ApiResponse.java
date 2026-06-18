package com.jovanne.smartApi.infraestructure.http.response;

import java.util.Optional;

public record ApiResponse<T>(boolean success, T data) {
}
