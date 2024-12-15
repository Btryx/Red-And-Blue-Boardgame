
import model.GameModel;
import model.MyCircle;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelTest2 {


    GameModel model = new GameModel();

    @Test
    void AreAllElementsTheSameTest(){
        ArrayList<Integer> nums = Stream.of(1, 2, 3, 4, 5).collect(Collectors.toCollection(ArrayList::new));
        assertFalse(model.areAllElementsTheSame(nums));
    }


    @Test
    void AreAllElementsTheSameTest2(){
        ArrayList<Integer> nums = Stream.of(1, 1, 1, 1).collect(Collectors.toCollection(ArrayList::new));
        assertTrue(model.areAllElementsTheSame(nums));
    }

    @Test
    void isGameOverTest(){
        if(model.redWon()) assertTrue(model.isGameOver());
        if(model.blueWon()) assertTrue(model.isGameOver());
        if(!model.blueWon() && !model.redWon()) assertFalse(model.isGameOver());
    }

    @Test
    void isCellEmptyTest(){
        assertTrue(model.isCellEmpty(-1, 0));
        if(model.board[2][2] == null) assertTrue(model.isCellEmpty(2, 2));
    }

    @Test
    void canBlueMoveToTest(){
        var c = model.board[2][3];
        if(c == MyCircle.RED) assertTrue(model.canBlueMoveTo(2, 3));
        else assertFalse(model.canBlueMoveTo(2, 3));
    }

}
