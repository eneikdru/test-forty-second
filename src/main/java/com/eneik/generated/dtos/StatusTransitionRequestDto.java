package com.eneik.generated.dtos;

import java.time.LocalDateTime;

public class StatusTransitionRequestDto {
    private String currentStatus;
    private String targetStatus;
    private LocalDateTime timestamp;

    public StatusTransitionRequestDto() {}

    public StatusTransitionRequestDto(String currentStatus, String targetStatus, LocalDateTime timestamp) {
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
        this.timestamp = timestamp;
    }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getTargetStatus() { return targetStatus; }
    public void setTargetStatus(String targetStatus) { this.targetStatus = targetStatus; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
