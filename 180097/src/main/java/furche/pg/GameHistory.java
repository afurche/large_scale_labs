package furche.pg;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
public class GameHistory implements Cloneable {
    /**
     * furche.pg.GameHistory class represent game history of a specific player (aggregation relationship)
     * Attributes:
     * playedGamesList : list containing furche.pg.Game object of games played by a specific player
     * historyStats : hashtable containing summed stats of all games played (all values are int):
     *                  - number of all battle royals played
     *                  - number of battle royals won
     *                  - number of battle royals top3
     *                  - number of battle royals top5
     *                  - number of arenas played
     *                  - number of arenas won
     */

    // list for storing game entries
    private List<BattleRoyal> playedBattleRoyalsList;
    private List<Arena> playedArenasList;
    private GameListComparatorDate gameListComparator;

    // dictionary for storing overall values for game history : number of games, won games etc.
    private Hashtable<String, Integer> historyStats;


    public GameHistory(){
        this.playedBattleRoyalsList = new ArrayList<>();
        this.playedArenasList = new ArrayList<>();
        this.historyStats = new Hashtable<>();
        this.gameListComparator = new GameListComparatorDate();
    }

    public GameHistory(List<BattleRoyal> playedBattleRoyalsList, List<Arena> playedArenasList, Hashtable<String, Integer> historyStats){
        this.playedBattleRoyalsList = playedBattleRoyalsList;
        this.playedArenasList = playedArenasList;
        this.historyStats = historyStats;
        this.gameListComparator = new GameListComparatorDate();
    }

    /**
     * Function copying lists of Games with deep cloning of each game
     *
     *
     * @return Pair containing copied lists of Battle Royals and Arenas
     */
    private Pair<List<BattleRoyal>, List<Arena>> getGameListsDeepCopies(){
        List<BattleRoyal> playedBattleRoyalsListTmpCopy = new ArrayList<>();
        List<Arena> playedArenasListTmpCopy = new ArrayList<>();
        for(BattleRoyal game : this.playedBattleRoyalsList){
            playedBattleRoyalsListTmpCopy.add((BattleRoyal) game.clone());
        }
        for(Arena game : this.playedArenasList){
            playedArenasListTmpCopy.add((Arena) game.clone());
        }

        return new Pair<>(playedBattleRoyalsListTmpCopy, playedArenasListTmpCopy);
    }

    /**
     * Deep cloning of GameHistory
     *
     * @return deep clone of gameHistory
     */

    @Override
    public Object clone(){
        GameHistory gameHistoryCopy = null;
        Hashtable<String, Integer> historyStatsCopy = new Hashtable<>(this.historyStats);
        Pair<List<BattleRoyal>, List<Arena>> gameListsCopies = this.getGameListsDeepCopies();
        List<BattleRoyal> playedBattleRoyalsListCopy = gameListsCopies.getKey();
        List<Arena> playedArenasListCopy = gameListsCopies.getValue();

        try{
            gameHistoryCopy = (GameHistory) super.clone();
            gameHistoryCopy.setHistoryStats(historyStatsCopy);
            gameHistoryCopy.setPlayedBattleRoyalsList(playedBattleRoyalsListCopy);
            gameHistoryCopy.setPlayedArenasList(playedArenasListCopy);
        }catch (CloneNotSupportedException e){
            gameHistoryCopy = new GameHistory(playedBattleRoyalsListCopy, playedArenasListCopy, historyStatsCopy);
        }
        return gameHistoryCopy;
    }

    /**
     *
     * Adds game to the list storing played games.
     * After insertion to list, based on mode of the game (furche.pg.Arena, battle Royal) and results
     * corresponding values are incremented in historyStats
     * @param game : object of type game
     *
     * */
    public void addGameToHistory(Game game){
        if(game.getMode().equals("battleRoyal")) {
            this.playedBattleRoyalsList.add((BattleRoyal) game);
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
            this.playedArenasList.add((Arena) game);
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
     *  of furche.pg.Arena games. (sums, averages, K/D ratio, etc. (not implemented yet!))
     *
     * @return : String with player furche.pg.Arena stats
     */

    public String getPlayerArenaStats(){

        return String.format(" Played: %d Won: %d",
                this.historyStats.getOrDefault("arenasPlayed", 0),
                this.historyStats.getOrDefault("arenasWon", 0));
    }


    /**
     *
     * Function taking list of objects of chosen subtype of Game and printing all entries as Strings.
     * List is ordered ascending by date.
     *
     * @param gameList - list containing Game objects ( this.playedBattleRoyalsList or this.playedArenasList )
     */

    private static void printGameList(List<? extends Game> gameList, GameListComparatorDate comparator){
        List<? extends Game> sortedGames = new ArrayList<>(gameList);
        sortedGames.sort(comparator);
        for (Game game : sortedGames){
            System.out.println(game.gameStatsToString());
        }
    }


    /**
     * Based on value of choice (input from player_app in furche.pg.Player)
     * Prints entries from playerGamesList:
     * 1 -> game stats strings of all Battle Royal entries
     * 2 -> game stats strings of all furche.pg.Arena entries
     * 3 -> game stats strings of all entries
     *
     * @param choice : int in the interval [1;3]
     */

    public void showGameResults(int choice){

        switch(choice){
            case 1: // Battle Royal entries
                System.out.println("Battle Royal game history: ");
                printGameList(this.playedBattleRoyalsList, this.gameListComparator);
                break;
            case 2: // Arena entries
                System.out.println("Arena game history: ");
                printGameList(this.playedArenasList, this.gameListComparator);
                break;
            case 3: // All entries
                System.out.println("Game History:\nBattle Royal:");
                printGameList(this.playedBattleRoyalsList, this.gameListComparator);
                System.out.println("Arena:");
                printGameList(this.playedArenasList, this.gameListComparator);
                break;
        }
    }

    /**
     * Function returning sum of time player spent playing in his entire history
     *
     * @return float sum of time
     */
    public float sumTime(){
        float sumTime = 0;
        for(Game game : this.playedBattleRoyalsList){
            sumTime += Float.parseFloat(game.getGameStats().get("timeInGame"));
        }
        for(Game game : this.playedArenasList){
            sumTime += Float.parseFloat(game.getGameStats().get("timeInGame"));
        }
        return sumTime;
    }

    /**
     * Function calculating Kill/Death Ratio of player for Battle Royal games
     *
     *
     * @return float KDR
     */

    public float getKDRatio(){
        float sumKills = 0;
        for(Game game : this.playedBattleRoyalsList){
            sumKills += Float.parseFloat(game.getGameStats().get("kills"));
        }
        return sumKills / this.playedBattleRoyalsList.size();
    }

}
