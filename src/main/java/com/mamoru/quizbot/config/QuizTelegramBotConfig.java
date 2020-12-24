package com.mamoru.quizbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegrambot")
public class QuizTelegramBotConfig {
    String webHookPath;
    String userName;
    String botToken;
}
