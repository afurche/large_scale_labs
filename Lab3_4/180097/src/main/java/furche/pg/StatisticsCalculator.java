package furche.pg;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Getter
@Setter
public class StatisticsCalculator implements Runnable{

    private PlayerBase playerBase;
    private List<Float> playersInitScores;
    private Map<String, List<Double>> platformGameScores;
    private Integer[] indexInterval;

    public StatisticsCalculator(PlayerBase playerBase, List<Float> playersInitScores, Map<String, List<Double>> platformGameScores, Integer[] indexInterval){
        this.playerBase = playerBase;
        this.playersInitScores = playersInitScores;
        this.indexInterval = indexInterval;
        this.platformGameScores = platformGameScores;
    }

    public synchronized void addGameScoreToPlatformMap(String platform, double playerAvgNormalizedGameScore){
        this.platformGameScores.get(platform).add(playerAvgNormalizedGameScore);
    }

    public void threadOperations(){
        for(Player player : this.playerBase.getPlayerBaseList().subList(this.indexInterval[0], this.indexInterval[1])){
            List<Double> normalizedScores = player.getGameHistory().calcNormalizedBattleRoyalScores(this.playersInitScores);
            double playerAvgNormalizedGameScore = normalizedScores.stream().mapToDouble(v -> v).sum() / normalizedScores.size();
            this.addGameScoreToPlatformMap(player.getPlatform(), playerAvgNormalizedGameScore);
        }
    }

    public void run() {
        long start = System.currentTimeMillis();
        this.threadOperations();
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration %f",duration/1000f);
    }
}
