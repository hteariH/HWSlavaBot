package com.mamoru.hwslavabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
//import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class HWSlavaBotApplication {

    public static void main(String[] args) {
//        ApiContextInitializer.init();

        SpringApplication.run(HWSlavaBotApplication.class, args);

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
