package com.eneik.generated.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class TelegramSubscriptionDto {
    private String id;
    private String chatId;
    private List<String> topicPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TelegramSubscriptionDto() {}

    public TelegramSubscriptionDto(String id, String chatId, List<String> topicPreferences, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.topicPreferences = topicPreferences;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public List<String> getTopicPreferences() { return topicPreferences; }
    public void setTopicPreferences(List<String> topicPreferences) { this.topicPreferences = topicPreferences; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
