package com.ecommerce.mulboutique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(example = "{\"id\":1,\"name\":\"Boutique Dakar\",\"description\":\"Boutique de test\",\"logoUrl\":\"./image/logo.png\",\"contactEmail\":\"contact@boutiquier.sn\",\"contactPhone\":\"221770000000\",\"address\":\"1 avenue Test\",\"city\":\"Dakar\",\"country\":\"Senegal\",\"postalCode\":\"11000\",\"ownerId\":2,\"ownerName\":\"Mamadou Diop\",\"isActive\":true}")
public class StoreDto {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private Long ownerId;
    private String ownerName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StoreDto() {}

    public StoreDto(Long id, String name, String description, String contactEmail, Long ownerId, String ownerName, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contactEmail = contactEmail;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
