package com.mercadolivre.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.api.dto.ProductDTO;
import com.mercadolivre.api.mapper.ProductMapper;
import com.mercadolivre.api.model.Product;
import com.mercadolivre.api.service.ProductService;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController - Testes Unitários")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Notebook");
        product.setDescription("Notebook Dell Inspiron");
        product.setPrice(3500.00);

        productDTO = new ProductDTO();
        productDTO.setName("Notebook");
        productDTO.setDescription("Notebook Dell Inspiron");
        productDTO.setPrice(3500.00);
    }

    @Test
    @DisplayName("Deve retornar lista de produtos com sucesso")
    void getAllProducts_ShouldReturnProductsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.productList[0].name").value("Notebook"))
                .andExpect(jsonPath("$._embedded.productList[0].price").value(3500.00))
                .andExpect(jsonPath("$._links.self").exists());

        verify(productService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar produto por ID com sucesso")
    void getProductById_ShouldReturnProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.description").value("Notebook Dell Inspiron"))
                .andExpect(jsonPath("$.price").value(3500.00))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.products.href").exists());

        verify(productService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(product);
        when(productService.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(productMapper, times(1)).toEntity(any(ProductDTO.class));
        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Notebook Atualizado");
        updatedProduct.setDescription("Notebook Dell Inspiron Atualizado");
        updatedProduct.setPrice(3800.00);

        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(updatedProduct);
        when(productService.update(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook Atualizado"))
                .andExpect(jsonPath("$.price").value(3800.00))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.products.href").exists());

        verify(productMapper, times(1)).toEntity(any(ProductDTO.class));
        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar produto com dados inválidos")
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ProductDTO invalidDTO = new ProductDTO();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}
