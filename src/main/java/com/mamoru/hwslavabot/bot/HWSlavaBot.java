package com.mamoru.hwslavabot.bot;

import com.mamoru.hwslavabot.commons.Command;
import com.mamoru.hwslavabot.commons.Slava;
import com.mamoru.hwslavabot.commons.SlavaRepository;
import com.mamoru.hwslavabot.state.State;
import com.mamoru.hwslavabot.state.StateTracker;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Getter
@Setter
public class HWSlavaBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(HWSlavaBot.class);
    private String botPath;
    private String botUsername;
    private String botToken;

    private StateTracker stateTracker;

    @Autowired
    private SlavaRepository slavaRepository;


    public HWSlavaBot(DefaultBotOptions options, StateTracker stateTracker) {
        super(options);
        this.stateTracker = stateTracker;
    }


    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        SendMessage replyMessage = telegramFacade.handleUpdate(update);
        if (update.hasMessage() && update.getMessage().hasText()) {

            String incomingText = update.getMessage().getText();
            User sender = update.getMessage().getFrom();

            /* manage commands */

            if (incomingText.startsWith(Command.addSlava)) {

                return onCommandAddSlava(update);
            } else {//
//            } else if (incomingText.startsWith(Command.HELP)) {
//
//                return onCommandHelp(update);
//
//            } else {

                /* manage plain text with no commands */

                return manageHomeState(update);

            }

//        return replyMessage;
        }
        return null;
    }

    private BotApiMethod<?> onCommandAddSlava(Update update) {
        String s = update.getMessage().getText().split(" ")[1];
        System.out.println(update.getMessage().getText());
        System.out.println(s);
        Optional<Slava> byId = slavaRepository.findById(s);
        if(byId.isPresent()) {
            Slava slava = byId.get();
            return new SendMessage(update.getMessage().getChatId(),s + " already present");
        } else {
            Slava slava = new Slava();
            slava.setId(s);
        }
        return new SendMessage(update.getMessage().getChatId(),s + " added");
    }

    private BotApiMethod<?> manageHomeState(Update update) {
        String text = update.getMessage().getText();
        if (update.getMessage().getText().toLowerCase(Locale.ROOT).contains("слава украине")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
//            sendMessage.setText(getRandomWord() + " Слава!");
            sendMessage.setText(getRandomWordBD() + " Слава!");
//            new SetChatAdministratorCustomTitle()
            return sendMessage;
//            return new SendMessage(update.getMessage().getChatId(), getRandomWord() + " Слава!");
//            try {
//                execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//            return new PromoteChatMember(update.getMessage().getChatId(),update.getMessage().getFrom().getId());
        }
        if (update.getMessage().getFrom().getUserName().equals("BraveMamoru") && update.getMessage().getText().equals("Слава Украине!!!1")) {
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), getRandomWord() + " Слава!1");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return new SendMessage(update.getMessage().getChatId().toString(), getRandomWord() + " Слава!");

        }
        return null;
//        switch (text) {
//            case "Записаться на занятия":
//                return goToSignUp(update);
//            default:
//                return new SendMessage(update.getMessage().getChatId(), update.getMessage().getText());
//        }
    }

    private String getRandomWordBD() {
        Random rnd = new Random();
        Iterable<Slava> all = slavaRepository.findAll();
        List<String> result = new ArrayList<>();
        all.forEach(slava -> result.add(slava.getId()));
        int i = rnd.nextInt(result.size() - 1);
        return result.get(i);
    }

    private String getRandomWord() {
        ArrayList<String> words= new ArrayList<>();
        words.add("Героям");
        words.add("Беларуси");
        words.add("Лукашенке");
        words.add("Пыне");
        words.add("США");
        words.add("Героям");
        words.add("Холиварсу");
        words.add("Беркуту");
        words.add("Коммунизму");
        Random rnd = new Random();
        int i = rnd.nextInt(words.size()-1);
        return words.get(i);

    }

    private BotApiMethod<?> goToSignUp(Update update) {
        stateTracker.move(update.getMessage().getContact().getUserID(), State.SIGN_UP);

        return null;
    }

    private BotApiMethod<?> onCommandHelp(Update update) {
        return null;
    }

    private SendMessage onCommandStart(Update update) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        return createMessageWithKeyboard(update.getMessage().getChatId(), update.getMessage().getText(), replyKeyboardMarkup);
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        row1.add(new KeyboardButton("Записаться на занятия"));
        row2.add(new KeyboardButton("О нас"));
        row3.add(new KeyboardButton("Купить ролики"));
        row4.add(new KeyboardButton("Оставить отзыв"));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }

}
