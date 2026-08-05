CREATE TABLE telegram_subscription (
    id VARCHAR(36) PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL,
    topic_preferences VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE lms_sync_state (
    id VARCHAR(36) PRIMARY KEY,
    system_name VARCHAR(255) NOT NULL UNIQUE,
    last_successful_sync TIMESTAMP,
    sync_status VARCHAR(50) NOT NULL,
    token VARCHAR(1000)
);
