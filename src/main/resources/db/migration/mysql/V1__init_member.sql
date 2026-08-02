CREATE TABLE members (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255),
    username    VARCHAR(255),
    role        VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL
);
