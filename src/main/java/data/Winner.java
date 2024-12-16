package data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a winner in a game.
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
