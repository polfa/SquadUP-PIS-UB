package edu.ub.pis.firebaseexamplepis.model;

import java.util.ArrayList;

/**
 * Classe contenidor de la informació de l'usuari.
 */
public class User {
    private String mId; // Per exemple, el mail
    private String mFirstName;
    private String mLastName;
    private String mHobbies;
    private String mPictureURL;
    private String mail;// Url d'Internet, no la foto en si
    // Constructor
    public User(
        String id,
        String firstName,
        String lastName,
        String hobbies,
        String pictureURL,
        String mail
    ) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mHobbies = hobbies;
        this.mPictureURL = pictureURL;
        this.mail = mail;
    }

    // Getters
    public String getFirstName () {
        return this.mFirstName;
    }
    public String getLastName () {
        return this.mLastName;
    }
    public String getHobbies() {
        return this.mHobbies;
    }
    public String getURL() { return this.mPictureURL; }

    public String getID() { return this.mail; }

    // Setters
    public void setFirstName (String firstName) { this.mFirstName = firstName; }
    public void setLastName (String lastName) {
        this.mLastName = lastName;
    }
    public void setHobbies(String hobbies) {
        this.mHobbies = hobbies;
    }
    public void setUrl(String pictureUrl) { this.mPictureURL = pictureUrl; }
}
