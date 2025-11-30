package com.mercadolivre.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;

public interface ProductService {
    Page<ProductResponseDTO> getAllProducts(Pageable pageable);
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
}
