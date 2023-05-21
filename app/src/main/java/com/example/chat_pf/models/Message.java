package com.example.chat_pf.models;

import androidx.annotation.NonNull;

public class Message{
    public String name, text, id;
    public Long date;

    public Message(String name, String text, Long date, String email) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.id = email;
    }

    @NonNull
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[ date = " + date + ", name = " + name + ", " +
                "text = " + text + ", "  + "id = " + id + " ]";
    }
}

