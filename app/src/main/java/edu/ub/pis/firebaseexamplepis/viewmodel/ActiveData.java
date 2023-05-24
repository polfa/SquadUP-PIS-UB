package edu.ub.pis.firebaseexamplepis.viewmodel;

import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.model.User;

public class ActiveData {
    private Chat currentChat;
    private User currentUser;

    private static final ActiveData mInstance = new ActiveData();

    private ActiveData() {
        currentChat = null;
        currentUser = null;
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static ActiveData getInstance() {
        return mInstance;
    }

    public void setCurrentChat(Chat chat){
        currentChat = chat;
    }

    public Chat getCurrentChat(){ return currentChat; }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

}
