package com.eneik.generated.dtos;

import java.util.List;

public class TelegramSubscriptionRequestDto {
    private String chatId;
    private List<String> topicPreferences;

    public TelegramSubscriptionRequestDto() {}

    public TelegramSubscriptionRequestDto(String chatId, List<String> topicPreferences) {
        this.chatId = chatId;
        this.topicPreferences = topicPreferences;
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public List<String> getTopicPreferences() { return topicPreferences; }
    public void setTopicPreferences(List<String> topicPreferences) { this.topicPreferences = topicPreferences; }
}
