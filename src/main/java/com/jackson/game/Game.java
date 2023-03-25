package com.jackson.game;

import com.jackson.game.pieces.Piece;
import com.jackson.ui.Board;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Game {

    private static Player white;
    private static Player black;

    private static Board board;

    public void start(Board lboard) {
        board = lboard;

        Thread gameThread = new Thread(() -> {

            white = new Player(true);
            black = new Player(false);

            //Add all pieces
            white.initializePieces();
            black.initializePieces();

            //Set turns
            white.setTurn(true);
            black.setTurn(false);

            //Draw board for first time
            Platform.runLater(() -> board.drawBoard(white, black));

            //Main Game Loop
            while(true) {
                if(white.hasMoved()) {
                    white.setHasMoved(false);
                    white.setTurn(false); //Disables Pieces
                    black.setTurn(true); //Enables Pieces
                    System.out.println("White has moved");
                } else if(black.hasMoved()) {
                    black.setHasMoved(false);
                    black.setTurn(false); //Disables Pieces
                    white.setTurn(true); //Enables Pieces
                    System.out.println("Black has moved");
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });

        gameThread.setDaemon(true);
        gameThread.start();

    }

    public static Piece getPieceInCell(byte row, byte column) {
        for(Piece piece : getAllPieces()) {
            if(piece.getRow() == row && piece.getColumn() == column) {
                return piece;
            }
        }
        return null;
    }

    public static List<Piece> getAllPieces() {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(white.getPieces());
        allPieces.addAll(black.getPieces());

        return allPieces;
    }

    public static void rotateAllPieces(short rotation) {
        for(Piece piece : getAllPieces()) {
            piece.getImageView().setRotate(rotation);
        }
    }

    public static void move(Piece piece, byte row, byte column) {
        Player enemyPlayer = piece.isWhite() ? black : white;
        Player friendlyPlayer = piece.isWhite() ? white : black;

        for(Piece enemyPiece : enemyPlayer.getPieces()) {
            if(enemyPiece.getRow() == row && enemyPiece.getColumn() == column) {
                enemyPlayer.getPieces().remove(enemyPiece); //Remove enemy piece
                break;
            }
        }

        piece.setColumn(column);
        piece.setRow(row);

        friendlyPlayer.setHasMoved(true);

        board.drawBoard(white, black);
    }


}
