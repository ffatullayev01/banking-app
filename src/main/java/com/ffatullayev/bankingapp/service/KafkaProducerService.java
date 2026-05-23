package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

  private static final String TOPIC = "transaction-completed";

  private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

  public void sendTransactionEvent(TransactionEvent event){

    kafkaTemplate.send(TOPIC, event.getTransactionId().toString(), event)
        .whenComplete((result,ex)-> {
          if (ex==null) {
            log.info("Transaction event göndərildi: id={}, offset={}",
                event.getTransactionId(),
                result.getRecordMetadata().offset());
          } else {
            log.error("Transaction event göndərilmədi: id={}, səbəb={}",
          event.getTransactionId(), ex.getMessage());
          }
    });

  }
}
