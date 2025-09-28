package com.mayur.askvault;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class AskVaultApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AskVaultApplication.class, args);
    }
}

