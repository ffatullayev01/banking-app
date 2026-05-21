package com.ffatullayev.bankingapp.controller;

import com.ffatullayev.bankingapp.dto.AuthDtos;
import com.ffatullayev.bankingapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Qeydiyyat, giriş və token yenilənməsi")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "Yeni istifadəçi qeydiyyatı")
  public ResponseEntity<AuthDtos.AuthResponse> register(
      @Valid @RequestBody AuthDtos.RegisterRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(authService.register(request));
  }

  @PostMapping("/login")
  @Operation(summary = "Sistemə giriş - Jwt token alınır")
  public ResponseEntity<AuthDtos.AuthResponse> login(
      @Valid @RequestBody AuthDtos.LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh-token")
  @Operation(summary = "Refresh token ilə yeni access token al")
  public ResponseEntity<AuthDtos.AuthResponse> refreshToken(
      @Valid @RequestBody AuthDtos.RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request));
  }
}