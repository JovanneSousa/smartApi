package com.jovanne.smartApi.application.dtos;

import java.util.List;
import java.util.stream.Collectors;

public record CategoryDTO(String id, String name) {
    public static String formatSystemPrompt(List<CategoryDTO> cat) {
        return cat.stream()
                .map(c -> c.id + " - " + c.name)
                .collect(Collectors.joining(", "));
    }
}
