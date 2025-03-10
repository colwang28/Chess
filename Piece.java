package org.cis1200.chess;

import java.io.Serializable;

public abstract class Piece implements Serializable {
    private static final int WHITE = 0;
    private static final int BLACK = 1;
    private int color;
    private Position position;
    private boolean hasMoved;

    //creates Piece object
    public Piece(int color, Position position) {
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    //basic getters and setters for instance variables

    public int getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void markAsMoved() {
        this.hasMoved = true;
    }

    /**
     * Abstract function that tests if a move is valid,
     * requires individual implementation in subclasses
     * @param to Position to move the piece to.
     * @param game The state of the Chess game.
     * @return true if move is valid, false otherwise.
     */
    public abstract boolean isValidMove(Position to, Chess game);

    @Override
    public String toString() {
        if (color == WHITE) {
            return "White" + " " + this.getClass().getSimpleName();
        } else {
            return "Black" + " " + this.getClass().getSimpleName();
        }
    }
}