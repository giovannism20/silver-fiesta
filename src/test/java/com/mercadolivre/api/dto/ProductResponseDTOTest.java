package com.mercadolivre.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductResponseDTO - Testes Unitários")
class ProductResponseDTOTest {

    @Test
    @DisplayName("Deve criar ProductResponseDTO com construtor completo")
    void constructor_ShouldCreateDTOWithAllFields() {
        ProductResponseDTO dto = new ProductResponseDTO(1L, "Notebook", "Dell Inspiron", new BigDecimal("3500.00"));

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Notebook", dto.name());
        assertEquals("Dell Inspiron", dto.description());
        assertEquals(new BigDecimal("3500.00"), dto.price());
    }

    @Test
    @DisplayName("Deve validar igualdade entre ProductResponseDTO")
    void equals_ShouldCompareCorrectly() {
        ProductResponseDTO dto1 = new ProductResponseDTO(1L, "Mouse", "Logitech MX Master", new BigDecimal("450.00"));
        ProductResponseDTO dto2 = new ProductResponseDTO(1L, "Mouse", "Logitech MX Master", new BigDecimal("450.00"));

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
