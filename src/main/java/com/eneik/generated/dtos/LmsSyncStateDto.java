package com.eneik.generated.dtos;

import java.time.LocalDateTime;

public class LmsSyncStateDto {
    private String id;
    private String systemName;
    private LocalDateTime lastSuccessfulSync;
    private String syncStatus;
    private String token;

    public LmsSyncStateDto() {}

    public LmsSyncStateDto(String id, String systemName, LocalDateTime lastSuccessfulSync, String syncStatus, String token) {
        this.id = id;
        this.systemName = systemName;
        this.lastSuccessfulSync = lastSuccessfulSync;
        this.syncStatus = syncStatus;
        this.token = token;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public LocalDateTime getLastSuccessfulSync() { return lastSuccessfulSync; }
    public void setLastSuccessfulSync(LocalDateTime lastSuccessfulSync) { this.lastSuccessfulSync = lastSuccessfulSync; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
