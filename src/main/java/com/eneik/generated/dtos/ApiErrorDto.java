package com.eneik.generated.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorDto {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;

    public ApiErrorDto() {}

    public ApiErrorDto(String code, String message, LocalDateTime timestamp, List<String> details) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
