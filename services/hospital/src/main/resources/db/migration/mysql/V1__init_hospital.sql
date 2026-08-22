CREATE TABLE hospital
(
    hospital_id          BIGINT          PRIMARY KEY,
    hospital_name        VARCHAR(255)    NOT NULL,
    hpid                 VARCHAR(255)    NOT NULL,
    hospital_coordinates POINT SRID 4326 NOT NULL,
    hospital_address     VARCHAR(255)    NOT NULL,
    hospital_level       INT             NULL,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,

    SPATIAL INDEX idx_hospital_coordinates (hospital_coordinates)
);

CREATE TABLE hospital_specialty
(
    hospital_specialty_id BIGINT       PRIMARY KEY AUTO_INCREMENT,
    hospital_id           BIGINT       NOT NULL,
    specialty             VARCHAR(255) NOT NULL,
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL
);
