package com.ffatullayev.bankingapp.repository;

import com.ffatullayev.bankingapp.entity.Loan;
import com.ffatullayev.bankingapp.entity.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

  List<Loan> findByUserId(Long userId);

  List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

}
