package Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WinnerRepo extends GsonRepo<Winner>{

    public WinnerRepo() {super(Winner.class);}

    /**
     * Gives a list of the top 10 winners, who won with the least amount of moves.
     * @return list of winners
     */
    public List<Winner> topTen(){
        return elements.stream()
                .sorted(Comparator.comparing(Winner::getWinnerMoves))
                .limit(10)
                .collect(Collectors.toList());
    }
}
