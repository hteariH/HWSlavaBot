package com.mamoru.hwslavabot.slava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SlavaController {

    @Autowired
    private SlavaRepository slavaRepository;

    @GetMapping("/slava")
    public List<Slava> getSlavas(){
        return slavaRepository.findAll();
    }

    @GetMapping("/slava/{id}")
    public Slava getSlava(@PathVariable("id") String id){

        return slavaRepository.findById(id).orElse(null);
    }


}
