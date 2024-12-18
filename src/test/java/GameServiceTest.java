import javafx.scene.shape.Circle;
import state.GameState;
import state.MyCircle;
import service.GameService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private final GameState gameState = new GameState();
    private final GameService gameService = new GameService(gameState);

    @Test
    void testInitialBoardSetup() {
        MyCircle[][] board = gameState.getBoard();
        assertEquals(MyCircle.BLUE, board[0][4]);
        assertEquals(MyCircle.BLUE, board[2][2]);
        assertEquals(MyCircle.BLUE, board[4][0]);
        assertEquals(MyCircle.RED, board[0][0]);
    }

    @Test
    void testMovePieceBlueValidMove() {
        gameState.move(2, 2, 2, 3);
        assertEquals(MyCircle.NONE, gameState.getPieceAt(2, 2));
        assertEquals(MyCircle.BLUE, gameState.getPieceAt(2, 3));
    }

    @Test
    void testMovePieceRedValidMove() {
        gameState.move(2, 2, 2, 3);
        gameState.move(1, 1, 1, 2);
        assertEquals(MyCircle.NONE, gameState.getPieceAt(1, 1));
        assertEquals(MyCircle.RED, gameState.getPieceAt(1, 2));
    }

    @Test
    void testMovePieceOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> gameState.move(0, 0, -1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> gameState.move(4, 4, 5, 5));
    }

    @Test
    void testIsWithinBounds() {
        assertTrue(gameService.isWithinBounds(0, 0));
        assertTrue(gameService.isWithinBounds(4, 4));
        assertFalse(gameService.isWithinBounds(-1, 0));
        assertFalse(gameService.isWithinBounds(5, 5));
    }

    @Test
    void testCanBlueMoveTo() {
        assertTrue(gameService.canBlueMoveTo(1, 1));
        gameState.move(2, 2, 1, 1);
        assertFalse(gameService.canBlueMoveTo(1, 1));
    }

    @Test
    void testAreAllElementsTheSame() {
        ArrayList<Integer> allSame = Stream.of(2, 2, 2).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> notSame = Stream.of(1, 2, 3).collect(Collectors.toCollection(ArrayList::new));
        assertTrue(gameService.areAllElementsTheSame(allSame));
        assertFalse(gameService.areAllElementsTheSame(notSame));
    }

    @Test
    void testRedWon() {
        gameState.move(2, 2, 1, 2);
        gameState.move(2, 1, 2, 2);
        gameState.move(1, 2, 0, 2);
        gameState.move(1, 1, 2, 1);
        gameState.move(4, 0, 3, 0);
        gameState.move(2, 2, 1, 2);
        gameState.move(3, 0, 2, 0);
        gameState.move(4, 1, 4, 0);
        gameState.move(2, 0, 1, 0);
        gameState.move(4, 0, 4, 1);
        gameState.move(1, 0, 0, 0);

        assertTrue(gameService.redWon());
        assertTrue(gameService.isGameOver());
    }

    @Test
    void testIsPieceSelectable() {
        assertTrue(gameService.isPieceSelectable(0, 4));
        gameState.setBlueTurn(false);
        assertFalse(gameService.isPieceSelectable(0, 4));
    }

    @Test
    void testResetSelection() {
        Circle dummyCircle = new Circle();
        gameState.setSelectedCircle(dummyCircle);
        gameService.resetSelection();
        assertNull(gameState.getSelectedCircle());
    }

    @Test
    void testHandleNewSelection() {
        Circle dummyCircle = new Circle();
        gameService.handleNewSelection(dummyCircle, 2, 2);
        assertEquals(dummyCircle, gameState.getSelectedCircle());
        assertEquals(2, gameState.getRow());
        assertEquals(2, gameState.getCol());
    }

    @Test
    void testToString() {
        String boardString = gameState.toString();
        assertTrue(boardString.contains("BLUE"));
        assertTrue(boardString.contains("RED"));
    }

    @Test
    void testIsPieceBlue() {
        assertTrue(gameService.isPieceBlue(0, 4));
        assertFalse(gameService.isPieceBlue(0, 0));
    }

    @Test
    void testIsPieceRed() {
        assertTrue(gameService.isPieceRed(0, 0));
        assertFalse(gameService.isPieceRed(0, 4));
    }

    @Test
    void testIsPieceEmpty() {
        gameState.move(2, 2, 2, 3);
        assertTrue(gameService.isPieceEmpty(2, 2));
    }

    @Test
    void testIncrementBlueMoves() {
        int initialMoves = gameService.getBlueMoves();
        gameState.incrementBlueMoves();
        assertEquals(initialMoves + 1, gameService.getBlueMoves());
    }

    @Test
    void testIncrementRedMoves() {
        int initialMoves = gameService.getRedMoves();
        gameState.incrementRedMoves();
        assertEquals(initialMoves + 1, gameService.getRedMoves());
    }
}
