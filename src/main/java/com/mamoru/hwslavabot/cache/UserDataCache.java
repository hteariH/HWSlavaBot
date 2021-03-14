package com.mamoru.hwslavabot.cache;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * In-memory cache.
 *
 * usersBotStates: user_id and user's bot state
 *
 */
@Service
public class UserDataCache{
    private Map<Integer, BotState> usersBotStates = new HashMap<>();

    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MENU;
        }

        return botState;
    }

}
