package com.ffatullayev.bankingapp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

  private Long transactionId;
  private String senderIban;
  private String receiverIban;
  private BigDecimal amount;
  private String type;
  private String status;
  private String userEmail;
  private LocalDateTime createdAt;
}
