package org.cis1200.chess;

import java.io.Serializable;

public class King extends Piece implements Serializable {

    //creates King object using Piece's (superclass)'s constructor
    public King(int color, Position position) {
        super(color, position);
    }

    /**
     * Function to check if a move is valid for the king.
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

        //check for king only moving one square in any direction
        if (rowDifference > 1 || colDifference > 1) {
            return false;
        }
        //check if the target square is occupied by a friendly piece
        Piece p = game.getPiece(to);
        if (p != null && p.getColor() == this.getColor()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
