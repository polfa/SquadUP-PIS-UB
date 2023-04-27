package edu.ub.pis.firebaseexamplepis.model;

import java.util.Date;

public class Message {
    private String text;
    private Date timestamp;

    private Boolean read;

    public Message(String text) {
        this.text = text;
        this.timestamp = new Date();
        this.read = false;

    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void read(){
        this.read = true;
    }
}


