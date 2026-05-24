package com.ffatullayev.bankingapp.controller;


import com.ffatullayev.bankingapp.dto.loan.LoanDtos;
import com.ffatullayev.bankingapp.service.LoanService;
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
@RequestMapping("api/v1/loans")
@RequiredArgsConstructor
@Tag(name="Loan", description = "Kredit əməliyyatları")
public class LoanController {

  private final LoanService loanService;

  @PostMapping("/apply")
  @Operation(summary = "Kredit müraciəti")
  public ResponseEntity<LoanDtos.LoanResponse> applyForLoan(
      @RequestBody LoanDtos.LoanRequest request){
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(loanService.applyForLoan(request));
  }

  @GetMapping
  @Operation(summary = "Mənim kreditlərim")
  public ResponseEntity<List<LoanDtos.LoanResponse>> getMyLoans(){
    return ResponseEntity.ok(loanService.getMyLoans());
  }

  @GetMapping("/{loanId}/schedule")
  @Operation(summary = "Ödəniş planı")
  public ResponseEntity<List<LoanDtos.PaymentScheduleItem>> getPaymentSchedule(
      @PathVariable Long loanId) {
     return ResponseEntity.ok(loanService.getPaymentSchedule(loanId));
  }
}
