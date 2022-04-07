package furche.pg;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static List<Float> calcInitScores(PlayerBase playerBase){
        List<Float> playersInitScores = new ArrayList<>();
        for(Player player : playerBase.getPlayerBaseList()){
            playersInitScores.addAll(player.getGameHistory().calculateInitBattleRoyalScores());
        }
        return playersInitScores;
    }

    public static Map<String, List<Double>> initPlatformGameScoresMap(){
        Map<String, List<Double>> platformGameScores = new LinkedHashMap<>();
        platformGameScores.put("PC", new ArrayList<>());
        platformGameScores.put("Playstation", new ArrayList<>());
        platformGameScores.put("Xbox", new ArrayList<>());
        platformGameScores.put("Nintendo Switch", new ArrayList<>());
        return platformGameScores;
    }

    public static void calcPlatformAvg(Map<String, List<Double>> platformGameScores) {
        for (String platform : platformGameScores.keySet()) {
            double avgGameScore = platformGameScores.get(platform).stream().mapToDouble(v -> v).sum() / platformGameScores.get(platform).size();
            System.out.printf("Platform: %s -> Average game score: %f\n", platform, avgGameScore);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PlayerBase playerBase = new PlayerBase();
        List<Float> playersInitScores = calcInitScores(playerBase);
        Map<String, List<Double>> platformGameScores = initPlatformGameScoresMap();
        Integer[] indexInterval1 = {0, (playerBase.getPlayerBaseList().size()/4) - 1};
        Integer[] indexInterval2 = {playerBase.getPlayerBaseList().size()/4, (playerBase.getPlayerBaseList().size()/2) - 1};
        Integer[] indexInterval3 = {(playerBase.getPlayerBaseList().size()/2), (3*playerBase.getPlayerBaseList().size()/4) - 1};
        Integer[] indexInterval4 = {(3*playerBase.getPlayerBaseList().size()/4), playerBase.getPlayerBaseList().size()};
        StatisticsCalculator calcThread1 = new StatisticsCalculator(playerBase, playersInitScores, platformGameScores, indexInterval1);
        StatisticsCalculator calcThread2 = new StatisticsCalculator(playerBase, playersInitScores, platformGameScores, indexInterval2);
        StatisticsCalculator calcThread3 = new StatisticsCalculator(playerBase, playersInitScores, platformGameScores, indexInterval3);
        StatisticsCalculator calcThread4 = new StatisticsCalculator(playerBase, playersInitScores, platformGameScores, indexInterval4);
        Thread t1 = new Thread(calcThread1);
        Thread t2 = new Thread(calcThread2);
        Thread t3 = new Thread(calcThread3);
        Thread t4 = new Thread(calcThread4);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        calcPlatformAvg(platformGameScores);

    }
}
