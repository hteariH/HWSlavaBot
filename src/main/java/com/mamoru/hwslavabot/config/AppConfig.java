package com.mamoru.hwslavabot.config;

import com.mamoru.hwslavabot.bot.HWSlavaBot;
import com.mamoru.hwslavabot.state.State;
import com.mamoru.hwslavabot.state.StateTracker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import javax.naming.Context;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Configuration
@EnableScheduling
public class AppConfig {


    private final HWSlavaTelegramBotConfig botConfig;

    public AppConfig(HWSlavaTelegramBotConfig botConfig) {
        this.botConfig = botConfig;
    }

//    @Bean
//    public HWSlavaBot QuizTelegramBot(StateTracker stateTracker) {
//        DefaultBotOptions options = ApiContext
//                .getInstance(DefaultBotOptions.class);
//
//        HWSlavaBot telegramBot = new HWSlavaBot(options,stateTracker);
//        telegramBot.setBotUsername(botConfig.getUserName());
//        System.out.println(botConfig.getBotToken());
//        System.out.println(System.getenv(botConfig.getBotToken()));
//        telegramBot.setBotToken(System.getenv(botConfig.getBotToken()));
//        telegramBot.setBotPath(botConfig.getWebHookPath());
//
//        registerInTgApi(telegramBot.getBotToken(), telegramBot.getBotPath());
//
//        return telegramBot;
//    }

    @Bean
    public List<String> states(){
        return State.getList();
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
