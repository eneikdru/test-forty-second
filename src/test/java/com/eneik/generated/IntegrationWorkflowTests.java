package com.eneik.generated;

import com.eneik.generated.models.persistence.LmsSyncState;
import com.eneik.generated.models.persistence.TelegramSubscription;
import com.eneik.generated.repositories.LmsSyncStateRepository;
import com.eneik.generated.repositories.TelegramSubscriptionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IntegrationWorkflowTests {

    @Autowired
    private TelegramSubscriptionRepository subscriptionRepository;

    @Autowired
    private LmsSyncStateRepository syncStateRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        subscriptionRepository.deleteAll();
        syncStateRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testTelegramSubscriptionSecurity() {
        String id = "sub-test-id-12345";
        String plainChatId = "chat_id_777888999";
        String plainTopicPreferences = "epidem,infectious,pediatrics";

        TelegramSubscription subscription = new TelegramSubscription(
                id,
                plainChatId,
                plainTopicPreferences,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Save subscription
        subscriptionRepository.saveAndFlush(subscription);

        // Clear the entity manager persistence context to ensure we read from database
        entityManager.clear();

        // 1. Verify entity-level decryption (retrieved is transparently decrypted)
        TelegramSubscription retrieved = subscriptionRepository.findById(id).orElse(null);
        assertNotNull(retrieved);
        assertEquals(plainChatId, retrieved.getChatId());
        assertEquals(plainTopicPreferences, retrieved.getTopicPreferences());

        // 2. Verify database-level secure encryption (direct native SQL shows encrypted content)
        String encryptedChatId = (String) entityManager.createNativeQuery(
                "SELECT chat_id FROM telegram_subscription WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();

        String encryptedPreferences = (String) entityManager.createNativeQuery(
                "SELECT topic_preferences FROM telegram_subscription WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();

        assertNotEquals(plainChatId, encryptedChatId);
        assertNotEquals(plainTopicPreferences, encryptedPreferences);

        // Check that they contain recognizable cipher text (represented as hexadecimal/obfuscated content)
        assertTrue(encryptedChatId.length() > plainChatId.length());
        assertTrue(encryptedPreferences.length() > plainTopicPreferences.length());
    }

    @Test
    @Transactional
    public void testLmsSyncStateAndAtomicUpdates() {
        String id = "lms-sync-id-abcde";
        String systemName = "LMS-EPIDEMIOLOGY-SYSTEM";
        LocalDateTime lastSync = LocalDateTime.of(2026, 8, 5, 14, 0, 0);
        String initialStatus = "IDLE";
        String plainToken = "super-secret-lms-oauth-token-val-9988";

        LmsSyncState syncState = new LmsSyncState(
                id,
                systemName,
                lastSync,
                initialStatus,
                plainToken
        );

        // Save state
        syncStateRepository.saveAndFlush(syncState);
        entityManager.clear();

        // 1. Verify sync state successfully recorded & token is encrypted in database
        LmsSyncState retrieved = syncStateRepository.findById(id).orElse(null);
        assertNotNull(retrieved);
        assertEquals(systemName, retrieved.getSystemName());
        assertEquals(lastSync, retrieved.getLastSuccessfulSync());
        assertEquals(plainToken, retrieved.getToken());

        String encryptedToken = (String) entityManager.createNativeQuery(
                "SELECT token FROM lms_sync_state WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertNotEquals(plainToken, encryptedToken);

        // 2. Perform and verify atomically-guarded database update for state transition
        LocalDateTime newSyncTime = LocalDateTime.of(2026, 8, 5, 15, 30, 0);
        int rowsUpdated = syncStateRepository.updateSyncStatusAtomically(id, "IDLE", "SYNCING", newSyncTime);
        assertEquals(1, rowsUpdated);

        entityManager.clear();
        LmsSyncState updated = syncStateRepository.findById(id).orElse(null);
        assertNotNull(updated);
        assertEquals("SYNCING", updated.getSyncStatus());
        assertEquals(newSyncTime, updated.getLastSuccessfulSync());

        // 3. Verify that updating with mismatching expected status fails atomically (concurrency guard)
        int failedUpdateRows = syncStateRepository.updateSyncStatusAtomically(id, "IDLE", "FINISHED", LocalDateTime.now());
        assertEquals(0, failedUpdateRows); // Should affect 0 rows because current status is "SYNCING", not "IDLE"

        entityManager.clear();
        LmsSyncState finalState = syncStateRepository.findById(id).orElse(null);
        assertNotNull(finalState);
        assertEquals("SYNCING", finalState.getSyncStatus()); // Status must remain "SYNCING"
    }
}
