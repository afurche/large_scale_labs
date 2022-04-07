import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class PlayerBase {
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

    public void showPlayersBattleRoyalStats(){
        this.playerBaseList.forEach(Player::showBattleRoyalStats);
    }

    public void showPlayersArenaStats(){
        this.playerBaseList.forEach(Player::showArenaStats);
    }

    public void showPlayersTimePlayedStats(){
        float sumTimeAllPlayers = 0;
        for (Player player: this.playerBaseList){
            sumTimeAllPlayers += player.getSumTimePlayed();
        }
        System.out.printf("Sum time played: %f min%nAverage time played: %f min%n", sumTimeAllPlayers, sumTimeAllPlayers/this.playerBaseList.size());
    }

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

    public void showPlayerApp(String nick){
        for (Player player : this.playerBaseList){
            if(player.getNick().equals(nick)){
                player.playerApp();
                return;
            }
        }
    }

    public static void main(String[] args){
        PlayerBase base = new PlayerBase();
        for(int i=0; i < 10; i++){
            Player p = new Player(String.format("Adam%d", i), "Playstation");
            p.generateGameData();
            base.addPlayer(p);
            }
        System.out.println("Battle Royal Stats of each player");
        base.showPlayersBattleRoyalStats();
        System.out.println("Arena Stats of each player");
        base.showPlayersArenaStats();
        System.out.println("Time Stats");
        base.showPlayersTimePlayedStats();
        System.out.println("KD Ratio Stats");
        base.showPlayersKDRatioStats();
        System.out.println("Player App");
        base.showPlayerApp("Adam1");
        }


}
