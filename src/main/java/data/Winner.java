package data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for representing the winner of the game.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Winner {
    private String winnerName;
    private String winnerColor;
    private int winnerMoves;
}
