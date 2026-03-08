package com.ecommerce.mulboutique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(example = "{\"name\":\"Boutique Dakar\",\"description\":\"Boutique de test\",\"logoUrl\":\"./image/logo.png\",\"contactEmail\":\"contact@boutiquier.sn\",\"contactPhone\":\"221770000000\",\"address\":\"1 avenue Test\",\"city\":\"Dakar\",\"country\":\"Senegal\",\"postalCode\":\"11000\"}")
public class StoreRequest {
    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String logoUrl;

    @Email
    @Size(max = 150)
    private String contactEmail;

    @Size(max = 30)
    private String contactPhone;

    @Size(max = 200)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String postalCode;

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
}
