package com.jackson.game;

import com.jackson.game.pieces.King;
import com.jackson.game.pieces.Knight;
import com.jackson.game.pieces.Pawn;
import com.jackson.game.pieces.Piece;
import com.jackson.ui.Board;
import com.jackson.ui.SoundEffectsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

import java.util.*;

public class Game {

    private static Player white;
    private static Player black;

    private static BooleanProperty isWhiteTurn;

    private Piece[][] basicBoard;

    private Board board;

    private Piece checkingPiece;

    private boolean inCheck;


    public void start(Stage stage) {
        this.inCheck = false;
        this.basicBoard = new Piece[8][8];
        this.board = new Board(stage, this.basicBoard, this);
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

    public static Set<byte[]> getAllEnemyMoves(boolean isWhite, Piece[][] board, boolean isProtected) {
        Set<byte[]> moves = new HashSet<>();
        Player player = isWhite ? black : white;
        List<Piece> pieces = player.getPieces();
        for(Piece piece : pieces) {
            moves.addAll(isProtected ? piece.getSquaresProtected(board) : piece.getValidMoves(board));
//            moves.addAll(piece.getSquaresProtected(board));
        }
        moves.removeIf(n -> n[0] < 0 || n[0] > 7 || n[1] < 0 || n[1] > 7);
        return moves;
    }

    public static List<Piece> getAllPieces() {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(white.getPieces());
        allPieces.addAll(black.getPieces());

        return allPieces;
    }

    public static Set<byte[]> getEnemyPawnDiagonals(boolean isWhite) {
        Player player = isWhite ? black : white;
        Set<byte[]> diagonals = new HashSet<>();
        for(Pawn pawn : player.getAllPawns()) {
            diagonals.addAll(pawn.getDiagonals());
        }
        diagonals.removeIf(n -> n[0] < 0 || n[0] > 7 || n[1] < 0 || n[1] > 7);

        return diagonals;
    }

    public static void rotateAllPieces(short rotation) {
        for(Piece piece : getAllPieces()) {
            piece.getImageView().setRotate(rotation);
        }
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

    public void move(Piece selectedPiece, byte[] move , SoundEffectsController soundEffectsController) {
        this.basicBoard[selectedPiece.getColumn()][selectedPiece.getRow()] = null; //Sets previous location to null

        if(this.basicBoard[move[0]][move[1]] != null) {
            soundEffectsController.playCaptureEffect();
        } else {
            soundEffectsController.playMoveEffect();
        }

        this.basicBoard[move[0]][move[1]] = null; //Deletes any piece already on target square
        this.basicBoard[move[0]][move[1]] = selectedPiece; //Sets piece to new location
        this.board.clearAllIndicators(); //clears all indicators

        selectedPiece.setColumn(move[0]);
        selectedPiece.setRow(move[1]);

        board.removeCheckIndicator();

        //Look for check
        King enemyKing = getKing(!selectedPiece.isWhite());
        if(enemyKing.isInCheck(basicBoard)) {
            this.inCheck = true;
            board.highlightCheck(enemyKing);
        } else {
            this.inCheck = false;
        }
//        List<byte[]> newMoves = selectedPiece.getValidMoves(this.basicBoard);
//        for(byte[] newMove : newMoves) {
//            if(newMove[0] == enemyKing.getColumn() && newMove[1] == enemyKing.getRow()) {
//                this.checkingPiece = selectedPiece;
//                board.highlightCheck(enemyKing);
//                break;
//            }
//        }
    }

    public static King getKing(boolean isWhite) {
        Player player = isWhite ? white : black;
        for(Piece piece : player.getPieces()) {
            if(piece.getClass().getSimpleName().equals("King")) {
                return (King) piece;
            }
        }
        return null;
    }

    public static List<Pawn> getPawns(boolean isWhite) {
        Player player = isWhite ? white : black;
        List<Pawn> pawns = new ArrayList<>();
        for(Piece piece : player.getPieces()) {
            if(piece.getClass().getSimpleName().equals("Pawn")) {
                pawns.add((Pawn) piece);
            }
        }
        return pawns;
    }

    public static List<Knight> getKnights(boolean isWhite) {
        Player player = isWhite ? white : black;
        List<Knight> knights = new ArrayList<>();
        for(Piece piece : player.getPieces()) {
            if(piece.getClass().getSimpleName().equals("Knight")) {
                knights.add((Knight) piece);
            }
        }
        return knights;
    }

    public boolean isInCheck() {
        return inCheck;
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }
}
