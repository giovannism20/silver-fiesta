package com.mercadolivre.api.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        log.debug("Fetching products with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDTO> result = productRepository.findAll(pageable)
                .map(productMapper::toDto);
        log.info("Retrieved {} products", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        log.debug("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    log.info("Product found: id={}", id);
                    return productMapper.toDto(product);
                })
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Objects.requireNonNull(productRequestDTO, "Product request cannot be null");
        log.debug("Creating new product: name={}", productRequestDTO.name());
        var product = productMapper.toEntity(productRequestDTO);
        Objects.requireNonNull(product, "Product entity cannot be null");
        var savedProduct = productRepository.save(product);
        log.info("Product created successfully: id={}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#id")
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        Objects.requireNonNull(productRequestDTO, "Product request cannot be null");
        log.debug("Updating product: id={}", id);
        var product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for update: id={}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        productMapper.updateEntityFromDto(productRequestDTO, product);
        Objects.requireNonNull(product, "Product entity cannot be null");
        var updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: id={}", id);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Objects.requireNonNull(id, "Product ID cannot be null");
        log.debug("Deleting product: id={}", id);

        boolean existed = productRepository.existsById(id);
        productRepository.deleteById(id);

        if (existed) {
            log.info("Product deleted successfully: id={}", id);
        } else {
            log.info("Product not found for deletion (idempotent operation): id={}", id);
        }
    }
}
