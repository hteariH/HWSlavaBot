package com.mamoru.hwslavabot.slavav2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SlaveController {

    @Autowired
    private SlaveRepository slavaRepository;

    @GetMapping("/slave")
    public List<Slave> getSlavas(){
        return slavaRepository.findAll();
    }

    @GetMapping("/slave/{id}")
    public Slave getSlave(@PathVariable("id") Long id){
        return slavaRepository.findById(id).orElse(null);
    }


    @PutMapping("/slave")
    public ResponseEntity<HttpStatus> putSlave(@RequestBody Slave slava){
        Slave slave = new Slave();
        slave.setName(slava.getName());
        slave.setMultiplier(slava.getMultiplier());
        slave.setChatId(slava.getChatId());
        slavaRepository.save(slave);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/slave/{id}")
    public ResponseEntity<HttpStatus> deleteSlave(@PathVariable("id") Long id){
        try {
            slavaRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
