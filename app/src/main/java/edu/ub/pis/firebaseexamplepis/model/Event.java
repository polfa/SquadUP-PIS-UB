package edu.ub.pis.firebaseexamplepis.model;

import java.sql.Time;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private User user;

    private String eventID;
    private String description;
    private String gameImageId;
    private String rankImageId;
    private com.google.firebase.Timestamp startTime;

    public Event(String userID,String eventID, String description, String gameImageId, String rankImageId, com.google.firebase.Timestamp startTime){
        UserRepository userRepository = UserRepository.getInstance();
        userRepository.getUserById(userID);
        this.description = description;
        this.gameImageId = gameImageId;
        this.rankImageId = rankImageId;
        this.startTime = startTime;
        this.eventID = eventID;

    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGameImageId() {
        return gameImageId;
    }

    public void setGameImageId(String gameImageId) {
        this.gameImageId = gameImageId;
    }

    public String getRankImageId() {
        return rankImageId;
    }

    public void setRankImageId(String rankImageId) {
        this.rankImageId = rankImageId;
    }

    public com.google.firebase.Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(com.google.firebase.Timestamp startTime) {
        this.startTime = startTime;
    }
}
