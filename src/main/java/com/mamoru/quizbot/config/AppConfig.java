package com.mamoru.quizbot.config;

import com.mamoru.quizbot.bot.QuizTelegramBot;
import com.mamoru.quizbot.bot.TelegramFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
public class AppConfig {


    private final QuizTelegramBotConfig botConfig;

    public AppConfig(QuizTelegramBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public QuizTelegramBot QuizTelegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);

        QuizTelegramBot telegramBot = new QuizTelegramBot(options, telegramFacade);
        telegramBot.setBotUsername(botConfig.getUserName());
        System.out.println(botConfig.getBotToken());
        System.out.println(System.getenv(botConfig.getBotToken()));
        telegramBot.setBotToken(System.getenv(botConfig.getBotToken()));
        telegramBot.setBotPath(botConfig.getWebHookPath());

        registerInTgApi(telegramBot.getBotToken(), telegramBot.getBotPath());

        return telegramBot;
    }

    private void registerInTgApi(String botToken, String botPath) {
        URL url = null;
        try {
            url = new URL("https://api.telegram.org/bot" + botToken + "/setWebhook?url=" + botPath);
            System.out.println("url="+url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("responsecode=" + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
