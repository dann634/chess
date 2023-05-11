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

    private boolean isCheckmate;
    private boolean isPieceCaptured;


    public void start(Stage stage) {
        this.inCheck = false;
        this.basicBoard = new Piece[8][8];
        this.stage = stage;
        this.board = new Board(this.stage, this.basicBoard, this);
        isWhiteTurn = initTurnProperty();

        Thread gameThread = new Thread(() -> {

            // TODO: 11/05/2023 Dont need this thread

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


    // TODO: 28/03/2023 Add isWhiteTurnProperty


    public Piece[][] getBasicBoard() {
        return basicBoard;
    }

    public void move(Piece selectedPiece, byte[] move , SoundEffectsController soundEffectsController) {

        /*
        - Saves if piece was captured (flag for sound)
        - Sets previous location to null
        - Moves selectedPiece to new position on Piece[][]
        - Updates selectedPiece row and column to new position
         */
        movePieces(selectedPiece, move);

        /*
        - Removes Red Highlight on King
        - Hides all movement indicators
         */
        updateUI();

        /*
        - If pawn and on row 0 or 8
        - Create dialog box with buttons for promotion
         */
        canPromote(selectedPiece);

        /*
        - Gets enemy king
        - If in check sets
            - inCheck to true
            -Highlights cell in red
        - If checkmate go to end game screen
         */
        isCheck(selectedPiece);

        /*
        - Plays correct sound for move
        - If checkmate, don't play move or capture as it bugs
         */
        playSound(soundEffectsController);



    }

    //Move Method Broken Up

    private void movePieces(Piece piece, byte[] move) {

        this.isPieceCaptured = (this.basicBoard[move[0]][move[1]] != null); //Flag for sound


        this.basicBoard[piece.getColumn()][piece.getRow()] = null; //Sets previous location to null

        //Remove piece from pieces
        Piece targetPiece = this.basicBoard[move[0]][move[1]];
        if(targetPiece != null) {
            Player player = targetPiece.isWhite() ? white : black;
            player.getPieces().remove(targetPiece);
        }

        this.basicBoard[move[0]][move[1]] = null; //Deletes any piece already on target square
        this.basicBoard[move[0]][move[1]] = piece; //Sets piece to new location

        piece.setColumn(move[0]); //Sets new column
        piece.setRow(move[1]); //Sets new Row


    }

    private void updateUI() {
        this.board.removeCheckIndicator(); //Remove Red Highlight on King
        this.board.clearAllIndicators(); //Removes all movement indicators on board
    }

    private void isCheck(Piece piece) {
        this.inCheck = false;
        //Look for check
        King enemyKing = getKing(!piece.isWhite());
        if(enemyKing.isInCheck(basicBoard)) {
            //Print all enemy check moves
            if(getAllCheckMoves(!piece.isWhite()).isEmpty()) {
                System.out.println((piece.isWhite() ? "White" : "Black") + " wins!!");
                //Go to end game screen
                isCheckmate = true;
            }
            this.inCheck = true;
            board.highlightCheck(enemyKing);
        }
    }

    private void canPromote(Piece piece) {
        if(piece.getClass().getSimpleName().equals("Pawn") && (piece.getRow() == 0 || piece.getRow() == 7)) {
            promote(piece);
        }
    }

    private void playSound(SoundEffectsController soundEffectsController) {
        if(this.isCheckmate) {
            soundEffectsController.playSound("win");
        } else {
            if(this.isPieceCaptured) {
                soundEffectsController.playSound("capture");
            } else {
                soundEffectsController.playSound("move");
            }
        }
    }


    //Get Pieces

    public static List<Piece> getAllPieces() {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(white.getPieces());
        allPieces.addAll(black.getPieces());

        return allPieces;
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

    public static List<Rook> getRooks(boolean isWhite) {
        Player player = isWhite ? white : black;
        List<Rook> rooks = new ArrayList<>();
        for(Piece piece : player.getPieces()) {
            if(piece.getClass().getSimpleName().equals("Rook")) {
                rooks.add((Rook) piece);
            }
        }
        return rooks;
    }



    //Check
    public boolean isInCheck() {
        return inCheck;
    }

    private List<byte[]> getAllCheckMoves(boolean isWhite) {
        List<byte[]> moves = new ArrayList<>();
        Player player = isWhite ? white : black;
        for(Piece piece : player.getPieces()) {
            moves.addAll(piece.getCheckMoves(basicBoard));
        }
        return moves;
    }


    //Promotion
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
            piece.getImageView().setTranslateX(-8); //FOr some reason it's not centred
            piece.getImageView().setTranslateY(-5);
            this.board.addImageView(piece.getImageView(), piece.getColumn(), piece.getRow());

            //Highlight check
            King king = Game.getKing(!piece.isWhite());
            if(king.isInCheck(this.basicBoard)) {
                board.highlightCheck(king);
            }

            promotionStage.close();

        });

        return btn;
    }


    //Castling
    private void castle() {

    }



}
