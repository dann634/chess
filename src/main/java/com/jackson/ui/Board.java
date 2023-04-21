package com.jackson.ui;

import com.jackson.game.Game;
import com.jackson.game.Player;
import com.jackson.game.pieces.MoveTask;
import com.jackson.game.pieces.Piece;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board {

    private Piece selectedPiece;

    private SimpleBooleanProperty isBoardFacingWhite;

    private GridPane root;

    private Player white;
    private Player black;

    private Thread moveThread;

    private List<Circle> movementIndicators;

    public Scene getScene() {
        this.movementIndicators = new ArrayList<>();
        isBoardFacingWhite = new SimpleBooleanProperty(true);
        isBoardFacingWhite.addListener((observableValue, aBoolean, current) -> {
            //Rotates Board
            if(current) {
                this.root.setRotate(0);
                Game.rotateAllPieces((short) 0);
            } else {
                root.setRotate(180);
                Game.rotateAllPieces((short) 180);
            }
        });
        root = new GridPane();
        addCellsToGrid(); //Adds cells to board

        Scene scene = new Scene(root);
        scene.getStylesheets().add("file:src/main/resources/stylesheets/board.css");
        return scene;
    }

    public void drawBoard(Player white, Player black) {

        this.white = white;
        this.black = black;

        root.getChildren().removeIf(n -> n.getClass().getSimpleName().equals("ImageView"));

        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(white.getPieces());
        allPieces.addAll(black.getPieces());

        for(Piece piece : allPieces) {
            root.add(piece.getImageView(), piece.getColumn(), piece.getRow());
        }
    }

    private void selfDestructBoardAndAddPiecesAgain() {
        this.root.getChildren().clear();
        addCellsToGrid();
        drawBoard(white, black);
    }

    private void removeMovementIndicators() {
        // FIXME: 20/04/2023
        this.root.getChildren().removeAll(this.movementIndicators);
        this.movementIndicators.clear();
    }

    private void addCellsToGrid() { //Adds cells to board
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                root.add(new Cell(i, j).getCell(), j, i);
            }
        }
    }

    public void addMovementIndicators(Set<byte[]> moves) {

        for(byte[] move : moves) {
            Circle movementIndicator = initMovementIndicator(move[0], move[1]);
            movementIndicators.add(movementIndicator);
            root.add(movementIndicator, move[1], move[0]);
        }
    }

    private Circle initMovementIndicator(byte row, byte column) {
        Circle circle = new Circle();
        circle.setRadius(20);
        circle.setTranslateX(18);
        circle.setId("movementIndicator");

        circle.setOnMouseClicked(mouseEvent -> {
            //Call move piece

            Game.move(this.selectedPiece, row, column);

            //Calls rotate board
            if(root.getRotate() == 0) {
                this.isBoardFacingWhite.setValue(false);
            } else {
                this.isBoardFacingWhite.setValue(true);
            }

            removeMovementIndicators();
        });

        return circle;
    }

    private Board getInstance() {
        return this;
    }

    public boolean isCellOccupied(byte targetRow, byte targetColumn) {
        return false;
    }

    public GridPane getBoardRoot() {
        return this.root;
    }

    class Cell {

        private byte row;
        private byte column;
        private VBox vBox;
        private Piece pieceInCell;

        private MoveTask moveTask;

        public Cell(byte row, byte column) {
            this.row = row;
            this.column = column;

            this.vBox = new VBox();
            this.vBox.getStyleClass().add("cell");
            boolean isLight = (row + column) % 2 == 0;
            this.vBox.setId(isLight ? "lightCell" : "darkCell");

            this.vBox.setOnMouseClicked(mouseEvent -> {

                removeMovementIndicators();

                //Add movement indicators
                this.pieceInCell = Game.getPieceInCell(row, column);
                selectedPiece = this.pieceInCell;

                if(this.pieceInCell != null) {
                    //Add Movement Indicators

//                    selfDestructBoardAndAddPiecesAgain();

                    moveTask = new MoveTask(this.pieceInCell, Game.getAllPieces(), getInstance());

                    moveTask.setOnSucceeded(workerStateEvent -> {
                        //Add movement indicators
                        removeMovementIndicators();
                        addMovementIndicators(moveTask.getValue());
                    });

                    moveThread = new Thread(moveTask);
                    if((selectedPiece.isWhite() && Game.isWhiteTurn()) || (!selectedPiece.isWhite() && !Game.isWhiteTurn())) {
                        moveThread.start();
                    }


                }

            });



        }

        public VBox getCell() {
            return this.vBox;
        }
    }


}
