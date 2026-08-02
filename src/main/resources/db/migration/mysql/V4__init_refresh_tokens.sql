CREATE TABLE refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT NOT NULL,
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    expires_at  DATETIME(6)  NOT NULL,
    used_at     DATETIME(6)  NULL,
    created_at  DATETIME(6)  NOT NULL,
    CONSTRAINT fk_refresh_token_member FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE INDEX idx_refresh_token_member ON refresh_tokens (member_id);
CREATE INDEX idx_refresh_token_expires_at ON refresh_tokens (expires_at);
