package com.ffatullayev.bankingapp.dto.loan;


import com.ffatullayev.bankingapp.entity.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanDtos {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LoanRequest{
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LoanResponse{
    private Long id;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private BigDecimal remainingAmount;
    private LoanStatus status;
    private LocalDateTime createdAt;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PaymentScheduleItem{
    private Integer month;
    private BigDecimal payment;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal remainingBalance;
  }
}
