package furche.pg;

import java.util.*;

public class GameListComparatorDate implements Comparator<Game> {
    /**
     * Function parsing date in String to list with each part of date as element:
     * list[0] -> day
     * list[1] -> month
     * list[2] -> year
     *
     * @param game - Game played game
     * @return list containing parsed date
     */

    private List<Integer> getParsedDate(Game game){
        String date = game.getGameStats().get("date");
        StringTokenizer tokenizer = new StringTokenizer(date, "-");
        List<Integer> parsedDate = new ArrayList<>();
        while(tokenizer.hasMoreTokens()){
            String nextToken = tokenizer.nextToken();
            if(nextToken.startsWith("0")){
                parsedDate.add(Integer.parseInt(nextToken.substring(1)));
            }
            else {
                parsedDate.add(Integer.parseInt(nextToken));
            }
        }
        return parsedDate;
    }

    /**
     * Function comparing two dates
     * @param parsedDateG1 - parsed date of object Game g1
     * @param parsedDateG2 - parsed date of object Game g2
     * @return 1 if dateG1 > dateG2, 0 if dateG1 == dateG2, -1 if, if dateG1 < dateG2
     */

    private int compareDates(List<Integer> parsedDateG1, List<Integer> parsedDateG2){

        if(parsedDateG1.get(2) > parsedDateG2.get(2)){ // compare year
            return 1;
        }
        else if(parsedDateG1.get(2) < parsedDateG2.get(2)){
            return -1;
        }
        else {

            if(parsedDateG1.get(1) > parsedDateG2.get(1)){ // compare month
                return 1;
            }
            else if(parsedDateG1.get(1) < parsedDateG2.get(1)){
                return -1;
            }
            else {
                return parsedDateG1.get(0).compareTo(parsedDateG2.get(0)); // compare day
            }
        }
    }

    @Override
    public int compare(Game g1, Game g2){
        List<Integer> parsedDateG1 = this.getParsedDate(g1);
        List<Integer> parsedDateG2 = this.getParsedDate(g2);
        return this.compareDates(parsedDateG1, parsedDateG2);
    }
}
