package edu.ub.pis.firebaseexamplepis.model;

import java.util.Date;

public class Message {
    private String text;
    private Date time;
    private Boolean read;

    private String userID;

    public Message(String userID, String text, Date time, boolean read) {
        this.text = text;
        this.time = time;
        this.read = read;
        this.userID = userID;

    }

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public String getUserID(){
        return userID;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setRead(String userReading){
        if (!userReading.equals(userID)) {
            this.read = true;
        }
    }

    public boolean read(){
        return read;
    }
}


