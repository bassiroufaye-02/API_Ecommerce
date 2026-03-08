package com.ecommerce.mulboutique.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(example = "{\"usernameOrEmail\":\"boutiquier1\",\"password\":\"password\"}")
public class LoginRequest {

    @NotBlank(message = "Le nom d'utilisateur ou l'email est obligatoire")
    private String usernameOrEmail;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
