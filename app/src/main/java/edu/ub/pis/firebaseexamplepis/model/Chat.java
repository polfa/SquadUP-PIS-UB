package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;
import java.util.Date;

public class Chat {
    private ArrayList<Message> messages;
    private User user1;
    private User user2;

    private Message lastMessage;

    String chatID;

    public Chat(String chatID, String idUser1, String idUser2, ArrayList<Message> missatges) {
        UserRepository uRepo = UserRepository.getInstance();
        this.user1 = uRepo.getUserById(idUser1);
        this.user2 = uRepo.getUserById(idUser2);
        this.messages = missatges;
        if (!messages.isEmpty()) {
            this.lastMessage = messages.get(messages.size() - 1);
        }else{
            lastMessage = null;
        }

        this.chatID = chatID;
    }

    public String getId(){
        return chatID;
    }
    public void addMessage(String userID, String text) {
        Message message = new Message(userID,text, new Date(), false);
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public User getUser(User currentUser) {
        if (user1 == currentUser){
            return user2;
        }else if (user2 == currentUser){
            return user1;
        }else{
            throw new RuntimeException("user not in chat");
        }
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public boolean userInChat(User user){
        return user == user1 || user == user2;
    }
}


