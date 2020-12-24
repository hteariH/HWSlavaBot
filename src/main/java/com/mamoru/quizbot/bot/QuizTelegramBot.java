package com.mamoru.quizbot.bot;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Getter
@Setter
public class QuizTelegramBot extends TelegramLongPollingBot {
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            response.setChatId(String.valueOf(chatId));
            String text = message.getText();
            response.setText(text);
            try {
                execute(response);
                System.out.println("Sent message \"{" + text + "}\" to {" + chatId + "}");
            } catch (TelegramApiException e) {
                System.out.println("Failed to send message \"{\" + text + \"}\" to {\" + chatId + \"} due to error: {" + e.getMessage() + "}");
            }
        }
    }
}
