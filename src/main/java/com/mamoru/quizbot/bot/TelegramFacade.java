package com.mamoru.quizbot.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramFacade {
    public SendMessage handleUpdate(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        System.out.println("update");
        System.out.println(update.getMessage().getText());
        return sendMessage;
    }
}
