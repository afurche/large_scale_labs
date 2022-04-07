package furche.pg;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
abstract class Game implements Comparable<Game>{
    /**
     * Abstract class representing game in Apex Legends.
     * Attributes :
     * gameStats : hash map storing all relevant game statistics :
     *              - String date
     *              - int time spent in game
     *              - String hero used in game
     *              - int number of kills
     *              - int number of assists
     *              - int number of damage dealt
     *
     */

    protected LinkedHashMap<String, String> gameStats; // hashmap storing all relevant game statistics

    public Game(String mode, String date, float timeInGame, String hero, int kills, int assists, int damage){
        this.gameStats = new LinkedHashMap<>();
        this.gameStats.put("mode", mode);
        this.gameStats.put("date", date);
        this.gameStats.put("timeInGame", Float.toString(timeInGame));
        this.gameStats.put("hero", hero);
        this.gameStats.put("kills", Integer.toString(kills));
        this.gameStats.put("assists", Integer.toString(assists));
        this.gameStats.put("damage", Integer.toString(damage));
    }

    public Game(LinkedHashMap<String, String> gameStats){
        this.gameStats = gameStats;
    }


    /**
     * Returns a string containg all entries from gameStats in form "Key: value ".
     * Capitalizes first letters of keys.
     *
     * @return String containing all entries from gameStats
     */
    public String gameStatsToString(){
        StringBuilder gameStatsStringBuilder = new StringBuilder(); // builder for creating output string
        String keyCapitalized; // value for storing capitalized keys (with 1 letter captital)
        for (Map.Entry<String, String> entry : this.gameStats.entrySet()){ // for each key append out string with "Key: val"
            keyCapitalized = entry.getKey().substring(0,1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
            gameStatsStringBuilder.append(String.format("%s: %s ", keyCapitalized, entry.getValue()));
        }

        return gameStatsStringBuilder.toString();

    }

    /**
     * Returns game mode from gameStats (used in furche.pg.GameHistory for mode identification)
     * @return String game mode
     */
    public String getMode(){
        return this.gameStats.get("mode");
    }

    /**
     * Function comparing two games based on amount of kills scored
     */
    @Override
    public int compareTo(Game game){
        return Integer.compare( Integer.parseInt(game.getGameStats().get("kills")), Integer.parseInt(this.gameStats.get("kills")));
    }

    /**
     * Function that should return game results in string format in a way relevant to specific game mode
     *
     * @return String with game results
     */

    abstract public String getGameResult();



}

@Getter
@Setter
class BattleRoyal extends Game implements Cloneable{ // first example of inheritance
    /**
     * Class describing game of Battle Royal, inheriting from furche.pg.Game class.
     * Additional attribute in gameStats:
     * position -> int [1;20] describing position in the game
     *
     */

    public BattleRoyal(String date, float timeInGame, String hero, int kills, int assists, int damage, int position){
        super("battleRoyal", date, timeInGame, hero, kills, assists, damage);
        this.gameStats.put("position", Integer.toString(position));
    }

    public BattleRoyal(LinkedHashMap<String, String> gameStats){
       super(gameStats);
    }

    /**
     * Returns a string with results of the game in format relevant to Battle Royal game mode
     * (positions above top 5 don't matter very much and categories "win", "top3", and "top5" are considered
     * somewhat of a standard)
     *
     * @return String with game results
     */
    public String getGameResult(){  // polymorphism
        int position = Integer.parseInt(this.gameStats.get("position"));
        if(position == 1){
            return "win";
        }
        else if(position > 1 && position <= 3) {
            return "top3";
        }
        else if(position > 3 && position <= 5) {
            return "top5";
        }
        else if (position > 5 && position <= 10){
            return "6-10";
        }
        else {
            return "11-20";
        }
    }

    /**
     * Deep cloning of Battle Royal Game object
     *
     * @return deep clone of Battle Royal object
     */

