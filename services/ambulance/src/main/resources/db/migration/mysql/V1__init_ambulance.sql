CREATE TABLE ambulance
(
    ambulance_id      BIGINT       PRIMARY KEY,
    device_id         VARCHAR(255) NOT NULL,
    license_plate     VARCHAR(255) NOT NULL,
    fire_station_name VARCHAR(255) NOT NULL,
    jurisdiction      VARCHAR(255) NOT NULL,
    operation_status  VARCHAR(255) NULL,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,

    CONSTRAINT uk_ambulance_device_id UNIQUE (device_id)
);
