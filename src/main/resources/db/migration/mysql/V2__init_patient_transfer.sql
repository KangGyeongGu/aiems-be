CREATE TABLE patient
(
    patient_id           BIGINT          PRIMARY KEY AUTO_INCREMENT,
    ambulance_id         BIGINT          NOT NULL,
    patient_name         VARCHAR(255)    NOT NULL,
    patient_gender       VARCHAR(255)    NOT NULL,
    patient_age          INT             NOT NULL,
    pre_ktas             VARCHAR(255)    NULL,
    symptoms             VARCHAR(255)    NOT NULL,
    min_blood_pressure   INT             NULL,
    max_blood_pressure   INT             NULL,
    pulse                INT             NULL,
    respiratory_rate     INT             NULL,
    temperature          DOUBLE          NULL,
    first_aid            VARCHAR(255)    NULL,
    cause                VARCHAR(255)    NULL,
    underlying_disease   VARCHAR(255)    NULL,
    accident_coordinates POINT SRID 4326 NULL,
    accident_address     VARCHAR(255)    NULL,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL
);

CREATE TABLE transfer_record
(
    transfer_record_id BIGINT       PRIMARY KEY AUTO_INCREMENT,
    ambulance_id       BIGINT       NULL,
    hospital_id        BIGINT       NULL,
    patient_id         BIGINT       NULL,
    started_at         DATETIME(6)  NULL,
    ended_at           DATETIME(6)  NULL,
    transfer_report    VARCHAR(255) NULL,
    treatment_record   JSON         NULL,
    created_at         DATETIME(6)  NOT NULL,
    updated_at         DATETIME(6)  NOT NULL,

    CONSTRAINT uk_transfer_record_patient_id UNIQUE (patient_id)
);
