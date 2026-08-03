CREATE TABLE member
(
    member_id   BIGINT        PRIMARY KEY AUTO_INCREMENT,
    member_type VARCHAR(31)   NOT NULL,
    login_id    VARCHAR(255)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,

    CONSTRAINT uk_member_login_id UNIQUE (login_id)
);

CREATE TABLE ambulance
(
    member_id         BIGINT        NOT NULL PRIMARY KEY,
    device_id         VARCHAR(255)  NOT NULL,
    fire_station_name VARCHAR(255)  NOT NULL,
    license_plate     VARCHAR(255)  NOT NULL,
    jurisdiction      VARCHAR(255)  NOT NULL,
    operation_status  VARCHAR(255)  NULL,

    CONSTRAINT uk_ambulance_device_id UNIQUE (device_id),
    CONSTRAINT fk_ambulance_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE control_system
(
    member_id                  BIGINT          NOT NULL PRIMARY KEY,
    system_name                VARCHAR(255)    NOT NULL,
    control_system_coordinates POINT SRID 4326 NOT NULL,
    control_system_address     VARCHAR(255)    NOT NULL,

    CONSTRAINT fk_control_system_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE hospital
(
    member_id            BIGINT          NOT NULL PRIMARY KEY,
    hospital_address     VARCHAR(255)    NOT NULL,
    hospital_name        VARCHAR(255)    NOT NULL,
    hpid                 VARCHAR(255)    NOT NULL COMMENT '국립중앙의료원 제공 코드',
    hospital_coordinates POINT SRID 4326 NOT NULL,
    hospital_level       INT             NULL,

    CONSTRAINT fk_hospital_member_id FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE SPATIAL INDEX idx_hospital_coordinates ON hospital (hospital_coordinates);

CREATE TABLE hospital_specialty
(
    hospital_specialty_id BIGINT        PRIMARY KEY AUTO_INCREMENT,
    hospital_id           BIGINT        NOT NULL,
    specialty             VARCHAR(255)  NOT NULL,
    created_at            DATETIME(6)   NOT NULL,
    updated_at            DATETIME(6)   NOT NULL
);
