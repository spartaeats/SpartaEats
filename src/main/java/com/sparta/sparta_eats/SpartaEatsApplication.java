package com.sparta.sparta_eats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SpartaEatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaEatsApplication.class, args);
    }

}
