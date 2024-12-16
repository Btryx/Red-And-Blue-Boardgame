package state;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameState {
    private Circle selectedCircle;
    private int row;
    private int col;
    private boolean blueTurn = true;
    private int countBlueMoves = 0;
    private int countRedMoves = 0;

    public void setSelectedCircle(Circle circle, int row, int col, Color highlightColor) {
        this.selectedCircle = circle;
        this.row = row;
        this.col = col;
        circle.setFill(highlightColor);
    }

    public void resetSelection() {
        this.selectedCircle = null;
    }

    public void switchTurn(boolean blueTurn) {
        this.blueTurn = blueTurn;
    }

    public void incrementBlueMoves() {
        countBlueMoves++;
    }

    public void incrementRedMoves() {
        countRedMoves++;
    }

    // Getters
    public Circle getSelectedCircle() {
        return selectedCircle;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isBlueTurn() {
        return blueTurn;
    }

    public boolean isAnythingSelected() {
        return selectedCircle != null;
    }

    public int getBlueMoves() {
        return countBlueMoves;
    }

    public int getRedMoves() {
        return countRedMoves;
    }
}
