package edu.ub.pis.firebaseexamplepis.model;

import java.util.Date;

public class Message {
    private String text;
    private Date time;
    private Boolean read;

    public Message(String text, Date time, boolean read) {
        this.text = text;
        this.time = time;
        this.read = read;

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


