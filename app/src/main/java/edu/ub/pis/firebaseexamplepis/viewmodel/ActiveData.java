package edu.ub.pis.firebaseexamplepis.viewmodel;

import edu.ub.pis.firebaseexamplepis.model.Chat;

public class ActiveData {
    private Chat currentChat;

    private static final ActiveData mInstance = new ActiveData();

    private ActiveData() {
        currentChat = null;
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

}
