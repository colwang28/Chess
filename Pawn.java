package org.cis1200.chess;

import java.io.Serializable;

public class Pawn extends Piece implements Serializable {

    //creates Pawn object using Piece's (superclass)'s constructor
    public Pawn(int color, Position position) {
        super(color, position);
    }



    @Override
    public boolean isValidMove(Position to, Chess game) {
        //checks if the target position is invalid
        if (!game.isValidPosition(to)) {
            return false;
        }

        //calculates the # difference in row & columns between the target and original positions
        Position from = this.getPosition();
        int rowDifference = to.getRow() - from.getRow();
        int colDifference = to.getCol() - from.getCol();
        int dir;
        if (this.getColor() == 0) {
            dir = -1;
        } else {
            dir = 1;
        }

        //examines if moving 1 move forward is valid
        if (colDifference == 0 && rowDifference == dir && game.getPiece(to) == null) {
            return true;
        }

        //examines if moving 2 moves forward is valid - only happen if the pawn did not move before
        if (colDifference == 0 && rowDifference == dir * 2 && !this.hasMoved()) {
            Position middleSquare = new Position(from.getRow() + dir, from.getCol());
            return game.getPiece(middleSquare) == null && game.getPiece(to) == null;
        }

        //examines if capturing is valid
        if ((colDifference == 1 || colDifference == -1) && rowDifference == dir) {
            Piece toCapture = game.getPiece(to);
            return toCapture != null && toCapture.getColor() != this.getColor();
        }

        //means that the move is invalid if it doesn't pass the above checks
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
