package com.mamoru.hwslavabot.users;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Chatter")
public class Chatter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String chatId;

    private String city;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chatter chatter = (Chatter) o;
        return Objects.equals(id, chatter.id) && Objects.equals(chatId, chatter.chatId) && Objects.equals(city, chatter.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, city);
    }

    @Override
    public String toString() {
        return "Chatter{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
