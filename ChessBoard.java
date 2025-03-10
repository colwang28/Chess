package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChessBoard extends JPanel {
    private Chess gameModel;
    private Position selectedPosition;

    public ChessBoard() {
        gameModel = new Chess();
        setPreferredSize(new Dimension(640, 640));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked at: (" + e.getX() + ", " + e.getY() + ")");
                int x = e.getX() / 80;
                int y = e.getY() / 80;
                handleClick(x, y);
            }
        });
        try {
            loadGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    g.setColor(new Color(150, 77, 34));
                } else {
                    g.setColor(new Color(238, 220, 151));
                }
                g.fillRect(j * 80, i * 80, 80, 80);

                if (selectedPosition != null && selectedPosition.getRow() == i
                        && selectedPosition.getCol() == j) {
                    g.setColor(new Color(255, 255, 0, 100));
                    g.fillRect(j * 80, i * 80, 80, 80);
                }

                Piece piece = gameModel.getPiece(new Position(i, j));
                if (piece != null) {
                    drawPiece(g, piece, i, j);
                }
            }
        }

    }

    private void drawPiece(Graphics g, Piece piece, int row, int col) {
        Image pieceImage = PieceImages.getImage(piece);
        if (pieceImage != null) {
            g.drawImage(pieceImage, col * 80, row * 80, 80, 80, null);
        }
    }

    private void handleClick(int x, int y) {
        Position clicked = new Position(y, x);

        if (selectedPosition == null) {
            Piece piece = gameModel.getPiece(clicked);
            //System.out.println("Piece at clicked position: " + piece);

            int currentPlayerColor;
            if (gameModel.isWhiteTurn()) {
                currentPlayerColor = 0;
            } else {
                currentPlayerColor = 1;
            }

            if (piece != null && piece.getColor() == currentPlayerColor) {
                selectedPosition = clicked;
                repaint();
            } else {
                System.out.println("Invalid selection. No piece or wrong player's turn.");
            }
        } else {
            boolean hasMoved = gameModel.movePiece(selectedPosition, clicked);
            System.out.println("Move successful: " + hasMoved);

            selectedPosition = null;
            repaint();

            if (hasMoved) {
                if (gameModel.isPawnPromotion(clicked)) {
                    handlePawnPromotion(clicked);
                }

                String result = gameModel.checkForGameOver();
                System.out.println("Game over result: " + result);
                if (result != null) {
                    String message;
                    if (result.equals("Checkmate")) {
                        if (gameModel.isWhiteTurn()) {
                            message = "Black wins by Checkmate!";
                        } else {
                            message = "White wins by Checkmate!";
                        }
                    } else if (result.equals("Stalemate")) {
                        message = "The game is a draw due to Stalemate.";
                    } else if (result.equals("Draw by insufficient material")) {
                        message = "The game is a draw due to insufficient material.";
                    } else {
                        message = "Game over.";
                    }
                    JOptionPane.showMessageDialog(this, message,
                            "Game Over", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Position kingPosition;
                    if (gameModel.isWhiteTurn()) {
                        kingPosition = gameModel.getWhiteKingPosition();
                    } else {
                        kingPosition = gameModel.getBlackKingPosition();
                    }

                    if (gameModel.isKingInCheck(kingPosition)) {
                        String message;
                        if (gameModel.isWhiteTurn()) {
                            message = "White is in Check!";
                        } else {
                            message = "Black is in Check!";
                        }
                        JOptionPane.showMessageDialog(this,
                                message,
                                "Check",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

        }
    }

    private void handlePawnPromotion(Position position) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose promotion piece:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        Piece promotedPiece;
        int color = gameModel.getPiece(position).getColor();
        switch (choice) {
            case 0:
                promotedPiece = new Queen(color, position);
                break;
            case 1:
                promotedPiece = new Rook(color, position);
                break;
            case 2:
                promotedPiece = new Bishop(color, position);
                break;
            case 3:
                promotedPiece = new Knight(color, position);
                break;
            default:
                promotedPiece = new Queen(color, position);
        }
        gameModel.setPiece(position, promotedPiece);
        gameModel.switchTurn();
        repaint();
    }

    private void saveGame() {
        gameModel.saveGameState("moveHistory");
        JOptionPane.showMessageDialog(this, "Game saved successfully!",
                "Save Game", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadGame() {
        try {
            gameModel.loadGameState("moveHistory");
            repaint();
            JOptionPane.showMessageDialog(this, "Game loaded successfully!",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            gameModel = new Chess();
        }
    }

    public void addWindowListener(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveGame();
                frame.dispose();
            }
        });
    }

    public void resetBoard() {
        gameModel.reset();
        selectedPosition = null;
        repaint();
    }

}
