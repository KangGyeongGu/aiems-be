CREATE TABLE social_accounts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    provider    VARCHAR(20)  NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    CONSTRAINT uk_social_provider UNIQUE (provider, provider_id),
    CONSTRAINT fk_social_account_member FOREIGN KEY (member_id) REFERENCES members (id)
);
