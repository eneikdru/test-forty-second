package com.eneik.generated.models.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lms_sync_state")
public class LmsSyncState {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "system_name", nullable = false, unique = true)
    private String systemName;

    @Column(name = "last_successful_sync")
    private LocalDateTime lastSuccessfulSync;

    @Column(name = "sync_status", nullable = false, length = 50)
    private String syncStatus;

    @Column(length = 1000)
    @Convert(converter = SecureStringConverter.class)
    private String token;

    public LmsSyncState() {}

    public LmsSyncState(String id, String systemName, LocalDateTime lastSuccessfulSync, String syncStatus, String token) {
        this.id = id;
        this.systemName = systemName;
        this.lastSuccessfulSync = lastSuccessfulSync;
        this.syncStatus = syncStatus;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public LocalDateTime getLastSuccessfulSync() {
        return lastSuccessfulSync;
    }

    public void setLastSuccessfulSync(LocalDateTime lastSuccessfulSync) {
        this.lastSuccessfulSync = lastSuccessfulSync;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
