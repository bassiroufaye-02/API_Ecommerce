package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.ProductDto;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.ProductRepository;
import com.ecommerce.mulboutique.repository.ProductSpecifications;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.repository.CategoryRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductDto> getProductsByStore(Long storeId) {
        return productRepository.findByStoreIdAndIsActiveTrue(storeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(this::convertToDto);
    }

    public ProductDto createProduct(Product product, Long storeId, Long categoryId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvée"));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        product.setName(TextSanitizer.clean(product.getName()));
        product.setDescription(TextSanitizer.clean(product.getDescription()));
        product.setImageUrl(TextSanitizer.clean(product.getImageUrl()));
        product.setSku(TextSanitizer.clean(product.getSku()));
        product.setStore(store);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    public ProductDto updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé"));


        product.setName(TextSanitizer.clean(productDetails.getName()));
        product.setDescription(TextSanitizer.clean(productDetails.getDescription()));
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setImageUrl(TextSanitizer.clean(productDetails.getImageUrl()));
        product.setSku(TextSanitizer.clean(productDetails.getSku()));

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé"));
        
        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<ProductDto> searchProducts(String text,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           Long categoryId,
                                           Long storeId,
                                           Boolean inStock,
                                           Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecifications.isActive())
                .and(ProductSpecifications.nameOrDescriptionContains(text))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice))
                .and(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.hasStore(storeId))
                .and(ProductSpecifications.inStock(inStock));

        return productRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    public List<ProductDto> getLowStockProducts(Long storeId, int threshold) {
        return productRepository.findLowStockProductsByStore(storeId, threshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setSku(product.getSku());
        dto.setIsActive(product.getIsActive());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setStoreId(product.getStore().getId());
        dto.setStoreName(product.getStore().getName());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}

