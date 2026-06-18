package com.jovanne.smartApi.infraestructure.persistense.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_users")
public class TelegramUser {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
