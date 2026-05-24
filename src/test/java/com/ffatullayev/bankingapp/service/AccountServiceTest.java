package com.ffatullayev.bankingapp.service;

import com.ffatullayev.bankingapp.dto.account.AccountDtos;
import com.ffatullayev.bankingapp.entity.Account;
import com.ffatullayev.bankingapp.entity.User;
import com.ffatullayev.bankingapp.entity.enums.AccountStatus;
import com.ffatullayev.bankingapp.entity.enums.AccountType;
import com.ffatullayev.bankingapp.entity.enums.Role;
import com.ffatullayev.bankingapp.repository.AccountRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit testlər")
public class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AccountService accountService;

  private User currentUser;
  private Account account;

  @BeforeEach
  void setUp() {
    currentUser = User.builder()
        .id(1L)
        .email("fuad@gmail.com")
        .firstName("Farid")
        .lastName("Fətullayev")
        .role(Role.CUSTOMER)
        .enabled(true)
        .build();

    account = Account.builder()
        .id(1L)
        .iban("AZ03BANK8745300815942568")
        .type(AccountType.CURRENT)
        .status(AccountStatus.ACTIVE)
        .balance(BigDecimal.valueOf(1000))
        .user(currentUser)
        .build();

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("farid@gmail.com");
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.findByEmail("farid@gmail.com"))
        .thenReturn(Optional.of(currentUser));
  }

  @Test
  @DisplayName("Uğurlu hesab açma — hesab qaytarılmalıdır")
  void createAccount_success() {
    AccountDtos.CreateAccountRequest request = new AccountDtos.CreateAccountRequest();
    request.setType(AccountType.CURRENT);

    when(accountRepository.existsByIban(any())).thenReturn(false);
    when(accountRepository.save(any(Account.class))).thenReturn(account);

    AccountDtos.AccountResponse response = accountService.createAccount(request);

    assertThat(response).isNotNull();
    assertThat(response.getIban()).isEqualTo("AZ03BANK8745300815942568");
    assertThat(response.getType()).isEqualTo(AccountType.CURRENT);
    assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    verify(accountRepository).save(any(Account.class));
  }

  @Test
  @DisplayName("Hesablarım — siyahı qaytarılmalıdır")
  void getMyAccounts_success() {
    when(accountRepository.findByUserId(currentUser.getId()))
        .thenReturn(List.of(account));

    List<AccountDtos.AccountResponse> responses = accountService.getMyAccounts();

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getIban()).isEqualTo("AZ03BANK8745300815942568");
  }

  @Test
  @DisplayName("IBAN-a görə hesab — düzgün hesab qaytarılmalıdır")
  void getAccountByIban_success() {
    when(accountRepository.findByIban("AZ03BANK8745300815942568"))
        .thenReturn(Optional.of(account));

    AccountDtos.AccountResponse response =
        accountService.getAccountByIban("AZ03BANK8745300815942568");

    assertThat(response.getIban()).isEqualTo("AZ03BANK8745300815942568");
    assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
  }

  @Test
  @DisplayName("Başqasının hesabına giriş — exception atılmalıdır")
  void getAccountByIban_notOwner_throwsException() {
    User anotherUser = User.builder()
        .id(2L)
        .email("other@gmail.com")
        .build();

    Account anotherAccount = Account.builder()
        .id(2L)
        .iban("AZ99BANK1234567890123456")
        .user(anotherUser)
        .build();

    when(accountRepository.findByIban("AZ99BANK1234567890123456"))
        .thenReturn(Optional.of(anotherAccount));

    assertThatThrownBy(() ->
        accountService.getAccountByIban("AZ99BANK1234567890123456"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("icazəniz yoxdur");
  }

}
