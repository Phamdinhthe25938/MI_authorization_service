package com.example.demomicroservice.config.auditor;

import com.example.demomicroservice.model.dto.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    try {
      CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
          .getPrincipal();
      return Optional.of(user.getUsername());
    } catch (Exception e) {
      return Optional.of("SYSTEM");
    }
  }
}
