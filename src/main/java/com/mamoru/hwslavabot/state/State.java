package com.mamoru.hwslavabot.state;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class State {

    public static final String FIRST_STATE = "FIRST_STATE";
    public static final String HOME = "HOME";
    public static final String SIGN_UP = "SIGN_UP";
    public static final String SECOND_STATE = "SECOND_STATE";

    private static List<String> STATES = new ArrayList<>();

    static {
        STATES.add(FIRST_STATE);
        STATES.add(SECOND_STATE);
        STATES.add(HOME);
        STATES.add(SIGN_UP);
    }

    /* TODO: add your states here */

    public static List<String> getList() {
        return STATES;
    }

}
