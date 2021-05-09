package pl.rjsk.librarymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class LibraryManagementAppConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
