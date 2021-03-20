package com.mamoru.hwslavabot.slava;

import com.mamoru.hwslavabot.slava.Slava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SlavaRepository extends JpaRepository<Slava,String> {

}
