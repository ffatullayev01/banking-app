package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

  @KafkaListener(
      topics = "transaction-completed",
      groupId = "bankinng-group"
  )
  public void handleTransactionEvent(TransactionEvent event){
    log.info("Yeni transaction bildirişi alındı: id={}, type={}, amount={}",
        event.getTransactionId(),
        event.getType(),
        event.getAmount());

    sendEmailNotification(event);
  }

  private void sendEmailNotification(TransactionEvent event){
    log.info("EMAIL GÖNDƏRİLDİ → {}: {} əməliyyatı uğurla tamamlandı. Məbləğ: {} AZN",
        event.getUserEmail(),
        event.getType(),
        event.getAmount());
  }
}
