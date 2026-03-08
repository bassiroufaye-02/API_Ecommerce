package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.auth.JwtResponse;
import com.ecommerce.mulboutique.dto.auth.LoginRequest;
import com.ecommerce.mulboutique.dto.auth.SignUpRequest;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.security.UserPrincipal;
import com.ecommerce.mulboutique.security.jwt.JwtTokenProvider;
import com.ecommerce.mulboutique.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentification", description = "API pour l'authentification des utilisateurs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion rÃ©ussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "400", description = "RequÃªte invalide")
    })
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(
            jwt,
            refreshToken,
            "Bearer",
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "CrÃ©e un nouveau compte utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur crÃ©Ã© avec succÃ¨s"),
        @ApiResponse(responseCode = "400", description = "DonnÃ©es utilisateur invalides"),
        @ApiResponse(responseCode = "409", description = "Utilisateur dÃ©jÃ  existant")
    })
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (authService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>("Nom d'utilisateur dÃ©jÃ  pris!", HttpStatus.BAD_REQUEST);
        }

        if (authService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>("Email dÃ©jÃ  utilisÃ©!", HttpStatus.BAD_REQUEST);
        }

        authService.createUser(signUpRequest);

        return new ResponseEntity<>("Utilisateur enregistrÃ© avec succÃ¨s!", HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "RafraÃ®chir le token", description = "GÃ©nÃ¨re un nouveau token JWT Ã  partir du refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token rafraÃ®chi avec succÃ¨s"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalide")
    })
    public ResponseEntity<JwtResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {

        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String username = tokenProvider.getUsernameFromJWT(refreshToken);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            username, null, null
        );

        String newJwt = tokenProvider.generateTokenFromUsername(username);

        User user = authService.findByUsernameOrEmail(username)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvÃ©"));

        return ResponseEntity.ok(new JwtResponse(
            newJwt,
            refreshToken,
            "Bearer",
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole().name()
        ));
    }

    @PostMapping("/logout")
    @Operation(summary = "DÃ©connexion", description = "Invalide le token cÃ´tÃ© client (JWT stateless)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DÃ©connexion rÃ©ussie")
    })
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("DÃ©connectÃ©");
    }
}


