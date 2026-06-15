package com.jovanne.smartApi.application;

public interface BaseUseCase<T, R> {
    T execute(R param);
}
