package com.eneik.generated.repositories;

import com.eneik.generated.models.persistence.LmsSyncState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LmsSyncStateRepository extends JpaRepository<LmsSyncState, String> {

    /**
     * Atomically updates sync state based on the current status to avoid concurrency/race conditions.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE LmsSyncState s SET s.syncStatus = :newStatus, s.lastSuccessfulSync = :lastSuccessfulSync " +
           "WHERE s.id = :id AND s.syncStatus = :expectedStatus")
    int updateSyncStatusAtomically(
            @Param("id") String id,
            @Param("expectedStatus") String expectedStatus,
            @Param("newStatus") String newStatus,
            @Param("lastSuccessfulSync") LocalDateTime lastSuccessfulSync
    );
}
