package com.eneik.generated.dtos;

public class IndexingFailureDto {
    private String id;
    private String reason;

    public IndexingFailureDto() {}

    public IndexingFailureDto(String id, String reason) {
        this.id = id;
        this.reason = reason;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
