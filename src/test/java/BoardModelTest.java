import state.GameState;
import state.MyCircle;
import org.junit.jupiter.api.Test;
import state.Direction;
import service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardModelTest {

    GameState gameState = new GameState();
    private final GameService gameService = new GameService(gameState);

    @Test
    void moveTest(){
        gameState.move(2, 2, Direction.UP, true);
        if(!gameService.isGameOver()) {
            assertEquals(gameState.getBoard()[2][2], MyCircle.NONE);
            assertEquals(gameState.getBoard()[1][2], MyCircle.BLUE);
            gameState.move(3, 2, Direction.UP, false);
            assertEquals(gameState.getBoard()[3][2], MyCircle.NONE);
            assertEquals(gameState.getBoard()[2][2], MyCircle.RED);
        }else{
            assertEquals(gameState.getBoard()[2][2], MyCircle.BLUE);
        }
    }

    @Test
    void toStringTest(){
        String expected = "RED RED RED RED BLUE \n" +
                "RED RED RED RED RED \n" +
                "RED RED BLUE RED RED \n" +
                "RED RED RED RED RED \n" +
                "BLUE RED RED RED RED \n";
        String toStr = gameState.toString();
        assertEquals(expected, toStr);
    }
}
