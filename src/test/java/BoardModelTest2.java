
import model.BoardModel;
import state.MyCircle;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoardModelTest2 {

    BoardModel model = new BoardModel();
    private final GameService gameService = new GameService(model);

    @Test
    void AreAllElementsTheSameTest(){
        ArrayList<Integer> nums = Stream.of(1, 2, 3, 4, 5).collect(Collectors.toCollection(ArrayList::new));
        assertFalse(gameService.areAllElementsTheSame(nums));
    }


    @Test
    void AreAllElementsTheSameTest2(){
        ArrayList<Integer> nums = Stream.of(1, 1, 1, 1).collect(Collectors.toCollection(ArrayList::new));
        assertTrue(gameService.areAllElementsTheSame(nums));
    }

    @Test
    void isGameOverTest(){
        if(gameService.redWon()) assertTrue(gameService.isGameOver());
        if(gameService.blueWon()) assertTrue(gameService.isGameOver());
        if(!gameService.blueWon() && !gameService.redWon()) assertFalse(gameService.isGameOver());
    }

    @Test
    void isCellEmptyTest(){
        assertTrue(gameService.isCellEmpty(-1, 0));
        if(model.getBoard()[2][2] == null) assertTrue(gameService.isCellEmpty(2, 2));
    }

    @Test
    void canBlueMoveToTest(){
        var c = model.getBoard()[2][3];
        if(c == MyCircle.RED) assertTrue(gameService.canBlueMoveTo(2, 3));
        else assertFalse(gameService.canBlueMoveTo(2, 3));
    }

}
