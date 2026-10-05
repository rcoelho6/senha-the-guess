package com.senhadeguess.backend.persistence;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PersistenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersistenceApplication.class, args);
    }

    @Bean
    Clock utcClock() {
        return Clock.systemUTC();
    }
}