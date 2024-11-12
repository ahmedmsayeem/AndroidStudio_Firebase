package com.example.videochat.utils;

import java.util.Collection;

public class Message {
    private String username;
    private String content;

    // Default constructor for Firebase
    public Message() {}

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }


}
