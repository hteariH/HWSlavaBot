package com.mamoru.hwslavabot.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Chatter,Long> {

    Optional<Chatter> findFirstByChatId(String chatId);
}
