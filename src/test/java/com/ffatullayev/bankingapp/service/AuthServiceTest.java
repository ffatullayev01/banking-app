package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.AuthDtos;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.Role;
import com.ffatullayev.bankingapp.exception.EmailAlreadyExistsException;
import com.ffatullayev.bankingapp.repository.UserRepository;
import com.ffatullayev.bankingapp.security.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit testler")
public class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtService jwtService;
  @Mock
  private AuthenticationManager authenticationManager;
  @InjectMocks
  private AuthService authService;

  private AuthDtos.RegisterRequest registerRequest;
  private AuthDtos.LoginRequest loginRequest;
  private User savedUser;

  @BeforeEach
  void setUp() {
    registerRequest = new AuthDtos.RegisterRequest(
        "Fuad", "Fətullayev",
        "fuad@gmail.com", "password123", Role.CUSTOMER);

    loginRequest = new AuthDtos.LoginRequest(
        "fuad@gmail.com", "password123");

    savedUser = User.builder()
        .id(1L)
        .email("fuad@gmail.com")
        .firstName("Fuad")
        .lastName("Fətullayev")
        .role(Role.CUSTOMER)
        .password("hashedPassword")
        .enabled(true)
        .build();
  }

  @Test
  @DisplayName("Uğurlu qeydiyyat — token qaytarılmalıdır")
  void register_success() {
    when(userRepository.existsByEmail(registerRequest.getEmail()))
        .thenReturn(false);
    when(passwordEncoder.encode(any()))
        .thenReturn("hashedPassword");
    when(userRepository.save(any(User.class)))
        .thenReturn(savedUser);
    when(jwtService.generateAccessToken(any()))
        .thenReturn("access-token");
    when(jwtService.generateRefreshToken(any()))
        .thenReturn("refresh-token");

    AuthDtos.AuthResponse response = authService.register(registerRequest);

    assertThat(response.getAccessToken()).isEqualTo("access-token");
    assertThat(response.getEmail()).isEqualTo("fuad@gmail.com");
    assertThat(response.getRole()).isEqualTo("CUSTOMER");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Mövcud email ilə qeydiyyat — exception atılmalıdır")
  void register_duplicateEmail_throwsException() {
    when(userRepository.existsByEmail(registerRequest.getEmail()))
        .thenReturn(true);

    assertThatThrownBy(() -> authService.register(registerRequest))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining("fuad@gmail.com");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Uğurlu login — token qaytarılmalıdır")
  void login_success() {
    when(authenticationManager.authenticate(any()))
        .thenReturn(null);
    when(userRepository.findByEmail(loginRequest.getEmail()))
        .thenReturn(Optional.of(savedUser));
    when(jwtService.generateAccessToken(any()))
        .thenReturn("access-token");
    when(jwtService.generateRefreshToken(any()))
        .thenReturn("refresh-token");

    AuthDtos.AuthResponse response = authService.login(loginRequest);

    assertThat(response.getAccessToken()).isEqualTo("access-token");
    assertThat(response.getRole()).isEqualTo("CUSTOMER");
  }

  @Test
  @DisplayName("Yanlış şifrə ilə login — exception atılmalıdır")
  void login_wrongPassword_throwsException() {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Yanlış şifrə"));

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(BadCredentialsException.class);

    verify(userRepository, never()).findByEmail(any());
  }

}
