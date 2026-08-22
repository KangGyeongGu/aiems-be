CREATE TABLE member
(
    member_id   BIGINT        PRIMARY KEY AUTO_INCREMENT,
    login_id    VARCHAR(255)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    role        VARCHAR(31)   NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,

    CONSTRAINT uk_member_login_id UNIQUE (login_id)
);
