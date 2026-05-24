package com.ffatullayev.bankingapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffatullayev.bankingapp.dto.AuthDtos;
import com.ffatullayev.bankingapp.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Auth Controller Integration Testlər")
class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("banking_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Register — 201 qaytarmalıdır")
    void register_success() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest(
                "Fuad", "Fətullayev",
                "fuad_integration@gmail.com",
                "password123",
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value("fuad_integration@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @DisplayName("Register — eyni email ilə 409 qaytarmalıdır")
    void register_duplicateEmail_returns409() throws Exception {
        AuthDtos.RegisterRequest request = new AuthDtos.RegisterRequest(
                "Fuad", "Fətullayev",
                "duplicate@gmail.com",
                "password123",
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Login — düzgün credentials ilə 200 qaytarmalıdır")
    void login_success() throws Exception {
        AuthDtos.RegisterRequest registerRequest = new AuthDtos.RegisterRequest(
                "Fuad", "Fətullayev",
                "login_test@gmail.com",
                "password123",
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest(
                "login_test@gmail.com", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Login — yanlış şifrə ilə 401 qaytarmalıdır")
    void login_wrongPassword_returns401() throws Exception {
        AuthDtos.RegisterRequest registerRequest = new AuthDtos.RegisterRequest(
                "Fuad", "Fətullayev",
                "wrong_pass@gmail.com",
                "password123",
                Role.CUSTOMER
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthDtos.LoginRequest loginRequest = new AuthDtos.LoginRequest(
                "wrong_pass@gmail.com", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}