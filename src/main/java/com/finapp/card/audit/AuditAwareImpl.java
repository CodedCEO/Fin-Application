package com.finapp.card.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Return static system user or fetch authenticated username if Spring Security is present
        return Optional.of("CARDS_SERVICE");
    }
}