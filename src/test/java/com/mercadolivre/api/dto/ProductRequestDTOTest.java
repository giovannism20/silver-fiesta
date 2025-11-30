package com.mercadolivre.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductRequestDTO - Unit Tests")
class ProductRequestDTOTest {

    @Test
    @DisplayName("Should create ProductRequestDTO with full constructor")
    void constructor_ShouldCreateDTOWithAllFields() {
        ProductRequestDTO dto = new ProductRequestDTO("Notebook", "Dell Inspiron", new BigDecimal("3500.00"));

        assertNotNull(dto);
        assertEquals("Notebook", dto.name());
        assertEquals("Dell Inspiron", dto.description());
        assertEquals(new BigDecimal("3500.00"), dto.price());
    }

    @Test
    @DisplayName("Should validate equality between ProductRequestDTO")
    void equals_ShouldCompareCorrectly() {
        ProductRequestDTO dto1 = new ProductRequestDTO("Mouse", "Logitech MX Master", new BigDecimal("450.00"));
        ProductRequestDTO dto2 = new ProductRequestDTO("Mouse", "Logitech MX Master", new BigDecimal("450.00"));

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
