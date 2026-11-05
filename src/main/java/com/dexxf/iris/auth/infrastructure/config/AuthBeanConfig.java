package com.dexxf.iris.auth.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Clock;

@Configuration
public class AuthBeanConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(@Value("${security.hash.bcrypt.strength}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
