package furche.pg;

import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
public class StatsCalcRunnable implements Runnable{
    /**
     * Runnable for threads in StatisticsCalculator.
     * Attributes:
     * PlayerBase playerBase - base of players
     * List<Float> playerInitScores - list containing not normalized scores of all games of all players
     * Map<String, List<Double>> platformGameScores - map storing normalized game scores of all games separately for each platform
     * int start - start of interval for taking players from list in playerBase
     * int end - end of interval for taking players from list in playerBase
     */

    private PlayerBase playerBase;
    private List<Float> playersInitScores;
    private final Map<String, List<Double>> platformGameScores;
    private int start;
    private int end;

    /**
     * Constructor of class StatsCalcRunnable.
     * @param playerBase - PlayerBase base of players
     * @param playersInitScores List<Float> containing not normalized scores of all games of all players
     * @param platformGameScores  Map<String, List<Double>>storing normalized game scores of all games separately for each platform
     * @param start - int start of interval for taking players from list in playerBase
     * @param end - int end of interval for taking players from list in playerBase
     */

    public StatsCalcRunnable(PlayerBase playerBase, List<Float> playersInitScores, Map<String, List<Double>> platformGameScores, int start, int end){
        this.playerBase = playerBase;
        this.playersInitScores = playersInitScores;
        this.platformGameScores = platformGameScores;
        this.start = start;
        this.end = end;
    }

    /**
     * Function calculating average normalized game of score of players from given subset of player list in playerBase.
     */

    public void operations(){
        for(Player player : this.playerBase.getPlayerBaseList().subList(this.start, this.end)){
            List<Double> normalizedScores = player.getGameHistory().calcNormalizedBattleRoyalScores(this.playersInitScores);
            double playerAvgNormalizedGameScore = normalizedScores.stream().mapToDouble(v -> v).sum() / normalizedScores.size();
            synchronized (this.platformGameScores){
                this.platformGameScores.get(player.getPlatform()).add(playerAvgNormalizedGameScore);
            }
        }
    }

    /**
     * Required method of Runnable.
     */

    public void run() {
        this.operations();
    }
}
