package com.jovanne.smartApi.infraestructure.persistense.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telegram_users")
@Data
public class TelegramUser {
    @Id
    @GeneratedValue()
    private UUID id;

    @Column(unique = true, nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
