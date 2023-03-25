package com.jackson.ui;

import com.jackson.game.Game;
import com.jackson.game.pieces.MoveTask;
import com.jackson.game.Player;
import com.jackson.game.pieces.Piece;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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

    public Scene getScene() {
        isBoardFacingWhite = new SimpleBooleanProperty(true);
        isBoardFacingWhite.addListener((observableValue, aBoolean, current) -> {
            //Rotates Board
            if(current) {
                root.setRotate(0);
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

    private void removeMovementIndicators() {
        if (root.getChildren().removeIf(n -> n.getClass().getSimpleName().equals("Circle"))
        ) {
            System.out.println("Circles Removed");
        }
        root.setPrefHeight(root.getPrefHeight() + 0.0001); //Updates Gridpane
    }

    private void addCellsToGrid() { //Adds cells to board
        for (byte i = 0; i < 8; i++) {
            for (byte j = 0; j < 8; j++) {
                root.add(new Cell(i, j).getCell(), j, i);
            }
        }
    }

    private void addMovementIndicators(Set<byte[]> moves) {

        System.out.println(moves.size());
        removeMovementIndicators();

        for(byte[] move : moves) {
            Circle movementIndicator = initMovementIndicator(move[0], move[1]);
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

        public Cell(byte row, byte column) {
            this.row = row;
            this.column = column;

            this.vBox = new VBox();
            this.vBox.getStyleClass().add("cell");
            boolean isLight = (row + column) % 2 == 0;
            this.vBox.setId(isLight ? "lightCell" : "darkCell");

            this.vBox.setOnMouseClicked(mouseEvent -> {

                //Add movement indicators
                this.pieceInCell = Game.getPieceInCell(row, column);
                selectedPiece = this.pieceInCell;

                root.setPrefHeight(root.getPrefHeight() + 0.001);

                if(this.pieceInCell != null) {
                    //Add Movement Indicators
                    removeMovementIndicators();

                    MoveTask moveTask = new MoveTask(this.pieceInCell, Game.getAllPieces());
                    moveTask.setOnSucceeded(workerStateEvent -> {
                        if(this.pieceInCell.isWhite() && white.isTurn()) {
                            addMovementIndicators(moveTask.getValue());
                        } else if(this.pieceInCell.isWhite() && black.isTurn()) {
                            addMovementIndicators(moveTask.getValue());
                        }
                    });
                    Thread moveThread = new Thread(moveTask);
                    moveThread.setDaemon(true);
                    moveThread.start();
                }
            });

        }



        public VBox getCell() {
            return this.vBox;
        }
    }


}
