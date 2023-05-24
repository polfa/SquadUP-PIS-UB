package edu.ub.pis.firebaseexamplepis.view.imageURLs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public enum VideogameLogos {
    ROCKET_LEAGUE("https://logos-world.net/wp-content/uploads/2020/11/Rocket-League-Emblem.png","rl"),
    VALORANT("https://seeklogo.com/images/V/valorant-logo-FAB2CA0E55-seeklogo.com.png", "val"),
    CSGO("https://seeklogo.com/images/C/csgo-logo-CAA0A4D48A-seeklogo.com.png", "csgo");

    private String image_location;
    private HashMap<String,String> ranks;
    VideogameLogos(String image_location, String gameID){
        this.image_location = image_location;
        ranks = new HashMap<>();
        if (gameID == "rl"){
            ranks.put("bronze_1","https://i.pinimg.com/originals/05/e2/5a/05e25a3efa2a3ce3faca3acd4bced82f.png");
            ranks.put("bronze_2","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-bronze-ii.png");
            ranks.put("bronze_3", "https://fireteam.fr/wp-content/uploads/2020/02/bronze-3.png");
            ranks.put("silver_1", "https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-i.png");
            ranks.put("silver_2","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-ii.png");
            ranks.put("silver_3","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-silver-iii.png");
            ranks.put("gold_1","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-gold-i.png");
            ranks.put("gold_2","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-gold-ii.png");
            ranks.put("gold_3","https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank15.png");
            ranks.put("champion_1","https://www.seekpng.com/png/full/20-204758_champion-rank-rocket-league.png");
            ranks.put("champion_2","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-champion-ii.png");
            ranks.put("champion_3","https://www.esports.net/wp-content/uploads/2022/05/rocket-league-champion-iii.png");
            ranks.put("grand_champion_1","https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank19.png");
            ranks.put("grand_champion_2","https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank20.png");
            ranks.put("grand_champion_3","https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank21.png");
            ranks.put("supersonic_legend","https://trackercdn.com/cdn/tracker.gg/rocket-league/ranks/s15rank22.png");
        }else if (gameID == "val"){
            ranks.put("iron_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Iron-1-Valorant-Rank.png");
            ranks.put("iron_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Iron-2-Valorant-Rank.png");
            ranks.put("iron_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Iron-3-Valorant-Rank.png");
            ranks.put("bronze_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Bronze-1-Valorant-Rank.png");
            ranks.put("bronze_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Bronze-2-Valorant-Rank.png");
            ranks.put("bronze_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Bronze-3-Valorant-Rank.png");
            ranks.put("silver_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Silver-1-Valorant-Rank.png");
            ranks.put("silver_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Silver-2-Valorant-Rank.png");
            ranks.put("silver_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Silver-3-Valorant-Rank.png");
            ranks.put("gold_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Gold-1-Valorant-Rank.png");
            ranks.put("gold_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Gold-2-Valorant-Rank.png");
            ranks.put("gold_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Gold-3-Valorant-Rank.png");
            ranks.put("platinum_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Platinum-1-Valorant-Rank.png");
            ranks.put("platinum_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Platinum-2-Valorant-Rank.png");
            ranks.put("platinum_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Platinum-3-Valorant-Rank.png");
            ranks.put("diamond_1", "https://img.rankedboost.com/wp-content/uploads/2020/04/Diamond-1-Valorant-Rank.png");
            ranks.put("diamond_2", "https://img.rankedboost.com/wp-content/uploads/2020/04/Diamond-2-Valorant-Rank.png");
            ranks.put("diamond_3", "https://img.rankedboost.com/wp-content/uploads/2020/04/Diamond-3-Valorant-Rank.png");
            ranks.put("immortal_1", "https://cdn3.emoji.gg/emojis/1518-valorant-immortal-1.png");
            ranks.put("immortal_2", "https://cdn3.emoji.gg/emojis/1775-valorant-immortal-2.png");
            ranks.put("immortal_3", "https://cdn3.emoji.gg/emojis/5979-valorant-immortal-3.png");
            ranks.put("radiant", "https://i.imgur.com/iE0jMGR.png");
        }else if (gameID == "csgo"){
            ranks.put("silver_1", "https://cdn.ligadegamers.com/imagenes/2-silver-1.jpg");
            ranks.put("silver_2", "https://cdn.ligadegamers.com/imagenes/3-silver-2.jpg");
            ranks.put("silver_3", "https://cdn.ligadegamers.com/imagenes/4-silver-3.jpg");
            ranks.put("silver_4", "https://cdn.ligadegamers.com/imagenes/5-silver-4.jpg");
            ranks.put("silver_elite", "https://cdn.ligadegamers.com/imagenes/6-silver-elite.jpg");
            ranks.put("silver_elite_master", "https://cdn.ligadegamers.com/imagenes/7-silver-elite-master.jpg");
            ranks.put("gold_nova_1", "https://cdn.ligadegamers.com/imagenes/8-gold-nova-1.jpg");
            ranks.put("gold_nova_2", "https://cdn.ligadegamers.com/imagenes/9-gold-nova-2.jpg");
            ranks.put("gold_nova_3", "https://cdn.ligadegamers.com/imagenes/10-gold-nova-3.jpg");
            ranks.put("gold_nova_master", "https://cdn.ligadegamers.com/imagenes/11-gold-nova-master-o-gold-nova-4.jpg");
            ranks.put("master_guardian_1", "https://cdn.ligadegamers.com/imagenes/12-master-guardian-1.jpg");
            ranks.put("master_guardian_2", "https://cdn.ligadegamers.com/imagenes/13-master-guardian-2.jpg");
            ranks.put("master_guardian_elite", "https://cdn.ligadegamers.com/imagenes/14-master-guardian-elite.jpg");
            ranks.put("distinguished_master_guardian", "https://cdn.ligadegamers.com/imagenes/15-distinguished-master-guardian.jpg");
            ranks.put("legendary_eagle", "https://cdn.ligadegamers.com/imagenes/16-legendary-eagle.jpg");
            ranks.put("legendary_eagle_master", "https://cdn.ligadegamers.com/imagenes/17-legendary-eagle-master.jpg");
            ranks.put("supreme_master_first_class", "https://cdn.ligadegamers.com/imagenes/18-supreme-master-first-class.jpg");
            ranks.put("global_elite", "https://cdn.ligadegamers.com/imagenes/19-the-global-elite.jpg");
        }
    }

    public String getImageLocation(){
        return this.image_location;
    }

    public String getRank(String r){
        return ranks.get(r);
    }

    public String[] getRankNames(){
        ArrayList<String> sortedList = new ArrayList<>(ranks.keySet());
        Collections.sort(sortedList);
        return sortedList.toArray(new String[0]);
    }
}
