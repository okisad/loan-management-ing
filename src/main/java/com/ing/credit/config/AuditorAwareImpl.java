package com.ing.credit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String user;
            try {
                user = JwtTokenContext.getUsername();
            }catch (Exception e) {
                user = "system";
            }

            return Optional.of(user);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.of("system");
        }


    }
}