    @Override
    public Object clone() {
        BattleRoyal clonedBattleRoyal;
        LinkedHashMap<String, String> gameStatsCopy = new LinkedHashMap<>(this.gameStats);
        try{
            clonedBattleRoyal = (BattleRoyal) super.clone();
            clonedBattleRoyal.setGameStats(gameStatsCopy);
        }catch(CloneNotSupportedException e){
            clonedBattleRoyal = new BattleRoyal(gameStatsCopy);
        }
        return clonedBattleRoyal;
    }
}

class RankedBattleRoyal extends BattleRoyal {

    /**
     * Class describing ranked Battle Royal inheriting from Battle Royal class.
     * Additional field:
     * rankNegativePoints : amount of negative ranking points assigned to the rank of the player
     *                      ( the higher the rank the more negative points ).
     *                      0 for bronze (lowest rank)
     *                      +12 each consecutive rank
     *
     *
     */

    private int rankNegativePoints;

    public RankedBattleRoyal(String date, float timeInGame, String hero, int kills, int assists, int damage, int position, PlayerRank rank){
        super(date, timeInGame, hero, kills, assists, damage, position);
        switch (rank){
            case BRONZE:
                this.rankNegativePoints = 0;
                break;
            case SILVER:
                this.rankNegativePoints = 12;
                break;
            case GOLD:
                this.rankNegativePoints = 24;
                break;
            case PLATINUM:
                this.rankNegativePoints = 36;
                break;
        }
    }

    /**
     * Function calculating ranking score for the game
     * Points are acquired in two ways:
     * - by scoring kills or assists
     * - by finishing the game with high position
     * The score is calculated by summing points for kills with points for position and subtracting negative points for
     * rank.
     *
     * @return int with ranking points
     */
    private int calculateGameRankScore(){
        int position = Integer.parseInt(this.gameStats.get("position"));

        int sumKA = Integer.parseInt(this.gameStats.get("kills")) + Integer.parseInt(this.gameStats.get("assists")); // sum of kills and assists

        int pointsKA; // points acquired through kills and assists

        // if sum of amounts of kills and assists is <6 than sumKA*15 else 6*15
        if(sumKA < 6){
            pointsKA = sumKA*15;
        }
        else {
            pointsKA = 6*15;
        }

        int pointsPosition = -1; // points acquired by finishing game on certain position

        if(position > 10){
            pointsPosition = 0;
        }
        else if(position == 10 || position == 9){
            pointsPosition = 10;
        }
        else if(position == 8 || position == 7){
            pointsPosition = 20;
        }
        else if(position == 6 || position == 5){
            pointsPosition = 30;
        }
        else if(position == 4 || position == 3){
            pointsPosition = 40;
        }
        else if(position == 2){
            pointsPosition = 60;
        }
        else if(position == 1){
            pointsPosition = 100;
        }
        return pointsKA + pointsPosition - this.rankNegativePoints;
    }

    @Override    // example of method overriding
    public String gameStatsToString(){
        return super.gameStatsToString() + String.format("RankPoints: %d ", this.calculateGameRankScore());
    }

}
@Getter
@Setter
class Arena extends Game implements Cloneable{
    /**
     * Class representing game of furche.pg.Arena, inheriting from furche.pg.Game class.
     */

    public Arena(String date, float timeInGame, String hero, int kills, int assists, int damage, boolean hasWon){
        super("arena", date, timeInGame, hero, kills, assists, damage);
        this.gameStats.put("hasWon", Boolean.toString(hasWon));
    }

    public Arena(LinkedHashMap<String, String> gameStats){
        super(gameStats);
    }

    public String getGameResult(){ // polymorphism
        if(Boolean.parseBoolean(this.gameStats.get("hasWon"))){
            return "win";
        }
        else{
            return "loss";
        }
    }

    /**
     * Deep cloning of Arena Game object
     *
     * @return deep clone of Arena object
     */

    @Override
    public Object clone(){
        Arena clonedArena;
        LinkedHashMap<String, String> gameStatsCopy = new LinkedHashMap<>(this.gameStats);
        try{
            clonedArena = (Arena) super.clone();
            clonedArena.setGameStats(gameStatsCopy);
        }catch(CloneNotSupportedException e){
            clonedArena = new Arena(gameStatsCopy);
        }
        return clonedArena;
    }

}