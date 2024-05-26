package com.example.forge;

import java.util.UUID;

public class Message {
    private String id;
    private String text;
    private boolean sent;

    public Message() {
    }

    public Message(String text, boolean sent) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.sent = sent;
    }

    public Message(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
    }

    public Message(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public boolean isSent() {
        return sent;
    }
}
