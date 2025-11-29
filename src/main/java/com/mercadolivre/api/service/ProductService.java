package com.mercadolivre.api.service;

import java.util.List;

import com.mercadolivre.api.model.Product;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    Product update(Long id, Product product);
    void delete(Long id);
}
