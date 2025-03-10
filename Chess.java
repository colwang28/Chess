package org.cis1200.chess;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Chess implements Serializable {
    private Piece[][] board;
    private List<Move> moveHistory;
    private Move lastMove;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private Position whiteKingPosition;
    private Position blackKingPosition;

    public Chess() {
        reset();
    }

    //resets the game state and initializes board
    public void reset() {
        board = new Piece[8][8];
        moveHistory = new ArrayList<>();
        isWhiteTurn = true;
        isGameOver = false;

        initializeBoard();
    }

    //initializes chessboard with pieces in their starting positions
    public void initializeBoard() {
        //set the pawns
        for (int col = 0; col < 8; col++) {
            setPiece(new Position(1, col), new Pawn(1, new Position(1, col)));
            setPiece(new Position(6, col), new Pawn(0, new Position(6, col)));
        }
        //set black pieces
        setPiece(new Position(0,0), new Rook(1, new Position(0, 0)));
        setPiece(new Position(0,1), new Knight(1, new Position(0, 1)));
        setPiece(new Position(0,2), new Bishop(1, new Position(0, 2)));
        setPiece(new Position(0,3), new Queen(1, new Position(0, 3)));
        setPiece(new Position(0,4), new King(1, new Position(0, 4)));
        setPiece(new Position(0,5), new Bishop(1, new Position(0, 5)));
        setPiece(new Position(0,6), new Knight(1, new Position(0, 6)));
        setPiece(new Position(0,7), new Rook(1, new Position(0, 7)));

        //set white pieces
        setPiece(new Position(7,0), new Rook(0, new Position(7,0)));
        setPiece(new Position(7,1), new Knight(0, new Position(7,1)));
        setPiece(new Position(7,2), new Bishop(0, new Position(7,2)));
        setPiece(new Position(7,3), new Queen(0, new Position(7,3)));
        setPiece(new Position(7,4), new King(0, new Position(7,4)));
        setPiece(new Position(7,5), new Bishop(0, new Position(7,5)));
        setPiece(new Position(7,6), new Knight(0, new Position(7,6)));
        setPiece(new Position(7,7), new Rook(0, new Position(7,7)));

        //set king positions
        whiteKingPosition = new Position(7,4);
        blackKingPosition = new Position(0,4);
    }

    /**
     * Gets the piece at the given position.
     * @param position The given position.
     * @throws IllegalArgumentException When the position's row or column indices are out of bounds.
     * @return The piece at the given position, or null if the position is empty.
     */
    public Piece getPiece(Position position) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return board[position.getRow()][position.getCol()];
    }

    /**
     * Sets the piece at the given position.
     * @param position The given position.
     * @param piece The piece to place.
     * @throws IllegalArgumentException When the position's row or column indices are out of bounds.
     */
    public void setPiece(Position position, Piece piece) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Invalid position");
        }
        board[position.getRow()][position.getCol()] = piece;
        if (piece instanceof King) {
            if (piece.getColor() == 0) {
                whiteKingPosition = position;
            } else {
                blackKingPosition = position;
            }
        }

    }

    /**
     * Checks if a position is within the bounds of the board.
     * @param position The given position.
     * @return true if the position is valid, false if not.
     */
    public boolean isValidPosition(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Moves a piece from one position to another.
     * Validates the move and checks for special moves like castling and en passant.
     * @param from The starting position.
     * @param to The target position.
     * @return true if the move was successful, false otherwise.
     */
    public boolean movePiece(Position from, Position to) {
        if (isGameOver) {
            return false; //movement not allowed if game is over
        }
        if (!isValidPosition(from) || !isValidPosition(to)) {
            throw new IllegalArgumentException("Invalid position");
        }
        Piece movingPiece = getPiece(from);
        if (movingPiece == null) {
            return false;
        }

        //checks for en passant
        if (attemptEnPassant(from, to)) {
            isWhiteTurn = !isWhiteTurn;
            return true;
        }

        //checks for castling
        if (movingPiece instanceof King && Math.abs(from.getCol() - to.getCol()) == 2) {
            return attemptCastle(from, to);
        }

        //if move is not valid then returns false
        if (!movingPiece.isValidMove(to, this)) {
            return false;
        }

        //if not the right color piece by turn, returns false
        if ((isWhiteTurn && movingPiece.getColor() != 0) ||
                (!isWhiteTurn && movingPiece.getColor() == 0)) {
            return false;
        }

        //checks if there is an opponent's piece at the target position
        Piece capturedPiece = getPiece(to);

        //checks for check by simulating a move
        setPiece(to, movingPiece);
        setPiece(from, null);
        movingPiece.setPosition(to);

        //checks for if the mover's king is in check after the move (an invalid move)
        Position kingPosition;
        if (isWhiteTurn) {
            kingPosition = whiteKingPosition;
        } else {
            kingPosition = blackKingPosition;
        }
        boolean isInCheck = isKingInCheck(kingPosition);

        //undos the simulated move
        setPiece(from, movingPiece);
        setPiece(to, capturedPiece);
        movingPiece.setPosition(from);
        if (isInCheck) {
            return false;
        }

        //makes the actual move only after the check verification is done
        setPiece(to, movingPiece);
        setPiece(from, null);
        movingPiece.setPosition(to);

        if (capturedPiece != null) {
            capturedPiece.setPosition(null);
        }

        if (isPawnPromotion(to)) {
            return true;
        }

        movingPiece.markAsMoved();
        lastMove = new Move(from, to, movingPiece, capturedPiece);
        moveHistory.add(lastMove);
        switchTurn();

        //check for checkmate
        String gameOverResult = checkForGameOver();
        if (gameOverResult != null) {
            System.out.println(gameOverResult + "! Game over.");
            isGameOver = true;
        }

        return true;
    }

    /**
     * Checks if king is in check at a certain king position.
     * @param kP The position in question
     * @return true if King at kP is in check, false if not
     */
    public boolean isKingInCheck(Position kP) {
        int currentPlayerColor;
        if (isWhiteTurn) {
            currentPlayerColor = 0;
        } else {
            currentPlayerColor = 1;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    if (piece.getColor() != currentPlayerColor) {
                        if (piece.isValidMove(kP, this)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Attempts to perform the castling move.
     * @param from The starting position.
     * @param to The ending position.
     * @return true if castling is successful, false otherwise
     */
    private boolean attemptCastle(Position from, Position to) {
        Piece king = getPiece(from);

        //checks if King already moved (can't castle) or if piece is not a king
        if (king.hasMoved() || !(king instanceof King)) {
            System.out.println("Castling failed: King has moved or is not a king.");
            return false;
        }

        int row = from.getRow();
        int direction;

        //checks direction of castle
        if (to.getCol() > from.getCol()) {
            direction = 1; //right
        } else {
            direction = -1; //left
        }

        //sets rook position
        Position rookPosition;
        if (direction == 1) {
            rookPosition = new Position(row, 7);
        } else {
            rookPosition = new Position(row, 0);
        }

        Piece rook = getPiece(rookPosition);
        //checks if rook has moved (can't castle) or if piece is not a rook
        if (rook.hasMoved() || !(rook instanceof Rook)) {
            System.out.println("Castling failed: Rook has moved or is not a rook.");
            return false;
        }

        //check for empty squares between the king and rook
        int start = Math.min(from.getCol(), rookPosition.getCol()) + 1;
        int end = Math.max(from.getCol(), rookPosition.getCol());
        for (int col = start; col < end; col++) {
            if (getPiece(new Position(row, col)) != null) {
                System.out.println("Castling failed: Path between king and rook is not clear.");
                return false;
            }
        }

        //checks that king does not move through check
        for (int col = from.getCol(); col != to.getCol() + direction; col += direction) {
            Position potentialPiecePosition = new Position(row, col);
            if (isKingInCheck(potentialPiecePosition)) {
                System.out.println("Castling failed: King passes through or into check.");
                return false;
            }
        }

        //actual movement of the pieces
        Position rookTo = new Position(row, from.getCol() + direction);
        setPiece(to, king);
        setPiece(from, null);
        king.setPosition(to);
        setPiece(rookTo, rook);
        setPiece(rookPosition, null);
        rook.setPosition(rookTo);

        //marks both pieces as moved
        king.markAsMoved();
        rook.markAsMoved();

        //update game state
        moveHistory.add(new Move(from, to, king, null));
        switchTurn();

        return true;
    }

    /**
     * Attempts to perform the en passant move.
     * @param from The starting position.
     * @param to The ending position.
     * @return true if en passant is successful, false otherwise
     */
    private boolean attemptEnPassant(Position from, Position to) {
        Piece movingPiece = getPiece(from);

        //checks if the moving piece is a pawn
        if (!(movingPiece instanceof Pawn)) {
            return false;
        }

        //determines the direction
        int direction;
        if (movingPiece.getColor() == 0) {
            direction = -1; //white moves up
        } else {
            direction = 1;  //black moves down
        }

        //finds square that the opponent's pawn occupies
        Position capturedPosition = new Position(from.getRow(), to.getCol());

        //checks last move for en passant eligibility
        if (lastMove != null && lastMove.getMovedPiece() instanceof Pawn) {
            //checks that the last move was a pawn push forward by 2 squares
            if (Math.abs(lastMove.getFrom().getRow() - lastMove.getTo().getRow()) == 2 &&
                    lastMove.getTo().equals(capturedPosition)) {
                //performs en passant capture
                setPiece(to, movingPiece); //move capturing pawn
                setPiece(from, null); //remove capturing pawn from original square
                movingPiece.setPosition(to);

                //remove captured pawn
                setPiece(lastMove.getTo(), null);

                return true;
            }
        }
        return false;
    }

    /**
     * Checks for all game over scenarios
     * @return "Checkmate" for checkmate scenario, "Stalemate" for stalemate scenario,
     * "Draw by insufficient material" for Draw by insufficient material scenario
     */

    public String checkForGameOver() {
        //check for checkmate
        if (isKingInCheck(whiteKingPosition) && !hasValidMoves(0)) {
            return "Checkmate";
        }
        if (isKingInCheck(blackKingPosition) && !hasValidMoves(1)) {
            return "Checkmate";
        }
        //check for stalemate
        if (!isKingInCheck(whiteKingPosition) && !hasValidMoves(0)) {
            return "Stalemate";
        }
        if (!isKingInCheck(blackKingPosition) && !hasValidMoves(1)) {
            return "Stalemate";
        }

        //check for insufficient material
        if (hasInsufficientMaterial()) {
            return "Draw by insufficient material";
        }
        return null;
    }

    /**
     * Checks if the pieces left on the board leave insufficient material for a win
     * @return true if yes, false otherwise
     */
    private boolean hasInsufficientMaterial() {
        int whitePieces = countPieces(0);
        int blackPieces = countPieces(1);

        //just the two kings left
        if (whitePieces == 1 && blackPieces == 1) {
            return true;
        }

        //king and bishop/knight vs king
        if ((whitePieces == 2 && blackPieces == 1) || (whitePieces == 1 && blackPieces == 2)) {
            return hasOnlyKingAndMinorPiece(0) || hasOnlyKingAndMinorPiece(1);
        }

        //king and bishop vs king and bishop (opp color bishops)
        if (whitePieces == 2 && blackPieces == 2) {
            return hasOnlyKingAndMinorPiece(0) && hasOnlyKingAndMinorPiece(1)
                    && hasOppositeColoredBishops();
        }

        return false;

    }

    /**
     * Checks if the pieces left on the board for a side are only the king and a minor piece
     * @param color color of player making the move
     * @return true if only king and minor piece are remaining, false otherwise
     */
    private boolean hasOnlyKingAndMinorPiece(int color) {
        int pieceCount = 0;
        boolean hasKing = false;
        boolean hasMinorPiece = false;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == color) {
                    pieceCount++;
                    if (piece instanceof King) {
                        hasKing = true;
                    } else if (piece instanceof Bishop || piece instanceof Knight) {
                        hasMinorPiece = true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return hasKing && hasMinorPiece && pieceCount == 2;
    }

    /**
     * Checks if the two sides have same color square bishops
     * @return true if yes, false otherwise
     */
    private boolean hasOppositeColoredBishops() {
        Bishop whiteBishop = null;
        Bishop blackBishop = null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece instanceof Bishop) {
                    if (piece.getColor() == 0 && whiteBishop == null) {
                        whiteBishop = (Bishop) piece;
                    } else if (piece.getColor() == 1 && blackBishop == null) {
                        blackBishop = (Bishop) piece;
                    } else {
                        return false;
                    }
                }
            }
        }

        if (whiteBishop == null || blackBishop == null) {
            return false;
        }
        //checks if bishops are on opposite colored squares
        return (whiteBishop.getPosition().getRow() + whiteBishop.getPosition().getCol()) % 2 !=
                (blackBishop.getPosition().getRow() + blackBishop.getPosition().getCol()) % 2;
    }

    /**
     * Counts the number of pieces on the board with the given color.
     * @param color color of player making the move
     * @return the number of pieces on the board with color color
     */
    private int countPieces(int color) {
        int count = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Checks if a player has any valid moves.
     * @param currentPlayerColor color of player making the move
     * @return true if there are valid moves, false otherwise
     */
    private boolean hasValidMoves(int currentPlayerColor) {
        //iterates through the board to find pieces of the current player
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == currentPlayerColor) {
                    //tries all possible moves for this piece
                    for (int targetRow = 0; targetRow < 8; targetRow++) {
                        for (int targetCol = 0; targetCol < 8; targetCol++) {
                            Position target = new Position(targetRow, targetCol);
                            //checks if the move is valid for the piece
                            if (piece.isValidMove(target, this)) {
                                //simulate the move
                                if (canMoveWithoutPuttingKingInCheck(piece,
                                        new Position(row, col), target)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if a player can move without putting their king in check.
     * @param piece piece to move
     * @param from Starting position
     * @param to Target Position
     * @return true if there is a valid move that doesn't put the
     * player's king in check, false otherwise
     */
    private boolean canMoveWithoutPuttingKingInCheck(Piece piece,
                                                     Position from, Position to) {
        //simulate move
        Piece capturedPiece = getPiece(to);
        setPiece(to, piece);
        setPiece(from, null);
        piece.setPosition(to);

        //finds king position after the move
        Position kingPosition;
        if (piece.getColor() == 0) {
            kingPosition = whiteKingPosition;
        } else {
            kingPosition = blackKingPosition;
        }

        boolean stillInCheck = isKingInCheck(kingPosition);

        //undos the move
        setPiece(from, piece);
        setPiece(to, capturedPiece);
        piece.setPosition(from);

        //return true if the move doesn't put the king in check
        return !stillInCheck;
    }

    /**
     * Checks if a pawn promotion happens
     * @param to Target Position
     * @return true if there is a pawn promotion, false otherwise.
     */
    public boolean isPawnPromotion(Position to) {
        Piece piece = getPiece(to);
        return piece instanceof Pawn && (to.getRow() == 0 || to.getRow() == 7);
    }

    public Position getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public Position getBlackKingPosition() {
        return blackKingPosition;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    //saves current game state to a file
    public void saveGameState(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(board);
            oos.writeObject(moveHistory);
            oos.writeBoolean(isWhiteTurn);
            oos.writeObject(whiteKingPosition);
            oos.writeObject(blackKingPosition);
        } catch (IOException e) {
            System.err.println("Error saving game state: " + e.getMessage());
        }
    }

    //loads game state from a file
    public void loadGameState(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            board = (Piece[][]) ois.readObject();
            moveHistory = (List<Move>) ois.readObject();
            isWhiteTurn = ois.readBoolean();
            whiteKingPosition = (Position) ois.readObject();
            blackKingPosition = (Position) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            throw new RuntimeException("Failed to load game state", e);
        }
    }

    //switches the player's turn
    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    public void printBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(piece.getClass().getSimpleName().charAt(0) + " ");
                }
            }
            System.out.println();
        }
    }
}





