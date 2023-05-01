package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Event {
    private User user;

    private String eventID;
    private String description;
    private String gameImageId;
    private String rankImageId;
    private com.google.firebase.Timestamp startTime;

    private Long maxMembers;
    private Long numMembers;
    private HashMap<String,User> members;

    public Event(String userID,String eventID, String description, String gameImageId, String rankImageId, com.google.firebase.Timestamp startTime, Long maxMembers, String stringMembers){
        UserRepository uRepo = UserRepository.getInstance();
        this.user = uRepo.getUserById(userID);
        this.description = description;
        this.gameImageId = gameImageId;
        this.rankImageId = rankImageId;
        this.startTime = startTime;
        this.eventID = eventID;
        this.maxMembers = maxMembers;
        this.members = new HashMap<>();
        initMembers(stringMembers, uRepo);

    }

    private void initMembers(String stringMembers, UserRepository uRepo) {
        String current = "";
        for (char s: stringMembers.toCharArray()){
            if (s != ',' && s!= ' '){
                current += s;
            }else if (!current.isEmpty()){
                System.out.println(current);
                members.put(current,uRepo.getUserById(current));
                current = "";
            }else{
                current = "";
            }
        }
        if (!current.isEmpty() && !(current.charAt(0) == ',')) {
            members.put(current, uRepo.getUserById(current));
        }
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

    public String getEventID(){
        return eventID;
    }

    public void addMember(User user) throws Exception {
        EventRepository eventRepository = EventRepository.getInstance();
        if (members.size() >= maxMembers){
            throw new Exception("No hi poden entrar mes usuaris en aquest event");
        }
        members.put(user.getID(),user);
        eventRepository.updateUserEvent(user, Event.this);

    }

    public HashMap<String, User> getMembers (){
        return members;
    }

    public User getMemberByMail(String mail){
        return members.get(mail);
    }

    public boolean userInEvent(String mail){
        if (mail.charAt(0) == ' '){
            mail = mail.substring(1);
        }
        System.out.println(members.containsKey(mail) + "  " + mail + " ");
        return members.containsKey(mail);
    }

    public int getCurrentMembers() {
        return members.size();
    }

    public Long getMaxMembers() {
        return maxMembers;
    }

    public Object getUserID() {
        return user.getID();
    }

    public String getMembersString() {
        String current = "";
        for (User s: members.values()){
            current += s.getID();
            current += ", ";
        }
        return current;
    }

    public void removeMember(User user) {
        EventRepository eventRepository = EventRepository.getInstance();
        members.remove(user.getID());
        eventRepository.updateUserEvent(user, Event.this);
    }
}
