package edu.ub.pis.firebaseexamplepis.view.imageURLs;

public enum RocketLeagueRanks {
    BRONZE_1("https://i.pinimg.com/originals/05/e2/5a/05e25a3efa2a3ce3faca3acd4bced82f.png"),
    BRONZE_2("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-bronze-ii.png"),
    BRONZE_3("https://fireteam.fr/wp-content/uploads/2020/02/bronze-3.png"),
    SILVER_1("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-i.png"),
    SILVER_2("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-ii.png"),
    SILVER_3("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-iii.png"),
    GOLD_1("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-gold-i.png"),
    GOLD_2("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-gold-ii.png"),
    GOLD_3("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-gold-iii.png"),
    CHAMPION_1("https://www.seekpng.com/png/full/20-204758_champion-rank-rocket-league.png"),
    CHAMPION_2("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-champion-ii.png"),
    CHAMPION_3("https://www.esports.net/wp-content/uploads/2022/05/rocket-league-champion-iii.png"),
    GRAND_CHAMPION_1("https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank.p19ng"),
    GRAND_CHAMPION_2("https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank20.png"),
    GRAND_CHAMPION_3("https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank21.png"),
    SUPERSONIC_LEGEND("https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank22.png");

    private String image_location;
    private RocketLeagueRanks(String image_location){
        this.image_location = image_location;
    }

    public String getImageLocation(){
        return this.image_location;
    }
}
