package com.mamoru.hwslavabot.bot;

import com.mamoru.hwslavabot.commons.Command;
import com.mamoru.hwslavabot.slava.Slava;
import com.mamoru.hwslavabot.slava.SlavaRepository;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

                return onSlavaUkraineReply(update);

            }

//        return replyMessage;
        }
        return null;
    }

    private BotApiMethod<?> onCommandAddSlava(Update update) {
        String[] s = update.getMessage().getText().split(" ");
        List<String> list = new ArrayList<>(List.of(s));
        String multiplier = list.remove(list.size() - 1);
        list.remove(0);
        String res = list.stream().map(str -> str + " ").collect(Collectors.joining());
        res = res.trim();
        System.out.println(update.getMessage().getText());
        System.out.println(res);
        Optional<Slava> byId = slavaRepository.findById(res);
        if (byId.isPresent()) {
            Slava slava = byId.get();
            if(slava.getMultiplier().equals(Integer.parseInt(multiplier))){
                return new SendMessage(update.getMessage().getChatId(), res + " already present");
            }else {
                slava.setMultiplier(Integer.parseInt(multiplier));
                slavaRepository.save(slava);
                return new SendMessage(update.getMessage().getChatId(),res + " multiplier set to "+multiplier);
            }
        } else {
            Slava slava = new Slava();
            slava.setId(res);
            slava.setMultiplier(Integer.parseInt(multiplier));
            slavaRepository.save(slava);
        }
        return new SendMessage(update.getMessage().getChatId(), res + " added");
    }

    private BotApiMethod<?> onSlavaUkraineReply(Update update) {
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
        List<Slava> all = slavaRepository.findAll();
        List<String> result = new ArrayList<>();
        all.forEach(slava -> {
            Integer multiplier = slava.getMultiplier();
            IntStream.range(0, multiplier)
                    .mapToObj(i -> slava.getId())
                    .forEach(result::add);
        });
        System.out.println(result.size());
        int i = rnd.nextInt(result.size());
        return result.get(i);
    }

    private String getRandomWord() {
        ArrayList<String> words = new ArrayList<>();
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
        int i = rnd.nextInt(words.size() - 1);
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
