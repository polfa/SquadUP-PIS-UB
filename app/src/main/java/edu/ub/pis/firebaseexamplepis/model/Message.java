package edu.ub.pis.firebaseexamplepis.model;

import java.util.Date;

public class Message {
    private String text;
    private Date time;
    private Boolean read;

    public Message(String text) {
        this.text = text;
        this.time = new Date();
        this.read = false;

    }

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setRead(){
        this.read = true;
    }

    public boolean read(){
        return read;
    }
}


