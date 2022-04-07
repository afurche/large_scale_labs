import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.Scanner;

@Getter
@Setter
public class Player {
    private String nick;
    private String platform;
    private final GameHistory gameHistory;
    private String rank;

     public Player(String nick, String platform){
         this.nick = nick;
         this.platform = platform;
         this.gameHistory = new GameHistory();
         this.rank = "bronze";
     }

    public Player(String nick, String platform, String rank){  // constructor overloading
        this.nick = nick;
        this.platform = platform;
        this.gameHistory = new GameHistory();
        this.rank = rank;
    }

     public void addGameToHistory(String date, float timeInGame, String hero, int kills, int assists, int damage, int position){
         Game game = new BattleRoyal(date, timeInGame, hero, kills, assists, damage, position);
         this.gameHistory.addGameToHistory(game);
     }

     public void addGameToHistory(String date, float timeInGame, String hero, int kills, int assists, int damage, boolean hasWon){ // method overloading
        Game game = new Arena(date, timeInGame, hero, kills, assists, damage, hasWon);
        this.gameHistory.addGameToHistory(game);
     }

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
         System.out.printf("Games played: %d Battle Royal: %d Arena: %d%n", battleRoyalsPlayed + arenasPlayed, battleRoyalsPlayed, arenasPlayed);
     }

     public void showBattleRoyalStats(){
         System.out.printf("Player: %s Platform: %s Battle Royal Stats : %s%n", this.nick, this.platform, this.gameHistory.getPlayerBattleRoyalStats());
     }

     public void showArenaStats(){
         System.out.printf("Player: %s Platform%s Arena Stats %s%n", this.nick, this.platform, this.gameHistory.getPlayerArenaStats());
     }

     public float getSumTimePlayed(){
         return this.gameHistory.sumTime();
     }

     public float getKDRatio(){
         return this.gameHistory.getKDRatio();
     }

    /**
     * Function generating small amount of random data for testing purposes
     */
     public void generateGameData() {
         Random rand = new Random();
         for (int i = 0; i < 5; i++) {
             String date = "11-01-2022";
             String hero = "Lifeline";
             float timeInGame = rand.nextFloat() * 20;
             int kills = rand.nextInt(10);
             int assists = rand.nextInt(10);
             int damage = (kills * 200) + (assists * 100);
             int position = 1 + rand.nextInt(19);
             boolean hasWon = i % 2 == 0;
             //usage of overloaded method
             this.addGameToHistory(date, timeInGame, hero, kills, assists, damage, position);
             this.addGameToHistory(date, timeInGame, hero, kills, assists, damage, hasWon);
             this.addRankedGameToHistory(date, timeInGame, hero, kills, assists, damage, position);
         }
     }


    /**
     * Function created to show functionalities regarding Player class
     * Allows for 3 operations:
     * 1 -> show statistics regarding number of played games (total games, Battle Royal games, Arena games
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

    public static void main(String[] args){
         Player p = new Player("Adam", "Playstation", "gold");
         p.playerApp();
     }
}

