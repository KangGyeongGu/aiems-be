CREATE TABLE patient
(
    patient_id                 BIGINT          PRIMARY KEY AUTO_INCREMENT,
    ambulance_id               BIGINT          NOT NULL,
    ambulance_license_plate    VARCHAR(255)    NULL,
    ambulance_fire_station_name VARCHAR(255)   NULL,
    ambulance_device_id        VARCHAR(255)    NULL,
    ambulance_jurisdiction     VARCHAR(255)    NULL,
    ambulance_operation_status VARCHAR(255)    NULL,
    patient_name               VARCHAR(255)    NOT NULL,
    patient_age                INT             NOT NULL,
    patient_gender             VARCHAR(255)    NOT NULL,
    min_blood_pressure         INT             NULL,
    max_blood_pressure         INT             NULL,
    pulse                      INT             NULL,
    respiratory_rate           INT             NULL,
    temperature                DOUBLE          NULL,
    symptoms                   VARCHAR(255)    NOT NULL,
    pre_ktas                   VARCHAR(255)    NULL,
    first_aid                  VARCHAR(255)    NULL,
    cause                      VARCHAR(255)    NULL,
    underlying_disease         VARCHAR(255)    NULL,
    accident_coordinates       POINT SRID 4326 NULL,
    accident_address           VARCHAR(255)    NULL,
    created_at                 DATETIME(6)     NOT NULL,
    updated_at                 DATETIME(6)     NOT NULL
);

CREATE TABLE transfer_record
(
    transfer_record_id BIGINT       PRIMARY KEY AUTO_INCREMENT,
    ambulance_id       BIGINT       NULL,
    hospital_id        BIGINT       NULL,
    hospital_name      VARCHAR(255) NULL,
    hospital_address   VARCHAR(255) NULL,
    patient_id         BIGINT       NULL,
    transfer_report    VARCHAR(255) NULL,
    treatment_record   JSON         NULL,
    started_at         DATETIME(6)  NULL,
    ended_at           DATETIME(6)  NULL,
    created_at         DATETIME(6)  NOT NULL,
    updated_at         DATETIME(6)  NOT NULL
);
