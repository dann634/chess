package com.jackson.game;

import com.jackson.game.pieces.Piece;
import com.jackson.ui.Board;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Game {

    private static Player white;
    private static Player black;

    private static BooleanProperty isWhiteTurn;

    private Piece[][] basicBoard;


    public void start(Stage stage) {
        this.basicBoard = new Piece[8][8];
        Board board = new Board(stage, this.basicBoard, this);
        isWhiteTurn = initTurnProperty();

        Thread gameThread = new Thread(() -> {

            white = new Player(true);
            black = new Player(false);

            //Add all pieces
            white.initializePieces(this.basicBoard);
            black.initializePieces(this.basicBoard);

            //Draw board for first time
            Platform.runLater(() -> board.drawBoard(this.basicBoard));

            //Main Game Loop
            while(true) {
                if(white.hasMoved()) {
                    white.setHasMoved(false);
                    isWhiteTurn.set(false);
                } else if(black.hasMoved()) {
                    black.setHasMoved(false);
                    isWhiteTurn.set(true);
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
    }

    private SimpleBooleanProperty initTurnProperty() {
        SimpleBooleanProperty property = new SimpleBooleanProperty(true);
        property.addListener((observableValue, aBoolean, t1) -> {
            white.setPiecesEnabled(t1);
            black.setPiecesEnabled(!t1);
        });
        return property;
    }

    public static boolean isWhiteTurn() {
        return isWhiteTurn.get();
    }

    // TODO: 28/03/2023 Add isWhiteTurnProperty


    public Piece[][] getBasicBoard() {
        return basicBoard;
    }

    public void move(Piece selectedPiece, byte[] move) {
        this.basicBoard[selectedPiece.getColumn()][selectedPiece.getRow()] = null;
        this.basicBoard[move[0]][move[1]] = selectedPiece;

        //Update Board
    }

}
