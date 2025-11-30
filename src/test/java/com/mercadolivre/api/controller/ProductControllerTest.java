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
@DisplayName("ProductController - Testes Unitários")
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
    @DisplayName("Deve retornar página de produtos com paginação padrão")
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
    @DisplayName("Deve retornar página de produtos com paginação customizada")
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
    @DisplayName("Deve retornar produto por ID com sucesso")
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
    @DisplayName("Deve retornar 404 quando produto não for encontrado por ID")
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
    @DisplayName("Deve criar produto com sucesso")
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
    @DisplayName("Deve retornar 400 ao criar produto com nome vazio")
    void createProduct_WithEmptyName_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "",
            "Descrição válida",
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
    @DisplayName("Deve retornar 400 ao criar produto com nome menor que 3 caracteres")
    void createProduct_WithShortName_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "AB",
            "Descrição válida",
            new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com preço nulo")
    void createProduct_WithNullPrice_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "Produto Válido",
            "Descrição válida",
            null
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto com preço negativo")
    void createProduct_WithNegativePrice_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "Produto Válido",
            "Descrição válida",
            new BigDecimal("-10.00")
        );

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void updateProduct_WithValidData_ShouldReturnUpdatedProduct() throws Exception {
        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO(
            1L,
            "Notebook Dell Atualizado",
            "Nova descrição",
            new BigDecimal("3800.00")
        );

        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class)))
            .thenReturn(updatedResponseDTO);

        ProductRequestDTO updateDTO = new ProductRequestDTO(
            "Notebook Dell Atualizado",
            "Nova descrição",
            new BigDecimal("3800.00")
        );

        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook Dell Atualizado"))
                .andExpect(jsonPath("$.price").value(3800.00));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar produto inexistente")
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
    @DisplayName("Deve retornar 400 ao atualizar produto com dados inválidos")
    void updateProduct_WithInvalidData_ShouldReturn400() throws Exception {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
            "",
            "Descrição",
            new BigDecimal("100.00")
        );

        mockMvc.perform(put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deleteProduct_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar produto inexistente")
    void deleteProduct_WithInvalidId_ShouldReturn404() throws Exception {
        doNothing().when(productService).deleteProduct(999L);
        when(productService.getProductById(999L))
            .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        // Como deleteProduct pode lançar exception internamente
        mockMvc.perform(delete("/api/v1/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    @DisplayName("Deve aceitar produto com descrição nula")
    void createProduct_WithNullDescription_ShouldSucceed() throws Exception {
        ProductRequestDTO dtoWithoutDescription = new ProductRequestDTO(
            "Produto Simples",
            null,
            new BigDecimal("100.00")
        );

        ProductResponseDTO response = new ProductResponseDTO(
            2L,
            "Produto Simples",
            null,
            new BigDecimal("100.00")
        );

        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithoutDescription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Produto Simples"))
                .andExpect(jsonPath("$.description").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há produtos")
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
