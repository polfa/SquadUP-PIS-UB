package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;

public class Grup {
    private ArrayList<Message> messages;
    private String missatge;
    private ArrayList<User> usuaris;

    private User user1, user2;

    public Grup(String idUser1, String idUser2, String missatge) {
        UserRepository uRepo = UserRepository.getInstance();
        this.user1 = uRepo.getUserById(idUser1);
        this.user2 = uRepo.getUserById(idUser2);
        this.messages = new ArrayList<>();
        this.missatge = missatge;
    }

    public void addMessage(Message message) {
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

    public String getMessage() {
        return missatge;
    }
}


