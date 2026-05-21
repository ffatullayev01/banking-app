package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.AuthDtos;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.Role;
import com.ffatullayev.bankingapp.exception.EmailAlreadyExistsException;
import com.ffatullayev.bankingapp.repository.UserRepository;
import com.ffatullayev.bankingapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Bu email artıq qeydiyyatdan keçib: " + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthDtos.AuthResponse(
                accessToken, refreshToken,
                user.getEmail(), user.getRole().name());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthDtos.AuthResponse(
                accessToken, refreshToken,
                user.getEmail(), user.getRole().name());
    }

    public AuthDtos.AuthResponse refreshToken(AuthDtos.RefreshTokenRequest request) {
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("İstifadəçi tapılmadı"));

        if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
            throw new IllegalArgumentException("Refresh token keçərsizdir və ya vaxtı bitib");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthDtos.AuthResponse(
                newAccessToken, request.getRefreshToken(),
                user.getEmail(), user.getRole().name());
    }
}