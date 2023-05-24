package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;
import java.util.Date;

public class Grup {
    private ArrayList<Message> messages;

    private ArrayList<String> users;
    private Message lastMessage;

    private String imageURL;

    private String groupName;

    private String description;

    private String id;

    public Grup(String id ,String groupName, ArrayList<String> users, ArrayList<Message> missatges, String imageURL, String description) {
        this.users = users;
        this.messages = missatges;
        if (!messages.isEmpty()) {
            this.lastMessage = messages.get(messages.size() - 1);
        }else{
            lastMessage = null;
        }
        this.imageURL = imageURL;
        this.groupName = groupName;
        this.description = description;
        this.id = id;
    }

    public void addMessage(String userID, String text) {
        Message message = new Message(userID,text, new Date(), false);
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void addUser(String userID){
        if (!users.contains(userID)){
            users.add(userID);
        }
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public boolean userInGrup(String user){
        return users.contains(user);
    }

    public void setGroupName(String name){
        groupName = name;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getId(){
        return id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


