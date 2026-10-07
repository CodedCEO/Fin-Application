package Audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class AuditAwareImpCard implements AuditorAware<String> {
    //AuditorAware<String> is the Spring Data interface for supplying the current user
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Admin");
    }

}
