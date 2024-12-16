import model.GameModel;
import enums.MyCircle;
import org.junit.jupiter.api.Test;
import enums.Direction;
import service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameModelTest {

    GameModel model = new GameModel();
    private final GameService gameService = new GameService(model);

    @Test
    void moveTest(){
        model.move(2, 2, Direction.UP);
        if(!gameService.isGameOver()) {
            assertEquals(model.getBoard()[2][2], MyCircle.NONE);
            assertEquals(model.getBoard()[1][2], MyCircle.BLUE);
            model.move(3, 2, Direction.UP);
            assertEquals(model.getBoard()[3][2], MyCircle.NONE);
            assertEquals(model.getBoard()[2][2], MyCircle.RED);
        }else{
            assertEquals(model.getBoard()[2][2], MyCircle.BLUE);
        }
    }

    @Test
    void toStringTest(){
        String expected = "RED RED RED RED BLUE \n" +
                "RED RED RED RED RED \n" +
                "RED RED BLUE RED RED \n" +
                "RED RED RED RED RED \n" +
                "BLUE RED RED RED RED \n";
        String toStr = model.toString();
        assertEquals(expected, toStr);
    }
}
