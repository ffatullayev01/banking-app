package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.annotation.Auditable;
import com.ffatullayev.bankingapp.dto.account.AccountDtos;
import com.ffatullayev.bankingapp.entity.Account;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.AccountStatus;
import com.ffatullayev.bankingapp.entity.enums.AccountType;
import com.ffatullayev.bankingapp.repository.AccountRepository;
import com.ffatullayev.bankingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  @Auditable(action = "CREATE_ACCOUNT")
  @Transactional
  public AccountDtos.AccountResponse createAccount(AccountDtos.CreateAccountRequest request){

    User currentUser = getCurrentUser();
    String iban = generateUniqueIban();

    Account account = Account.builder()
        .iban(iban)
        .type(request.getType())
        .status(AccountStatus.ACTIVE)
        .balance(BigDecimal.ZERO)
        .user(currentUser)
        .build();

    Account saved = accountRepository.save(account);
    return toResponse(saved);
  }

  public List<AccountDtos.AccountResponse> getMyAccounts(){
    User currentUser = getCurrentUser();
    return accountRepository.findByUserId(currentUser.getId())
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public AccountDtos.AccountResponse getAccountByIban(String iban){
    User currentUser = getCurrentUser();
    Account account = accountRepository.findByIban(iban)
        .orElseThrow(()->new IllegalArgumentException("Hesab tapılmadı: " + iban));

    if (!account.getUser().getId().equals(currentUser.getId())) {
      throw new IllegalArgumentException("Bu hesaba giriş icazəniz yoxdur");
    }

    return toResponse(account);
  }

  private User getCurrentUser(){
    String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return userRepository.findByEmail(email)
        .orElseThrow(()-> new UsernameNotFoundException("İstifadəçi tapılmadı"));
  }

  private String generateUniqueIban(){
    String iban;
    do {
      iban = generateIban();
    }while (accountRepository.existsByIban(iban));
    return iban;
  }

  private String generateIban(){
    SecureRandom secureRandom = new SecureRandom();
    StringBuilder sb = new StringBuilder("AZ");
    for (int i=0; i<2; i++){
      sb.append(secureRandom.nextInt(10));
    }
    sb.append("BANK");
    for (int i=0; i<16; i++){
      sb.append(secureRandom.nextInt(10));
    }
    return sb.toString();
  }

  private AccountDtos.AccountResponse toResponse(Account account){
    AccountDtos.AccountResponse response = new AccountDtos.AccountResponse();
    response.setId(account.getId());
    response.setIban(account.getIban());
    response.setType(account.getType());
    response.setStatus(account.getStatus());
    response.setBalance(account.getBalance());
    response.setCreatedAt(account.getCreatedAt());
    return response;
  }
}
