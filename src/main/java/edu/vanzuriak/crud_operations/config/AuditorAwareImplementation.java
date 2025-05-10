package edu.vanzuriak.crud_operations.config;

/*
    @author  olexander
    @project crud_operations
    @class   AuditorAware
    @version 1.0.0
    @since 5/10/25 - 09 - 21
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImplementation implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("admin");
    }
}
