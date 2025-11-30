package com.mercadolivre.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;

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

@DisplayName("GlobalExceptionHandler - Unit Tests")
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
    @DisplayName("Should handle ResourceNotFoundException")
    void handleResourceNotFoundException_ShouldReturnNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Product", 1L);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Product not found with ID: 1", body.getMessage());
        assertEquals("/api/products/1", body.getPath());
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with custom message")
    void handleResourceNotFoundException_WithCustomMessage_ShouldReturnNotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals("Resource not found", body.getMessage());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    @SuppressWarnings("null")
    void handleValidationException_ShouldReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("product", "name", "Name is required");
        FieldError fieldError2 = new FieldError("product", "price", "Price must be positive");

        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodParameter parameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Validation error in the provided fields", body.getMessage());
        assertNotNull(body.getErrors());
        assertEquals(2, body.getErrors().size());
        assertEquals("name", body.getErrors().get(0).getField());
        assertEquals("Name is required", body.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Internal error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse body = Objects.requireNonNull(response.getBody());

        assertEquals(500, body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        assertEquals("An internal server error occurred", body.getMessage());
        assertEquals("/api/products/1", body.getPath());
    }
}
