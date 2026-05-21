package com.ffatullayev.bankingapp.repository;

import com.ffatullayev.bankingapp.entity.Account;
import com.ffatullayev.bankingapp.entity.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

  List<Account> findByUserId(Long userId);

  Optional<Account> findByIban(String iban);

  boolean existsByIban(String iban);

  List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);
}
