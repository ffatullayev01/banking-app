package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.loan.LoanDtos;
import com.ffatullayev.bankingapp.entity.Loan;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.LoanStatus;
import com.ffatullayev.bankingapp.entity.enums.Role;
import com.ffatullayev.bankingapp.repository.LoanRepository;
import com.ffatullayev.bankingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Unit Testlər")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private User currentUser;
    private Loan loan;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .email("fuad@gmail.com")
                .firstName("Fuad")
                .lastName("Fətullayev")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        loan = Loan.builder()
                .id(1L)
                .user(currentUser)
                .amount(BigDecimal.valueOf(1000))
                .termMonths(12)
                .interestRate(BigDecimal.valueOf(12))
                .monthlyPayment(BigDecimal.valueOf(88.85))
                .remainingAmount(BigDecimal.valueOf(1000))
                .status(LoanStatus.PENDING)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("fuad@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("fuad@gmail.com"))
                .thenReturn(Optional.of(currentUser));
    }

    @Test
    @DisplayName("Uğurlu kredit müraciəti — kredit qaytarılmalıdır")
    void applyForLoan_success() {
        LoanDtos.LoanRequest request = new LoanDtos.LoanRequest(
                BigDecimal.valueOf(1000),
                12,
                BigDecimal.valueOf(12)
        );

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanDtos.LoanResponse response = loanService.applyForLoan(request);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(response.getTermMonths()).isEqualTo(12);
        assertThat(response.getStatus()).isEqualTo(LoanStatus.PENDING);
        assertThat(response.getMonthlyPayment()).isEqualByComparingTo(BigDecimal.valueOf(88.85));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    @DisplayName("Kreditlərim — siyahı qaytarılmalıdır")
    void getMyLoans_success() {
        when(loanRepository.findByUserId(currentUser.getId()))
                .thenReturn(List.of(loan));

        List<LoanDtos.LoanResponse> responses = loanService.getMyLoans();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    @DisplayName("Ödəniş planı — 12 aylıq plan qaytarılmalıdır")
    void getPaymentSchedule_success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        List<LoanDtos.PaymentScheduleItem> schedule =
                loanService.getPaymentSchedule(1L);

        assertThat(schedule).hasSize(12);
        assertThat(schedule.get(0).getMonth()).isEqualTo(1);
        assertThat(schedule.get(11).getRemainingBalance())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Başqasının kreditinə giriş — exception atılmalıdır")
    void getPaymentSchedule_notOwner_throwsException() {
        User anotherUser = User.builder()
                .id(2L)
                .email("other@gmail.com")
                .build();

        Loan anotherLoan = Loan.builder()
                .id(2L)
                .user(anotherUser)
                .build();

        when(loanRepository.findById(2L)).thenReturn(Optional.of(anotherLoan));

        assertThatThrownBy(() -> loanService.getPaymentSchedule(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("icazəniz yoxdur");
    }

    @Test
    @DisplayName("Aylıq ödəniş düzgün hesablanmalıdır")
    void calculateMonthlyPayment_correct() {
        LoanDtos.LoanRequest request = new LoanDtos.LoanRequest(
                BigDecimal.valueOf(1000),
                12,
                BigDecimal.valueOf(12)
        );

        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setId(1L);
            return savedLoan;
        });

        LoanDtos.LoanResponse response = loanService.applyForLoan(request);

        assertThat(response.getMonthlyPayment())
                .isEqualByComparingTo(BigDecimal.valueOf(88.85));
    }
}