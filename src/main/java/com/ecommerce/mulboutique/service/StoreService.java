package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.StoreDto;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.repository.UserRepository;
import com.ecommerce.mulboutique.util.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<StoreDto> getAllActiveStores() {
        return storeRepository.findActiveWithOwner().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<StoreDto> getStoreById(Long id) {
        return storeRepository.findActiveByIdWithOwner(id)
                .map(this::convertToDto);
    }

    public StoreDto createStore(Store store, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Propriétaire non trouvé"));
        
        store.setName(TextSanitizer.clean(store.getName()));
        store.setDescription(TextSanitizer.clean(store.getDescription()));
        store.setLogoUrl(TextSanitizer.clean(store.getLogoUrl()));
        store.setContactEmail(TextSanitizer.clean(store.getContactEmail()));
        store.setContactPhone(TextSanitizer.clean(store.getContactPhone()));
        store.setAddress(TextSanitizer.clean(store.getAddress()));
        store.setCity(TextSanitizer.clean(store.getCity()));
        store.setCountry(TextSanitizer.clean(store.getCountry()));
        store.setPostalCode(TextSanitizer.clean(store.getPostalCode()));
        store.setOwner(owner);
        Store savedStore = storeRepository.save(store);
        return convertToDto(savedStore);
    }

    public StoreDto updateStore(Long id, Store storeDetails) {
        Store store = storeRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvée"));

        store.setName(TextSanitizer.clean(storeDetails.getName()));
        store.setDescription(TextSanitizer.clean(storeDetails.getDescription()));
        store.setLogoUrl(TextSanitizer.clean(storeDetails.getLogoUrl()));
        store.setContactEmail(TextSanitizer.clean(storeDetails.getContactEmail()));
        store.setContactPhone(TextSanitizer.clean(storeDetails.getContactPhone()));
        store.setAddress(TextSanitizer.clean(storeDetails.getAddress()));
        store.setCity(TextSanitizer.clean(storeDetails.getCity()));
        store.setCountry(TextSanitizer.clean(storeDetails.getCountry()));
        store.setPostalCode(TextSanitizer.clean(storeDetails.getPostalCode()));

        Store updatedStore = storeRepository.save(store);
        return convertToDto(updatedStore);
    }

    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvée"));
        
        store.setIsActive(false);
        storeRepository.save(store);
    }

    public List<StoreDto> getStoresByOwner(Long ownerId) {
        return storeRepository.findActiveByOwnerIdWithOwner(ownerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private StoreDto convertToDto(Store store) {
        StoreDto dto = new StoreDto();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setDescription(store.getDescription());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setContactEmail(store.getContactEmail());
        dto.setContactPhone(store.getContactPhone());
        dto.setAddress(store.getAddress());
        dto.setCity(store.getCity());
        dto.setCountry(store.getCountry());
        dto.setPostalCode(store.getPostalCode());
        dto.setOwnerId(store.getOwner().getId());
        dto.setOwnerName(store.getOwner().getFirstName() + " " + store.getOwner().getLastName());
        dto.setIsActive(store.getIsActive());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        return dto;
    }
}

