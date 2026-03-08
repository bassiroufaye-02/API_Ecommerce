package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.ProductDto;
import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.repository.CategoryRepository;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_assignsStoreAndCategory() {
        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");

        Category category = new Category();
        category.setId(2L);
        category.setName("Laptop");

        Product product = new Product();
        product.setName("EliteBook");
        product.setPrice(new BigDecimal("1200.00"));
        product.setSku("EL-001");

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto dto = productService.createProduct(product, 1L, 2L);

        assertEquals("EliteBook", dto.getName());
        assertEquals("Tech", dto.getStoreName());
        assertEquals("Laptop", dto.getCategoryName());
        assertEquals(1L, dto.getStoreId());
        assertEquals(2L, dto.getCategoryId());
    }

    @Test
    void getProductById_filtersInactive() {
        Product product = new Product();
        product.setId(10L);
        product.setIsActive(false);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertTrue(productService.getProductById(10L).isEmpty());
    }

    @Test
    void updateProduct_updatesFields() {
        Store store = new Store();
        store.setId(1L);
        store.setName("Tech");

        Category category = new Category();
        category.setId(2L);
        category.setName("Laptop");

        Product existing = new Product();
        existing.setId(99L);
        existing.setStore(store);
        existing.setCategory(category);
        existing.setIsActive(true);

        Product update = new Product();
        update.setName("New Name");
        update.setDescription("Desc");
        update.setPrice(new BigDecimal("99.90"));
        update.setStockQuantity(7);
        update.setImageUrl("img.png");
        update.setSku("SKU-2");

        when(productRepository.findById(99L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto dto = productService.updateProduct(99L, update);

        assertEquals("New Name", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertEquals(new BigDecimal("99.90"), dto.getPrice());
        assertEquals(7, dto.getStockQuantity());
        assertEquals("img.png", dto.getImageUrl());
        assertEquals("SKU-2", dto.getSku());
    }

    @Test
    void deleteProduct_marksInactive() {
        Product product = new Product();
        product.setId(5L);
        product.setIsActive(true);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        productService.deleteProduct(5L);

        assertFalse(product.getIsActive());
        verify(productRepository).save(product);
    }
}
