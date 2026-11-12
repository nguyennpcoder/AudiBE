package com.audistore.audi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AudiApplication {

    public static void main(String[] args) {

        SpringApplication.run(AudiApplication.class, args);
    }
}