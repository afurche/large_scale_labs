package furche.pg;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class PlayerBase {

    /**
     * Class describing player base (include some subset of Apex Legends players)
     * Attributes:
     * playerBaseList : list including furche.pg.Player objects
     * numberOfPlayers : int storing number of players in the base
     *
     */

    private List<Player> playerBaseList;
    private int numberOfPlayers;

    public PlayerBase(){
        this.playerBaseList = new ArrayList<>();
        this.numberOfPlayers = 0;
    }

    public void addPlayer(Player p){
        this.playerBaseList.add(p);
        this.numberOfPlayers += 1;
    }

    /**
     * Functions showing Battle Royal cumulative game stats for eac consecutive player in the base.
     *
     */

    public void showPlayersBattleRoyalStats(){
        this.playerBaseList.forEach(Player::showBattleRoyalStats);
    }

    /**
     * Functions showing Arena cumulative game stats for eac consecutive player in the base.
     *
     */

    public void showPlayersArenaStats(){
        this.playerBaseList.forEach(Player::showArenaStats);
    }

    /**
     * Function showing cumulative time stats (sum of time played by all players and average time)
     * for players in the base.
     *
     */

    public void showPlayersTimePlayedStats(){
        float sumTimeAllPlayers = 0;
        for (Player player: this.playerBaseList){
            sumTimeAllPlayers += player.getSumTimePlayed();
        }
        System.out.printf("Sum time played: %f min%nAverage time played: %f min%n", sumTimeAllPlayers, sumTimeAllPlayers/this.playerBaseList.size());
    }

    /**
     * Function showing average, max and min KD Ratio for players in the player base.
     *
     */

    public void showPlayersKDRatioStats(){
        float sumKDRatio = 0;
        float minKDRatio = 0;
        float maxKDRatio = 0;
        for (Player player : this.playerBaseList){
            sumKDRatio += player.getKDRatio();
            if (minKDRatio == 0 || player.getKDRatio() < minKDRatio){
                minKDRatio = player.getKDRatio();
            }
            if (maxKDRatio == 0 || player.getKDRatio() > minKDRatio){
                maxKDRatio = player.getKDRatio();
            }
        }
        System.out.printf("Average KD Ratio: %f%nMin KD Ratio: %f%nMax KD Ratio: %f%n", sumKDRatio/this.playerBaseList.size(),minKDRatio, maxKDRatio);
    }

    /**
     * Function that gives functionalities of playerApp for a player found by a nick.
     *
     * @param nick : nick of player
     */
    public void showPlayerApp(String nick){
        for (Player player : this.playerBaseList){
            if(player.getNick().equals(nick)){
                player.playerApp();
                return;
            }
        }
    }

    /**
     * Calculates sum of kills on Battle Royal games of each player in the PlayerBase and prints it's to the screen
     */

    private void calculatePlayerSumKillsBattleRoyal(){
        for(Player player : this.playerBaseList){
            GameHistory gameHistory = player.getGameHistoryClone();
            int sumKills = 0;
            for(Game game : gameHistory.getPlayedBattleRoyalsList()){
                sumKills += Integer.parseInt(game.getGameStats().get("kills"));
            }
            System.out.printf("Player: %s | Sum of kills: %d\n", player.getNick(), sumKills);
        }
    }

    public static void main(String[] args){
        PlayerBase base = new PlayerBase();
        for(int i=0; i < 10; i++){
            Player p = new Player(String.format("Adam%d", i), "Playstation");
            p.generateGameData();
            base.addPlayer(p);
        }
        base.calculatePlayerSumKillsBattleRoyal();
//        System.out.println("Battle Royal Stats of each player");
//        base.showPlayersBattleRoyalStats();
//        System.out.println("furche.pg.Arena Stats of each player");
//        base.showPlayersArenaStats();
//        System.out.println("Time Stats");
//        base.showPlayersTimePlayedStats();
//        System.out.println("KD Ratio Stats");
//        base.showPlayersKDRatioStats();
//        System.out.println("Player App");
//        base.showPlayerApp("Adam1");
    }


}
