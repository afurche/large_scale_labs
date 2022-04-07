package furche.pg;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

import java.rmi.dgc.DGC;
import java.util.*;
import java.lang.Math;


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

    /**
     * Constructor of GameHistory
     *
     */
    public GameHistory(){
        this.playedBattleRoyalsList = new ArrayList<>();
        this.playedArenasList = new ArrayList<>();
        this.historyStats = new Hashtable<>();
        this.gameListComparator = new GameListComparatorDate();
    }


    /**
     * Constructor of GameHistory used for deep cloning
     * @param playedBattleRoyalsList - deep copy of battle royal list from original object
     * @param playedArenasList - deep copy of arena list from original object
     * @param historyStats - deep copy of history stats from original object
     */
    public GameHistory(List<BattleRoyal> playedBattleRoyalsList, List<Arena> playedArenasList, Hashtable<String, Integer> historyStats){
        this.playedBattleRoyalsList = playedBattleRoyalsList;
        this.playedArenasList = playedArenasList;
        this.historyStats = historyStats;
        this.gameListComparator = new GameListComparatorDate();
    }

    /**
     * Function copying lists of Games with deep cloning of each game

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

    /**
     * Function calculating players average kills, assists and damage for their whole Battle Royal history
     *
     * @return hashtable with player averages
     */
    public Map<String, Double> playerHistoryAveragesBattleRoyal(){
        Map<String, Double> averages = new LinkedHashMap<>();
        int sumKills = 0;
        int sumAssists = 0;
        int sumDamage = 0;
        int minKills = -1;
        int maxKills = -1;;
        int minAssists = -1;
        int maxAssists = -1;
        int minDamage = -1;
        int maxDamage = -1;
        double meanKills;
        double meanAssists;
        double meanDamage;
        double varKills = 0;
        double stdKills = 0;
        double varAssists = 0;
        double stdAssists = 0;
        double varDamage = 0;
        double stdDamage = 0;
        int list_len = this.playedBattleRoyalsList.size();
        for (Game game : this.playedBattleRoyalsList){
            int kills = Integer.parseInt(game.getGameStats().get("kills"));
            int assists = Integer.parseInt(game.getGameStats().get("assists"));
            int damage = Integer.parseInt(game.getGameStats().get("damage"));
            sumKills += kills;
            sumAssists += assists;
            sumDamage += damage;
            if (minKills > kills || minKills == -1){
                minKills = kills;
            }
            else if (maxKills < kills || maxKills == -1){
                maxKills = kills;
            }

            if (minAssists > assists || minAssists == -1){
                minAssists = assists;
            }
            else if (maxAssists < assists || maxAssists == -1){
                maxAssists = assists;
            }
            if (minDamage > damage || minDamage == -1){
                minDamage = damage;
            }
            else if (maxDamage < damage || maxDamage == -1){
                maxDamage = damage;
            }
        }
        meanKills = (double)sumKills/list_len;
        meanAssists = (double)sumAssists/list_len;
        meanDamage = (double)sumDamage/list_len;
        for (Game game : this.playedBattleRoyalsList){
            varKills += (Double.parseDouble(game.getGameStats().get("kills")) - meanKills) * (Double.parseDouble(game.getGameStats().get("kills")) - meanKills);
            varAssists += (Double.parseDouble(game.getGameStats().get("assists")) - meanAssists) * (Double.parseDouble(game.getGameStats().get("assists")) - meanAssists);
            varDamage += (Double.parseDouble(game.getGameStats().get("damage")) - meanDamage) * (Double.parseDouble(game.getGameStats().get("damage")) - meanDamage);
        }
        varKills = varKills / list_len;
        varAssists = varAssists / list_len;
        varDamage = varDamage / list_len;

        stdKills = Math.sqrt(varKills);
        stdAssists = Math.sqrt(varAssists);
        stdDamage = Math.sqrt(varDamage);

        averages.put("minKills", (double)minKills);
        averages.put("avgKills", meanKills);
        averages.put("maxKills", (double)maxKills);
        averages.put("stdKills", stdKills);
        averages.put("varKills", varKills);

        averages.put("minAssists", (double)minAssists);
        averages.put("avgAssists", meanAssists);
        averages.put("maxAssists", (double)maxAssists);
        averages.put("stdAssists", stdAssists);
        averages.put("varAssists", varAssists);


        averages.put("minDamage", (double)minDamage);
        averages.put("avgDamage", meanDamage);
        averages.put("maxDamage", (double)maxDamage);
        averages.put("stdDamage", stdDamage);
        averages.put("varDamage", varDamage);
        return averages;
    }

    public List<Double> calcNormalizedBattleRoyalScores(){
        List<Float> initScores = new ArrayList<>();
        List<Double> normalizedScores = new ArrayList<>();
        for (Game game : this.playedBattleRoyalsList){
            initScores.add(game.countGameGrade());
        }
        double max = initScores.stream().mapToDouble(v -> v).max().orElseThrow(NoSuchElementException::new);
        double min = initScores.stream().mapToDouble(v -> v).min().orElseThrow(NoSuchElementException::new);
        for(Float gameScore : initScores){
            normalizedScores.add(5*(((double)gameScore - min)/ (max - min)));
        }
        return normalizedScores;
    }

    public void getTimeSeriesPlot(){
        List<? extends Game> sortedGames = new ArrayList<>(this.playedBattleRoyalsList);
        sortedGames.sort(this.gameListComparator);
        List<Float> initScores = new ArrayList<>();
        List<String> gameDates = new ArrayList<>();
        List<Double> normalizedScores = new ArrayList<>();
        for (Game game : sortedGames){
            initScores.add(game.countGameGrade());
            gameDates.add(game.getGameStats().get("date"));
        }
        double max = initScores.stream().mapToDouble(v -> v).max().orElseThrow(NoSuchElementException::new);
        double min = initScores.stream().mapToDouble(v -> v).min().orElseThrow(NoSuchElementException::new);
        for(Float gameScore : initScores){
            normalizedScores.add(5*(((double)gameScore - min)/ (max - min)));
        }
        String[] gamesDatesCol = new String[gameDates.size()];
        gameDates.toArray(gamesDatesCol);

        Table gameDatesScores = Table.create().addColumns(StringColumn.create("Date", gameDates),
                                                            DoubleColumn.create("Game Score", normalizedScores));
        Plot.show(TimeSeriesPlot.create("Date vs Game Score", gameDatesScores, "Date", "Game Score"));
    }
}
