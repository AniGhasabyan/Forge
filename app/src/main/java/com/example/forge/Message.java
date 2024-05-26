package com.example.forge;

import java.util.Date;
import java.util.UUID;

public class Message {
    private String id;
    private String text;
    private boolean sent;
    private Date timestamp;
    private int place;

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = new Date();
    }

    public Message(String text, boolean sent, Date timestamp) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.sent = sent;
        this.timestamp = timestamp != null ? timestamp : new Date();
    }

    public Message(String text, boolean sent) {
        this(text, sent, new Date());
    }

    public Message(String text) {
        this(text, true, new Date());
    }

    public Message(String id, String text) {
        this.id = id;
        this.text = text;
        this.timestamp = new Date();
    }
    public Message(String text, int place) {
        this.text = text;
        this.place = place;
    }

    public int getPlace() {
        return place;
    }
    public void setPlace(int place) {
        this.place = place;
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

    public Date getTimestamp() {
        return timestamp;
    }
}
