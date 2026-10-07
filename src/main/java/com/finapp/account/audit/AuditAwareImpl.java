package com.finapp.account.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component //registers the class as a Spring bean, so Spring finds it automatically and can call it.
public class AuditAwareImpl implements AuditorAware<String> {
//AuditorAware<String> is the Spring Data interface for supplying the current user
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("ACCOUNTS_MS");
    }

}
