package com.ecommerce.mulboutique.dto.user;

import com.ecommerce.mulboutique.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "{\"id\":2,\"username\":\"boutiquier1\",\"email\":\"boutiquier1@mulboutique.sn\",\"firstName\":\"Mamadou\",\"lastName\":\"Diop\",\"phone\":\"221771234567\",\"role\":\"STORE_OWNER\"}")
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private User.Role role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}
