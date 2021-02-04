package com.mamoru.quizbot.bot;

import com.mamoru.quizbot.cache.BotState;
import com.mamoru.quizbot.cache.UserDataCache;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramFacade {

    private final UserDataCache userDataCache;
    private BotStateContext botStateContext;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        System.out.println("update");
        System.out.println(update.getMessage().getText());
        SendMessage replyMessage = null;
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            System.out.printf("New message from User:%s, chatId: %s,  with text: %s",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());

            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "На главную":
                botState = BotState.SHOW_MENU;
                break;
            case "Регистрация":
                botState = BotState.SHOW_FUTURE_GAMES;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
