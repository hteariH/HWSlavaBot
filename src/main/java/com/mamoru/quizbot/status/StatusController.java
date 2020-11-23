package com.mamoru.quizbot.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/status")
    public Status status(){
        return new Status("ok heroku");
    }
}
