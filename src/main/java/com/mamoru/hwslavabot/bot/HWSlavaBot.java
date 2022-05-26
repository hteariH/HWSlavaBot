package com.mamoru.hwslavabot.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mamoru.hwslavabot.commons.Command;
import com.mamoru.hwslavabot.slavav2.Slave;
import com.mamoru.hwslavabot.slavav2.SlaveRepository;
import com.mamoru.hwslavabot.state.StateTracker;
import com.mamoru.hwslavabot.users.Chatter;
import com.mamoru.hwslavabot.users.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@Component
public class HWSlavaBot extends TelegramWebhookBot {
    private static final Logger logger = LoggerFactory.getLogger(HWSlavaBot.class);

    @Value("${telegrambot.webHookPath}")
    private String botPath;

    @Value("${telegrambot.userName}")
    private String botUsername;

    @Value("${BOT_TOKEN}")
    private String botToken;

    private Integer oldMessageId;
    private StateTracker stateTracker;

    @Autowired
    private SlaveRepository slavaRepository;
    private long karnoObosralsa = 0L;
    private boolean deleteCarnoPHoto = false;

    @Override
    public void onRegister() {
        System.out.println("REGISTERINGBOT");
        System.out.println(botUsername);
        System.out.println(botPath);
        System.out.println(botToken);
        URL url = null;
        try {
            url = new URL("https://api.telegram.org/bot" + botToken + "/setWebhook?url=" + botPath);
            System.out.println("url=" + url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("responsecode=" + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



//    public HWSlavaBot(DefaultBotOptions options, StateTracker stateTracker) {
//        super(options);
//        this.stateTracker = stateTracker;
//    }

    @Autowired
    private UserRepository userRepository;

    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
//        SendMessage replyMessage = telegramFacade.handleUpdate(update);
        try {
            if (update.hasMessage()) {

                Message message = update.getMessage();
                String incomingText = message.getText();
                /* manage commands */
                if (update.getMessage().hasText()) {
                    if (incomingText.startsWith(Command.addSlava)) {
                        return onCommandAddSlava(update);
                    } else if (incomingText.startsWith(Command.deleteSlava)) {
                        return onCommandDeleteSlava(update);
                    } else if (incomingText.startsWith("/start")) {
                        return new SendMessage(String.valueOf(update.getMessage().getChatId()),"Щоб користуватись ботом є дві команди:\n" +
                                "/get <Назва міста> - Отримати список азс із бензином в наявності\n" +
                                "/subscribe <Назва міста> - Підписатися на нотіфікації коли на нових азс в вказаному місті з'явиться бензин\n" +
                                "Наприклад:\n" +
                                "/get Київ");
                    } else if (incomingText.startsWith(Command.listSlava)) {
                        return onCommandListSlava(update);
                    } else if (incomingText.startsWith("/get")) {
                        try {
                            String[] s = update.getMessage().getText().split(" ");
                            String city = null;
                            if (s.length > 1) {
                                city = s[1];
                            }
                            List<String> fuel = getFuel(city);
                            if (fuel.isEmpty()){
                                execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), "Немає бензину на жодній заправці"));
                            }
                            fuel.forEach(f -> {
                                try {
                                    execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), f));
                                } catch (TelegramApiException e) {
//                                    e.printStackTrace();
                                }
                            });
                            return null;
                        } catch (Exception e) {
                        }
                    } else if (incomingText.startsWith("/subscribe")) {
                        try {
                            String[] s = update.getMessage().getText().split(" ");
                            String city = "";
                            if (s.length > 1) {
                                city = s[1];
                            }
                            String s1 = String.valueOf(update.getMessage().getChatId());
                            Optional<Chatter> byChatId = userRepository.findFirstByChatId(s1);

                            Chatter user = byChatId.orElseGet(() -> {
                                Chatter user1 = new Chatter();
                                user1.setChatId(s1);
                                return user1;
                            });
                            user.setCity(city);
                            System.out.println(user);
                            userRepository.save(user);
//                            execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), "Subs"));

                            List<String> fuel = getFuel(city);
                            if (fuel.isEmpty()){
                                execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), "Немає бензину на жодній заправці"));
                            }
                            fuel.forEach(f -> {
                                try {
                                    execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), f));
                                } catch (TelegramApiException e) {
//                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                }

//        return replyMessage;
            }
        } catch (TelegramApiException e) {
            System.out.println("error" + e.getMessage());

            return null;
        }
        return null;
    }


    private BotApiMethod<?> onCommandListSlava(Update update) throws TelegramApiException {

        StringBuilder stringBuilder = new StringBuilder();
        List<Slave> all = slavaRepository.findAllByChatIdOrderById(String.valueOf(update.getMessage().getChatId()));
        all.sort(Comparator.comparing(Slave::getName));
        all.forEach(slava -> stringBuilder.append(slava.getName()).append(" ").append(slava.getMultiplier()).append("\n"));
        Message execute = execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), stringBuilder.toString()));
        if (oldMessageId != null) {
            System.out.println("oldMessage=" + oldMessageId);
            execute(new DeleteMessage(String.valueOf(update.getMessage().getChatId()), oldMessageId));
        }
        oldMessageId = execute.getMessageId();
        System.out.println("newMessage=" + execute.getMessageId());
        return null;
    }

    @Transactional
    public BotApiMethod<?> onCommandDeleteSlava(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        List<String> list = new ArrayList<>(List.of(update.getMessage().getText().split(" ")));
        list.remove(0);
        String collect = list.stream().map(str -> str + " ").collect(Collectors.joining());
        String trim = collect.trim();
        if (trim.isEmpty()) {
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
        multiplier = Math.abs(multiplier);
        if (multiplier > 100) {
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
                return new SendMessage(String.valueOf(update.getMessage().getChatId()), res + " already present");
            } else {
                slava.setMultiplier(multiplier);
                slavaRepository.save(slava);
                return new SendMessage(update.getMessage().getChatId().toString(), res + " multiplier set to " + multiplier);
            }
        } else {
            Slave slava = new Slave();
            slava.setName(res);
            slava.setMultiplier(multiplier);
            slava.setChatId(String.valueOf(update.getMessage().getChatId()));
            slavaRepository.save(slava);
        }
        return new SendMessage(update.getMessage().getChatId().toString(), res + " added");
    }

    private BotApiMethod<?> onSlavaUkraineReply(Update update) {
        String text = update.getMessage().getText();
        if (text.toLowerCase(Locale.ROOT).contains("слава україні") || text.toLowerCase(Locale.ROOT).contains("слава украине")) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            String randomWordBD = getRandomWordBD(String.valueOf(update.getMessage().getChatId()));
            String slava = getSlava(randomWordBD);

            sendMessage.setText(randomWordBD + slava);
            return sendMessage;
        }
