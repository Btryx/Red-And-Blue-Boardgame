package data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository specifically for managing game winners.
 */
public class WinnerRepository extends GenericRepository<Winner> {

    /**
     * Constructs a new WinnerRepository.
     */
    public WinnerRepository() {
        super(Winner.class);
    }

    /**
     * Retrieves the top 10 winners based on the least number of moves.
     *
     * @return list of top 10 winners sorted by moves
     */
    public List<Winner> getTopTenWinners() {
        return getElements().stream()
                .sorted(Comparator.comparing(Winner::getWinnerMoves))
                .limit(10)
                .collect(Collectors.toList());
    }
}
