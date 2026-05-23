package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.account.AccountDtos;
import com.ffatullayev.bankingapp.dto.event.TransactionEvent;
import com.ffatullayev.bankingapp.entity.Account;
import com.ffatullayev.bankingapp.entity.Transaction;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.AccountStatus;
import com.ffatullayev.bankingapp.entity.enums.AccountType;
import com.ffatullayev.bankingapp.entity.enums.TransactionStatus;
import com.ffatullayev.bankingapp.entity.enums.TransactionType;
import com.ffatullayev.bankingapp.repository.AccountRepository;
import com.ffatullayev.bankingapp.repository.TransactionRepository;
import com.ffatullayev.bankingapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final KafkaProducerService kafkaProducerService;

  @Transactional
  public AccountDtos.TransactionResponse transfer(AccountDtos.TransferRequest request){

    User currentUser = getCurrentUser();

    Account sender = accountRepository.findByIban(request.getSenderIban())
        .orElseThrow(()-> new IllegalArgumentException(
            "Göndərən hesab tapılmadı: " + request.getSenderIban()));

    if (!sender.getUser().getId().equals(getCurrentUser().getId())){
      throw new IllegalArgumentException("Bu hesab sizin deyil");
    }
    if (sender.getStatus() != AccountStatus.ACTIVE){
      throw new IllegalArgumentException("Göndərən hesab aktiv deyil");
    }

    Account receiver = accountRepository.findByIban(request.getReceiverIban())
        .orElseThrow(() -> new IllegalArgumentException(
            "Alan hesab tapılmadı: " + request.getReceiverIban()));

    if (receiver.getStatus() != AccountStatus.ACTIVE) {
      throw new IllegalArgumentException("Alan hesab aktiv deyil");
    }
    if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Məbləğ sıfırdan böyük olmalıdır");
    }

    if (sender.getBalance().compareTo(request.getAmount()) < 0) {
      throw new IllegalArgumentException("Balans kifayət deyil");
    }

    sender.setBalance(sender.getBalance().subtract(request.getAmount()));
    receiver.setBalance(receiver.getBalance().add(request.getAmount()));

    accountRepository.save(sender);
    accountRepository.save(receiver);

    Transaction transaction = Transaction.builder()
        .senderAccount(sender)
        .receiverAccount(receiver)
        .amount(request.getAmount())
        .type(TransactionType.TRANSFER)
        .status(TransactionStatus.SUCCESS)
        .description(request.getDescription())
        .build();

    Transaction saved = transactionRepository.save(transaction);

    TransactionEvent event = new TransactionEvent(
        saved.getId(),
        sender.getIban(),
        receiver.getIban(),
        saved.getAmount(),
        saved.getType().name(),
        saved.getStatus().name(),
        currentUser.getEmail(),
        saved.getCreatedAt()
    );

    kafkaProducerService.sendTransactionEvent(event);

    return toResponse(saved);
  }

  private User getCurrentUser(){
    String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return userRepository.findByEmail(email)
        .orElseThrow(()->new UsernameNotFoundException("İstifadəçi tapılmadı"));
  }

  @Transactional(readOnly = true)
  public Page<AccountDtos.TransactionResponse> getAccountHistory(
      String iban, int page, int size) {
    User currentUser = getCurrentUser();

    Account account = accountRepository.findByIban(iban)
        .orElseThrow(() -> new IllegalArgumentException("Hesab tapılmadı: " + iban));

    if (!account.getUser().getId().equals(currentUser.getId())) {
      throw new IllegalArgumentException("Bu hesaba giriş icazəniz yoxdur");
    }

    Pageable pageable = PageRequest.of(page, size);

    return transactionRepository.findAllByAccountIdPageable(account.getId(), pageable)
        .map(this::toResponse);
  }

  private AccountDtos.TransactionResponse toResponse(Transaction t) {
    AccountDtos.TransactionResponse response = new AccountDtos.TransactionResponse();
    response.setId(t.getId());
    response.setSenderIban(t.getSenderAccount() != null
        ? t.getSenderAccount().getIban() : null);
    response.setReceiverIban(t.getReceiverAccount() != null
        ? t.getReceiverAccount().getIban() : null);
    response.setAmount(t.getAmount());
    response.setType(t.getType().name());
    response.setStatus(t.getStatus().name());
    response.setDescription(t.getDescription());
    response.setCreatedAt(t.getCreatedAt());
    return response;
  }
}