//        else if(text.toLowerCase(Locale.ROOT).contains("слава украине")){
//
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.setChatId(update.getMessage().getChatId());
//
//            sendMessage.setText("Русский корабль иди нахуй");
//            return sendMessage;
//        }
        return null;
    }

    private String getSlava(String randomPhrase) {

        if (randomPhrase.toUpperCase().equals(randomPhrase)) {
            return " СЛАВА!";
        } else if (randomPhrase.toLowerCase().equals(randomPhrase)) {
            return " слава!";
        } else {
            return " Слава!";
        }
    }

    private String getRandomWordBD(String chatId) {
        Random rnd = new Random();

        List<Slave> all = slavaRepository.findAllByChatIdOrderById(chatId);
        if (all.isEmpty()) {
            return "Героям";
        }
        List<Pair<Slave, Double>> collect = all.stream().map(i -> new Pair<>(i, Double.valueOf(i.getMultiplier()))).collect(Collectors.toList());
        Slave sample = new EnumeratedDistribution<>(collect).sample();
        return sample.getName();
//        List<String> result = new LinkedList<>();
//        all.forEach(slava -> {
//            Integer multiplier = slava.getMultiplier();
//            IntStream.range(0, multiplier)
//                    .mapToObj(i -> slava.getName())
//                    .forEach(result::add);
//        });
//        System.out.println(result.size());
//        if (result.size() == 0) {
//            return "Героям";
//        }
//        int i = rnd.nextInt(result.size());
//        return result.get(i);
    }


    String stationsURL = "https://api.wog.ua/fuel_stations";

    Map<String, Boolean> fuel = new HashMap<>();

    @Scheduled(fixedDelay = 90000)
    public void checkFuel() throws JsonProcessingException, TelegramApiException {

        List<Chatter> all = userRepository.findAll();

        for (Chatter user : all) {
            ArrayList<String> stationsNumbers = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(stationsURL, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode path = root.path("data").path("stations");
            ArrayNode stations = (ArrayNode) path;
            for (JsonNode station : stations) {
                System.out.println("CITY:" + station.path("city").asText());
                if (user.getCity().equals("")){
                    stationsNumbers.add(String.valueOf(station.get("id").asLong()));
                } else
                if (station.path("city").asText().equalsIgnoreCase(user.getCity())) {
                    stationsNumbers.add(String.valueOf(station.get("id").asLong()));
                }
            }
            ArrayList<String> objects = new ArrayList<>();
            for (String stationsNumber : stationsNumbers) {
                if (!fuel.containsKey(stationsNumber)) {
                    fuel.put(stationsNumber, false);
                }
                ResponseEntity<String> forEntity = restTemplate.getForEntity(stationsURL + "/" + stationsNumber, String.class);
                JsonNode tree = mapper.readTree(forEntity.getBody());
                String s = tree.path("data").path("workDescription").asText();

                System.out.println("DESCRIPTION:" + s);
                if (s.contains("95 - Готівка")) {
                    System.out.println("M95");
                    JsonNode name = tree.path("data").path("name");
                    System.out.println(name.asText());
                    if (!fuel.get(stationsNumber)) {
                        fuel.put(stationsNumber, true);
                        execute(new SendMessage(user.getChatId(), name.asText()));
                    }
                } else {
                    fuel.put(stationsNumber, false);
                }

            }
        }


    }


    public List<String> getFuel(String city) throws JsonProcessingException, TelegramApiException {

        ArrayList<String> stationsNumbers = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(stationsURL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode path = root.path("data").path("stations");
        ArrayNode stations = (ArrayNode) path;
        for (JsonNode station : stations) {
            System.out.println("CITY:" + station.path("city").asText());
            if (city == null){
                stationsNumbers.add(String.valueOf(station.get("id").asLong()));
            } else if (station.path("city").asText().equalsIgnoreCase(city)) {
                stationsNumbers.add(String.valueOf(station.get("id").asLong()));
            }
        }
        ArrayList<String> objects = new ArrayList<>();
        for (String stationsNumber : stationsNumbers) {
            ResponseEntity<String> forEntity = restTemplate.getForEntity(stationsURL + "/" + stationsNumber, String.class);
            JsonNode tree = mapper.readTree(forEntity.getBody());
            String s = tree.path("data").path("workDescription").asText();

            System.out.println("DESCRIPTION:" + s);
            if (s.contains("95 - Готівка")) {
                System.out.println("M95");
                JsonNode name = tree.path("data").path("name");
                System.out.println(name.asText());
                objects.add(name.asText());
//                }
            }
        }
        return objects;
    }

}
