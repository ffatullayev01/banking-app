package com.ffatullayev.bankingapp.controller;

import com.ffatullayev.bankingapp.dto.account.AccountDtos;
import com.ffatullayev.bankingapp.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Pul köçürmə əməliyyatları")
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/transfer")
  @Operation(summary = "Hesablar arası köçürmə")
  public ResponseEntity<AccountDtos.TransactionResponse> transfer(
      @RequestBody AccountDtos.TransferRequest request) {
    return ResponseEntity.ok(transactionService.transfer(request));
  }

  @GetMapping("/history/{iban}")
  @Operation(summary = "Hesabın əməliyyat tarixçəsi")
  public ResponseEntity<List<AccountDtos.TransactionResponse>> getHistory(
      @PathVariable String iban) {
    return ResponseEntity.ok(transactionService.getAccountHistory(iban));
  }
}
