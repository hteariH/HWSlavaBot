package com.mamoru.hwslavabot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegrambot")
public class HWSlavaTelegramBotConfig {
    String webHookPath;
    String userName;
    String botToken;
}
