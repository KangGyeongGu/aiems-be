package com.A.B;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("local")
@DisplayName("애플리케이션 부팅 스모크 테스트")
class BApplicationTests {

    @Test
    @DisplayName("스프링 컨텍스트가 정상적으로 로드된다")
    void contextLoads() {
    }

}
