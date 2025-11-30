package com.mercadolivre.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorResponse - Unit Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse with empty constructor")
    void emptyConstructor_ShouldCreateWithTimestamp() {
        ErrorResponse error = new ErrorResponse();

        assertNotNull(error);
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should create ErrorResponse with full constructor")
    void fullConstructor_ShouldCreateWithAllFields() {
        ErrorResponse error = new ErrorResponse(404, "Not Found", "Resource not found", "/api/products/1");

        assertNotNull(error);
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("Resource not found", error.getMessage());
        assertEquals("/api/products/1", error.getPath());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Should allow setting custom timestamp")
    void setTimestamp_ShouldUpdateTimestamp() {
        ErrorResponse error = new ErrorResponse();
        LocalDateTime customTimestamp = LocalDateTime.of(2025, 11, 30, 12, 0);

        error.setTimestamp(customTimestamp);

        assertEquals(customTimestamp, error.getTimestamp());
    }

    @Test
    @DisplayName("Should allow adding list of field errors")
    void setErrors_ShouldAddFieldErrors() {
        ErrorResponse error = new ErrorResponse();
        List<ErrorResponse.FieldError> fieldErrors = List.of(
            new ErrorResponse.FieldError("name", "Name is required"),
            new ErrorResponse.FieldError("price", "Price must be positive")
        );

        error.setErrors(fieldErrors);

        assertNotNull(error.getErrors());
        assertEquals(2, error.getErrors().size());
        assertEquals("name", error.getErrors().get(0).getField());
        assertEquals("Name is required", error.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Should create FieldError correctly")
    void fieldError_ShouldCreateWithFieldAndMessage() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("email", "Invalid email");

        assertEquals("email", fieldError.getField());
        assertEquals("Invalid email", fieldError.getMessage());
    }

    @Test
    @DisplayName("Should allow modifying FieldError")
    void fieldError_ShouldAllowModification() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("name", "Original");

        fieldError.setField("updatedField");
        fieldError.setMessage("Updated message");

        assertEquals("updatedField", fieldError.getField());
        assertEquals("Updated message", fieldError.getMessage());
    }

    @Test
    @DisplayName("Should return null when errors not defined")
    void getErrors_ShouldReturnNullWhenNotSet() {
        ErrorResponse error = new ErrorResponse();

        assertNull(error.getErrors());
    }

    @Test
    @DisplayName("Should allow modifying all fields")
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
