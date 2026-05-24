package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.loan.LoanDtos;
import com.ffatullayev.bankingapp.entity.Loan;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.LoanStatus;
import com.ffatullayev.bankingapp.repository.LoanRepository;
import com.ffatullayev.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository loanRepository;
  private final UserRepository userRepository;

  @Transactional
  public LoanDtos.LoanResponse applyForLoan(LoanDtos.LoanRequest request) {
    User currentUser = getCurrentUser();

    BigDecimal monthlyPayment = calculateMonthlyPayment(
        request.getAmount(),
        request.getInterestRate(),
        request.getTermMonths()
    );

    Loan loan = Loan.builder()
        .user(currentUser)
        .amount(request.getAmount())
        .termMonths(request.getTermMonths())
        .interestRate(request.getInterestRate())
        .monthlyPayment(monthlyPayment)
        .remainingAmount(request.getAmount())
        .status(LoanStatus.PENDING)
        .build();

    Loan saved = loanRepository.save(loan);
    return toResponse(saved);
  }

  public List<LoanDtos.LoanResponse> getMyLoans(){
    User currentUser = getCurrentUser();
    return loanRepository.findByUserId(currentUser.getId())
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public List<LoanDtos.PaymentScheduleItem> getPaymentSchedule(Long loanId){

    User currentUser = getCurrentUser();

    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(()-> new IllegalArgumentException("Kredit tapılmadı: " + loanId));


    if (!loan.getUser().getId().equals(currentUser.getId())) {
      throw new IllegalArgumentException("Bu kreditə giriş icazəniz yoxdur");
    }

    return calculatePaymentSchedule(
        loan.getAmount(),
        loan.getInterestRate(),
        loan.getTermMonths(),
        loan.getMonthlyPayment()
    );
  }

  private BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                             BigDecimal annualRate,
                                             int termMonths){
    BigDecimal monthlyRate = annualRate
        .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
        .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

    if (monthlyRate.compareTo(BigDecimal.ZERO)==0){
      return amount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
    }

    BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
    BigDecimal onePlusRPowN = onePlusR.pow(termMonths, new MathContext(10));
    BigDecimal numerator = amount.multiply(monthlyRate).multiply(onePlusRPowN);
    BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);

    return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
  }

  private List<LoanDtos.PaymentScheduleItem> calculatePaymentSchedule(
      BigDecimal amount, BigDecimal annualRate,
      int termMonths, BigDecimal monthlyPayment){

    List<LoanDtos.PaymentScheduleItem> schedule = new ArrayList<>();

    BigDecimal monthlyRate = annualRate
        .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
        .divide(BigDecimal.valueOf(22), 10, RoundingMode.HALF_UP);

    BigDecimal balance = amount;

    for (int month = 1; month<=termMonths; month++){
      BigDecimal interest = balance.multiply(monthlyRate)
          .setScale(2, RoundingMode.HALF_UP);
      BigDecimal principal = monthlyPayment.subtract(interest);
      balance = balance.subtract(principal);

      if (balance.compareTo(BigDecimal.ZERO)<0){
        balance=BigDecimal.ZERO;
      }

      LoanDtos.PaymentScheduleItem item = new LoanDtos.PaymentScheduleItem(
          month, monthlyPayment, principal, interest, balance
      );
      schedule.add(item);
    }
    return schedule;
  }

  private LoanDtos.LoanResponse toResponse(Loan loan){
    LoanDtos.LoanResponse response = new LoanDtos.LoanResponse();
    response.setId(loan.getId());
    response.setAmount(loan.getAmount());
    response.setTermMonths(loan.getTermMonths());
    response.setInterestRate(loan.getInterestRate());
    response.setMonthlyPayment(loan.getMonthlyPayment());
    response.setRemainingAmount(loan.getRemainingAmount());
    response.setStatus(loan.getStatus());
    response.setCreatedAt(loan.getCreatedAt());
    return response;
  }

  private User getCurrentUser(){
    String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return userRepository.findByEmail(email)
        .orElseThrow(()-> new UsernameNotFoundException("İstifadəçi tapılmadı"));
  }
}