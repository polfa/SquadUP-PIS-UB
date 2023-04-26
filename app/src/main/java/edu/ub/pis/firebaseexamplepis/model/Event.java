package edu.ub.pis.firebaseexamplepis.model;

import java.sql.Time;

public class Event {
    private User user;
    private String description;
    private String gameImageId;
    private String rankImageId;
    private Time startTime;

    public Event(User user, String description, String gameImageId, String rankImageId, Time startTime){
        this.user = user;
        this.description = description;
        this.gameImageId = gameImageId;
        this.rankImageId = rankImageId;
        this.startTime = startTime;
    }
}
