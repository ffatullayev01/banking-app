package com.ffatullayev.bankingapp.dto;

import com.ffatullayev.bankingapp.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDtos {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RegisterRequest {
    @NotBlank(message = "Ad boş ola bilməz")
    private String firstName;

    @NotBlank(message = "Soyad boş ola bilməz")
    private String lastName;

    @Email(message = "Email formatı yanlışdır")
    @NotBlank(message = "Email boş ola bilməz")
    private String email;

    @Size(min = 8, message = "Şifrə minimum 8 simvol olmalıdır")
    @NotBlank(message = "Şifrə boş ola bilməz")
    private String password;

    private Role role;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
  }
}