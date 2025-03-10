package org.cis1200.chess;

import java.io.Serializable;

public class Rook extends Piece implements Serializable {

    //creates Rook object using Piece's (superclass)'s constructor
    public Rook(int color, Position position) {
        super(color, position);
    }

    /**
     * Function to check if a move is valid for the rook.
     * @param to Position to move the piece to.
     * @param game The state of the Chess game.
     * @return true if move is valid, false otherwise.
     */

    @Override
    public boolean isValidMove(Position to, Chess game) {
        //checks if the target position is invalid
        if (!game.isValidPosition(to)) {
            return false;
        }

        //calculates the # difference in row & columns between the target and original positions
        Position from = this.getPosition();
        int rowDifference = Math.abs(to.getRow() - from.getRow());
        int colDifference = Math.abs(to.getCol() - from.getCol());

        //checks if move is a straight line move
        if (rowDifference != 0 && colDifference != 0) {
            return false;
        }

        //checks if there are friendly pieces in path
        if (!isPathClear(from, to, game)) {
            return false;
        }

        Piece p = game.getPiece(to);
        return p == null || p.getColor() != this.getColor();
    }

    /**
     * Helper function to check if the path between two positions is clear.
     * @param from  The starting position.
     * @param to    The end position.
     * @param game The chessboard.
     * @return true if the path is clear, false otherwise.
     */

    private boolean isPathClear(Position from, Position to, Chess game) {
        int rowDirection = Integer.compare(to.getRow(), from.getRow());
        int colDirection = Integer.compare(to.getCol(), from.getCol());
        int currentRow = from.getRow() + rowDirection;
        int currentCol = from.getCol() + colDirection;

        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            if (game.getPiece(new Position(currentRow, currentCol)) != null) {
                return false;
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
