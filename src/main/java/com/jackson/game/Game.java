package com.jackson.game;

import com.jackson.game.pieces.*;
import com.jackson.main.Main;
import com.jackson.ui.Board;
import com.jackson.ui.SoundEffectsController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private static Player white;
    private static Player black;

    private static BooleanProperty isWhiteTurn;

    private Piece[][] basicBoard;

    private Board board;
    private boolean inCheck;
    private Stage stage;


    public void start(Stage stage) {
        this.inCheck = false;
        this.basicBoard = new Piece[8][8];
        this.stage = stage;
        this.board = new Board(this.stage, this.basicBoard, this);
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

        //Sounds
        if(this.basicBoard[move[0]][move[1]] != null) {
            soundEffectsController.playCaptureEffect();
        } else {
            soundEffectsController.playMoveEffect();
        }

        //Remove piece from pieces
        Piece targetPiece = this.basicBoard[move[0]][move[1]];
        if(targetPiece != null) {
            Player player = targetPiece.isWhite() ? white : black;
            player.getPieces().remove(targetPiece);
        }

        this.basicBoard[move[0]][move[1]] = null; //Deletes any piece already on target square
        this.basicBoard[move[0]][move[1]] = selectedPiece; //Sets piece to new location
        this.board.clearAllIndicators(); //clears all indicators

        selectedPiece.setColumn(move[0]);
        selectedPiece.setRow(move[1]);

        board.removeCheckIndicator();

        //Promotion check
        if(selectedPiece.getClass().getSimpleName().equals("Pawn") && (selectedPiece.getRow() == 0 || selectedPiece.getRow() == 7)) {
            promote(selectedPiece);
        }

        this.inCheck = false;

        //Look for check
        King enemyKing = getKing(!selectedPiece.isWhite());
        if(enemyKing.isInCheck(basicBoard)) {
            //Print all enemy check moves
            if(getAllCheckMoves(!selectedPiece.isWhite()).isEmpty()) {
                System.out.println((selectedPiece.isWhite() ? "White" : "Black") + " wins!!");
                //Go to end game screen
            }
            this.inCheck = true;
            board.highlightCheck(enemyKing);
        }


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

    private List<byte[]> getAllCheckMoves(boolean isWhite) { // FIXME: 11/05/2023 
        List<byte[]> moves = new ArrayList<>();
        Player player = isWhite ? white : black;
        for(Piece piece : player.getPieces()) {
            moves.addAll(piece.getCheckMoves(basicBoard));
        }
        return moves;
    }

    public void promote(Piece oldPiece) {
        Stage promoteStage = new Stage();
        promoteStage.initOwner(this.stage);
        promoteStage.initModality(Modality.APPLICATION_MODAL);
        promoteStage.setResizable(false);

        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(8, 8, 8, 8));
        vBox.setAlignment(Pos.CENTER);

        Button queenButton = createPromoteButton(new Queen((byte) -1, (byte) -1, oldPiece.isWhite()), oldPiece, promoteStage);
        Button rookButton = createPromoteButton(new Rook((byte) -1, (byte) -1, oldPiece.isWhite()), oldPiece, promoteStage);
        Button bishopButton = createPromoteButton(new Bishop((byte) -1, (byte) -1, oldPiece.isWhite()), oldPiece, promoteStage);
        Button knightButton = createPromoteButton(new Knight((byte) -1, (byte) -1, oldPiece.isWhite()), oldPiece, promoteStage);

        vBox.getChildren().addAll(queenButton, rookButton, bishopButton, knightButton);
        promoteStage.setScene(new Scene(vBox));

        promoteStage.show();
    }

    private Button createPromoteButton(Piece piece, Piece oldPiece, Stage promotionStage) {
        ImageView imageView = piece.getImageView();
        Button btn = new Button();

        btn.setGraphic(imageView);

        btn.setOnAction(e -> {

            piece.setColumn(oldPiece.getColumn());
            piece.setRow(oldPiece.getRow());
            this.basicBoard[oldPiece.getColumn()][oldPiece.getRow()] = piece;

            Player player = piece.isWhite() ? white : black;
            player.getPieces().add(piece);

            this.board.removeImageView(oldPiece.getColumn(), oldPiece.getRow());
            this.board.addImageView(piece.getImageView(), piece.getColumn(), piece.getRow());

            promotionStage.close();

        });

        return btn;
    }



}
