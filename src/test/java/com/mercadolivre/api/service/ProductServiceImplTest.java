package com.mercadolivre.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.exception.ResourceNotFoundException;
import com.mercadolivre.api.mapper.ProductMapper;
import com.mercadolivre.api.model.Product;
import com.mercadolivre.api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl - Testes Unitários")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Notebook");
        product.setDescription("Dell Inspiron");
        product.setPrice(new BigDecimal("3500.00"));

        requestDTO = new ProductRequestDTO(
            "Notebook",
            "Dell Inspiron",
            new BigDecimal("3500.00")
        );

        responseDTO = new ProductResponseDTO(
            1L,
            "Notebook",
            "Dell Inspiron",
            new BigDecimal("3500.00")
        );
    }

    @Test
    @DisplayName("Deve listar todos os produtos com paginação")
    @SuppressWarnings("null")
    void getAllProducts_ShouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Produto 1");
        product1.setDescription("Descrição 1");
        product1.setPrice(new BigDecimal("100.00"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Produto 2");
        product2.setDescription("Descrição 2");
        product2.setPrice(new BigDecimal("200.00"));

        Page<Product> productPage = new PageImpl<>(Arrays.asList(product1, product2));

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDTO);

        Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void getProductById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
    }

    @Test
    @DisplayName("Deve criar novo produto")
    @SuppressWarnings("null")
    void createProduct_ShouldReturnCreatedProduct() {
        when(productMapper.toEntity(requestDTO)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.createProduct(requestDTO);

        assertNotNull(result);
        assertEquals("Notebook", result.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve atualizar produto existente")
    @SuppressWarnings("null")
    void updateProduct_ShouldReturnUpdatedProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

        assertNotNull(result);
        verify(productMapper, times(1)).updateEntityFromDto(requestDTO, product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Deve deletar produto")
    void deleteProduct_ShouldDeleteSuccessfully() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
