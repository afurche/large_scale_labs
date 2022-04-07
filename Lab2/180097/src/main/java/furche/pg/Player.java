package furche.pg;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Player {
    /**
     * Class describing player of Apex Legends
     * Attributes:
     * nick : String player nick
     * platform : String platform players plays on (e.g."PC", "Playstation" etc.)
     * gameHistory : object of class GameHistory storing players game history
     * rank : String storing game rank of player (e.g. "bronze", "silver")
     */

    //example of hermetization
    private String nick;
    private String platform;
    private final GameHistory gameHistory;
    private PlayerRank rank;

    public Player(String nick, String platform){
        this.nick = nick;
        this.platform = platform;
        this.gameHistory = new GameHistory();
        this.rank = PlayerRank.BRONZE;
    }

    public Player(String nick, String platform, PlayerRank rank){  // constructor overloading
        this.nick = nick;
        this.platform = platform;
        this.gameHistory = new GameHistory();
        this.rank = rank;
    }


    /**
     * Addition of Battle Royal game
     * @param date - String date of the game
     * @param timeInGame - float time spent in game in minutes
     * @param hero - String name of hero used in game
     * @param kills - int number of kills scored
     * @param assists - int number of assists scored
     * @param damage - int number of damage dealt
     * @param position - int position on which player finished the game, in the interval [1;20]
     */

    public void addGameToHistory(String date, float timeInGame, String hero, int kills, int assists, int damage, int position){
        Game game = new BattleRoyal(date, timeInGame, hero, kills, assists, damage, position);
        this.gameHistory.addGameToHistory(game);
    }

    /**
     * Addition of furche.pg.Arena game
     * @param date - String date of the game
     * @param timeInGame - float time spent in game in minutes
     * @param hero - String name of hero used in game
     * @param kills - int number of kills scored
     * @param assists - int number of assists scored
     * @param damage - int number of damage dealt
     * @param hasWon - bool true if player won the game false if lost
     */

    public void addGameToHistory(String date, float timeInGame, String hero, int kills, int assists, int damage, boolean hasWon){ // method overloading
        Game game = new Arena(date, timeInGame, hero, kills, assists, damage, hasWon);
        this.gameHistory.addGameToHistory(game);
    }
    /**
     * Addition of Ranked Battle Royal game
     * Only difference from normal Battle Royal is that rank of a player is used in the constructor of the game
     */


    public void addRankedGameToHistory(String date, float timeInGame, String hero, int kills, int assists, int damage, int position){
        Game game = new RankedBattleRoyal(date, timeInGame, hero, kills, assists, damage, position, this.rank);
        this.gameHistory.addGameToHistory(game);
    }

    public void showGameHistory(int choiceGameType){
        this.gameHistory.showGameResults(choiceGameType);
    }

    public void showNumberOfGamesPlayed(){
        int battleRoyalsPlayed = this.gameHistory.getHistoryStats().getOrDefault("battleRoyalsPlayed", 0);
        int arenasPlayed = this.gameHistory.getHistoryStats().getOrDefault("arenasPlayed", 0);
        System.out.printf("Games played: %d Battle Royal: %d furche.pg.Arena: %d%n", battleRoyalsPlayed + arenasPlayed, battleRoyalsPlayed, arenasPlayed);
    }

    public void showBattleRoyalStats(){
        System.out.printf("furche.pg.Player: %s Platform: %s Battle Royal Stats : %s%n", this.nick, this.platform, this.gameHistory.getPlayerBattleRoyalStats());
    }

    public void showArenaStats(){
        System.out.printf("furche.pg.Player: %s Platform%s Arena Stats %s%n", this.nick, this.platform, this.gameHistory.getPlayerArenaStats());
    }

    public float getSumTimePlayed(){
        return this.gameHistory.sumTime();
    }

    public float getKDRatio(){
        return this.gameHistory.getKDRatio();
    }

    public GameHistory getGameHistoryClone(){
        return (GameHistory) this.gameHistory.clone();
    }

    /**
     * Function generating small amount of random data for testing purposes
     */
    public void generateGameData() {
        List<String> dates = Arrays.asList("10-03-2012", "12-11-2021", "11-01-2022", "04-05-2019", "12-01-2022");
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            String hero = "Lifeline";
            float timeInGame = rand.nextFloat() * 20;
            int kills = rand.nextInt(10);
            int assists = rand.nextInt(10);
            int damage = (kills * 200) + (assists * 100);
            int position = 1 + rand.nextInt(19);
            boolean hasWon = i % 2 == 0;
            //usage of overloaded method
            this.addGameToHistory(dates.get(i), timeInGame, hero, kills, assists, damage, position);
            this.addGameToHistory(dates.get(i), timeInGame, hero, kills, assists, damage, hasWon);
            this.addRankedGameToHistory(dates.get(i), timeInGame, hero, kills, assists, damage, position);
        }
    }


    /**
     * Function created to show functionalities regarding furche.pg.Player class
     * Allows for 3 operations:
     * 1 -> show statistics regarding number of played games (total games, Battle Royal games, furche.pg.Arena games
     * 2 -> show Battle Royal statistics (games played, games won, games top3, games top5...)
     * 3 -> show all game entries for chosen mode or for all
     *
     */
    public void playerApp() {
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Menu\n1.Show number of played games\n2.Show battle royal stats\n3.Show game history\n9.Exit");
        while (choice != 9) {
            System.out.println("Type choice:");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    this.showNumberOfGamesPlayed();
                    break;
                case 2:
                    this.showBattleRoyalStats();
                    break;
                case 3:
                    System.out.println("Choose game type:\n1.Battle Royal\n2.Arena\n3.All");
                    int choiceGameType = scanner.nextInt();
                    this.showGameHistory(choiceGameType);
                    break;
                case 9:
                    System.out.println("Goodbye !");
                    break;
                default:
                    System.out.println("No such option");
                    break;
            }
        }
    }

    /**
     * Function testing correctness of implemented deep cloning.
     *
     */

    public void testDeepCopy(){
        GameHistory deepCopyGameHistory = (GameHistory) this.gameHistory.clone();
        System.out.printf("Orignal == Clone: %b\n", this.gameHistory == deepCopyGameHistory);
        System.out.printf("Original Battle Royal List == Clone Battle Royal List: %b\n", this.gameHistory.getPlayedBattleRoyalsList() == deepCopyGameHistory.getPlayedBattleRoyalsList());
        System.out.printf("Orignal Battle Royal First Entry == Clone Battle Royal First Entry: %b\n", this.gameHistory.getPlayedBattleRoyalsList().get(0) == deepCopyGameHistory.getPlayedBattleRoyalsList().get(0));
        Game game = new BattleRoyal("11-12-2001", 69.69f,"Ash", 69, 69, 6969, 1);
        deepCopyGameHistory.addGameToHistory(game);
        System.out.println("Original game history: ");
        this.showGameHistory(1);
        System.out.println("Deep copy game history: ");
        deepCopyGameHistory.showGameResults(1);
    }


    public static void main(String[] args) {
        Player p = new Player("Adam", "Playstation", PlayerRank.GOLD);
        p.generateGameData();
        p.playerApp();
//        p.testDeepCopy();

    }
}

