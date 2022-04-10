package furche.pg;

import java.util.*;

public class StatisticsCalculator {
    /**
     * Class which can be used for calculating statistics, at the moment supports calculating average game scores for
     * each platform.
     * Attributes :
     * List<Float> playersInitScores -> list of not normalized game scores of all games of all players
     * Map<String, List<Double>> platformGameScores -> map storing normalized game scores of all games separately for each platform
     * PlayerBase playerBase - > PlayerBase object player base with 1200 players at max (that's how much data is available)
     */
    private final List<Float> playersInitScores;
    private final Map<String, List<Double>> platformGameScores;
    private final PlayerBase playerBase;

    /**
     * Constructor of class Statistics Calculator.
     * @param playerBase - PlayerBase base of players
     */

    public StatisticsCalculator(PlayerBase playerBase){
        this.playerBase = playerBase;
        this.playersInitScores = calcInitScores();
        this.platformGameScores = this.initPlatformGameScoresMap();
    }

    /**
     * Function calculates not normalized game scores of each game for all players and collects them in a list
     * @return List containing not normalized game scores of all games of all players.
     */

    public List<Float> calcInitScores(){
        List<Float> playersInitScores = new ArrayList<>();
        for(Player player : this.playerBase.getPlayerBaseList()){
            playersInitScores.addAll(player.getGameHistory().calculateInitBattleRoyalScores());
        }
        return playersInitScores;
    }

    /**
     * Function initializing platformGameScores map to have an entry for each platform
     * @return Map with platform -> initialized List<Double> for storing average normalized game scores
     */

    public Map<String, List<Double>> initPlatformGameScoresMap(){
        Map<String, List<Double>> platformGameScores = new LinkedHashMap<>();
        platformGameScores.put("PC", new ArrayList<>());
        platformGameScores.put("Playstation", new ArrayList<>());
        platformGameScores.put("Xbox", new ArrayList<>());
        platformGameScores.put("Nintendo Switch", new ArrayList<>());
        return platformGameScores;
    }

    /**
     * Function that calculates average of average game score for each platform and prints it to the screen.
     * Additionaly prints amount of entries in game all lists in the platformGameScores and number of players in base
     * to show that there is no race condition and critical section is handled.
     */

    public void calcPlatformAvg() {
        int sumGameScoreEntries = 0;
        for (String platform : this.platformGameScores.keySet()) {
            int platformSize = this.platformGameScores.get(platform).size();
            double avgGameScore = this.platformGameScores.get(platform).stream().mapToDouble(v -> v).sum() / platformSize;
            System.out.printf("Platform: %s -> Average game score: %f\n", platform, avgGameScore);
            sumGameScoreEntries += platformSize;
        }
        System.out.printf("Average game score entries: %d  Number of players in base: %d", sumGameScoreEntries, this.playerBase.getPlayerBaseList().size());
    }

    /**
     * Operation of calculating average normalized game score for each player in additions and insertion of the value
     * to platformGameScores map
     * Performs without multithreading.
     */
    public void operationsSingleThread(){
        long start = System.currentTimeMillis();
        for(Player player : this.playerBase.getPlayerBaseList()){
            List<Double> normalizedScores = player.getGameHistory().calcNormalizedBattleRoyalScores(this.playersInitScores);
            double playerAvgNormalizedGameScore = normalizedScores.stream().mapToDouble(v -> v).sum() / normalizedScores.size();
            this.platformGameScores.get(player.getPlatform()).add(playerAvgNormalizedGameScore);
        }
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration %f\n",duration/1000f);
        calcPlatformAvg();
    }
    /**
     * Operation of calculating average normalized game score for each player in additions and insertion of the value
     * to platformGameScores map
     * Performs with multithreading, allows to manually choose number of threads used.
     */
    public void operationsMultipleThreads() throws InterruptedException {


        List<Thread> threads = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type num of threads: ");
        int numOfThreads = scanner.nextInt();
        int intervalLength = this.playerBase.getPlayerBaseList().size() / numOfThreads;

        for(int i = 0; i < numOfThreads; i++){
            int start;
            int end;
            if(i != numOfThreads - 1){
                if(i == 0)
                {
                    start = 0;
                    end = intervalLength;
                }else
                {
                    start = i * intervalLength;
                    end = (i + 1) * intervalLength;
                }
            }else
            {
                start = i * intervalLength;
                end = (i + 1) * intervalLength + (this.playerBase.getPlayerBaseList().size() % numOfThreads);
            }

            threads.add(new Thread(new StatsCalcRunnable(this.playerBase, this.playersInitScores, this.platformGameScores, start, end)));
        }


        long start = System.currentTimeMillis();
        for(Thread t : threads){
            t.start();
        }
        for(Thread t : threads){
            t.join();
        }
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration %f\n",duration/1000f);
        calcPlatformAvg();

    }

    /**
     * Main function used to experiment with multithreading.
     * Allows choosing amount of players and choose if single or multithreaded.
     */

    public static void main(String[] args) throws InterruptedException {

        PlayerBase playerBase;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Menu:\n1.Choose player cap\n2.Max player cap\n");
        System.out.println("Type choice:");
        if (scanner.nextInt() == 1) {
            System.out.println("Type player cap:");
            playerBase = new PlayerBase(scanner.nextInt());
        }else{
            playerBase = new PlayerBase();
        }
        StatisticsCalculator calc = new StatisticsCalculator(playerBase);
        System.out.println("Menu:\n1.Single Threaded\n2.Multi Threaded");
        System.out.println("Type choice:");
        switch (scanner.nextInt()) {
            case 1:
                calc.operationsSingleThread();
                break;
            case 2:
                calc.operationsMultipleThreads();
                break;
            default:
                System.out.println("No such option");
                break;
        }
    }
}
