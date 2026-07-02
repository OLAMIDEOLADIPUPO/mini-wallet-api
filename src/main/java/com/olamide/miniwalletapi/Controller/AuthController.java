package com.olamide.miniwalletapi.Controller;

import com.olamide.miniwalletapi.DTO.AuthResponseDTO;
import com.olamide.miniwalletapi.DTO.LoginRequestDTO;
import com.olamide.miniwalletapi.DTO.RegisterUserDTO;
import com.olamide.miniwalletapi.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration,login and refresh access token")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and wallet. Returns wallet number on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use")
    })
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterUserDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));

    }
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT access token. Use this token in the Authorize button above.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful — JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
