import model.GameModel;
import model.MyCircle;
import org.junit.jupiter.api.Test;
import model.Direction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameModelTest {

    GameModel model = new GameModel();

    @Test
    void moveTest(){
        model.move(2, 2, Direction.UP);
        if(!model.isGameOver()) {
            assertEquals(model.board[2][2], MyCircle.NONE);
            assertEquals(model.board[1][2], MyCircle.BLUE);
            model.move(3, 2, Direction.UP);
            assertEquals(model.board[3][2], MyCircle.NONE);
            assertEquals(model.board[2][2], MyCircle.RED);
        }else{
            assertEquals(model.board[2][2], MyCircle.BLUE);
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
