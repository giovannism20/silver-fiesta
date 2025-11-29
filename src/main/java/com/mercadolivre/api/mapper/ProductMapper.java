package com.mercadolivre.api.mapper;

import org.springframework.stereotype.Component;

import com.mercadolivre.api.dto.ProductDTO;
import com.mercadolivre.api.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        return product;
    }

    public ProductDTO toDTO(Product entity) {
        return new ProductDTO(
            entity.getName(),
            entity.getDescription(),
            entity.getPrice()
        );
    }

    public void updateEntityFromDTO(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
    }
}
