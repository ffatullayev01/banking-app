package com.ffatullayev.bankingapp.aspect;

import com.ffatullayev.bankingapp.annotation.Auditable;
import com.ffatullayev.bankingapp.entity.AuditLog;
import com.ffatullayev.bankingapp.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

  private final AuditLogRepository auditLogRepository;

  @Around("@annotation(com.ffatullayev.bankingapp.annotation.Auditable)")
  public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Auditable auditable = signature.getMethod().getAnnotation(Auditable.class);

    String userEmail = getUserEmail();
    String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
    String action = auditable.action();

    Object result;
    try {
      result = joinPoint.proceed();

      AuditLog auditLog = AuditLog.builder()
          .userEmail(userEmail)
          .action(action)
          .methodName(methodName)
          .result("SUCCESS")
          .build();

      auditLogRepository.save(auditLog);
      log.info("Audit log yazıldı: user={}, action={}", userEmail, action);

      return result;
    } catch (Throwable ex) {
      AuditLog auditLog = AuditLog.builder()
          .userEmail(userEmail)
          .action(action)
          .methodName(methodName)
          .result("FAILED: " + ex.getMessage())
          .build();

      auditLogRepository.save(auditLog);
      log.error("Audit log yazıldı (xəta): user={}, action={}, səbəb={}",
          userEmail, action, ex.getMessage());

      throw ex;
    }
  }

  private String getUserEmail() {
    try {
      return SecurityContextHolder.getContext()
          .getAuthentication()
          .getName();
    } catch (Exception e) {
      return "anonymous";
    }
  }
}
