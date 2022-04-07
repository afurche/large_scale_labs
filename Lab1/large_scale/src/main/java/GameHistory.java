import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


@Getter
@Setter
public class GameHistory {
    /**
     * GameHistory class represent game history of a specific player (aggregation relationship)
     * Attributes:
     * playedGamesList : list containing Game object of games played by a specific player
     * historyStats : hashtable containing summed stats of all games played (all values are int):
     *                  - number of all battle royals played
     *                  - number of battle royals won
     *                  - number of battle royals top3
     *                  - number of battle royals top5
     *                  - number of arenas played
     *                  - number of arenas won
     */

    // list for storing game entries
    private List<Game> playedGamesList;
    // dictionary for storing overall values for game history : number of games, won games etc.
    private Hashtable<String, Integer> historyStats;


    public GameHistory(){
        this.playedGamesList = new ArrayList<>();
        this.historyStats = new Hashtable<>();
    }

    /**
     *
     * Adds game to the list storing played games.
     * After insertion to list, based on mode of the game (Arena, battle Royal) and results
     * corresponding values are incremented in historyStats
     * @param game : object of type game
     *
     * */
    public void addGameToHistory(Game game){
        this.playedGamesList.add(game);
        if(game.getMode().equals("battleRoyal")) {
            int countBattleRoyalsPlayed = this.historyStats.getOrDefault("battleRoyalsPlayed", 0);
            this.historyStats.put("battleRoyalsPlayed", countBattleRoyalsPlayed + 1);
            String gameResult = game.getGameResult();

            switch(gameResult){
                case "win":
                    int countBattleRoyalsWon = this.historyStats.getOrDefault("battleRoyalsWon", 0);
                    this.historyStats.put("battleRoyalsWon", countBattleRoyalsWon + 1);
                    break;
                case "top3":
                    int countBattleRoyalsTop3 = this.historyStats.getOrDefault("battleRoyalsTop3", 0);
                    this.historyStats.put("battleRoyalsTop3", countBattleRoyalsTop3 + 1);
                    break;
                case "top5":
                    int countBattleRoyalsTop5 = this.historyStats.getOrDefault("battleRoyalsTop5", 0);
                    this.historyStats.put("battleRoyalsTop5", countBattleRoyalsTop5 + 1);
            }
        }
        else if(game.getMode().equals("arena")){
            int countArenasPlayed = this.historyStats.getOrDefault("arenasPlayed", 0);
            this.historyStats.put("arenasPlayed", countArenasPlayed + 1);
            boolean hasWon =  Boolean.getBoolean(game.getGameStats().get("hasWon"));
            if(hasWon){
                int countArenasWon = this.historyStats.getOrDefault("arenasWon", 0);
                this.historyStats.put("arenasWon", countArenasWon + 1);
            }
        }
    }

    /**
     *  Returns a string containing concatenated statistics about history
     *  of Battle Royal games. (sums, averages, K/D ratio, etc. (not implemented yet!))
     *
     * @return : String with player Battle Royal stats
     */

    public String getPlayerBattleRoyalStats(){

        return String.format(" Played: %d Won: %d Top3: %d Top5: %d",
                this.historyStats.getOrDefault("battleRoyalsPlayed", 0),
                this.historyStats.getOrDefault("battleRoyalsWon", 0),
                this.historyStats.getOrDefault("battleRoyalsTop3", 0),
                this.historyStats.getOrDefault("battleRoyalsTop5", 0));

    }

    /**
     *  Returns a string containing concatenated statistics about history
     *  of Arena games. (sums, averages, K/D ratio, etc. (not implemented yet!))
     *
     * @return : String with player Arena stats
     */

    public String getPlayerArenaStats(){

        return String.format(" Played: %d Won: %d",
                this.historyStats.getOrDefault("arenasPlayed", 0),
                this.historyStats.getOrDefault("arenasWon", 0));
    }

    /**
     * Based on value of choice (input from player_app in Player)
     * Prints entries from playerGamesList:
     * 1 -> game stats strings of all Battle Royal entries
     * 2 -> game stats strings of all Arena entries
     * 3 -> game stats strings of all entres
     *
     * @param choice : int in the interval [1;3]
     */

    public void showGameResults(int choice){

        switch(choice){
            case 1:
                for (Game game : this.playedGamesList){
                    if(game.getMode().equals("battleRoyal")){
                        System.out.println(game.gameStatsToString());
                    }
                }
                break;
            case 2:
                for (Game game : this.playedGamesList){
                    if(game.getMode().equals("arena")){
                        System.out.println(game.gameStatsToString());
                    }
                }
                break;
            case 3:
                for (Game game : this.playedGamesList){
                        System.out.println(game.gameStatsToString());
                }
        }
    }

    public float sumTime(){
        float sumTime = 0;
        for(Game game : this.playedGamesList){
            sumTime += Float.parseFloat(game.getGameStats().get("timeInGame"));
        }
        return sumTime;
    }

    public float getKDRatio(){
        float sumKills = 0;
        for(Game game : this.playedGamesList){
            sumKills += Float.parseFloat(game.getGameStats().get("kills"));
        }
        return sumKills / this.playedGamesList.size();
    }
}
