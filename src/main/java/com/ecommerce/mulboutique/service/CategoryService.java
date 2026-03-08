package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.CategoryDto;
import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.CategoryRepository;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    public List<CategoryDto> getCategoriesByStore(Long storeId) {
        return categoryRepository.findByStoreId(storeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    public CategoryDto createCategory(Category category, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvée"));
        
        category.setName(TextSanitizer.clean(category.getName()));
        category.setDescription(TextSanitizer.clean(category.getDescription()));
        category.setStore(store);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public CategoryDto updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));

        category.setName(TextSanitizer.clean(categoryDetails.getName()));
        category.setDescription(TextSanitizer.clean(categoryDetails.getDescription()));

        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Catégorie non trouvée"));
        
        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setStoreId(category.getStore().getId());
        dto.setStoreName(category.getStore().getName());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}

