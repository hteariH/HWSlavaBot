package com.mamoru.quizbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class QuizbotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(QuizbotApplication.class, args);

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
