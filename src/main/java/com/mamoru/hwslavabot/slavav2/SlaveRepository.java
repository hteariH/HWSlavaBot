package com.mamoru.hwslavabot.slavav2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlaveRepository extends JpaRepository<Slave,Long> {

    Optional<Slave> findFirstByNameAndChatId(String name, String chatId);
    List<Slave> findAllByChatId(String chatId);

}
