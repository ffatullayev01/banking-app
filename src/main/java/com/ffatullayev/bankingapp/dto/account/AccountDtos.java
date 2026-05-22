package com.ffatullayev.bankingapp.dto.account;

import com.ffatullayev.bankingapp.entity.enums.AccountStatus;
import com.ffatullayev.bankingapp.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDtos {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateAccountRequest{
    private AccountType type;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AccountResponse{
    private Long id;
    private String iban;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal balance;
    private LocalDateTime createdAt;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TransferRequest {
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;
    private String description;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TransactionResponse {
    private Long id;
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;
  }
}
