package com.eneik.generated;

import com.eneik.generated.controllers.BadRequestException;
import com.eneik.generated.controllers.IntegrationController;
import com.eneik.generated.controllers.UnauthorizedException;
import com.eneik.generated.dtos.*;
import com.eneik.generated.models.persistence.LmsSyncState;
import com.eneik.generated.models.persistence.TelegramSubscription;
import com.eneik.generated.repositories.LmsSyncStateRepository;
import com.eneik.generated.repositories.TelegramSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LmsSyncAndNotificationTest {

    private LmsSyncStateRepository lmsSyncStateRepository;
    private TelegramSubscriptionRepository telegramSubscriptionRepository;
    private IntegrationController integrationController;

    @BeforeEach
    public void setUp() {
        lmsSyncStateRepository = mock(LmsSyncStateRepository.class);
        telegramSubscriptionRepository = mock(TelegramSubscriptionRepository.class);
        integrationController = new IntegrationController(lmsSyncStateRepository, telegramSubscriptionRepository);
    }

    // --- Happy Path (Positive Scenarios) ---

    @Test
    public void testLmsSyncInitiationSuccess() {
        LmsSyncInitiationRequestDto request = new LmsSyncInitiationRequestDto(
                "LMS-CANVAS",
                "IDLE",
                "super-secret-token"
        );

        LmsSyncState mockSavedState = new LmsSyncState(
                "generated-id-123",
                "LMS-CANVAS",
                null,
                "IDLE",
                "super-secret-token"
        );

        when(lmsSyncStateRepository.saveAndFlush(any(LmsSyncState.class))).thenReturn(mockSavedState);

        ResponseEntity<LmsSyncStateDto> response = integrationController.initializeLmsSync(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("generated-id-123", response.getBody().getId());
        assertEquals("LMS-CANVAS", response.getBody().getSystemName());
        assertEquals("IDLE", response.getBody().getSyncStatus());
        assertEquals("********", response.getBody().getToken()); // Secure masking
    }

    @Test
    public void testTelegramSubscriptionSuccess() {
        TelegramSubscriptionRequestDto request = new TelegramSubscriptionRequestDto(
                "chat_id_555",
                List.of("ординатура", "аспирантура")
        );

        TelegramSubscription mockSavedSub = new TelegramSubscription(
                "sub-id-555",
                "chat_id_555",
                "ординатура,аспирантура",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(telegramSubscriptionRepository.saveAndFlush(any(TelegramSubscription.class))).thenReturn(mockSavedSub);

        ResponseEntity<TelegramSubscriptionDto> response = integrationController.subscribeBot(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("chat_id_555", response.getBody().getChatId());
        assertEquals(List.of("ординатура", "аспирантура"), response.getBody().getTopicPreferences());
    }

    @Test
    public void testBotWebhookDeliverySuccess() {
        BotWebhookPayloadDto payload = new BotWebhookPayloadDto(
                "msg-uuid-999",
                "DOCUMENT_UPDATED",
                "chat_id_999",
                "ординатура",
                "Syllabus updated",
                LocalDateTime.now()
        );

        ResponseEntity<WebhookResponseDto> response = integrationController.processBotWebhook(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Webhook payload processed and notification queued.", response.getBody().getMessage());
    }

    // --- Negative Scenarios (Error Handling & Invalid Inputs) ---

    @Test
    public void testLmsSyncInitiationValidationFails() {
        // 1. systemName is empty
        LmsSyncInitiationRequestDto requestMissingName = new LmsSyncInitiationRequestDto(
                "",
                "IDLE",
                "token"
                );
        assertThrows(BadRequestException.class, () -> {
            integrationController.initializeLmsSync(requestMissingName);
        });

        // 2. syncStatus is null
        LmsSyncInitiationRequestDto requestMissingStatus = new LmsSyncInitiationRequestDto(
                "LMS",
                null,
                "token"
        );
        assertThrows(BadRequestException.class, () -> {
            integrationController.initializeLmsSync(requestMissingStatus);
        });
    }

    @Test
    public void testTelegramSubscriptionValidationFails() {
        // chatId is null
        TelegramSubscriptionRequestDto requestMissingChat = new TelegramSubscriptionRequestDto(
                null,
                List.of("ординатура")
        );
        assertThrows(BadRequestException.class, () -> {
            integrationController.subscribeBot(requestMissingChat);
        });
    }

    @Test
    public void testBotWebhookDeliveryAuthorization() {
        // Unauthorized invalid chat_id prefix "invalid"
        BotWebhookPayloadDto invalidPayload = new BotWebhookPayloadDto(
                "msg-123",
                "DOCUMENT_UPDATED",
                "invalid_chat_123",
                "ординатура",
                "Update notification",
                LocalDateTime.now()
        );

        assertThrows(UnauthorizedException.class, () -> {
            integrationController.processBotWebhook(invalidPayload);
        });
    }

    // --- Boundary Conditions ---

    @Test
    public void testLmsSyncStateTransitionAtomicOptimisticFail() {
        String id = "lms-sync-uuid";
        StatusTransitionRequestDto request = new StatusTransitionRequestDto(
                "IDLE",
                "SYNCING",
                LocalDateTime.now()
        );

        LmsSyncState mockState = new LmsSyncState(id, "Canvas", LocalDateTime.now(), "SYNCING", "token");
        when(lmsSyncStateRepository.findById(id)).thenReturn(Optional.of(mockState));

        // Let update fail (returns 0 rows because expected state IDLE doesn't match database state SYNCING)
        when(lmsSyncStateRepository.updateSyncStatusAtomically(anyString(), anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(0);

        assertThrows(BadRequestException.class, () -> {
            integrationController.transitionSyncStatus(id, request);
        });
    }
}
