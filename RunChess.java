package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RunChess implements Runnable {
    public void run() {
        //creates top level frame
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //creates and adds chessboard object to center of border layout
        ChessBoard board = new ChessBoard();
        frame.add(board, BorderLayout.CENTER);

        //creates new game button
        JButton newGame = new JButton("New Game");
        //adds mouseListener that resets the board when a click is tracked
        newGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                board.resetBoard();
            }
        });

        //adds button and windowListener (listening for closing of the window)
        frame.add(newGame, BorderLayout.SOUTH);
        board.addWindowListener(frame);

        //instructions popup
        final JPopupMenu popup = new JPopupMenu("Instructions");
        String instructions = "Welcome to Chess!\n" +
               "Chess is a two player, turn-based strategy board game where the " +
               "objective is to checkmate your opponent's king. " +
               "The basic rules include:\n" +
               "Each player starts with 16 pieces: 8 pawns, 2 knights, " +
                "2 bishops, 2 rooks, 1 queen, and 1 king.\n" +
               "Pawns can move forward one square (or two squares on their first move) " +
                "and capture diagonally.\n" +
               "Rooks move horizontally or vertically, bishops move diagonally, " +
                "and queens combine both.\n" +
               "Knights move in an 'L' shape: two squares in one direction and " +
                "one in the perpendicular direction.\n" +
               "Kings move one square in any direction. Protect your king!\n" +
               "There are also other complex move implementations, such as castling, " +
               "en passant, and pawn promotion.\n" +
               "To move a piece, please click on it (which will highlight it), " +
               "and then click on the desired square to move it to.\n" +
               "Click 'Ok' to begin. Good luck!";

        JOptionPane.showMessageDialog(frame, instructions, 
                "Instructions", JOptionPane.INFORMATION_MESSAGE);

        frame.pack();
        frame.setVisible(true);

    }
}
