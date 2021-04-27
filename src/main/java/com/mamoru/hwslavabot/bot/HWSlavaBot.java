package com.mamoru.hwslavabot.bot;

import com.mamoru.hwslavabot.commons.Command;
import com.mamoru.hwslavabot.slavav2.Slave;
import com.mamoru.hwslavabot.slavav2.SlaveRepository;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
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
    private Integer oldMessageId;
    private StateTracker stateTracker;

    @Autowired
    private SlaveRepository slavaRepository;


    public HWSlavaBot(DefaultBotOptions options, StateTracker stateTracker) {
        super(options);
        this.stateTracker = stateTracker;
    }


    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        SendMessage replyMessage = telegramFacade.handleUpdate(update);
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {

                String incomingText = update.getMessage().getText();
                User sender = update.getMessage().getFrom();
                /* manage commands */

                if (incomingText.startsWith(Command.addSlava)) {
                    return onCommandAddSlava(update);
                } else if (incomingText.startsWith(Command.deleteSlava)) {
                    return onCommandDeleteSlava(update);
                } else if (incomingText.startsWith(Command.listSlava)) {
                    return onCommandListSlava(update);
                } else {
                    //
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
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
            return null;
        }
        return null;
    }

    private BotApiMethod<?> onCommandListSlava(Update update) throws TelegramApiException {
        if (update.getMessage().getFrom().getId().equals(906452258)) {
            return new SendMessage(String.valueOf(update.getMessage().getChatId()), "Слава Навальному!");
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Slave> all = slavaRepository.findAllByChatIdOrderById(String.valueOf(update.getMessage().getChatId()));
        all.forEach(slava -> stringBuilder.append(slava.getName()).append(" ").append(slava.getMultiplier()).append("\n"));
        Message execute = execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), stringBuilder.toString()));
        if(oldMessageId!=null){
            System.out.println("oldMessage="+oldMessageId);
            execute(new DeleteMessage(String.valueOf(update.getMessage().getChatId()),oldMessageId));
        }
        oldMessageId=execute.getMessageId();
        System.out.println("newMessage=" + execute.getMessageId());
        return null;
    }

    @Transactional
    public BotApiMethod<?> onCommandDeleteSlava(Update update) {
        if (update.getMessage().getFrom().getId().equals(906452258)) {
            return new SendMessage(String.valueOf(update.getMessage().getChatId()), "Слава Нации!");
        }
        String chatId = String.valueOf(update.getMessage().getChatId());
        List<String> list = new ArrayList<>(List.of(update.getMessage().getText().split(" ")));
        list.remove(0);
        String collect = list.stream().map(str -> str + " ").collect(Collectors.joining());
        String trim = collect.trim();
        if(trim.isEmpty()){
            return new SendMessage(String.valueOf(update.getMessage().getChatId()), "анус свой удали, пёс");
        }
        Optional<Slave> byNameAndChatId = slavaRepository.findFirstByNameAndChatId(trim, chatId);
        if (byNameAndChatId.isPresent()) {
            slavaRepository.deleteByNameAndChatId(trim, chatId);
        } else {
            //TODO DELETE EVERYTHING IGNORE CASE
        }

        return new SendMessage(String.valueOf(update.getMessage().getChatId()), trim + " deleted");
    }

    private BotApiMethod<?> onCommandAddSlava(Update update) {
        if (update.getMessage().getFrom().getId().equals(906452258)) {
            return new SendMessage(String.valueOf(update.getMessage().getChatId()), "Слава Нации!");
        }

        String[] s = update.getMessage().getText().split(" ");
        List<String> list = new ArrayList<>(List.of(s));
        int multiplier;
        String remove;
        try {
            remove = list.get(list.size() - 1);
            multiplier = Integer.parseInt(remove);
            list.remove(list.size() - 1);
        } catch (NumberFormatException e) {
            multiplier = 1;
        }
        multiplier= Math.abs(multiplier);
        if (multiplier>100){
            multiplier = 100;
        }
        list.remove(0);
        String res = list.stream().map(str -> str + " ").collect(Collectors.joining());
        res = res.trim();
        System.out.println(update.getMessage().getText());
        System.out.println(res);
        Optional<Slave> byNameAndChatId = slavaRepository.findFirstByNameAndChatId(res, String.valueOf(update.getMessage().getChatId()));
        if (byNameAndChatId.isPresent()) {
            Slave slava = byNameAndChatId.get();
            if (slava.getMultiplier().equals(multiplier)) {
                return new SendMessage(update.getMessage().getChatId(), res + " already present");
            } else {
                slava.setMultiplier(multiplier);
                slavaRepository.save(slava);
                return new SendMessage(update.getMessage().getChatId(), res + " multiplier set to " + multiplier);
            }
        } else {
            Slave slava = new Slave();
            slava.setName(res);
            slava.setMultiplier(multiplier);
            slava.setChatId(String.valueOf(update.getMessage().getChatId()));
            slavaRepository.save(slava);
        }
        return new SendMessage(update.getMessage().getChatId(), res + " added");
    }

    private BotApiMethod<?> onSlavaUkraineReply(Update update) {
        String text = update.getMessage().getText();
        if (text.toLowerCase(Locale.ROOT).contains("слава украине")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            String randomWordBD = getRandomWordBD(String.valueOf(update.getMessage().getChatId()));
            String slava = getSlava(randomWordBD);

            sendMessage.setText(randomWordBD + slava);
            return sendMessage;
        }
        return null;
    }

    private String getSlava(String randomPhrase) {

        if(randomPhrase.toUpperCase().equals(randomPhrase)){
            return " СЛАВА!";
        } else if(randomPhrase.toLowerCase().equals(randomPhrase)) {
            return " слава!";
        } else {
            return " Слава!";
        }
    }

    private String getRandomWordBD(String chatId) {
        Random rnd = new Random();

        List<Slave> all = slavaRepository.findAllByChatIdOrderById(chatId);

        List<String> result = new LinkedList<>();
        all.forEach(slava -> {
            Integer multiplier = slava.getMultiplier();
            IntStream.range(0, multiplier)
                    .mapToObj(i -> slava.getName())
                    .forEach(result::add);
        });
        System.out.println(result.size());
        if (result.size() == 0) {
            return "Героям";
        }
        int i = rnd.nextInt(result.size());
        return result.get(i);
    }

}
