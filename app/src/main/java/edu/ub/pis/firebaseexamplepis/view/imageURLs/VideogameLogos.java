package edu.ub.pis.firebaseexamplepis.view.imageURLs;

public enum VideogameLogos {
    ROCKET_LEAGUE("https://logos-world.net/wp-content/uploads/2020/11/Rocket-League-Emblem.png","rl"),
    VALORANT("https://seeklogo.com/images/V/valorant-logo-FAB2CA0E55-seeklogo.com.png", "val"),
    CSGO("https://seeklogo.com/images/C/csgo-logo-CAA0A4D48A-seeklogo.com.png", "csgo");

    private String image_location;
    private Class ranks;
    private VideogameLogos(String image_location, String gameID){
        this.image_location = image_location;
        /*
        if (gameID == "rl"){
            ranks = RocketLeagueRanks;
        }
        */
    }

    public String getImageLocation(){
        return this.image_location;
    }
}
