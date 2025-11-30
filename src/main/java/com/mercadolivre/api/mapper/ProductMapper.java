package com.mercadolivre.api.mapper;

import org.springframework.stereotype.Component;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        return product;
    }

    public ProductRequestDTO toRequestDTO(Product entity) {
        return new ProductRequestDTO(
            entity.getName(),
            entity.getDescription(),
            entity.getPrice()
        );
    }

    public ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice()
        );
    }

    public void updateEntityFromDTO(ProductRequestDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
    }
}
