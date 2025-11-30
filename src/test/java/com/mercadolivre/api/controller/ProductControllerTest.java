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

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.api.dto.ProductRequestDTO;
import com.mercadolivre.api.dto.ProductResponseDTO;
import com.mercadolivre.api.exception.ResourceNotFoundException;
import com.mercadolivre.api.service.ProductService;

@SuppressWarnings("null")
@WebMvcTest(ProductController.class)
@DisplayName("ProductController - Unit Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductRequestDTO validRequestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new ProductRequestDTO(
            "Notebook Dell",
            "Notebook Dell Inspiron 15",
            new BigDecimal("3500.00")
        );

        responseDTO = new ProductResponseDTO(
            1L,
            "Notebook Dell",
            "Notebook Dell Inspiron 15",
            new BigDecimal("3500.00")
        );
    }

    @Test
    @DisplayName("Should return page of products with default pagination")
    void getAllProducts_WithDefaultPagination_ShouldReturnPagedProducts() throws Exception {
        Page<ProductResponseDTO> page = new PageImpl<>(Collections.singletonList(responseDTO));
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Notebook Dell"))
                .andExpect(jsonPath("$.content[0].price").value(3500.00))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService, times(1)).getAllProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return page of products with custom pagination")
    void getAllProducts_WithCustomPagination_ShouldReturnPagedProducts() throws Exception {
        Page<ProductResponseDTO> page = new PageImpl<>(
            Collections.singletonList(responseDTO),
            PageRequest.of(0, 5),
            1
        );
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "name")
                .param("direction", "DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Notebook Dell"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));

        verify(productService, times(1)).getAllProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return product by ID successfully")
    void getProductById_WithValidId_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook Dell"))
                .andExpect(jsonPath("$.description").value("Notebook Dell Inspiron 15"))
                .andExpect(jsonPath("$.price").value(3500.00));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Should return 404 when product not found by ID")
    void getProductById_WithInvalidId_ShouldReturn404() throws Exception {
        when(productService.getProductById(999L))
            .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(get("/api/v1/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_WithValidData_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook Dell"))
                .andExpect(jsonPath("$.price").value(3500.00));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when creating product with empty name")
    void createProduct_WithEmptyName_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "",
            "Valid description",
            new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("Should return 400 when creating product with name less than 3 characters")
    void createProduct_WithShortName_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "AB",
            "Valid description",
            new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when creating product with null price")
    void createProduct_WithNullPrice_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "Valid Product",
            "Valid description",
            null
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 400 when creating product with negative price")
    void createProduct_WithNegativePrice_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "Valid Product",
            "Valid description",
            new BigDecimal("-10.00")
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_WithValidData_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO(
            1L,
            "Notebook Dell Updated",
            "New description",
            new BigDecimal("3800.00")
        );

        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class)))
            .thenReturn(updatedResponseDTO);

        ProductRequestDTO updateDTO = new ProductRequestDTO(
            "Notebook Dell Updated",
            "New description",
            new BigDecimal("3800.00")
        );

        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook Dell Updated"))
                .andExpect(jsonPath("$.price").value(3800.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void updateProduct_WithInvalidId_ShouldReturn404() throws Exception {
        when(productService.updateProduct(eq(999L), any(ProductRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(put("/api/v1/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(999L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when updating product with invalid data")
    void updateProduct_WithInvalidData_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "",
            "Description",
            new BigDecimal("100.00")
        );

        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent product")
    void deleteProduct_WithInvalidId_ShouldReturn404() throws Exception {
        doNothing().when(productService).deleteProduct(999L);
        when(productService.getProductById(999L))
            .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(delete("/api/v1/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    @DisplayName("Should accept product with null description")
    void createProduct_WithNullDescription_ShouldSucceed() throws Exception {
        ProductRequestDTO dtoWithoutDescription = new ProductRequestDTO(
            "Simple Product",
            null,
            new BigDecimal("100.00")
        );

        ProductResponseDTO response = new ProductResponseDTO(
            2L,
            "Simple Product",
            null,
            new BigDecimal("100.00")
        );

        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithoutDescription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Simple Product"))
                .andExpect(jsonPath("$.description").isEmpty());
    }

    @Test
    @DisplayName("Should return empty page when no products")
    void getAllProducts_WhenNoProducts_ShouldReturnEmptyPage() throws Exception {
        Page<ProductResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
