package com.ffatullayev.bankingapp.controller;


import com.ffatullayev.bankingapp.dto.account.AccountDtos;
import com.ffatullayev.bankingapp.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name="Account", description = "Hesab əməliyyatları")
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  @Operation(summary = "Yeni hesab aç")
  public ResponseEntity<AccountDtos.AccountResponse> create(
      @RequestBody AccountDtos.CreateAccountRequest request){

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(accountService.createAccount(request));
  }

  @GetMapping
  @Operation(summary = "Mənim hesablarım")
  public ResponseEntity<List<AccountDtos.AccountResponse>> getMyAccounts() {
    return ResponseEntity.ok(accountService.getMyAccounts());
  }

  @GetMapping("/{iban}")
  @Operation(summary = "IBAN-a görə hesab məlumatı")
  public ResponseEntity<AccountDtos.AccountResponse> getAccountByIban(
      @PathVariable String iban) {
    return ResponseEntity.ok(accountService.getAccountByIban(iban));
  }

}
