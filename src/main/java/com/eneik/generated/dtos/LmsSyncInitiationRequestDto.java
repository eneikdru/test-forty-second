package com.eneik.generated.dtos;

public class LmsSyncInitiationRequestDto {
    private String systemName;
    private String syncStatus;
    private String token;

    public LmsSyncInitiationRequestDto() {}

    public LmsSyncInitiationRequestDto(String systemName, String syncStatus, String token) {
        this.systemName = systemName;
        this.syncStatus = syncStatus;
        this.token = token;
    }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
