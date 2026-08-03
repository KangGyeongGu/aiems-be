-- 중앙관제센터 데이터 (Control System Seed Data)
-- 전국 19개 소방재난본부 정보

INSERT INTO member (member_id, login_id, password, member_type, created_at, updated_at) VALUES
    (100001, 'seoul_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100002, 'busan_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100003, 'incheon_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100004, 'daegu_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100005, 'gwangju_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100006, 'daejeon_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100007, 'ulsan_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100008, 'sejong_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100009, 'gyeonggi_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100010, 'gyeonggi_north_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100011, 'gangwon_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100012, 'chungbuk_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100013, 'chungnam_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100014, 'jeonbuk_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100015, 'jeonnam_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100016, 'gyeongbuk_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100017, 'gyeongnam_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100018, 'changwon_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW()),
    (100019, 'jeju_control@fire.go.kr', '$2y$10$/G0dMUhMeohGHBCzDpE49egUps06FAxF.1MDDcg6mWMNtd.rqhL7G', 'ControlSystem', NOW(), NOW());

-- Control_System 테이블에 상세 정보 추가 (실제 GPS 좌표 포함)
INSERT INTO control_system (member_id, system_name, control_system_address, control_system_coordinates) VALUES
    (100001, '서울소방재난본부', '서울특별시 중구 퇴계로26길 52', ST_SRID(POINT(126.988996322522, 37.5589214478953), 4326)),
    (100002, '부산소방재난본부', '부산광역시 연제구 고분로 216', ST_SRID(POINT(129.105747814753, 35.1834432601063), 4326)),
    (100003, '인천소방본부', '인천광역시 미추홀구 인하로 190', ST_SRID(POINT(126.669060075088, 37.448445762434), 4326)),
    (100004, '대구소방안전본부', '대구광역시 달서구 와룡로 49길 30', ST_SRID(POINT(128.535507536497, 35.8547400362547), 4326)),
    (100005, '광주소방안전본부', '광주광역시 서구 내방로 111', ST_SRID(POINT(126.851461925213, 35.1600994105234), 4326)),
    (100006, '대전소방본부', '대전광역시 서구 둔산로 100', ST_SRID(POINT(127.384633005948, 36.3503849976553), 4326)),
    (100007, '울산소방본부', '울산광역시 남구 중앙로 201', ST_SRID(POINT(129.311356392207, 35.5390270962011), 4326)),
    (100008, '세종특별자치시소방본부', '세종특별자치시 한누리대로 2130', ST_SRID(POINT(127.289039408864, 36.4800579897497), 4326)),
    (100009, '경기소방재난본부', '경기도 수원시 팔달구 효원로 1', ST_SRID(POINT(127.009619860326, 37.2746661643172), 4326)),
    (100010, '경기북부소방재난본부', '경기도 의정부시 금오로23번길 22-40', ST_SRID(POINT(127.069826839826, 37.755136797668), 4326)),
    (100011, '강원특별자치도소방본부', '강원특별자치도 춘천시 중앙로 1', ST_SRID(POINT(127.727907820318, 37.8800729197963), 4326)),
    (100012, '충북소방본부', '충북 청주시 청원구 밀레니엄1로 57', ST_SRID(POINT(127.476234665186, 36.6799163292586), 4326)),
    (100013, '충남소방본부', '충청남도 홍성군 홍북읍 충남대로 21', ST_SRID(POINT(126.673057036952, 36.6590416999343), 4326)),
    (100014, '전북소방본부', '전북특별자치도 전주시 완산구 효자로 225', ST_SRID(POINT(127.106396942356, 35.8194621650578), 4326)),
    (100015, '전남소방본부', '전라남도 장흥군 장흥읍 북부로 138', ST_SRID(POINT(126.915634298616, 34.6890789889294), 4326)),
    (100016, '경북소방본부', '경상북도 안동시 풍천면 도청대로 455', ST_SRID(POINT(128.505722686385, 36.5761205474728), 4326)),
    (100017, '경남소방본부', '경상남도 창원시 의창구 중앙대로 300', ST_SRID(POINT(128.691940442146, 35.2378032514675), 4326)),
    (100018, '창원소방본부', '경상남도 창원시 진해구 진해대로 1101', ST_SRID(POINT(128.710235537961, 35.131470934474), 4326)),
    (100019, '제주특별자치도소방안전본부', '제주특별자치도 제주시 신대로9길 22', ST_SRID(POINT(126.498981738928, 33.4906121551199), 4326));