package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.view.imageURLs.VideogameLogos;

/**
 * Classe contenidor de la informació de l'usuari.
 */
public class User {
    private String mId; // Per exemple, el mail
    private String mNickname;
    private String mDescripcio;
    private String gameImageId;
    private String rankImageId;
    private String mPictureURL;
    private String gameImage, rankImage;

    private ArrayList<String> friends;

    VideogameLogos vl;
    private String mail;// Url d'Internet, no la foto en si
    // Constructor
    public User(
        String id,
        String Nickname,
        String descripcio,
        String pictureURL,
        String mail,
        String gameImageId,
        String rankImageId,
        ArrayList<String> friends
    ) {
        this.mId = id;
        this.mNickname = Nickname;
        this.mDescripcio = descripcio;
        this.mPictureURL = pictureURL;
        this.mail = mail;
        this.gameImageId = gameImageId;
        this.rankImageId = rankImageId;
        vl = VideogameLogos.valueOf(gameImageId);
        gameImage = vl.getImageLocation();
        rankImage = vl.getRank(getRankImageId());
        this.friends = friends;
    }

    // Getters
    public String getNickname () {
        return this.mNickname;
    }
    public String getDescripcio() {
        return this.mDescripcio;
    }
    public String getURL() { return this.mPictureURL; }
    public String getID() { return this.mId; }
    public String getGameImageId() {
        return gameImageId;
    }
    public String getGameImage() {
        return gameImage;
    }
    public String getRankImageId() {
        return rankImageId;
    }
    public String getRankImage() {
        return rankImage;
    }


    // Setters
    public void setNickname (String nickname) { this.mNickname = nickname; }
    public void setDescripcio(String descripcio) {
        this.mDescripcio = descripcio;
    }
    public void setUrl(String pictureUrl) { this.mPictureURL = pictureUrl; }
    public void setRankImageId(String rankImageId) {
        this.rankImageId = rankImageId;
    }
    public void setGameImageId(String gameImageId) {
        this.gameImageId = gameImageId;
    }

    public boolean isFriend(String id){
        if (friends.contains(id)){
            return true;
        }else{
            return false;
        }
    }
}
