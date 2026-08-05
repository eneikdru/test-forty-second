package com.eneik.generated;

import com.eneik.generated.controllers.IntegrationController;
import com.eneik.generated.controllers.IntegrationExceptionHandler;
import com.eneik.generated.dtos.*;
import com.eneik.generated.models.persistence.LmsSyncState;
import com.eneik.generated.models.persistence.TelegramSubscription;
import com.eneik.generated.repositories.LmsSyncStateRepository;
import com.eneik.generated.repositories.TelegramSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class IntegrationWorkflowControllerTests {

    @Autowired
    private LmsSyncStateRepository lmsSyncStateRepository;

    @Autowired
    private TelegramSubscriptionRepository telegramSubscriptionRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        lmsSyncStateRepository.deleteAll();
        telegramSubscriptionRepository.deleteAll();

        IntegrationController controller = new IntegrationController(
                lmsSyncStateRepository,
                telegramSubscriptionRepository
        );

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new IntegrationExceptionHandler())
                .setMessageConverters(converter)
                .build();
    }

    @Test
    @Transactional
    public void testLmsSyncStateLifecycle() throws Exception {
        // 1. Get all sync states (initially empty)
        mockMvc.perform(get("/api/v1/integrations/lms/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        // 2. Initialize a new LMS sync state
        LmsSyncInitiationRequestDto initRequest = new LmsSyncInitiationRequestDto(
                "LMS-EPIDEMIOLOGY-SYSTEM",
                "IDLE",
                "super-secret-lms-oauth-token-val-9988"
        );

        String responseBody = mockMvc.perform(post("/api/v1/integrations/lms/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.systemName").value("LMS-EPIDEMIOLOGY-SYSTEM"))
                .andExpect(jsonPath("$.syncStatus").value("IDLE"))
                .andExpect(jsonPath("$.token").value("********")) // Check that token is masked
                .andReturn().getResponse().getContentAsString();

        LmsSyncStateDto createdDto = objectMapper.readValue(responseBody, LmsSyncStateDto.class);
        String syncId = createdDto.getId();
        assertNotNull(syncId);

        // 3. Fetch LMS Sync State by ID
        mockMvc.perform(get("/api/v1/integrations/lms/sync/{id}", syncId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(syncId))
                .andExpect(jsonPath("$.systemName").value("LMS-EPIDEMIOLOGY-SYSTEM"))
                .andExpect(jsonPath("$.syncStatus").value("IDLE"))
                .andExpect(jsonPath("$.token").value("********"));

        // 4. Update secure credentials/token
        LmsTokenUpdateRequestDto tokenUpdateRequest = new LmsTokenUpdateRequestDto("new-rotated-super-secret-token");
        mockMvc.perform(put("/api/v1/integrations/lms/sync/{id}/token", syncId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenUpdateRequest)))
                .andExpect(status().isNoContent());

        // Verify that the token was updated in database (is decrypted transparently as new token)
        LmsSyncState dbState = lmsSyncStateRepository.findById(syncId).orElseThrow();
        assertEquals("new-rotated-super-secret-token", dbState.getToken());

        // 5. Test atomically-guarded transition status (concurrency guard verification)
        StatusTransitionRequestDto correctTransition = new StatusTransitionRequestDto(
                "IDLE",
                "SYNCING",
                LocalDateTime.of(2026, 8, 5, 15, 30, 0)
        );

        mockMvc.perform(post("/api/v1/integrations/lms/sync/{id}/transition", syncId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctTransition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.syncStatus").value("SYNCING"))
                .andExpect(jsonPath("$.lastSuccessfulSync").value("2026-08-05T15:30:00"));

        // Try transitioning from wrong starting state "IDLE" -> "COMPLETED" (should fail with HTTP 400 BadRequest)
        StatusTransitionRequestDto wrongTransition = new StatusTransitionRequestDto(
                "IDLE",
                "COMPLETED",
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/integrations/lms/sync/{id}/transition", syncId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongTransition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("State transition failed due to mismatching expected current status or business rules."));

        // 6. Test 404 for non-existent LMS state
        String randomUuid = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/v1/integrations/lms/sync/{id}", randomUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("LMS Sync State not found for ID: " + randomUuid));
    }

    @Test
    public void testSsoOperations() throws Exception {
        // 1. SSO Metadata
        mockMvc.perform(get("/api/v1/integrations/sso/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuer").value("https://sso.cniiep.ru/oauth2"))
                .andExpect(jsonPath("$.authorizationEndpoint").value("https://sso.cniiep.ru/oauth2/authorize"))
                .andExpect(jsonPath("$.tokenEndpoint").value("https://sso.cniiep.ru/oauth2/token"))
                .andExpect(jsonPath("$.activeProviders[0]").value("OIDC"))
                .andExpect(jsonPath("$.activeProviders[1]").value("SAML2"));

        // 2. SSO Successful Login
        SsoLoginRequestDto successLogin = new SsoLoginRequestDto("OIDC", "valid_oauth_assertion_code_112233");
        mockMvc.perform(post("/api/v1/integrations/sso/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(successLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.sso_login_successful"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.userId").value("d4bbf3da-6bdf-48c3-af9d-b84fb65a9ef2"))
                .andExpect(jsonPath("$.user.username").value("ivanov_ii"))
                .andExpect(jsonPath("$.user.email").value("ivanov@epidemiology.ru"))
                .andExpect(jsonPath("$.user.roles[0]").value("ORDINATOR"))
                .andExpect(jsonPath("$.user.roles[1]").value("INSTRUCTOR"));

        // 3. SSO Failed Login
        SsoLoginRequestDto failedLogin = new SsoLoginRequestDto("OIDC", "invalid_token");
        mockMvc.perform(post("/api/v1/integrations/sso/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failedLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Assertion token invalid or expired."));
    }

    @Test
    public void testSearchIndexing() throws Exception {
        IndexableDocumentDto doc = new IndexableDocumentDto(
                "DOC-1234",
                "Рабочая программа ординатуры",
                "Содержание программы включает изучение эпидемиологии...",
                "ординатура",
                List.of("эпидемиология", "рабочая программа"),
                List.of("ФБУН", "ЦНИИ", "РПД"),
                "PDF",
                LocalDateTime.of(2026, 8, 5, 12, 0, 0)
        );

        SearchIndexRequestDto indexRequest = new SearchIndexRequestDto(
                UUID.randomUUID().toString(),
                List.of(doc)
        );

        mockMvc.perform(post("/api/v1/integrations/search/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(indexRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.indexedCount").value(1))
                .andExpect(jsonPath("$.failures").isEmpty());
    }

    @Test
    @Transactional
    public void testTelegramBotSubscriptionsAndWebhook() throws Exception {
        // 1. Get empty active bot subscriptions
        mockMvc.perform(get("/api/v1/integrations/bot/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        // 2. Create subscription
        TelegramSubscriptionRequestDto subRequest = new TelegramSubscriptionRequestDto(
                "chat_id_777888999",
                List.of("ординатура", "аспирантура")
        );

        mockMvc.perform(post("/api/v1/integrations/bot/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.chatId").value("chat_id_777888999"))
                .andExpect(jsonPath("$.topicPreferences[0]").value("ординатура"))
                .andExpect(jsonPath("$.topicPreferences[1]").value("аспирантура"));

        // 3. Verify bot subscription list is now of size 1
        mockMvc.perform(get("/api/v1/integrations/bot/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].chatId").value("chat_id_777888999"));

        // 4. Test Webhook successful push
        BotWebhookPayloadDto payload = new BotWebhookPayloadDto(
                UUID.randomUUID().toString(),
                "DOCUMENT_UPDATED",
                "chat_id_777888999",
                "ординатура",
                "Обновлена рабочая программа по специальности 'Эпидемиология'!",
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/integrations/bot/webhooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Webhook payload processed and notification queued."));

        // 5. Test Webhook unauthorized invalid chat_id
        BotWebhookPayloadDto invalidPayload = new BotWebhookPayloadDto(
                UUID.randomUUID().toString(),
                "DOCUMENT_UPDATED",
                "invalid_chat_id",
                "ординатура",
                "Error trigger text",
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/v1/integrations/bot/webhooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Unauthorized webhook delivery request."));
    }
}
