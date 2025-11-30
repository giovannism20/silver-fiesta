package com.mercadolivre.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorResponse - Testes Unitários")
class ErrorResponseTest {

    @Test
    @DisplayName("Deve criar ErrorResponse com construtor vazio")
    void emptyConstructor_ShouldCreateWithTimestamp() {
        ErrorResponse error = new ErrorResponse();

        assertNotNull(error);
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com construtor completo")
    void fullConstructor_ShouldCreateWithAllFields() {
        ErrorResponse error = new ErrorResponse(404, "Not Found", "Recurso não encontrado", "/api/products/1");

        assertNotNull(error);
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("Recurso não encontrado", error.getMessage());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Deve permitir definir timestamp customizado")
    void setTimestamp_ShouldUpdateTimestamp() {
        ErrorResponse error = new ErrorResponse();
        LocalDateTime customTimestamp = LocalDateTime.of(2025, 11, 30, 12, 0);

        error.setTimestamp(customTimestamp);

        assertEquals(customTimestamp, error.getTimestamp());
    }

    @Test
    @DisplayName("Deve permitir adicionar lista de erros de campo")
    void setErrors_ShouldAddFieldErrors() {
        ErrorResponse error = new ErrorResponse();
        List<ErrorResponse.FieldError> fieldErrors = List.of(
            new ErrorResponse.FieldError("name", "Nome é obrigatório"),
            new ErrorResponse.FieldError("price", "Preço deve ser positivo")
        );

        error.setErrors(fieldErrors);

        assertNotNull(error.getErrors());
        assertEquals(2, error.getErrors().size());
        assertEquals("name", error.getErrors().get(0).getField());
        assertEquals("Nome é obrigatório", error.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Deve criar FieldError corretamente")
    void fieldError_ShouldCreateWithFieldAndMessage() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("email", "Email inválido");

        assertEquals("email", fieldError.getField());
        assertEquals("Email inválido", fieldError.getMessage());
    }

    @Test
    @DisplayName("Deve permitir modificar FieldError")
    void fieldError_ShouldAllowModification() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("name", "Original");

        fieldError.setField("updatedField");
        fieldError.setMessage("Updated message");

        assertEquals("updatedField", fieldError.getField());
        assertEquals("Updated message", fieldError.getMessage());
    }

    @Test
    @DisplayName("Deve retornar null quando errors não definido")
    void getErrors_ShouldReturnNullWhenNotSet() {
        ErrorResponse error = new ErrorResponse();

        assertNull(error.getErrors());
    }

    @Test
    @DisplayName("Deve permitir modificar todos os campos")
    void setters_ShouldModifyAllFields() {
        ErrorResponse error = new ErrorResponse();

        error.setStatus(400);
        error.setError("Bad Request");
        error.setMessage("Validation error");
        error.setPath("/api/test");

        assertEquals(400, error.getStatus());
        assertEquals("Bad Request", error.getError());
        assertEquals("Validation error", error.getMessage());
        assertEquals("/api/test", error.getPath());
    }
}
