package com.sparta.sparta_eats;

import com.sparta.sparta_eats.global.config.PaginationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableConfigurationProperties(PaginationProperties.class)
public class SpartaEatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpartaEatsApplication.class, args);
    }

}
