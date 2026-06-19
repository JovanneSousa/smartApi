package com.jovanne.smartApi.infraestructure.http.response.internal;


public record InternalApiResponse<T >(boolean success, T data) {
}
