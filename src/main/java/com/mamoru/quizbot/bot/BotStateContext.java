package com.mamoru.quizbot.bot;

import com.mamoru.quizbot.menu.ShowFutureGamesService;
import com.mamoru.quizbot.cache.BotState;
import com.mamoru.quizbot.menu.MainMenuService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BotStateContext {
    MainMenuService mainMenuService;
    ShowFutureGamesService gamesService;

    public BotStateContext(MainMenuService mainMenuService, ShowFutureGamesService gamesService) {
        this.mainMenuService = mainMenuService;
        this.gamesService = gamesService;
    }

    public SendMessage processInputMessage(BotState botState, Message message) {
        if (botState.equals(BotState.SHOW_MENU)){
            return showMenu(message.getChatId(),"Меню");
        } else if(botState.equals(BotState.SHOW_FUTURE_GAMES)){
            return showFutureGames(message.getChatId(),"Будущие игры");
        }

        return new SendMessage(message.getChatId(),"not yet implemented");
    }

    private SendMessage showFutureGames(Long chatId, String textMessage) {
        return gamesService.getFutureGamesMenuMessage(chatId,textMessage);

    }

    private SendMessage showMenu(Long chatId, String textMessage) {

        return mainMenuService.getMainMenuMessage(chatId,textMessage);
    }
}
