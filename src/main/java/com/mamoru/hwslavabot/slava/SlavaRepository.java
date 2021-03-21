package com.mamoru.hwslavabot.slava;

import com.mamoru.hwslavabot.slava.Slava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SlavaRepository extends JpaRepository<Slava,String> {

    Optional<Slava> findByIdAndChatId(String id, String chatId);
    List<Slava> findAllByChatId(String chatId);

}
