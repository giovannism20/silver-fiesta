package com.mercadolivre.api.mapper;

import org.springframework.stereotype.Component;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.model.Product;

@Component
public class ProductMapper {

    public ProductResponseDTO toDto(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice()
        );
    }

    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());

        return product;
    }

    public void updateEntityFromDto(ProductRequestDTO dto, Product product) {
        if (dto == null || product == null) {
            return;
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            product.setName(dto.name());
        }
        if (dto.description() != null) {
            product.setDescription(dto.description());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }
    }
}
