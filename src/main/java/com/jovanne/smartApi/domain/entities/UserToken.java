package com.jovanne.smartApi.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public record UserToken(String id, String name) {
    public boolean isValid() {
        return id != null &&
                name != null;
    }
}
