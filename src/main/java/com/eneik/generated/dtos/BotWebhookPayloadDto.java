package com.eneik.generated.dtos;

import java.time.LocalDateTime;

public class BotWebhookPayloadDto {
    private String eventId;
    private String eventType;
    private String chatId;
    private String topic;
    private String message;
    private LocalDateTime timestamp;

    public BotWebhookPayloadDto() {}

    public BotWebhookPayloadDto(String eventId, String eventType, String chatId, String topic, String message, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.chatId = chatId;
        this.topic = topic;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
