package com.mercadolivre.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("ApiApplication - Unit Tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ApiApplicationTest {

    @Test
    @DisplayName("Should load Spring context without errors")
    void contextLoads() {
        // Test passes just by loading context without starting web server
    }
}
