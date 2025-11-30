package com.mercadolivre.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("ApiApplication - Testes Unitários")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ApiApplicationTest {

    @Test
    @DisplayName("Deve carregar contexto Spring sem erros")
    void contextLoads() {
        // Teste passa apenas carregando o contexto sem subir servidor web
    }
}
