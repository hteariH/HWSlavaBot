package com.mamoru.hwslavabot.webhook;

import com.mamoru.hwslavabot.bot.HWSlavaBot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {

    private final HWSlavaBot telegramBot;

    public WebhookController(HWSlavaBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        System.out.println("update recieved");
        System.out.println(update);
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
