package org.cis1200.chess;

import java.io.Serializable;

public class Knight extends Piece implements Serializable {

    //creates Knight object using Piece's (superclass)'s constructor
    public Knight(int color, Position position) {
        super(color, position);
    }

    /**
     * Function to check if a move is valid for the knight.
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

        //knights can only move in an L-shape, this boolean returns true if
        // the move is an L and false if not.
        boolean isLShape = (rowDifference == 2 && colDifference == 1) ||
                (rowDifference == 1 && colDifference == 2);
        if (!isLShape) {
            return false; //returns false when the move is not an L shape, meaning it is invalid.
        }

        Piece p = game.getPiece(to);
        //returns true when there is no piece at the target square or if the piece is opposite color
        //returns false if same color piece is at the target square
        return p == null || p.getColor() != this.getColor();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
