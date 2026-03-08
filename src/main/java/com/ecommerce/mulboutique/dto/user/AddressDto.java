package com.ecommerce.mulboutique.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "{\"id\":1,\"label\":\"Maison\",\"line1\":\"10 rue Exemple\",\"line2\":\"\",\"city\":\"Dakar\",\"state\":\"\",\"country\":\"Senegal\",\"postalCode\":\"11000\",\"phone\":\"221771234567\",\"isDefault\":true}")
public class AddressDto {
    private Long id;
    private String label;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
    private Boolean isDefault;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
