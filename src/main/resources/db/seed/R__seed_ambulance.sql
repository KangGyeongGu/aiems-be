INSERT INTO member (member_id, login_id, password, member_type, created_at, updated_at) VALUES
    (1, '40가 1284', '{bcrypt}$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'Ambulance', NOW(), NOW()),
    (2, '12나 2392', '{bcrypt}$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'Ambulance', NOW(), NOW()),
    (3, '29다 1239', '{bcrypt}$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'Ambulance', NOW(), NOW()),
    (4, '27라 2984', '{bcrypt}$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'Ambulance', NOW(), NOW()),
    (5, '37마 1928', '{bcrypt}$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'Ambulance', NOW(), NOW());

INSERT INTO ambulance (member_id, device_id, license_plate, fire_station_name, jurisdiction, operation_status)
VALUES (1, '9bf8cdfc-ef7e-4663-98fb-e98c9f0306c9', '40가 1284', '광주 광산 소방서', 'GWANGJU', 'STANDBY'),
       (2, '9bf8cdfc-ef7e-4663-98fb-e98c9f0306ca', '12나 2392', '광주 광산 소방서', 'GWANGJU', 'STANDBY'),
       (3, '9bf8cdfc-ef7e-4663-98fb-e98c9f0306cb', '29다 1239', '광주 광산 소방서', 'GWANGJU', 'STANDBY'),
       (4, '9bf8cdfc-ef7e-4663-98fb-e98c9f0306cc', '27라 2984', '광주 광산 소방서', 'GWANGJU', 'STANDBY'),
       (5, '9bf8cdfc-ef7e-4663-98fb-e98c9f0306cd', '37마 1928', '광주 광산 소방서', 'GWANGJU', 'STANDBY');
