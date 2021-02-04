package com.mamoru.quizbot.bot;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class QuizTelegramBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(QuizTelegramBot.class);
    private String botPath;
    private String botUsername;
    private String botToken;

    private TelegramFacade telegramFacade;

    public QuizTelegramBot(DefaultBotOptions options, TelegramFacade telegramFacade) {
        super(options);
        this.telegramFacade = telegramFacade;

    }


    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage replyMessage = telegramFacade.handleUpdate(update);

        return replyMessage;
    }

}
