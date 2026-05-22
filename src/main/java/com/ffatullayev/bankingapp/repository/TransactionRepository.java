package com.ffatullayev.bankingapp.repository;

import com.ffatullayev.bankingapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findBySenderAccountIdOrderByCreatedAtDesc(Long accountId);

  List<Transaction> findByReceiverAccountIdOrderByCreatedAtDesc(Long accountId);

  @Query("SELECT t FROM Transaction t WHERE " +
      "t.senderAccount.id = :accountId OR " +
      "t.receiverAccount.id = :accountId " +
      "ORDER BY t.createdAt DESC")
  List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);


  @Query("SELECT t FROM Transaction t WHERE " +
      "t.senderAccount.id = :accountId OR " +
      "t.receiverAccount.id = :accountId " +
      "ORDER BY t.createdAt DESC")
  Page<Transaction> findAllByAccountIdPageable(
      @Param("accountId") Long accountId,
      Pageable pageable);
}
