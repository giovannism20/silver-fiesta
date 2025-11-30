package com.mercadolivre.api.service;

import java.util.Objects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.exception.ResourceNotFoundException;
import com.mercadolivre.api.mapper.ProductMapper;
import com.mercadolivre.api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        log.debug("Buscando produtos com paginação: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDTO> result = productRepository.findAll(pageable)
                .map(productMapper::toDto);
        log.info("Recuperados {} produtos", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        log.debug("Buscando produto com ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    log.info("Produto encontrado: id={}, name={}", id, product.getName());
                    return productMapper.toDto(product);
                })
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado com id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Objects.requireNonNull(productRequestDTO, "Product request cannot be null");
        log.debug("Criando novo produto: name={}", productRequestDTO.name());
        var product = productMapper.toEntity(productRequestDTO);
        Objects.requireNonNull(product, "Product entity cannot be null");
        var savedProduct = productRepository.save(product);
        log.info("Produto criado com sucesso: id={}, name={}", savedProduct.getId(), savedProduct.getName());
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#id")
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        Objects.requireNonNull(productRequestDTO, "Product request cannot be null");
        log.debug("Atualizando produto: id={}", id);
        var product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado para atualização: id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        productMapper.updateEntityFromDto(productRequestDTO, product);
        Objects.requireNonNull(product, "Product entity cannot be null");
        var updatedProduct = productRepository.save(product);
        log.info("Produto atualizado com sucesso: id={}, name={}", id, updatedProduct.getName());
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        log.debug("Deletando produto: id={}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Produto não encontrado para deleção: id={}", id);
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Produto deletado com sucesso: id={}", id);
    }
}
