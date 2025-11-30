package com.mercadolivre.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects; // Importação adicionada

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@DisplayName("GlobalExceptionHandler - Testes Unitários")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/products/1");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException")
    void handleResourceNotFoundException_ShouldReturnNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Produto", 1L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // CORREÇÃO: Usar Objects.requireNonNull() para garantir que 'body' não é nulo.
        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Produto não encontrado com ID: 1", body.getMessage());
        assertEquals("/api/products/1", body.getPath());
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException com mensagem customizada")
    void handleResourceNotFoundException_WithCustomMessage_ShouldReturnNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // CORREÇÃO: Usar Objects.requireNonNull() para garantir que 'body' não é nulo.
        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals("Recurso não encontrado", body.getMessage());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    @SuppressWarnings("null")
    void handleValidationException_ShouldReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("product", "name", "Nome é obrigatório");
        FieldError fieldError2 = new FieldError("product", "price", "Preço deve ser positivo");

        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // CORREÇÃO: Usar Objects.requireNonNull() para garantir que 'body' não é nulo.
        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Erro de validação nos campos informados", body.getMessage());
        assertNotNull(body.getErrors());
        assertEquals(2, body.getErrors().size());
        assertEquals("name", body.getErrors().get(0).getField());
        assertEquals("Nome é obrigatório", body.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica")
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Erro interno");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // CORREÇÃO: Usar Objects.requireNonNull() para garantir que 'body' não é nulo.
        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(500, body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        assertEquals("Ocorreu um erro interno no servidor", body.getMessage());
        assertEquals("/api/products/1", body.getPath());
    }
}
