package furche.pg;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Getter
@Setter
public class StatisticsCalculator {

    private Map<String, List<Double>> platformGameScores;

    public StatisticsCalculator(){
        this.platformGameScores = new LinkedHashMap<>();
        this.platformGameScores.put("PC", new ArrayList<>());
        this.platformGameScores.put("Playstation", new ArrayList<>());
        this.platformGameScores.put("Xbox", new ArrayList<>());
        this.platformGameScores.put("Nintendo Switch", new ArrayList<>());
    }


    /**
     * Function used for creating a new player and inserting theirs game history from csv file.
     * @param playerNick - String nick of player
     * @param platform - String platform
     * @param rank - PlayerRank rank of player
     * @return Player - new player object
     */

    public static Player createPlayerAndReadGameHistory(String playerNick, String platform, PlayerRank rank){
        Player player = new Player(playerNick, platform, rank);
        String pathToGameHistory = "src/main/resources/game_history_files/"+player.getNick()+".csv";
        System.out.println(pathToGameHistory);
        try (BufferedReader br = new BufferedReader(new FileReader(pathToGameHistory))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                player.addGameToHistory(values[0], Float.parseFloat(values[1]), values[2], Integer.parseInt(values[3]), Integer.parseInt(values[4]), Integer.parseInt(values[5]), Integer.parseInt(values[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }

    /**
     * Function used for creating Player Base
     * New PlayerBase object is created, than based on data from player_info new players are created using createPlayerAndReadGameHistory
     * @return PlayerBase new player base object
     */

    public static PlayerBase createPlayerBase(){
        PlayerBase playerBase = new PlayerBase();
        String pathToPlayerInfo = "src/main/resources/player_info.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(pathToPlayerInfo))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                PlayerRank rank;
                switch(values[2]){
                    case "Silver":
                        rank = PlayerRank.SILVER;
                        break;
                    case "Gold":
                        rank = PlayerRank.GOLD;
                        break;
                    case "Platinum":
                        rank = PlayerRank.PLATINUM;
                        break;
                    default:
                        rank = PlayerRank.BRONZE;
                        break;
                }
                playerBase.addPlayer(createPlayerAndReadGameHistory(values[0], values[1], rank));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerBase;
    }

    public void statisticsOperations(PlayerBase playerBase){

        long start = System.currentTimeMillis();
        for(Player player : playerBase.getPlayerBaseList()){
            this.getPlatformGameScores().get(player.getPlatform()).addAll(player.getGameHistory().calcNormalizedBattleRoyalScores());
            System.out.println(player.getGameHistory().playerHistoryAveragesBattleRoyal());
        }

        for (List<Double> gameScoresPlatform : this.getPlatformGameScores().values()){
            float sumPlatform = 0;
            for(double gameScore : gameScoresPlatform){
                sumPlatform += gameScore;
            }
            float avgPlatform = sumPlatform / gameScoresPlatform.size();
            System.out.println(avgPlatform);
        }
        long end = System.currentTimeMillis();
        long duration = end-start;
        System.out.printf("Duration %f",duration/1000f);
    }

    public static void main(String[] argc){
        StatisticsCalculator calc = new StatisticsCalculator();
        PlayerBase playerBase = createPlayerBase();
        calc.statisticsOperations(playerBase);
    }
}
