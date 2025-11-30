package com.mercadolivre.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.model.Product;

@DisplayName("ProductMapper - Testes Unitários")
class ProductMapperTest {

    private ProductMapper productMapper;
    private ProductRequestDTO requestDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        requestDTO = new ProductRequestDTO(
            "Notebook Dell",
            "Notebook Dell Inspiron 15",
            new BigDecimal("3500.00")
        );

        product = new Product();
        product.setId(1L);
        product.setName("Notebook Dell");
        product.setDescription("Notebook Dell Inspiron 15");
        product.setPrice(new BigDecimal("3500.00"));
    }

    @Test
    @DisplayName("Deve converter ProductRequestDTO para Product")
    void toEntity_ShouldConvertDTOToEntity() {
        Product result = productMapper.toEntity(requestDTO);

        assertNotNull(result);
        assertEquals(requestDTO.name(), result.getName());
        assertEquals(requestDTO.description(), result.getDescription());
        assertEquals(requestDTO.price(), result.getPrice());
    }

    @Test
    @DisplayName("Deve retornar null ao converter ProductRequestDTO null para Product")
    void toEntity_ShouldReturnNullWhenDTOIsNull() {
        Product result = productMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve converter Product para ProductResponseDTO")
    void toDto_ShouldConvertEntityToResponseDTO() {
        ProductResponseDTO result = productMapper.toDto(product);

        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        assertEquals(product.getDescription(), result.description());
        assertEquals(product.getPrice(), result.price());
    }

    @Test
    @DisplayName("Deve retornar null ao converter Product null para ProductResponseDTO")
    void toDto_ShouldReturnNullWhenEntityIsNull() {
        ProductResponseDTO result = productMapper.toDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Deve atualizar Product a partir de ProductRequestDTO")
    void updateEntityFromDto_ShouldUpdateEntity() {
        ProductRequestDTO updateDTO = new ProductRequestDTO(
            "Notebook Atualizado",
            "Nova descrição",
            new BigDecimal("4000.00")
        );

        productMapper.updateEntityFromDto(updateDTO, product);

        assertEquals(updateDTO.name(), product.getName());
        assertEquals(updateDTO.description(), product.getDescription());
        assertEquals(updateDTO.price(), product.getPrice());
        assertEquals(1L, product.getId());
    }

    @Test
    @DisplayName("Não deve atualizar quando DTO é null")
    void updateEntityFromDto_ShouldNotUpdateWhenDTOIsNull() {
        String originalName = product.getName();

        productMapper.updateEntityFromDto(null, product);

        assertEquals(originalName, product.getName());
    }

    @Test
    @DisplayName("Não deve atualizar quando Product é null")
    void updateEntityFromDto_ShouldNotUpdateWhenEntityIsNull() {
        productMapper.updateEntityFromDto(requestDTO, null);

        // Não deve lançar exceção
    }
}
