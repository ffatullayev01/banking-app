package com.ffatullayev.bankingapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex){
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, ex.getMessage());

    problem.setTitle("Email artıq mövcuddur");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, "Email və ya şifrə yanlışdır");
    problem.setTitle("Authentication Uğursuz Oldu");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUserNotFound(UsernameNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("İstifadəçi Tapılmadı");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex){
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Yanlış Sorğu");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex){
    Map<String, String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fe -> fe.getDefaultMessage() != null
            ? fe.getDefaultMessage() : "Yanlış dəyər"
        ));

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Validation xətası");
    problem.setTitle("Validation uğursuz oldu");
    problem.setProperty("errors", errors);
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneral(Exception ex){
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    problem.setTitle("Sistem xətası");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

}
