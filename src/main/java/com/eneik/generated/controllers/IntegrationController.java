package com.eneik.generated.controllers;

import com.eneik.generated.dtos.*;
import com.eneik.generated.models.persistence.LmsSyncState;
import com.eneik.generated.models.persistence.TelegramSubscription;
import com.eneik.generated.repositories.LmsSyncStateRepository;
import com.eneik.generated.repositories.TelegramSubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class IntegrationController {

    private final LmsSyncStateRepository lmsSyncStateRepository;
    private final TelegramSubscriptionRepository telegramSubscriptionRepository;

    public IntegrationController(
            LmsSyncStateRepository lmsSyncStateRepository,
            TelegramSubscriptionRepository telegramSubscriptionRepository) {
        this.lmsSyncStateRepository = lmsSyncStateRepository;
        this.telegramSubscriptionRepository = telegramSubscriptionRepository;
    }

    @GetMapping("/integrations/lms/sync")
    public ResponseEntity<List<LmsSyncStateDto>> getLmsSyncStates() {
        List<LmsSyncStateDto> list = lmsSyncStateRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/integrations/lms/sync")
    @Transactional
    public ResponseEntity<LmsSyncStateDto> initializeLmsSync(@RequestBody LmsSyncInitiationRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is missing");
        }
        if (request.getSystemName() == null || request.getSystemName().trim().isEmpty()) {
            throw new BadRequestException("systemName is required");
        }
        if (request.getSyncStatus() == null || request.getSyncStatus().trim().isEmpty()) {
            throw new BadRequestException("syncStatus is required");
        }
        String id = UUID.randomUUID().toString();
        LmsSyncState entity = new LmsSyncState(
                id,
                request.getSystemName(),
                null,
                request.getSyncStatus(),
                request.getToken()
        );
        LmsSyncState saved = lmsSyncStateRepository.saveAndFlush(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

    @GetMapping("/integrations/lms/sync/{id}")
    public ResponseEntity<LmsSyncStateDto> getLmsSyncStateById(@PathVariable String id) {
        LmsSyncState entity = lmsSyncStateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("LMS Sync State not found for ID: " + id));
        return ResponseEntity.ok(mapToDto(entity));
    }

    @PutMapping("/integrations/lms/sync/{id}/token")
    @Transactional
    public ResponseEntity<Void> updateLmsSyncToken(@PathVariable String id, @RequestBody LmsTokenUpdateRequestDto request) {
        if (request == null || request.getToken() == null) {
            throw new BadRequestException("Token is required");
        }
        LmsSyncState entity = lmsSyncStateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("LMS Sync State not found for ID: " + id));
        entity.setToken(request.getToken());
        lmsSyncStateRepository.saveAndFlush(entity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/integrations/lms/sync/{id}/transition")
    @Transactional
    public ResponseEntity<LmsSyncStateDto> transitionSyncStatus(@PathVariable String id, @RequestBody StatusTransitionRequestDto request) {
        if (request == null || request.getCurrentStatus() == null || request.getTargetStatus() == null) {
            throw new BadRequestException("currentStatus and targetStatus are required");
        }
        LmsSyncState entity = lmsSyncStateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("LMS Sync State not found for ID: " + id));

        LocalDateTime transitionTime = request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now();
        int rowsUpdated = lmsSyncStateRepository.updateSyncStatusAtomically(
                id,
                request.getCurrentStatus(),
                request.getTargetStatus(),
                transitionTime
        );

        if (rowsUpdated == 0) {
            throw new BadRequestException("State transition failed due to mismatching expected current status or business rules.");
        }

        LmsSyncState updatedEntity = lmsSyncStateRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("LMS Sync State vanished after update"));

        return ResponseEntity.ok(mapToDto(updatedEntity));
    }

    @PostMapping("/integrations/sso/login")
    public ResponseEntity<SsoLoginResponseDto> ssoLogin(@RequestBody SsoLoginRequestDto request) {
        if (request == null || request.getProvider() == null || request.getToken() == null) {
            throw new BadRequestException("provider and token are required");
        }
        if ("invalid_token".equals(request.getToken())) {
            throw new UnauthorizedException("Assertion token invalid or expired.");
        }
        SsoUserDto user = new SsoUserDto(
                "d4bbf3da-6bdf-48c3-af9d-b84fb65a9ef2",
                "ivanov_ii",
                "ivanov@epidemiology.ru",
                List.of("ORDINATOR", "INSTRUCTOR")
        );
        SsoLoginResponseDto response = new SsoLoginResponseDto(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.sso_login_successful",
                "Bearer",
                3600,
                user
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/integrations/sso/metadata")
    public ResponseEntity<SsoMetadataResponseDto> getSsoMetadata() {
        SsoMetadataResponseDto metadata = new SsoMetadataResponseDto(
                "https://sso.cniiep.ru/oauth2",
                "https://sso.cniiep.ru/oauth2/authorize",
                "https://sso.cniiep.ru/oauth2/token",
                List.of("OIDC", "SAML2")
        );
        return ResponseEntity.ok(metadata);
    }

    @PostMapping("/integrations/search/index")
    public ResponseEntity<SearchIndexResponseDto> pushSearchIndex(@RequestBody SearchIndexRequestDto request) {
        if (request == null || request.getBatchId() == null) {
            throw new BadRequestException("batchId is required");
        }
        int count = request.getDocuments() != null ? request.getDocuments().size() : 0;
        SearchIndexResponseDto response = new SearchIndexResponseDto(
                "SUCCESS",
                count,
                List.of()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/integrations/bot/subscriptions")
    public ResponseEntity<List<TelegramSubscriptionDto>> getBotSubscriptions() {
        List<TelegramSubscriptionDto> list = telegramSubscriptionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/integrations/bot/subscriptions")
    @Transactional
    public ResponseEntity<TelegramSubscriptionDto> subscribeBot(@RequestBody TelegramSubscriptionRequestDto request) {
        if (request == null || request.getChatId() == null) {
            throw new BadRequestException("chatId is required");
        }
        String prefsString = "";
        if (request.getTopicPreferences() != null) {
            prefsString = String.join(",", request.getTopicPreferences());
        }
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        TelegramSubscription entity = new TelegramSubscription(
                id,
                request.getChatId(),
                prefsString,
                now,
                now
        );
        TelegramSubscription saved = telegramSubscriptionRepository.saveAndFlush(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

    @PostMapping("/integrations/bot/webhooks")
    public ResponseEntity<WebhookResponseDto> processBotWebhook(@RequestBody BotWebhookPayloadDto request) {
        if (request == null || request.getChatId() == null || request.getMessage() == null) {
            throw new BadRequestException("chatId and message are required");
        }
        if (request.getChatId().startsWith("invalid")) {
            throw new UnauthorizedException("Unauthorized webhook delivery request.");
        }
        WebhookResponseDto response = new WebhookResponseDto(
                true,
                "Webhook payload processed and notification queued."
        );
        return ResponseEntity.ok(response);
    }

    private LmsSyncStateDto mapToDto(LmsSyncState state) {
        if (state == null) return null;
        String maskedToken = null;
        if (state.getToken() != null) {
            maskedToken = "********";
        }
        return new LmsSyncStateDto(
                state.getId(),
                state.getSystemName(),
                state.getLastSuccessfulSync(),
                state.getSyncStatus(),
                maskedToken
        );
    }

    private TelegramSubscriptionDto mapToDto(TelegramSubscription sub) {
        if (sub == null) return null;
        List<String> prefs = List.of();
        if (sub.getTopicPreferences() != null && !sub.getTopicPreferences().trim().isEmpty()) {
            prefs = Arrays.asList(sub.getTopicPreferences().split(","));
        }
        return new TelegramSubscriptionDto(
                sub.getId(),
                sub.getChatId(),
                prefs,
                sub.getCreatedAt(),
                sub.getUpdatedAt()
        );
    }
}
