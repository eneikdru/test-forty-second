package com.eneik.generated.models.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telegram_subscription")
public class TelegramSubscription {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "chat_id", nullable = false)
    @Convert(converter = SecureStringConverter.class)
    private String chatId;

    @Column(name = "topic_preferences", nullable = false, length = 1000)
    @Convert(converter = SecureStringConverter.class)
    private String topicPreferences;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TelegramSubscription() {}

    public TelegramSubscription(String id, String chatId, String topicPreferences, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.topicPreferences = topicPreferences;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getTopicPreferences() {
        return topicPreferences;
    }

    public void setTopicPreferences(String topicPreferences) {
        this.topicPreferences = topicPreferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
