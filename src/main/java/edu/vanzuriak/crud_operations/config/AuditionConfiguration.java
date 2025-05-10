package edu.vanzuriak.crud_operations.config;

/*
    @author  olexander
    @project crud_operations
    @class   AuditionConfiguration
    @version 1.0.0
    @since 5/10/25 - 09 - 25
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@Configuration
public class AuditionConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImplementation();
    }
}
