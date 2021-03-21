package com.mamoru.hwslavabot.slava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PutMapping("/slava")
    public ResponseEntity<HttpStatus> putSlava(@RequestBody Slava slava){
        slavaRepository.save(slava);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/slava/{id}")
    public ResponseEntity<HttpStatus> deleteSlava(@PathVariable("id") String id){
        try{
            slavaRepository.deleteById(id);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
