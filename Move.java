package org.cis1200.chess;

import java.io.Serializable;

public class Move implements Serializable {
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;

    //constructs a Move object
    public Move(Position from, Position to, Piece movedPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    //basic getters and setters for instance variables

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public String toString() {
        if (movedPiece instanceof King && Math.abs(from.getCol() - to.getCol()) == 2) {
            if (to.getCol() > from.getCol()) {
                return "Castling kingside";
            } else {
                return "Castling queenside";
            }
        }

        String captureInfo = "";
        if (capturedPiece != null) {
            captureInfo = " capturing " + capturedPiece;
        }
        return movedPiece + " moved from " + from + " to " + to + captureInfo;
    }


}

